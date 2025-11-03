package com.example.ssokPlace.list.service;

import com.example.ssokPlace.error.ReportableError;
import com.example.ssokPlace.list.dto.ListSimpleDTO;
import com.example.ssokPlace.list.entity.PlaceList;
import com.example.ssokPlace.list.entity.PlaceListPlace;
import com.example.ssokPlace.list.repository.PlaceListPlaceRepository;
import com.example.ssokPlace.list.repository.PlaceListRepository;
import com.example.ssokPlace.places.entity.Place;
import com.example.ssokPlace.places.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ListCommandService {
    private final PlaceListRepository placeListRepository;
    private final PlaceListPlaceRepository placeListPlaceRepository;
    private final PlaceRepository placeRepository;

    @Transactional
    public ListSimpleDTO create(String name, String emoji) {
        if (placeListRepository.existsByName(name)) {
            throw new ReportableError(HttpStatus.CONFLICT, "동일한 리스트 이름이 이미 존재합니다.");
        }

        PlaceList list = PlaceList.builder()
                .name(name)
                .emoji(emoji)
                .build();

        placeListRepository.save(list);

        return new ListSimpleDTO(list.getId(), list.getName(), list.getEmoji(), 0L);
    }

    @Transactional
    public ListSimpleDTO update(Long listId, String name, String emoji) {
        PlaceList list = placeListRepository.findById(listId)
                .orElseThrow(() -> new ReportableError(HttpStatus.NOT_FOUND, "해당 리스트를 찾을 수 없습니다."));

        if (!list.getName().equals(name) && placeListRepository.existsByName(name)) {
            throw new ReportableError(HttpStatus.CONFLICT, "동일한 리스트 이름이 이미 존재합니다.");
        }
        list.rename(name, emoji);

        long placeCount = list.getPlaces().size();
        return new ListSimpleDTO(list.getId(), list.getName(), list.getEmoji(), placeCount);
    }

    @Transactional
    public void delete(Long listId){
        PlaceList list = placeListRepository.findById(listId)
                .orElseThrow(() -> new ReportableError(HttpStatus.NOT_FOUND, "해당 리스트를 찾을 수 없습니다."));
        placeListRepository.delete(list);
    }

    @Transactional
    public void addPlace(Long listId, Long placeId){
        PlaceList list = placeListRepository.findById(listId)
                .orElseThrow(() -> new ReportableError(HttpStatus.NOT_FOUND, "해당 리스트를 찾을 수 없습니다."));

        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new ReportableError(HttpStatus.NOT_FOUND, "해당 장소를 찾을 수 없습니다."));

        if(placeListPlaceRepository.existsByList_IdAndPlace_Id(listId, placeId)){
            throw new ReportableError(HttpStatus.CONFLICT, "이미 리스트에 포함된 장소입니다.");
        }

        PlaceListPlace.link(list, place); // cascade 저장
    }

    @Transactional
    public void removePlace(Long listId, Long placeId){
        if(!placeListRepository.existsById(placeId)){
            throw new ReportableError(HttpStatus.NOT_FOUND, "해당 리스트를 찾을 수 없습니다.");
        }
        long deleted = placeListPlaceRepository.deleteByList_IdAndPlace_Id(listId, placeId);
        if(deleted == 0){
            throw new ReportableError(HttpStatus.NOT_FOUND, "리스트에 해당 장소가 없습브니다.");
        }
    }
}
