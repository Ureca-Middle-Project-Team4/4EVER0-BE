package com.team4ever.backend.domain.recommend.service;

import com.team4ever.backend.domain.recommend.dto.MultiTurnRequest;
import com.team4ever.backend.domain.recommend.dto.RecommendedItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class RecommendService {

	private final RestTemplate restTemplate = new RestTemplate();

	@Value("${fastapi.recommend.host}")
	private String host;

	@Value("${fastapi.recommend.port}")
	private int port;

	@Value("${fastapi.recommend.path}")
	private String path;

	private String buildUrl() {
		return String.format("http://%s:%d%s", host, port, path);
	}

	public List<RecommendedItem> getRecommendation(MultiTurnRequest request) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<MultiTurnRequest> entity = new HttpEntity<>(request, headers);

		ResponseEntity<RecommendedItem[]> response = restTemplate.exchange(
				buildUrl(),
				HttpMethod.POST,
				entity,
				RecommendedItem[].class
		);

		return Arrays.asList(response.getBody());
	}
}
