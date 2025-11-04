package com.example.ssokPlace.places.repository;

import com.example.ssokPlace.places.entity.PlaceVisit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface PlaceVisitRepository extends JpaRepository<PlaceVisit, Long> {
    /**
     * 최근 lookbackDays 동안 가장 자주 방문한 장소 Top-N
     */
    @Query("""
        select v.placeId, count(v) as visitCount, max(v.visitedAt) as lastVisit
        from PlaceVisit v
        where v.user.id = :userId and v.visitedAt >= :since
        group by v.placeId
        order by visitCount desc, lastVisit desc
    """)
    List<Object[]> findFrequentPlaces(
            @Param("userId") Long userId,
            @Param("since") OffsetDateTime since,
            Pageable pageable
    );

    /**
     * 최근 lookbackDays 동안 방문하지 않은 장소 (UserPlace 기준)
     */
    @Query("""
        select up.placeId
        from UserPlace up
        where up.userId = :userId
          and not exists (
            select 1 from PlaceVisit v
            where v.user.id = up.userId
              and v.placeId = up.placeId
              and v.visitedAt >= :since
          )
    """)
    List<Long> findDormantPlaces(
            @Param("userId") Long userId,
            @Param("since") OffsetDateTime since,
            Pageable pageable
    );
}
