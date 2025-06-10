package com.team4ever.backend.domain.popups.controller;

import com.team4ever.backend.domain.popups.dto.PopupResponse;
import com.team4ever.backend.domain.popups.service.PopupService;
import com.team4ever.backend.global.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/popups")
@RequiredArgsConstructor
public class PopupController {

    private final PopupService popupService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<PopupResponse>>> getAll() {
        return ResponseEntity.ok(BaseResponse.success(popupService.getAllPopups(), "메인 구독 상품 조회 성공"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<PopupResponse>> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(BaseResponse.success(popupService.getPopupById(id), "메인 구독 상품 조회 성공"));
    }
}
