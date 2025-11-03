package com.example.ssokPlace.list.service;

import com.example.ssokPlace.common.PageDTO;
import com.example.ssokPlace.list.dto.ListSummaryDTO;
import com.example.ssokPlace.list.repository.PlaceListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;

@Service
@RequiredArgsConstructor
public class ListQueryService {

    private final PlaceListRepository placeListRepository;

    public PageDTO<ListSummaryDTO> getLists(Pageable pageable){
        Page<ListSummaryDTO> page = placeListRepository.findAllSummaries(pageable);
        return PageDTO.of(page);
    }
}
