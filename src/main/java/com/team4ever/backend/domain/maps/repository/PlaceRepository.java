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

        for (String brand : req.getTextQueryList()){
            String url = "https://places.googleapis.com/v1/places:searchText";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Goog-Api-Key", apiKey);
            headers.set("X-Goog-FieldMask", "places.displayName,places.location,places.formattedAddress");

            double lat = req.getLatitude();
            double lng = req.getLongitude();
            double radiusMeters = req.getRadius();

            double latDegreeDistance = 111000.0;
            double lngDegreeDistance = 111000.0 * Math.cos(Math.toRadians(lat));

            // 반경(m)를 위도, 경도 단위로 변환
            double latOffset = radiusMeters / latDegreeDistance;
            double lngOffset = radiusMeters / lngDegreeDistance;

            // 직사각형 경계 좌표
            JSONObject low = new JSONObject()
                    .put("latitude", lat - latOffset)
                    .put("longitude", lng - lngOffset);

            JSONObject high = new JSONObject()
                    .put("latitude", lat + latOffset)
                    .put("longitude", lng + lngOffset);

            // rectangle 객체 생성
            JSONObject rectangle = new JSONObject()
                    .put("low", low)
                    .put("high", high);

            // locationBias에 rectangle 넣기
            JSONObject locationRestriction = new JSONObject();
            locationRestriction.put("rectangle", rectangle);

            JSONObject body = new JSONObject();
            body.put("textQuery", brand);
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

                    // languageCode가 "ko"인지 확인
                    String langCode = displayNameObj.optString("languageCode", "");
                    if (!"ko".equals(langCode)) {
                        // 한글 응답이 아니면 건너뜀
                        continue;
                    }

                    String placeName = displayNameObj.optString("text", "");

                    // 브랜드명 포함 여부 체크 (예: brand 변수에 브랜드명 저장되어 있다고 가정)
                    // brand 변수는 해당 API 호출 시 사용한 브랜드명이어야 합니다.
                    if (!placeName.toLowerCase().contains(brand.toLowerCase())) {
                        continue;  // 브랜드명이 포함되지 않으면 건너뜀
                    }

                    PlaceSearchResponse.PlaceItem item = new PlaceSearchResponse.PlaceItem();
                    item.setId(idCounter);
                    idCounter++;
                    item.setName(placeName);

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

        PlaceSearchResponse result = new PlaceSearchResponse();
        result.setPlaces(allItems);
        return result;
    }
}
