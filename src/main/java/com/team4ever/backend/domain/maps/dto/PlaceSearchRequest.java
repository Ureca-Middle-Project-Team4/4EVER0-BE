package com.team4ever.backend.domain.maps.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PlaceSearchRequest {
    private String textQuery;   // ex) "베스킨라빈스"
    private Boolean openNow;    // 영업 중만
    private Integer pageSize;   // ex) 10
    private Double latitude;    // 중심 좌표 (UI에서 받음)
    private Double longitude;   // 중심 좌표 (UI에서 받음)
    private Double radius;      // 반경 (ex: 500)
}
