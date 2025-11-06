package com.example.ssokPlace.profile.service;

import com.example.ssokPlace.common.PageDTO;
import com.example.ssokPlace.error.ReportableError;
import com.example.ssokPlace.friend.repository.FriendRepository;
import com.example.ssokPlace.places.entity.PlaceMeta;
import com.example.ssokPlace.places.repository.PlaceMetaRepository;
import com.example.ssokPlace.profile.dto.ActivityDTO;
import com.example.ssokPlace.profile.dto.PlaceActivityDTO;
import com.example.ssokPlace.places.entity.UserPlace;
import com.example.ssokPlace.places.repository.PlaceVisitRepository;
import com.example.ssokPlace.places.repository.UserPlaceRepository;
import com.example.ssokPlace.profile.dto.*;
import com.example.ssokPlace.user.entity.User;
import com.example.ssokPlace.user.entity.UserKeyword;
import com.example.ssokPlace.user.entity.UserKeywordPrf;
import com.example.ssokPlace.user.repository.UserKeywordPrefRepository;
import com.example.ssokPlace.user.repository.UserKeywordRepository;
import com.example.ssokPlace.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ProfileService {
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final UserPlaceRepository userPlaceRepository;
    private final PlaceVisitRepository placeVisitRepository;
    private final UserKeywordPrefRepository userKeywordPrefRepository;
    private final UserKeywordRepository userKeywordRepository;
    private final PlaceMetaRepository placeMetaRepository;
    @Transactional(readOnly = true)
    public ActivityDTO getMyActivity(String myEmail, int lookbackDays) {
        User me = userRepository.findByEmail(myEmail)
                .orElseThrow(() -> new ReportableError(HttpStatus.NOT_FOUND, "유저 정보를 찾을 수 없습니다."));

        OffsetDateTime since = OffsetDateTime.now().minusDays(lookbackDays);

        var frequentRows = placeVisitRepository.findFrequentPlaces(me.getId(), since, PageRequest.of(0, 10));
        var frequentIds = frequentRows.stream().map(r -> (Long) r[0]).collect(Collectors.toSet());

        var dormantIds = placeVisitRepository.findDormantPlaces(me.getId(), since, PageRequest.of(0, 10));
        var dormantIdSet = new java.util.HashSet<>(dormantIds);

        // 메타 일괄 조회
        var allIds = new java.util.HashSet<Long>();
        allIds.addAll(frequentIds);
        allIds.addAll(dormantIdSet);
        var metaMap = placeMetaRepository.findByIdIn(allIds).stream()
                .collect(Collectors.toMap(PlaceMeta::getId, m -> m));

        var frequent = frequentRows.stream().map(row -> {
            Long placeId = (Long) row[0];
            var meta = metaMap.get(placeId);
            return new PlaceActivityDTO(
                    placeId,
                    meta != null ? meta.getName() : ("장소" + placeId),
                    meta != null ? meta.getEmoji() : "📍",
                    false,
                    meta != null ? (meta.getLat() == null ? 0.0 : meta.getLat()) : 0.0,
                    meta != null ? (meta.getLng() == null ? 0.0 : meta.getLng()) : 0.0,
                    0
            );
        }).toList();

        var dormant = dormantIds.stream().map(pid -> {
            var meta = metaMap.get(pid);
            return new PlaceActivityDTO(
                    pid,
                    meta != null ? meta.getName() : ("장소" + pid),
                    meta != null ? meta.getEmoji() : "📍",
                    false,
                    meta != null ? (meta.getLat() == null ? 0.0 : meta.getLat()) : 0.0,
                    meta != null ? (meta.getLng() == null ? 0.0 : meta.getLng()) : 0.0,
                    0
            );
        }).toList();

        return new ActivityDTO(frequent, dormant);
    }

    @Transactional(readOnly = true)
    public KeywordDTO getMyKeywords(String myEmail) {
        User me = userRepository.findByEmail(myEmail)
                .orElseThrow(() -> new ReportableError(HttpStatus.NOT_FOUND, "유저 정보를 찾을 수 없습니다."));

        var weights = userKeywordRepository.findTop10ByUserIdOrderByWeightDesc(me.getId());
        var prefs = userKeywordPrefRepository.findByUserId(me.getId()).stream()
                .collect(Collectors.toMap(UserKeywordPrf::getTerm, p -> p));

        var items = new ArrayList<KeywordDTO.KeywordItem>();
        for (UserKeyword w : weights) {
            var pref = prefs.get(w.getTerm());
            items.add(new KeywordDTO.KeywordItem(
                    w.getTerm(),
                    w.getWeight(),
                    pref != null && pref.isPinned(),
                    pref != null && pref.isHidden()
            ));
        }
        return new KeywordDTO(items);
    }
    @Transactional
    public void updateMyKeywords(String myEmail, KeywordUpdateRequest req) {
        User me = userRepository.findByEmail(myEmail)
                .orElseThrow(() -> new ReportableError(HttpStatus.NOT_FOUND, "유저 정보를 찾을 수 없습니다."));
        if (req == null) {
            throw new ReportableError(HttpStatus.BAD_REQUEST, "요청 데이터가 없습니다.");
        }

        var pin    = Optional.ofNullable(req.getPin()).orElse(List.of());
        var unpin  = Optional.ofNullable(req.getUnpin()).orElse(List.of());
        var hide   = Optional.ofNullable(req.getHide()).orElse(List.of());
        var unhide = Optional.ofNullable(req.getUnhide()).orElse(List.of());

        for (String term : pin)    upsertPref(me.getId(), term, true,  null);
        for (String term : unpin)  upsertPref(me.getId(), term, false, null);
        for (String term : hide)   upsertPref(me.getId(), term, null,  true);
        for (String term : unhide) upsertPref(me.getId(), term, null,  false);
    }

    private void upsertPref(Long userId, String term, Boolean pinned, Boolean hidden) {
        var prefOpt = userKeywordPrefRepository.findByUserIdAndTerm(userId, term);
        UserKeywordPrf pref = prefOpt.orElseGet(() ->
                UserKeywordPrf.of(userId, term,
                        pinned != null && pinned,
                        hidden != null && hidden)
        );

        if (prefOpt.isPresent()) {
            pref.apply(pinned != null ? pinned : pref.isPinned(),
                    hidden != null ? hidden : pref.isHidden());
        }
        userKeywordPrefRepository.save(pref);
    }

    @Transactional(readOnly = true)
    public ProfileDTO getUserProfile(String myEmail, Long userId) {
        User me = userRepository.findByEmail(myEmail)
                .orElseThrow(() -> new ReportableError(HttpStatus.NOT_FOUND, "유저 정보를 찾을 수 없습니다."));
        User target = userRepository.findById(userId)
                .orElseThrow(() -> new ReportableError(HttpStatus.NOT_FOUND, "대상 유저를 찾을 수 없습니다."));

        boolean isFriend = friendRepository.existsFriendship(me.getId(), target.getId());

        var visibility = target.getProfileVisibility();
        boolean forbidden = switch (visibility) {
            case PUBLIC   -> false;
            case FRIENDS  -> !isFriend;
            case PRIVATE  -> !Objects.equals(me.getId(), target.getId());
        };
        if (forbidden) {
            throw new ReportableError(HttpStatus.FORBIDDEN, "프로필 공개 범위로 인해 접근 불가합니다.");
        }

        long savedPlaces = userPlaceRepository.countByUserId(target.getId());

        var weights = userKeywordRepository.findTop10ByUserIdOrderByWeightDesc(target.getId());
        var prefs = userKeywordPrefRepository.findByUserId(target.getId()).stream()
                .collect(Collectors.toMap(UserKeywordPrf::getTerm, p -> p));

        var keywords = new ArrayList<ProfileDTO.KeywordInfo>();
        for (UserKeyword w : weights) {
            var pref = prefs.get(w.getTerm());
            if (pref != null && pref.isHidden()) continue;
            keywords.add(new ProfileDTO.KeywordInfo(w.getTerm(), w.getWeight()));
        }

        return new ProfileDTO(
                new ProfileDTO.UserInfo(target.getId(), target.getNickname()),
                keywords,
                new ProfileDTO.Stats((int) savedPlaces)
        );
    }


    @Transactional(readOnly = true)
    public PageDTO<ProfilePlaceDTO> getUserPlaces(String myEmail, Long userId, int page, int size) {
        User me = userRepository.findByEmail(myEmail)
                .orElseThrow(() -> new ReportableError(HttpStatus.NOT_FOUND, "유저 정보를 찾을 수 없습니다."));
        User target = userRepository.findById(userId)
                .orElseThrow(() -> new ReportableError(HttpStatus.NOT_FOUND, "대상 유저를 찾을 수 없습니다."));

        boolean isFriend = friendRepository.existsFriendship(me.getId(), target.getId());

        var pageResult = userPlaceRepository.findViewablePlaces(target.getId(), isFriend, PageRequest.of(page, size));

        var ids = pageResult.map(UserPlace::getPlaceId).toList();
        var metaMap = placeMetaRepository.findByIdIn(ids).stream()
                .collect(Collectors.toMap(PlaceMeta::getId, m -> m));

        var mapped = pageResult.map(up -> {
            var m = metaMap.get(up.getPlaceId());
            return new ProfilePlaceDTO(
                    up.getPlaceId(),
                    m != null ? m.getName() : ("장소" + up.getPlaceId()),
                    m != null ? m.getEmoji() : "📍",
                    m != null && m.getLat()!=null ? m.getLat() : 0.0,
                    m != null && m.getLng()!=null ? m.getLng() : 0.0,
                    0
            );
        });

        return PageDTO.of(mapped);
    }

    @Transactional
    public UserPlace.Visibility updateVisibility(String myEmail, Long placeId, String newVisibility) {
        User me = userRepository.findByEmail(myEmail)
                .orElseThrow(() -> new ReportableError(HttpStatus.NOT_FOUND, "유저 정보를 찾을 수 없습니다."));

        UserPlace up = userPlaceRepository.findByUserIdAndPlaceId(me.getId(), placeId)
                .orElseThrow(() -> new ReportableError(HttpStatus.NOT_FOUND, "해당 장소를 찾을 수 없습니다."));

        final UserPlace.Visibility vis;
        try {
            vis = UserPlace.Visibility.valueOf(newVisibility);
        } catch (IllegalArgumentException e) {
            throw new ReportableError(HttpStatus.BAD_REQUEST, "허용되지 않은 공개 범위입니다.");
        }

        up.changeVisibility(vis);
        userPlaceRepository.save(up);
        return vis;
    }
}