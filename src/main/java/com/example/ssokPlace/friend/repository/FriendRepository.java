package com.example.ssokPlace.friend.repository;

import com.example.ssokPlace.friend.dto.FriendDTO;
import com.example.ssokPlace.friend.entity.Friendship;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friendship, Long> {
    @Query(
            value = """
        select new com.example.ssokPlace.friend.dto.FriendDTO(
          case when f.userA.id = :meId then f.userB.id else f.userA.id end,
          case when f.userA.id = :meId then f.userB.nickname else f.userA.nickname end,
          f.status
        )
        from Friendship f
        where (:meId = f.userA.id or :meId = f.userB.id)
          and (
            :search is null
            or :search = ''
            or lower(
              case when f.userA.id = :meId then f.userB.nickname else f.userA.nickname end
            ) like concat('%', lower(:search), '%')
          )
        order by f.createdAt desc
      """,
            countQuery = """
        select count(f)
        from Friendship f
        where (:meId = f.userA.id or :meId = f.userB.id)
          and (
            :search is null
            or :search = ''
            or lower(
              case when f.userA.id = :meId then f.userB.nickname else f.userA.nickname end
            ) like concat('%', lower(:search), '%')
          )
      """
    )
    Page<FriendDTO> findFriendsPaged(@Param("meId") Long meId,
                                     @Param("search") String search,
                                     Pageable pageable);

    // (id1, id2) 어느 쪽이 A/B여도 존재 여부 확인
    @Query("""
      select (count(f) > 0)
      from Friendship f
      where (f.userA.id = :id1 and f.userB.id = :id2)
         or (f.userA.id = :id2 and f.userB.id = :id1)
    """)
    boolean existsFriendship(@Param("id1") Long id1, @Param("id2") Long id2);

    // 진행 중(PENDING) 요청이 양방향 중 하나라도 있는지
    @Query("""
      select (count(f) > 0)
      from Friendship f
      where (
          (f.userA.id = :id1 and f.userB.id = :id2)
       or (f.userA.id = :id2 and f.userB.id = :id1)
      )
      and f.status = 'PENDING'
    """)
    boolean existsPendingBetween(@Param("id1") Long id1, @Param("id2") Long id2);

    @Query("""
    select f from Friendship f
    where (f.userA.id = :userId or f.userB.id = :userId)
      and f.status = 'PENDING'
""")
    List<Friendship> findPendingRequests(@Param("userId") Long userId);

    @Query("""
    select f from Friendship f
    where (f.userA.id = :id1 and f.userB.id = :id2)
       or (f.userA.id = :id2 and f.userB.id = :id1)
""")
    Optional<Friendship> findBetweenUsers(@Param("id1") Long id1, @Param("id2") Long id2);


}