package com.example.ssokPlace.appointments.service;

import com.example.ssokPlace.appointments.dto.*;
import com.example.ssokPlace.appointments.entity.*;
import com.example.ssokPlace.appointments.entity.RsvpStatus;
import com.example.ssokPlace.appointments.repository.AppointmentMemberRepository;
import com.example.ssokPlace.appointments.repository.AppointmentRepository;
import com.example.ssokPlace.common.PageDTO;
import com.example.ssokPlace.error.ReportableError;
import com.example.ssokPlace.user.entity.User;
import com.example.ssokPlace.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final AppointmentMemberRepository appointmentMemberRepository;
    private final UserRepository userRepository;

    @Transactional
    public CreateAppointmentResponse create(String myEmail, CreateAppointmentRequest req) {
        User me = userRepository.findByEmail(myEmail)
                .orElseThrow(() -> new ReportableError(HttpStatus.NOT_FOUND, "유저 정보를 찾을 수 없습니다."));

        Instant startAt = req.getStartAt();
        if (startAt.isBefore(Instant.now())) {
            throw new ReportableError(HttpStatus.BAD_REQUEST, "startAt은 현재시각 이후여야 합니다.");
        }

        Long publicId = generatePublicId();

        var appt = Appointment.create(
                publicId,
                req.getTitle(),
                req.getNote(),
                req.getPlaceId(),
                startAt,
                me
        );

        var guestIds = (req.getMemberUserIds() == null) ? List.<Long>of()
                : req.getMemberUserIds().stream()
                .filter(id -> !Objects.equals(id, me.getId()))
                .distinct()
                .toList();

        if (!guestIds.isEmpty()) {
            var guests = userRepository.findAllById(guestIds);
            var found = guests.stream().map(User::getId).collect(Collectors.toSet());
            var missing = guestIds.stream().filter(id -> !found.contains(id)).toList();
            if (!missing.isEmpty()) {
                throw new ReportableError(HttpStatus.NOT_FOUND, "초대하려는 유저를 찾을 수 없습니다: " + missing);
            }
            guests.forEach(u -> appt.addMember(u, MemberRole.GUEST, RsvpStatus.PENDING));
        }

        appointmentRepository.save(appt);

        var membersDto = appt.getMembers().stream()
                .map(m -> new MemberDto(
                        m.getUser().getId(),
                        m.getMemberRole(),
                        m.getRsvp()
                ))
                .toList();

        return new CreateAppointmentResponse(
                appt.getPublicId(), // Long
                appt.getStatus(),
                membersDto
        );
    }

    /** GET: /appointments/{id} */
    @Transactional(readOnly = true)
    public AppointmentDetailResponse get(Long publicId){
        var a = appointmentRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ReportableError(HttpStatus.NOT_FOUND, "약속을 찾을 수 없습니다."));

        var members = a.getMembers().stream()
                .map(m -> new MemberDto(
                        m.getUser().getId(),
                        m.getMemberRole(),
                        m.getRsvp()
                ))
                .toList();

        return new AppointmentDetailResponse(
                a.getPublicId(),        // Long
                a.getTitle(),
                a.getPlaceId(),
                a.getStartAt(),
                a.getStatus(),
                members
        );
    }

    @Transactional
    public AppointmentsStatus confirm(Long publicId, String myEmail){
        var a = appointmentRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ReportableError(HttpStatus.NOT_FOUND, "약속을 찾을 수 없습니다."));

        var me = userRepository.findByEmail(myEmail)
                .orElseThrow(() -> new ReportableError(HttpStatus.NOT_FOUND, "유저 정보를 찾을 수 없습니다."));

        if (!a.isHost(me)) {
            throw new ReportableError(HttpStatus.FORBIDDEN, "호스트만 약속을 확정할 수 있습니다.");
        }

        a.confirm(); // status -> CONFIRMED
        return a.getStatus();
    }


    /** POST: /appointments/{id}/rsvp */
    @Transactional
    public void rsvp(Long publicId, String myEmail, RsvpStatus rsvp){
        var me = userRepository.findByEmail(myEmail)
                .orElseThrow(() -> new ReportableError(HttpStatus.NOT_FOUND, "유저 정보를 찾을 수 없습니다."));

        var m = appointmentMemberRepository
                .findByAppointment_PublicIdAndUser_Id(publicId, me.getId())
                .orElseThrow(() -> new ReportableError(HttpStatus.NOT_FOUND, "초대 멤버가 아닙니다."));

        try {
            m.respond(rsvp);
        } catch (IllegalStateException e) {
            throw new ReportableError(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /** POST: /appointments/{id}/cancel */
    @Transactional
    public AppointmentsStatus cancel(Long publicId, String myEmail){
        var a = appointmentRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ReportableError(HttpStatus.NOT_FOUND, "약속을 찾을 수 없습니다."));

        var me = userRepository.findByEmail(myEmail)
                .orElseThrow(() -> new ReportableError(HttpStatus.NOT_FOUND, "유저 정보를 찾을 수 없습니다."));

        if(!a.isHost(me)){
            throw new ReportableError(HttpStatus.FORBIDDEN, "호스트만 약속을 취소할 수 있습니다.");
        }

        a.cancel();
        return a.getStatus();
    }

    /** GET: /appointments ... : 검색 */
    @Transactional(readOnly = true)
    public Page<Appointment> search(AppointmentsStatus status, Instant from, Instant to, Pageable pageable){
        return appointmentRepository.search(status, from, to, pageable);
    }

    @Transactional(readOnly = true)
    public PageDTO<AppointmentSummary> searchSummaries(AppointmentsStatus status, Instant from, Instant to, Pageable pageable) {
        Page<Appointment> page = search(status, from, to, pageable);

        Page<AppointmentSummary> mapped = page.map(a ->
                new AppointmentSummary(
                        a.getPublicId(),    // Long
                        a.getTitle(),
                        a.getStartAt(),
                        a.getStatus()
                )
        );

        return PageDTO.of(mapped);
    }

    private static Long generatePublicId() {
        long id;
        do {
            id = ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);
        } while (id == 0L);
        return id;
    }
}
