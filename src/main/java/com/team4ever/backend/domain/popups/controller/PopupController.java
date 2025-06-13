package com.team4ever.backend.domain.popups.controller;

import com.team4ever.backend.domain.popups.dto.PopupResponse;
import com.team4ever.backend.domain.popups.service.PopupService;
import com.team4ever.backend.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/popups")
@RequiredArgsConstructor
public class PopupController {

    private final PopupService popupService;

    @Operation(summary = "팝업스토어 전체 조회")
    @GetMapping
    public ResponseEntity<BaseResponse<List<PopupResponse>>> getAllPopups() {
        List<PopupResponse> result = popupService.getAllPopups();
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @Operation(summary = "특정 팝업스토어 조회")
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<PopupResponse>> getPopupById(@PathVariable Integer id) {
        PopupResponse result = popupService.getPopupById(id);
        return ResponseEntity.ok(BaseResponse.success(result));
    }
}