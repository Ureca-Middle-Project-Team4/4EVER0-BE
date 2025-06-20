package com.team4ever.backend.domain.popups.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PopupResponse {
    private Integer id;
    private String name;
    private String description;
    private String address;
    private Double latitude;
    private Double longitude;

    @JsonProperty("image_url")
    private String imageUrl;
}