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
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.geom.impl.PackedCoordinateSequenceFactory;
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

    private static final GeometryFactory GF =
            new GeometryFactory(new PrecisionModel(), 4326,
                    PackedCoordinateSequenceFactory.DOUBLE_FACTORY);

    @Transactional
    public PlaceDTO saveUserPlace(String myEmail, PlaceCreateReq req) {
        User me = userRepository.findByEmail(myEmail)
                .orElseThrow(() -> new ReportableError(HttpStatus.NOT_FOUND, "유저 정보를 찾을 수 없습니다."));

        if (req.getPlaceId() == null) {
            throw new ReportableError(HttpStatus.BAD_REQUEST, "placeId는 필수입니다.");
        }
        if (req.getMemo() == null && (req.getTags() == null || req.getTags().isEmpty())) {
            throw new ReportableError(HttpStatus.BAD_REQUEST, "memo 또는 tags 중 하나는 있어야 합니다.");
        }

        // 1) Place 존재 여부 확인
        Place place = placeRepository.findById(req.getPlaceId())
                .orElseThrow(() -> new ReportableError(HttpStatus.NOT_FOUND, "해당 Place가 존재하지 않습니다."));

        // 2) UserPlace upsert
        UserPlace up = userPlaceRepository.findByUserIdAndPlaceId(me.getId(), place.getId())
                .orElseGet(() -> UserPlace.of(me.getId(), place.getId(), null, null, null));

        String tagsJson = (req.getTags() != null) ? writeJson(req.getTags()) : null;
        up.applyPatch(req.getMemo(), tagsJson);
        userPlaceRepository.save(up);

        // 3) 응답 DTO
        return PlaceDTO.builder()
                .id(place.getId())
                .name(place.getName())
                .address(place.getAddress())
                .lat(place.getLat())
                .lng(place.getLng())
                .isClosed(false)
                .memo(up.getMemo())
                .tags(readTags(up.getTags()))
                .createdAt(up.getCreatedAt().toInstant())
                .coverUrl(place.getCoverUrl())
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
                .coverUrl(p.getCoverUrl())
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
                .collect(Collectors.toMap(PlaceInsight::getPlaceId, PlaceInsight::getEmoji));

        var mapped = pageRes.map(p -> PlacePinDTO.builder()
                .id(p.getId())
                .name(p.getName())
                .lat(p.getLat())
                .lng(p.getLng())
                .isClosed(false)
                .emoji(emojiById.get(p.getId()))
                .distanceM((int)Math.round(
                        haversineMeters(q.getCenterLat(), q.getCenterLng(), p.getLat(), p.getLng())))
                .coverUrl(p.getCoverUrl())
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

        String memo = req.getMemo();
        String tagsJson = (req.getTags() != null) ? writeJson(req.getTags()) : null;

        up.applyPatch(memo, tagsJson);

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
