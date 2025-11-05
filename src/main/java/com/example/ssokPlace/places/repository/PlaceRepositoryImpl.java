package com.example.ssokPlace.places.repository;

import com.example.ssokPlace.places.entity.Place;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
class PlaceRepositoryImpl implements PlaceRepositoryCustom {

    private final EntityManager em;

    @Override
    public Page<Place> searchNearby(double centerLat, double centerLng, int radiusM,
                                    boolean includeClosed, Pageable pageable) {

        // includeClosed 필터가 필요하면 여기에 조건만 추가
        String baseWhere = """
            ST_Distance_Sphere(p.geo, ST_SRID(POINT(?1, ?2), 4326)) <= ?3
        """;

        String sql = """
            SELECT p.*, ST_Distance_Sphere(p.geo, ST_SRID(POINT(?1, ?2), 4326)) AS dist
            FROM place p
            WHERE """ + baseWhere + """
            ORDER BY dist ASC
            LIMIT ?4 OFFSET ?5
        """;

        var q = em.createNativeQuery(sql, Place.class)
                .setParameter(1, centerLng)          // ?1 = lng
                .setParameter(2, centerLat)          // ?2 = lat
                .setParameter(3, radiusM)            // ?3 = radius
                .setParameter(4, pageable.getPageSize())
                .setParameter(5, (int) pageable.getOffset());

        @SuppressWarnings("unchecked")
        List<Place> content = (List<Place>) q.getResultList();

        String countSql = """
            SELECT COUNT(*)
            FROM place p
            WHERE """ + baseWhere;

        long total = ((Number) em.createNativeQuery(countSql)
                .setParameter(1, centerLng)          // ?1 = lng
                .setParameter(2, centerLat)          // ?2 = lat
                .setParameter(3, radiusM)            // ?3 = radius
                .getSingleResult()).longValue();

        return new PageImpl<>(content, pageable, total);
    }
}
