package com.team4ever.backend.domain.ubti.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team4ever.backend.domain.ubti.dto.UBTIRequest;
import com.team4ever.backend.domain.ubti.dto.UBTIResult;
import com.team4ever.backend.global.exception.ErrorCode;
import com.team4ever.backend.global.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class UBTIService {

	private final WebClient webClient;
	private final String questionPath;
	private final String resultPath;
	private final ObjectMapper objectMapper;

	public UBTIService(
			WebClient.Builder webClientBuilder,
			@Value("${fastapi.ubti.host}") String host,
			@Value("${fastapi.ubti.port}") int port,
			@Value("${fastapi.ubti.path.question}") String questionPath,
			@Value("${fastapi.ubti.path.result}") String resultPath,
			ObjectMapper objectMapper
	) {
		this.webClient = webClientBuilder
				.baseUrl(String.format("http://%s:%d", host, port))
				.build();
		this.questionPath = questionPath;
		this.resultPath = resultPath;
		this.objectMapper = objectMapper;
	}

	public Flux<ServerSentEvent<String>> nextQuestionStream(UBTIRequest req) {
		return webClient.post()
				.uri(questionPath)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.TEXT_EVENT_STREAM)
				.bodyValue(req)
				.retrieve()
				.bodyToFlux(String.class)
				.map(data -> ServerSentEvent.builder(data).build())
				.doOnError(error -> log.error("UBTI 질문 스트리밍 오류: ", error));
	}

	// 최종 결과 조회 - data 필드 처리 수정
	public Mono<BaseResponse<UBTIResult>> finalResultWrapped(UBTIRequest req) {
		log.info("UBTI 결과 요청: session_id={}, message={}", req.getSession_id(), req.getMessage());

		return webClient.post()
				.uri(resultPath)
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(req)
				.retrieve()
				.bodyToMono(String.class)
				.doOnNext(rawResponse -> {
					log.info("Python FastAPI 원본 응답: {}", rawResponse);
				})
				.flatMap(this::parseUBTIResult)
				.map(data -> {
					log.info("파싱된 UBTI 결과: {}", data);
					return BaseResponse.success(data);
				})
				.onErrorResume(WebClientResponseException.class, e -> {
					log.error("FastAPI 호출 오류: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
					return Mono.just(BaseResponse.error(ErrorCode.UBTI_AI_API_ERROR));
				})
				.onErrorResume(JsonProcessingException.class, e -> {
					log.error("JSON 파싱 오류: ", e);
					return Mono.just(BaseResponse.error(ErrorCode.UBTI_JSON_PARSE_ERROR));
				})
				.onErrorResume(Exception.class, e -> {
					log.error("UBTI 결과 처리 중 예상치 못한 오류: ", e);
					return Mono.just(BaseResponse.error(ErrorCode.UBTI_GENERATION_FAILED));
				});
	}

	private Mono<UBTIResult> parseUBTIResult(String rawResponse) {
		return Mono.fromCallable(() -> {
			try {
				log.debug("JSON 파싱 시작: {}", rawResponse);

				// 1. 전체 응답 파싱
				JsonNode rootNode = objectMapper.readTree(rawResponse);
				log.debug("파싱된 루트 노드: {}", rootNode);

				// 2. FastAPI BaseResponse 구조 확인
				if (rootNode.has("status") && rootNode.has("data")) {
					// BaseResponse 래핑된 구조
					int status = rootNode.get("status").asInt();
					log.info("FastAPI 응답 상태: {}", status);

					if (status != 200) {
						String message = rootNode.has("message") ? rootNode.get("message").asText() : "Unknown error";
						log.error("FastAPI에서 에러 상태 반환: {} - {}", status, message);
						throw new RuntimeException("FastAPI 에러: " + status + " - " + message);
					}

					// data 필드에서 실제 UBTI 결과 추출
					JsonNode dataNode = rootNode.get("data");
					if (dataNode == null || dataNode.isNull()) {
						log.error("data 필드가 null입니다");
						throw new RuntimeException("응답에 data 필드가 없습니다");
					}

					log.debug("추출된 데이터 노드: {}", dataNode);
					return parseUBTIData(dataNode);
				}
				// 3. 직접 UBTI 결과 구조인 경우 (Python에서 직접 JSON 반환)
				else if (rootNode.has("ubti_type")) {
					log.info("직접 UBTI 결과 구조 감지");
					return parseUBTIData(rootNode);
				}
				// 4. 에러 응답인 경우
				else if (rootNode.has("error")) {
					String errorType = rootNode.get("error").asText();
					String errorMessage = rootNode.has("message") ? rootNode.get("message").asText() : "Unknown error";
					log.error("Python에서 에러 응답: {} - {}", errorType, errorMessage);
					throw new RuntimeException("Python API 에러: " + errorType + " - " + errorMessage);
				}
				// 5. 알 수 없는 구조
				else {
					log.error("알 수 없는 응답 구조: {}", rootNode);
					throw new RuntimeException("알 수 없는 응답 구조입니다");
				}

			} catch (JsonProcessingException e) {
				log.error("JSON 파싱 실패: {}", e.getMessage());
				throw new RuntimeException("JSON 파싱 실패: " + e.getMessage(), e);
			} catch (Exception e) {
				log.error("UBTI 결과 파싱 중 오류: ", e);
				throw new RuntimeException("UBTI 결과 파싱 실패: " + e.getMessage(), e);
			}
		});
	}

	private UBTIResult parseUBTIData(JsonNode dataNode) throws JsonProcessingException {
		log.debug("UBTI 데이터 파싱 시작: {}", dataNode);

		// 1. 필수 필드 존재 확인
		String[] requiredFields = {"ubti_type", "summary", "recommendation", "matching_type"};
		for (String field : requiredFields) {
			if (!dataNode.has(field) || dataNode.get(field).isNull()) {
				log.error("필수 필드 누락 또는 null: {} in {}", field, dataNode);
				throw new RuntimeException("필수 필드 누락: " + field);
			}
		}

		// 2. recommendation.plans 배열 확인
		JsonNode recommendation = dataNode.get("recommendation");
		if (recommendation != null && recommendation.has("plans")) {
			JsonNode plans = recommendation.get("plans");
			if (!plans.isArray()) {
				log.error("plans가 배열이 아님: {}", plans);
				throw new RuntimeException("plans는 배열이어야 합니다");
			}
			log.info("plans 배열 크기: {}", plans.size());
			if (plans.size() != 2) {
				log.warn("plans 배열 크기가 2가 아님: {}", plans.size());
			}
		}

		// 3. UBTIResult 객체로 변환
		UBTIResult result = objectMapper.treeToValue(dataNode, UBTIResult.class);
		log.info("UBTI 데이터 파싱 성공: ubti_type={}, plans_count={}",
				result.getUbti_type() != null ? result.getUbti_type().getCode() : "null",
				result.getRecommendation() != null && result.getRecommendation().getPlans() != null ?
						result.getRecommendation().getPlans().size() : 0);

		return result;
	}
}