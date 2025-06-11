package com.team4ever.backend.domain.ubti.service;

import com.team4ever.backend.domain.ubti.dto.UBTIRequest;
import com.team4ever.backend.domain.ubti.dto.UBTIResult;
import com.team4ever.backend.global.exception.ErrorCode;
import com.team4ever.backend.global.response.BaseResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UBTIService {

	private final WebClient webClient;
	private final String questionPath;
	private final String resultPath;

	public UBTIService(
			WebClient.Builder webClientBuilder,
			@Value("${fastapi.ubti.host}") String host,
			@Value("${fastapi.ubti.port}") int port,
			@Value("${fastapi.ubti.path.question}") String questionPath,
			@Value("${fastapi.ubti.path.result}") String resultPath
	) {
		this.webClient = webClientBuilder
				.baseUrl(String.format("http://%s:%d", host, port))
				.build();
		this.questionPath = questionPath;
		this.resultPath = resultPath;
	}

	public Flux<ServerSentEvent<String>> nextQuestionStream(UBTIRequest req) {
		return webClient.post()
				.uri(questionPath)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.TEXT_EVENT_STREAM)  // 스트리밍 명시
				.bodyValue(req)
				.retrieve()
				.bodyToFlux(String.class)
				.map(data -> ServerSentEvent.builder(data).build());
	}

	// 최종 결과 (단건, 스트리밍 아님)
	public Mono<BaseResponse<UBTIResult>> finalResultWrapped(UBTIRequest req) {
		return webClient.post()
				.uri(resultPath)
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(req)
				.retrieve()
				.bodyToMono(UBTIResult.class)
				.map(data -> BaseResponse.success(data))
				.onErrorResume(e -> Mono.just(BaseResponse.error(ErrorCode.INTERNAL_SERVER_ERROR)));
	}
}