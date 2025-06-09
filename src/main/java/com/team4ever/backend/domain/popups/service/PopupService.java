package com.team4ever.backend.domain.popups.service;

import com.team4ever.backend.domain.popups.dto.PopupResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PopupService {

    public List<PopupResponse> getAllPopups() {
        return List.of(
                PopupResponse.builder()
                        .id(1L)
                        .name("스타벅스 팝업스토어")
                        .description("신제품 음료 체험존")
                        .address("서울 강남구 테헤란로 123")
                        .latitude(37.5012345)
                        .longitude(127.0398741)
                        .imageUrl("https://example.com/store1.jpg")
                        .build(),

                PopupResponse.builder()
                        .id(2L)
                        .name("무신사 팝업스토어")
                        .description("신상품 런칭 이벤트")
                        .address("서울 마포구 어딘가로 456")
                        .latitude(37.5512345)
                        .longitude(126.9248741)
                        .imageUrl("https://example.com/store2.jpg")
                        .build()
        );
    }

    public PopupResponse getPopupById(Long id) {
        if (id == 1L) {
            return PopupResponse.builder()
                    .id(1L)
                    .name("스타벅스 팝업스토어")
                    .description("신제품 음료 체험존")
                    .address("서울 강남구 테헤란로 123")
                    .latitude(37.5012345)
                    .longitude(127.0398741)
                    .imageUrl("https://example.com/store1.jpg")
                    .build();
        } else if (id == 2L) {
            return PopupResponse.builder()
                    .id(2L)
                    .name("무신사 팝업스토어")
                    .description("신상품 런칭 이벤트")
                    .address("서울 마포구 어딘가로 456")
                    .latitude(37.5512345)
                    .longitude(126.9248741)
                    .imageUrl("https://example.com/store2.jpg")
                    .build();
        } else {
            throw new IllegalArgumentException("해당 ID의 팝업스토어가 없습니다.");
        }
    }
}
