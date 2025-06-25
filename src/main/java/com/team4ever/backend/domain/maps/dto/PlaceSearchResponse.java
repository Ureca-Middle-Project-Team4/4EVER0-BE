package com.team4ever.backend.domain.maps.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class PlaceSearchResponse {
    private List<PlaceItem> places;

    @Getter
    @Setter
    public static class PlaceItem {
        private int id;          // 기존 id
        private String name;     // 기존 name
        private Double lat;      // 기존 lat
        private Double lng;      // 기존 lng
        private String address;  // 기존 address

        // 추가된 필드
        private String brandName;   // 추가된 brandId
    }
}
