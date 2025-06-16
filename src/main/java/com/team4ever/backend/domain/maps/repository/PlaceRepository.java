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
        String url = "https://places.googleapis.com/v1/places:searchText";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Goog-Api-Key", apiKey);
        headers.set("X-Goog-FieldMask", "places.displayName,places.location,places.formattedAddress");

        JSONObject circle = new JSONObject();
        circle.put("center", new JSONObject()
                .put("latitude", req.getLatitude())
                .put("longitude", req.getLongitude()));
        circle.put("radius", req.getRadius());

        JSONObject locationBias = new JSONObject();
        locationBias.put("circle", circle);

        JSONObject body = new JSONObject();
        body.put("textQuery", req.getTextQuery());
        if (req.getOpenNow() != null) body.put("openNow", req.getOpenNow());
        if (req.getPageSize() != null) body.put("pageSize", req.getPageSize());
        body.put("locationBias", locationBias);
        body.put("languageCode", "ko");

        HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        JSONObject responseJson = new JSONObject(response.getBody());
        JSONArray placesArray = responseJson.optJSONArray("places");

        List<PlaceSearchResponse.PlaceItem> items = new ArrayList<>();
        if (placesArray != null) {
            for (int i = 0; i < placesArray.length(); i++) {
                JSONObject obj = placesArray.getJSONObject(i);
                PlaceSearchResponse.PlaceItem item = new PlaceSearchResponse.PlaceItem();

                item.setName(obj.optJSONObject("displayName") != null ?
                        obj.getJSONObject("displayName").optString("text", "") : "");
                item.setLat(obj.optJSONObject("location") != null ?
                        obj.getJSONObject("location").optDouble("latitude", 0.0) : null);
                item.setLng(obj.optJSONObject("location") != null ?
                        obj.getJSONObject("location").optDouble("longitude", 0.0) : null);
                item.setAddress(obj.optString("formattedAddress", ""));
                item.setOpenNow(obj.optJSONObject("openingHours") != null ?
                        obj.getJSONObject("openingHours").optBoolean("openNow", false) : null);

                items.add(item);
            }
        }
        PlaceSearchResponse result = new PlaceSearchResponse();
        result.setPlaces(items);
        return result;
    }
}
