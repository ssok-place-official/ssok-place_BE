package com.example.ssokPlace.list.repository;

import com.example.ssokPlace.list.entity.PlaceList;
import com.example.ssokPlace.list.entity.PlaceListPlace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceListPlaceRepository extends JpaRepository<PlaceListPlace, Long> {
    boolean existsByList_IdAndPlace_Id(Long listId, Long placeId);
    long deleteByList_IdAndPlace_Id(Long listId, Long placeId);
}