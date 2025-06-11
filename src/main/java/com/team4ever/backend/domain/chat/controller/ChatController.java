package com.team4ever.backend.domain.chat.controller;

import com.team4ever.backend.domain.chat.dto.ChatRequest;
import com.team4ever.backend.domain.chat.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import com.team4ever.backend.domain.chat.dto.ChatLikesRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import org.springframework.http.codec.ServerSentEvent;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

	private final ChatService chatService;

	@PostMapping(
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.TEXT_EVENT_STREAM_VALUE // 스트리밍용 Content-Type
	)
	public Flux<ServerSentEvent<String>> chat(@RequestBody ChatRequest req) {
		return chatService.getChatResponse(req);
	}

	@PostMapping(
			value = "/likes",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.TEXT_EVENT_STREAM_VALUE // 마찬가지로
	)
	public Flux<ServerSentEvent<String>> chatLikes(@RequestBody ChatLikesRequest req) {
		ChatRequest request = new ChatRequest();
		request.setSessionId(req.getSessionId());
		request.setMessage("likes");
		return chatService.getChatLikesResponse(request);
	}
}