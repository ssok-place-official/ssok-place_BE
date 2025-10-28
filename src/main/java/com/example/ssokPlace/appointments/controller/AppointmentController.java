package com.example.ssokPlace.appointments.controller;

import com.example.ssokPlace.appointments.dto.*;
import com.example.ssokPlace.appointments.entity.AppointmentsStatus;
import com.example.ssokPlace.appointments.entity.RsvpStatus;
import com.example.ssokPlace.appointments.service.AppointmentService;
import com.example.ssokPlace.common.CommonResponse;
import com.example.ssokPlace.common.PageDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public CommonResponse<CreateAppointmentResponse> create(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody CreateAppointmentRequest req
            ){
        var data = appointmentService.create(principal.getUsername(), req);
        return CommonResponse.created(data, "약속이 생성되었습니다");
    }

    // 상세 조회
    @GetMapping("/{id}")
    public CommonResponse<AppointmentDetailResponse> detail(@PathVariable Long id){
        return CommonResponse.ok(appointmentService.get(id), "약속 상세 조회 성공");
    }

    // RSVP
    @PostMapping("/{id}/rsvp")
    public CommonResponse<Map<String, Object>> rsvp(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long id,
            @Valid @RequestBody RsvpRequest req
    ) {
        appointmentService.rsvp(id, principal.getUsername(), req.getRsvp());
        return CommonResponse.ok(Map.of("rsvp", req.getRsvp()), "응답이 저장되었습니다.");
    }

    // 취소
    @PostMapping("/{id}/cancel")
    public CommonResponse<Map<String, Object>> cancel(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long id
    ) {
        var st = appointmentService.cancel(id, principal.getUsername());
        return CommonResponse.ok(Map.of("status", st), "약속이 취소되었습니다.");
    }


    @PostMapping("/{id}/confirm")
    public CommonResponse<Map<String, Object>> confirm(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long id
    ) {
        var st = appointmentService.confirm(id, principal.getUsername());
        return CommonResponse.ok(
                Map.of("status", st),
                "약속이 확정되었습니다."
        );
    }


    @GetMapping
    public CommonResponse<PageDTO<AppointmentSummary>> search(
            @RequestParam(required = false) AppointmentsStatus status,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "startAt,asc") String sort
    ) {
        var sp = sort.split(",");
        var sortBy = sp[0];
        var dir = (sp.length > 1 && "desc".equalsIgnoreCase(sp[1]))
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        var pageable = PageRequest.of(page, size, Sort.by(dir, sortBy));

        Instant fromUtc = (from == null)
                ? null
                : from.atStartOfDay().toInstant(java.time.ZoneOffset.UTC);

        Instant toUtc = (to == null)
                ? null
                : to.plusDays(1).atStartOfDay().toInstant(java.time.ZoneOffset.UTC);

        var data = appointmentService.searchSummaries(status, fromUtc, toUtc, pageable);
        return CommonResponse.ok(data, "약속 검색 성공");
    }
}
