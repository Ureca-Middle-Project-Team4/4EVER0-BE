package com.team4ever.backend.domain.maps.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter @Setter
public class PlaceSearchResponse {
    private List<PlaceItem> places;

    @Getter @Setter
    public static class PlaceItem {
        private String name;
        private Double lat;
        private Double lng;
        private String address;
    }
}
