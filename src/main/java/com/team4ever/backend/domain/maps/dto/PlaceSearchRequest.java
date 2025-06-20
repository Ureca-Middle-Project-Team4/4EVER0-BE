package com.team4ever.backend.domain.maps.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter @Setter
public class PlaceSearchRequest {
    private List<String> textQueryList;  // ex) ["베스킨라빈스", "올리브영", "스타벅스"]
    private Integer pageSize;   // ex) 10
    private Double latitude;    // 중심 좌표 (UI에서 받음)
    private Double longitude;   // 중심 좌표 (UI에서 받음)
    private Double radius;      // 반경 (ex: 500)
}
