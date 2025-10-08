package com.example.ssokPlace.places.repository;

import com.example.ssokPlace.places.entity.PlaceInsight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceInsightRepository extends JpaRepository<PlaceInsight, Long> {
}
