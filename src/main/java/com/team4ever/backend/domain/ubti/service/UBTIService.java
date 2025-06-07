package com.team4ever.backend.domain.ubti.service;

import com.team4ever.backend.domain.ubti.dto.UBTIRequest;
import com.team4ever.backend.domain.ubti.dto.UBTIResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
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
			@Value("${fastapi.ubti.path.result}")   String resultPath
	) {
		this.webClient = webClientBuilder
				.baseUrl(String.format("http://%s:%d", host, port))
				.build();
		this.questionPath = questionPath;
		this.resultPath   = resultPath;
	}

	/**
	 * 질문 단계: UBTIQuestion 또는 UBTIComplete 리턴
	 */
	public Mono<Object> nextQuestion(UBTIRequest req) {
		return webClient.post()
				.uri(questionPath)
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(req)
				.retrieve()
				.bodyToMono(Object.class);    // UBTIQuestion.class, UBTIComplete.class 를 섞어 받으려면 Object
	}

	/**
	 * 최종 결과 단계: UBTIResult 리턴
	 */
	public Mono<UBTIResult> finalResult(UBTIRequest req) {
		return webClient.post()
				.uri(resultPath)
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(req)
				.retrieve()
				.bodyToMono(UBTIResult.class);
	}
}
