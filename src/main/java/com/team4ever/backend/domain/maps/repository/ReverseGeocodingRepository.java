package com.team4ever.backend.domain.maps.repository;

import com.team4ever.backend.domain.maps.dto.ReverseGeocodeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Repository
@RequiredArgsConstructor
public class ReverseGeocodingRepository {

    private final RestTemplate restTemplate;
    @Value("${naver.cloud.access-key}")
    private String clientId;
    @Value("${naver.cloud.secret-key}")
    private String clientSecret;

    private static final String REVERSE_GEOCODE_URL =
            "https://maps.apigw.ntruss.com/map-reversegeocode/v2/gc";

    public ReverseGeocodeResponse reverseGeocode(double lat, double lng) {
        String coords = lng + "," + lat; // 경도,위도 순서

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(REVERSE_GEOCODE_URL)
                .queryParam("coords", coords)
                .queryParam("orders", "admcode")
                .queryParam("output", "json");

        String uriString = builder.toUriString();
        System.out.println("네이버 Reverse Geocoding 호출 URI: " + uriString);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-NCP-APIGW-API-KEY-ID", clientId);
        headers.set("X-NCP-APIGW-API-KEY", clientSecret);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<ReverseGeocodeResponse> response = restTemplate.exchange(
                uriString, HttpMethod.GET, entity, ReverseGeocodeResponse.class
        );
        return response.getBody();
    }
}
