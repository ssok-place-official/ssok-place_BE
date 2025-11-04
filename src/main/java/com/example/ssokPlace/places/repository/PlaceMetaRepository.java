package com.example.ssokPlace.places.repository;

import com.example.ssokPlace.places.entity.PlaceMeta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public interface PlaceMetaRepository extends JpaRepository<PlaceMeta, Long> {
    List<PlaceMeta> findByIdIn(Collection<Long> ids);
}
