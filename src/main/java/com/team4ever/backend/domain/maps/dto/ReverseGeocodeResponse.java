package com.team4ever.backend.domain.maps.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class ReverseGeocodeResponse {
    private Status status;
    private List<Result> results;

    @Getter @Setter
    public static class Status {
        private Integer code;
        private String name;
        private String message;
    }

    @Getter @Setter
    public static class Result {
        private String name;
        private Region region;
        // code, land 등 필요시 추가

        @Getter @Setter
        public static class Region {
            private Area area1;
            private Area area2;
            private Area area3;
            private Area area4;

            @Getter @Setter
            public static class Area {
                private String name;
                // 기타 필드 생략
            }
        }
    }
}
