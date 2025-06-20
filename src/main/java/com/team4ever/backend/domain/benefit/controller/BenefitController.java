package com.team4ever.backend.domain.benefit.controller;

import com.team4ever.backend.domain.benefit.dto.BenefitPreviewResponse;
import com.team4ever.backend.domain.benefit.dto.BenefitDetailResponse;
import com.team4ever.backend.domain.benefit.service.BenefitService;
import com.team4ever.backend.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/uplus-benefits")
@Tag(name = "유플투쁠 혜택 API", description = "유플투쁠 월별, 특정날짜 혜택 조회 API")
public class BenefitController {

    private final BenefitService benefitService;

    @Operation(
            summary = "이번 달 혜택 전체 조회",
            description = """
            이번 달에 제공되는 모든 유플투쁠 혜택을 조회합니다. (미리보기용)

            - 혜택은 날짜 기준으로 정렬되어 반환됩니다.
            - 브랜드명, 날짜, 이미지 URL만 포함됩니다.
            - 로그인 없이도 조회 가능합니다.
            """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "요청 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": true,
                      "message": "요청 성공",
                      "data": [
                        {
                          "brand": "리디셀렉트",
                          "date": "2025-06-01",
                          "imageUrl": "https://example.com/ridi.png"
                        },
                        {
                          "brand": "배스킨라빈스",
                          "date": "2025-06-01",
                          "imageUrl": "https://example.com/baskin.png"
                        }
                      ]
                    }
                    """
                            )
                    )
            )
    })
    @GetMapping()
    public BaseResponse<List<BenefitPreviewResponse>> getMonthlyBenefits() {
        return BaseResponse.success(benefitService.getMonthlyBenefits());
    }

    @Operation(
            summary = "특정 날짜 혜택 조회",
            description = """
            입력한 날짜(YYYY-MM-DD)에 해당하는 유플투쁠 혜택을 조회합니다. (상세정보 포함)

            - 날짜별 혜택을 캘린더 형태로 연동할 수 있습니다.
            - 브랜드명, 날짜, 이미지 URL, 설명, 카테고리가 모두 포함됩니다.
            - 형식이 올바르지 않거나 해당 날짜에 혜택이 없으면 빈 배열이 반환됩니다.
            """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "요청 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": true,
                      "message": "요청 성공",
                      "data": [
                        {
                          "brand": "리디셀렉트",
                          "date": "2025-06-01",
                          "imageUrl": "https://example.com/ridi.png",
                          "description": "전자책 및 웹툰 구독 서비스",
                          "category": "도서/콘텐츠"
                        }
                      ]
                    }
                    """
                            )
                    )
            )
    })
    @GetMapping("/{date}")
    public BaseResponse<List<BenefitDetailResponse>> getBenefitsByDate(@PathVariable String date) {
        return BaseResponse.success(benefitService.getBenefitsByDate(date));
    }
}