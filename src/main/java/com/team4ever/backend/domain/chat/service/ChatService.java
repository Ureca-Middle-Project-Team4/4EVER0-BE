package com.team4ever.backend.domain.chat.service;

import com.team4ever.backend.domain.chat.dto.ChatRequest;
import com.team4ever.backend.domain.chat.dto.UsageRecommendRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Slf4j
@Service
public class ChatService {

	private final WebClient webClient;
	private final String chatPath;
	private final String chatLikesPath;
	private final String usagePath;

	public ChatService(
			WebClient.Builder webClientBuilder,
			@Value("${fastapi.chat.host}") String host,
			@Value("${fastapi.chat.port}") int port,
			@Value("${fastapi.chat.path.chat}") String chatPath,
			@Value("${fastapi.chat.path.likes}") String chatLikesPath,
			@Value("${fastapi.chat.path.usage}") String usagePath
	) {
		this.webClient = webClientBuilder
				.baseUrl(String.format("http://%s:%d", host, port))
				.build();
		this.chatPath = chatPath;
		this.chatLikesPath = chatLikesPath;
		this.usagePath = usagePath;
	}

	/**
	 * 일반 채팅 스트리밍
	 */
	public Flux<ServerSentEvent<String>> streamChat(ChatRequest request) {
		log.info("FastAPI 채팅 호출 - session_id: {}, tone: {}", request.getSession_id(), request.getTone());

		return webClient.post()
				.uri(chatPath)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.TEXT_EVENT_STREAM)
				.bodyValue(request)  // tone 포함하여 전달
				.retrieve()
				.bodyToFlux(String.class)
				.map(data -> ServerSentEvent.builder(data).build())
				.doOnError(error -> log.error("채팅 스트리밍 오류: ", error));
	}

	/**
	 * 좋아요 기반 추천 스트리밍
	 */
	public Flux<ServerSentEvent<String>> streamChatLikes(ChatRequest request) {
		log.info("FastAPI 좋아요 추천 호출 - session_id: {}, tone: {}", request.getSession_id(), request.getTone());

		return webClient.post()
				.uri(chatLikesPath)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.TEXT_EVENT_STREAM)
				.bodyValue(request)  // tone 포함하여 전달
				.retrieve()
				.bodyToFlux(String.class)
				.map(data -> ServerSentEvent.builder(data).build())
				.doOnError(error -> log.error("좋아요 추천 스트리밍 오류: ", error));
	}

	/**
	 * 사용량 기반 추천 스트리밍
	 */
	public Flux<ServerSentEvent<String>> streamUsageRecommend(int userId, UsageRecommendRequest request) {
		log.info("FastAPI 사용량 추천 호출 - user_id: {}, tone: {}", userId, request.getTone());

		return webClient.post()
				.uri(uriBuilder -> uriBuilder
						.path(usagePath + "/recommend")
						.queryParam("user_id", userId)
						.queryParam("tone", request.getTone())
						.build())
				.accept(MediaType.TEXT_EVENT_STREAM)
				.retrieve()
				.bodyToFlux(String.class)
				.map(data -> ServerSentEvent.builder(data).build())
				.doOnError(error -> log.error("사용량 추천 스트리밍 오류: ", error))
				.doOnComplete(() -> log.info("사용량 추천 스트리밍 완료 - user_id: {}", userId));
	}
}