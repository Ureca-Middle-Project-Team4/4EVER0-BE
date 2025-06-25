package com.team4ever.backend.domain.maps.repository;

import com.team4ever.backend.domain.maps.dto.PlaceSearchRequest;
import com.team4ever.backend.domain.maps.dto.PlaceSearchResponse;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PlaceRepository {
    private final RestTemplate restTemplate;

    @Value("${google.places.api.key}")
    private String apiKey;

    public PlaceSearchResponse searchPlaces(PlaceSearchRequest req) throws JSONException {
        List<PlaceSearchResponse.PlaceItem> allItems = new ArrayList<>();
        int idCounter = 1;

        // 브랜드별로 처리
        for (String brand : req.getTextQueryList()) {
            String url = "https://places.googleapis.com/v1/places:searchText";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Goog-Api-Key", apiKey);
            headers.set("X-Goog-FieldMask", "places.displayName,places.location,places.formattedAddress");

            double lat = req.getLatitude();
            double lng = req.getLongitude();
            double radiusMeters = req.getRadius();

            // 직사각형 경계 좌표 계산
            double latDegreeDistance = 111000.0;
            double lngDegreeDistance = 111000.0 * Math.cos(Math.toRadians(lat));

            double latOffset = radiusMeters / latDegreeDistance;
            double lngOffset = radiusMeters / lngDegreeDistance;

            JSONObject low = new JSONObject()
                    .put("latitude", lat - latOffset)
                    .put("longitude", lng - lngOffset);

            JSONObject high = new JSONObject()
                    .put("latitude", lat + latOffset)
                    .put("longitude", lng + lngOffset);

            JSONObject rectangle = new JSONObject()
                    .put("low", low)
                    .put("high", high);

            JSONObject locationRestriction = new JSONObject();
            locationRestriction.put("rectangle", rectangle);

            JSONObject body = new JSONObject();
            body.put("textQuery", brand);  // 브랜드 이름을 쿼리로 사용
            body.put("openNow", true);
            if (req.getPageSize() != null) body.put("pageSize", req.getPageSize());
            body.put("locationRestriction", locationRestriction);
            body.put("languageCode", "ko");

            HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            JSONObject responseJson = new JSONObject(response.getBody());
            JSONArray placesArray = responseJson.optJSONArray("places");

            if (placesArray != null) {
                for (int i = 0; i < placesArray.length(); i++) {
                    JSONObject obj = placesArray.getJSONObject(i);

                    JSONObject displayNameObj = obj.optJSONObject("displayName");
                    if (displayNameObj == null) continue;

                    // "ko" 언어 코드만 처리
                    String langCode = displayNameObj.optString("languageCode", "");
                    if (!"ko".equals(langCode)) {
                        continue;
                    }

                    String placeName = displayNameObj.optString("text", "");

                    // 브랜드명이 포함되어 있는지 확인
                    if (!placeName.toLowerCase().contains(brand.toLowerCase())) {
                        continue;
                    }

                    PlaceSearchResponse.PlaceItem item = new PlaceSearchResponse.PlaceItem();
                    item.setId(idCounter);
                    idCounter++;
                    item.setName(placeName);

                    // 브랜드 이름을 PlaceItem에 추가
                    item.setBrandName(brand);  // brand_name을 PlaceItem에 설정

                    // 위치 및 주소 정보 설정
                    JSONObject locationObj = obj.optJSONObject("location");
                    if (locationObj != null) {
                        item.setLat(locationObj.optDouble("latitude", 0.0));
                        item.setLng(locationObj.optDouble("longitude", 0.0));
                    }

                    item.setAddress(obj.optString("formattedAddress", ""));
                    allItems.add(item);
                }
            }
        }

        // 최종 결과 반환
        PlaceSearchResponse result = new PlaceSearchResponse();
        result.setPlaces(allItems);
        return result;
    }
}
