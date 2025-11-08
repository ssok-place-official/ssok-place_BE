package com.example.ssokPlace.places.repository;

import com.example.ssokPlace.places.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long>, PlaceRepositoryCustom {

    @Query(value = """
        SELECT * FROM places
        WHERE JSON_UNQUOTE(JSON_EXTRACT(external_refs, '$.naver_place_id')) = :naverPlaceId
        LIMIT 1x
    """, nativeQuery = true)
    Optional<Place> findByNaverPlaceId(@Param("naverPlaceId") String naverPlaceId);

    // 반경 내 장소 개수
    @Query(value = """
        SELECT COUNT(*)
        FROM place p
        WHERE ST_Distance_Sphere(p.geo, ST_SRID(POINT(:lng,:lat),4326)) <= :radiusM
    """, nativeQuery = true)
    long countNearby(@Param("lat") double lat,
                     @Param("lng") double lng,
                     @Param("radiusM") int radiusM);
}