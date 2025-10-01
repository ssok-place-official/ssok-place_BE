package com.example.ssokPlace.places.service;

import com.example.ssokPlace.common.PageDTO;
import com.example.ssokPlace.error.ReportableError;
import com.example.ssokPlace.places.dto.*;
import com.example.ssokPlace.places.entity.Place;
import com.example.ssokPlace.places.entity.PlaceInsight;
import com.example.ssokPlace.places.entity.UserPlace;
import com.example.ssokPlace.places.repository.PlaceInsightRepository;
import com.example.ssokPlace.places.repository.PlaceRepository;
import com.example.ssokPlace.places.repository.UserPlaceRepository;
import com.example.ssokPlace.user.entity.User;
import com.example.ssokPlace.user.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final UserPlaceRepository userPlaceRepository;
    private final PlaceRepository placeRepository;
    private final UserRepository userRepository;
    private final PlaceInsightRepository placeInsightRepository;

    private final ObjectMapper om = new ObjectMapper();

    @Transactional
    public PlaceDTO createOrAttach(String myEmail, PlaceCreateReq req){
        User me = userRepository.findByEmail(myEmail)
                .orElseThrow(() -> new ReportableError(HttpStatus.NOT_FOUND, "유저 정보를 찾을 수 없습니다."));

        Place place = null;
        if(req.getNaverPlaceId()!=null && !req.getNaverPlaceId().isBlank()){
            place = placeRepository.findByNaverPlaceId(req.getNaverPlaceId()).orElse(null);
        }

        if(place == null && placeRepository.existsNearby(req.getLat(), req.getLng(), 10)){
            throw new ReportableError(HttpStatus.CONFLICT, "이미 등록된 장소가 있습니다.");
        }

        if (req.getName()==null || req.getName().isBlank()
                || req.getLat()==null || req.getLng()==null) {
            throw new ReportableError(HttpStatus.BAD_REQUEST, "name, lat, lng는 필수입니다.");
        }

        if (req.getMemo()==null && (req.getTags()==null || req.getTags().isEmpty())) {
            throw new ReportableError(HttpStatus.BAD_REQUEST, "변경할 필드가 없습니다.");
        }

        if(place==null){
            Map<String, Object> refs = new LinkedHashMap<>();
            if (req.getNaverPlaceId() != null) refs.put("naver_place_id", req.getNaverPlaceId());
            if (req.getPlaceUrl() != null) refs.put("place_url", req.getPlaceUrl());

            place = Place.builder()
                    .name(req.getName())
                    .address(req.getAddress())
                    .lat(req.getLat())
                    .lng(req.getLng())
                    .externalRefs(refs.isEmpty()? null : writeJson(refs))
                    .build();
            place = placeRepository.save(place);
        }

        Long placeId = place.getId();

        UserPlace up = userPlaceRepository.findByUserIdAndPlaceId(me.getId(), place.getId())
                .orElseGet(() -> UserPlace.builder()
                        .userId(me.getId())
                        .placeId(placeId)
                        .memo(null)
                        .tags(null)
                        .build());

        if (req.getMemo()!=null) {
            up = UserPlace.builder()
                    .id(up.getId())
                    .userId(up.getUserId())
                    .placeId(up.getPlaceId())
                    .memo(req.getMemo())
                    .tags(up.getTags())
                    .build();
        }
        if (req.getTags()!=null) {
            up = UserPlace.builder()
                    .id(up.getId())
                    .userId(up.getUserId())
                    .placeId(up.getPlaceId())
                    .memo(up.getMemo())
                    .tags(writeJson(req.getTags()))
                    .build();
        }
        up = userPlaceRepository.save(up);

        return PlaceDTO.builder()
                .id(place.getId())
                .name(place.getName())
                .address(place.getAddress())
                .lat(place.getLat())
                .lng(place.getLng())
                .isClosed(false)
                .memo(up.getMemo())
                .tags(readTags(up.getTags()))
                .createdAt(Instant.now())
                .build();
    }

    @Transactional(readOnly = true)
    public PlaceDTO getDetail(String myEmail, Long placeId, boolean includeInsight){
        User me = userRepository.findByEmail(myEmail)
                .orElseThrow(() -> new ReportableError(HttpStatus.NOT_FOUND, "유저 정보를 찾을 수 없습니다."));

        Place p = placeRepository.findById(placeId)
                .orElseThrow(() -> new ReportableError(HttpStatus.NOT_FOUND, "해당 장소를 찾을 수 없습니다."));

        UserPlace up = userPlaceRepository.findByUserIdAndPlaceId(me.getId(), placeId).orElse(null);

        InsightDTO insightDTO = null;
        if (includeInsight){
            PlaceInsight ins = placeInsightRepository.findById(placeId).orElse(null);
            if (ins != null){
                insightDTO = InsightDTO.builder()
                        .emoji(ins.getEmoji())
                        .keywords(readKeywords(ins.getKeywords()))
                        .build();
            }
        }

        return PlaceDTO.builder()
                .id(p.getId())
                .name(p.getName())
                .address(p.getAddress())
                .lat(p.getLat())
                .lng(p.getLng())
                .isClosed(false)
                .memo(up==null? null : up.getMemo())
                .tags(up==null? List.of() : readTags(up.getTags()))
                .insight(insightDTO)
                .build();
    }


    @Transactional(readOnly = true)
    public PageDTO<PlacePinDTO> nearby(String email, NearbyQuery q){
        userRepository.findByEmail(email)
                .orElseThrow(() -> new ReportableError(HttpStatus.NOT_FOUND, "유저 정보를 찾을 수 없습니다."));

        if (q.getCenterLat()==null || q.getCenterLng()==null || q.getRadiusM()==null || q.getRadiusM()<=0) {
            throw new ReportableError(HttpStatus.BAD_REQUEST, "centerLat, centerLng, radiusM는 필수이며 radiusM>0 이어야 합니다.");
        }

        int page = (q.getPage()==null || q.getPage()<0) ? 0 : q.getPage();
        int size = (q.getSize()==null || q.getSize()<=0) ? 20 : q.getSize();

        Pageable pageable = PageRequest.of(page, size);

        var pageRes = placeRepository.searchNearby(
                q.getCenterLat(), q.getCenterLng(), q.getRadiusM(),
                Boolean.TRUE.equals(q.getIncludeClosed()), pageable);

        var ids = pageRes.getContent().stream().map(Place::getId).toList();
        Map<Long, String> emojiById = placeInsightRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(PlaceInsight::getId, PlaceInsight::getEmoji));

        var mapped = pageRes.map(p -> PlacePinDTO.builder()
                .id(p.getId())
                .name(p.getName())
                .lat(p.getLat())
                .lng(p.getLng())
                .isClosed(false)
                .emoji(emojiById.get(p.getId()))
                .distanceM((int)Math.round(
                        haversineMeters(q.getCenterLat(), q.getCenterLng(), p.getLat(), p.getLng())))
                .build());

        return PageDTO.of(mapped);
    }


    @Transactional
    public Map<String, Object> updateUserPlace(String myEmail, Long placeId, PlacePatchReq req){
        User me = userRepository.findByEmail(myEmail)
                .orElseThrow(() -> new ReportableError(HttpStatus.NOT_FOUND, "유저 정보를 찾을 수 없습니다."));

        if (req.getMemo()==null && (req.getTags()==null || req.getTags().isEmpty())) {
            throw new ReportableError(HttpStatus.BAD_REQUEST, "변경할 필드가 없습니다.");
        }

        UserPlace up = userPlaceRepository.findByUserIdAndPlaceId(me.getId(), placeId)
                .orElseThrow(() -> new ReportableError(HttpStatus.NOT_FOUND, "해당 장소를 찾을 수 없습니다."));

        String newMemo = req.getMemo() != null ? req.getMemo() : up.getMemo();
        String newTags = req.getTags() != null ? writeJson(req.getTags()) : up.getTags();

        up = UserPlace.builder()
                .id(up.getId())
                .userId(up.getUserId())
                .placeId(up.getPlaceId())
                .memo(newMemo)
                .tags(newTags)
                .build();

        userPlaceRepository.save(up);

        return Map.of(
                "id", placeId,
                "memo", up.getMemo(),
                "tags", readTags(up.getTags())
        );
    }


    // Helper
    @SneakyThrows
    private String writeJson(Object o){
        return om.writeValueAsString(o);
    }

    @SneakyThrows
    private List<Map<String,Object>> readKeywords(String json){
        if(json==null) return null;
        return om.readValue(json, new TypeReference<List<Map<String, Object>>>(){});
    }

    @SneakyThrows
    private List<String> readTags(String json){
        if (json == null) return List.of();
        return om.readValue(json, new TypeReference<List<String>>() {});
    }


    private static double haversineMeters(double lat1, double lon1, double lat2, double lon2){
        double R=6371000.0, dLat=Math.toRadians(lat2-lat1), dLon=Math.toRadians(lon2-lon1);
        double a=Math.sin(dLat/2)*Math.sin(dLat/2)+Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2))*Math.sin(dLon/2)*Math.sin(dLon/2);
        return 2*R*Math.asin(Math.sqrt(a));
    }


}
