package com.team4ever.backend.domain.maps.service;

import com.team4ever.backend.domain.maps.dto.PlaceSearchRequest;
import com.team4ever.backend.domain.maps.dto.PlaceSearchResponse;
import com.team4ever.backend.domain.maps.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaceService {
    private final PlaceRepository googlePlaceRepository;

    public PlaceSearchResponse search(PlaceSearchRequest req) throws JSONException {
        return googlePlaceRepository.searchPlaces(req);
    }
}
