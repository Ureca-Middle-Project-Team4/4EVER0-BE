package com.team4ever.backend.domain.popups.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PopupResponse {
    private Long id;
    private String name;
    private String description;
    private String address;
    private Double latitude;
    private Double longitude;
    private String imageUrl;
}

