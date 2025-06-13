package com.team4ever.backend.domain.ubti.controller;

import com.team4ever.backend.domain.ubti.dto.UBTIRequest;
import com.team4ever.backend.domain.ubti.dto.UBTIResult;
import com.team4ever.backend.domain.ubti.service.UBTIService;
import com.team4ever.backend.global.response.BaseResponse;
import com.team4ever.backend.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/ubti")
@RequiredArgsConstructor
@Tag(name = "UBTI", description = "UBTI 타코시그널 테스트 API")
public class UBTIController {

	private final UBTIService ubtiService;

	@Operation(
			summary = "통신유형 질문 스트리밍",
			description = "UBTI 질문을 실시간으로 스트리밍하여 전송합니다."
	)
	@ApiResponses({
			@ApiResponse(
					responseCode = "200",
					description = "스트리밍 성공",
					content = @Content(mediaType = "text/event-stream")
			)
	})
	@PostMapping(
			value = "/question",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.TEXT_EVENT_STREAM_VALUE // 스트리밍 명시
	)
	public Flux<ServerSentEvent<String>> nextQuestion(@RequestBody UBTIRequest req) {
		return ubtiService.nextQuestionStream(req);
	}

	@Operation(
			summary = "UBTI 검사 최종 결과 조회",
			description = "모든 질문 완료 후 UBTI 타입과 요금제/구독 서비스 추천 결과를 반환합니다."
	)
	@ApiResponses({
			@ApiResponse(
					responseCode = "200",
					description = "검사 결과 조회 성공",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = BaseResponse.class),
							examples = @ExampleObject(
									name = "UBTI 결과 예시",
									description = "실제 UBTI 검사 결과 응답 예시",
									value = """
                    {
                      "status": 200,
                      "message": "요청 성공",
                      "data": {
                        "ubti_type": {
                          "code": "TK-Berry",
                          "name": "꾸안꾸 소셜타코",
                          "emoji": "🍓",
                          "description": "편안한 분위기에서 소통하며, 자연스럽게 사람들과 연결되는 걸 좋아하는 타입이에요.\\n\\n소소한 일상 속에서도 즐거움을 찾는 편이에요."
                        },
                        "summary": "🍽 오늘의 추천 타코야키 한 접시 나왔습니다!\\n\\n회원님은 자연스러운 소통을 즐기고, 일상 속 작은 행복을 소중히 여기는 분이에요.\\n\\n그래서 꾸안꾸 소셜타코인 TK-Berry 타입과 정말 잘 어울리시네요!",
                        "recommendation": {
                          "plans": [
                            {
                              "name": "너겟 30",
                              "description": "소셜미디어를 자주 사용하고, 데이터도 넉넉하게!\\n\\n일상 속에서 편리함을 챙기고 싶은 분께 잘 어울려요 👍"
                            },
                            {
                              "name": "너겟 31",
                              "description": "합리적인 가격에 기본 데이터량도 충분해요!\\n\\n가벼운 일상 소통을 즐기시는 분들께 추천드려요 💡"
                            }
                          ],
                          "subscription": {
                            "name": "지니뮤직 + 추가혜택(택1): 뮤직",
                            "description": "좋아하는 음악을 언제 어디서나!\\n\\n소소한 일상에 감성을 더해줄 찰떡 같은 서비스랍니다 🎶"
                          }
                        },
                        "matching_type": {
                          "code": "TK-Milky",
                          "name": "느긋한 라이트타코",
                          "emoji": "🥛",
                          "description": "편안하고 여유로운 시간을 소중히 여기는 타입이에요.\\n\\n혼자만의 여유를 즐기는 걸 좋아하죠."
                        }
                      }
                    }
                    """
							)
					)
			),
			@ApiResponse(
					responseCode = "400",
					description = "잘못된 요청"
			),
			@ApiResponse(
					responseCode = "500",
					description = "서버 내부 오류"
			)
	})
	@PostMapping(
			value = "/result",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE  // JSON 명시
	)
	public Mono<BaseResponse<UBTIResult>> finalResult(@RequestBody UBTIRequest req) {
		return ubtiService.finalResultWrapped(req);
	}
}