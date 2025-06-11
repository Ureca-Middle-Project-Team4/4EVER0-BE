package com.team4ever.backend.domain.chat.service;

import com.team4ever.backend.domain.chat.dto.ChatRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import org.springframework.http.codec.ServerSentEvent;

@Service
public class ChatService {

	private final WebClient webClient;
	private final String chatPath;
	private final String likesPath;

	public ChatService(
			@Value("${fastapi.chat.host}") String host,
			@Value("${fastapi.chat.port}") int port,
			@Value("${fastapi.chat.path}") String chatPath,
			@Value("${fastapi.chat.likes}") String likesPath
	) {
		this.webClient = WebClient.builder()
				.baseUrl(String.format("http://%s:%d", host, port))
				.build();
		this.chatPath = chatPath;
		this.likesPath = likesPath;
	}

	// 일반 챗 스트리밍 - text/event-stream 으로 스트리밍 명시
	public Flux<ServerSentEvent<String>> getChatResponse(ChatRequest request) {
		return webClient.post()
				.uri(chatPath)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.TEXT_EVENT_STREAM) // 반드시 스트리밍 명세
				.bodyValue(request)
				.retrieve()
				.bodyToFlux(String.class)
				.map(data -> ServerSentEvent.builder(data).build());
	}

	// 좋아요 기반 추천 - 마찬가지로 스트리밍 타입 지정
	public Flux<ServerSentEvent<String>> getChatLikesResponse(ChatRequest request) {
		return webClient.post()
				.uri(likesPath)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.TEXT_EVENT_STREAM) // 같은 스트리밍 타입으로 통일 권장
				.bodyValue(request)
				.retrieve()
				.bodyToFlux(String.class)
				.map(data -> ServerSentEvent.builder(data).build());
	}
}