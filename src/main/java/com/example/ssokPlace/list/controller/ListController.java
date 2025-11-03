package com.example.ssokPlace.list.controller;

import com.example.ssokPlace.common.CommonResponse;
import com.example.ssokPlace.common.PageDTO;
import com.example.ssokPlace.list.dto.*;
import com.example.ssokPlace.list.service.ListCommandService;
import com.example.ssokPlace.list.service.ListQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lists")
public class ListController {

    private final ListQueryService listQueryService;
    private final ListCommandService listCommandService;

    @GetMapping
    public ResponseEntity<CommonResponse<PageDTO<ListSummaryDTO>>> getLists(
            @PageableDefault(page = 0, size = 20) Pageable pageable
    ) {
        PageDTO<ListSummaryDTO> data = listQueryService.getLists(pageable);
        return ResponseEntity.ok(
                CommonResponse.ok(data, "리스트 조회 성공")
        );
    }

    @PostMapping
    public ResponseEntity<CommonResponse<ListSimpleDTO>> create(
            @Valid @RequestBody CreateListRequest req
    ) {
        ListSimpleDTO data = listCommandService.create(req.getName(), req.getEmoji());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.created(data, "리스트가 생성되었습니다."));
    }

    @PatchMapping("/{listId}")
    public ResponseEntity<CommonResponse<ListSimpleDTO>> update(
            @PathVariable Long listId,
            @Valid @RequestBody UpdateListRequest req
    ) {
        ListSimpleDTO data = listCommandService.update(listId, req.getName(), req.getEmoji());
        return ResponseEntity.ok(
                CommonResponse.ok(data, "리스트가 수정되었습니다.")
        );
    }

    @DeleteMapping("/{listId}")
    public ResponseEntity<CommonResponse<Map<String, Object>>> delete(
            @PathVariable Long listId
    ) {
        listCommandService.delete(listId);
        return ResponseEntity.ok(
                CommonResponse.ok(Map.of("id", listId), "리스트가 삭제되었습니다.")
        );
    }

    @PostMapping("/{listId}/places")
    public ResponseEntity<CommonResponse<Map<String, Object>>> addPlace(
            @PathVariable Long listId,
            @Valid @RequestBody AddPlaceRequest req
    ) {
        listCommandService.addPlace(listId, req.getPlaceId());
        return ResponseEntity.ok(
                CommonResponse.ok(
                        Map.of("listId", listId, "placeId", req.getPlaceId()),
                        "장소가 리스트에 추가되었습니다."
                )
        );
    }

    @DeleteMapping("/{listId}/places/{placeId}")
    public ResponseEntity<CommonResponse<Map<String, Object>>> removePlace(
            @PathVariable Long listId,
            @PathVariable Long placeId
    ) {
        listCommandService.removePlace(listId, placeId);
        return ResponseEntity.ok(
                CommonResponse.ok(
                        Map.of("listId", listId, "placeId", placeId),
                        "장소가 리스트에서 제거되었습니다."
                )
        );
    }
}
