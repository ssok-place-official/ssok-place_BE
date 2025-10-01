package com.example.ssokPlace.places.repository;

import com.example.ssokPlace.places.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long>, PlaceRepositoryCustom {

    @Query(value = """
        SELECT * FROM place
        WHERE JSON_UNQUOTE(JSON_EXTRACT(external_refs, '$.naver_place_id')) = :naverPlaceId
        LIMIT 1
    """, nativeQuery = true)
    Optional<Place> findByNaverPlaceId(String naverPlaceId);

    // 좌표 반경 내 존재 여부 (ego: POINT SRID 4326)
    @Query(value = """
        SELECT (COUNT(*) > 0)
        FROM place p
        WHERE ST_Distance_Sphere(p.ego, ST_SRID(POINT(:lng,:lat),4326)) <= :radiusM
    """, nativeQuery = true)
    boolean existsNearby(double lat, double lng, int radiusM);
}
