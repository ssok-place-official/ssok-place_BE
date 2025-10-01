package com.example.ssokPlace.places.repository;

import com.example.ssokPlace.places.entity.Place;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PlaceRepositoryCustom {
    Page<Place> searchNearby(double centerLat, double centerLng, int radiusM, boolean includeClosed, Pageable pageable);
}
