package com.example.ssokPlace.list.repository;

import com.example.ssokPlace.list.dto.ListSummaryDTO;
import com.example.ssokPlace.list.entity.PlaceList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceListRepository extends JpaRepository<PlaceList, Long> {
    boolean existsByName(String name);

    @Query(
            value = """
            select new com.example.ssokPlace.list.dto.ListSummaryDTO(
                l.id, l.name, l.emoji, count(plp.id), l.updatedAt
            )
            from PlaceList l
            left join l.places plp   -- place_list_place 링크 기준으로 카운트
            group by l.id, l.name, l.emoji, l.updatedAt
            order by l.updatedAt desc
        """,
            countQuery = """
            select count(l.id)
            from PlaceList l
        """
    )
    Page<ListSummaryDTO> findAllSummaries(Pageable pageable);
}