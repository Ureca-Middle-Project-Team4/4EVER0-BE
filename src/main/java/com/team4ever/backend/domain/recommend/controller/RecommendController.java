package com.team4ever.backend.domain.recommend.controller;

import com.team4ever.backend.domain.recommend.dto.MultiTurnRequest;
import com.team4ever.backend.domain.recommend.dto.RecommendedItem;
import com.team4ever.backend.domain.recommend.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recommend")
@RequiredArgsConstructor
public class RecommendController {

	private final RecommendService recommendService;

	@PostMapping
	public List<RecommendedItem> recommend(@RequestBody MultiTurnRequest request) {
		return recommendService.getRecommendation(request);
	}
}
