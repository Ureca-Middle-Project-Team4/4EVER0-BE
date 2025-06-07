package com.team4ever.backend.domain.chat.service;

import com.team4ever.backend.domain.chat.dto.ChatRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
public class ChatService {

	private final WebClient webClient;
	private final String path;

	public ChatService(
			@Value("${fastapi.chat.host}") String host,
			@Value("${fastapi.chat.port}") int port,
			@Value("${fastapi.chat.path}") String path
	) {
		this.webClient = WebClient.builder()
				.baseUrl(String.format("http://%s:%d", host, port))
				.build();
		this.path = path;
	}

	/**
	 * FastAPI의 /api/chat 에서 NDJSON 스트림으로 내려오는
	 * JSON 라인(문자열)을 그대로 Flux<String>으로 받아옵니다.
	 */
	public Flux<String> getChatResponse(ChatRequest request) {
		return webClient.post()
				.uri(path)
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(request)
				.retrieve()
				.bodyToFlux(String.class);
	}
}
