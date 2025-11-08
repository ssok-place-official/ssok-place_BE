package com.example.ssokPlace.appointments.entity;

import com.example.ssokPlace.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@AllArgsConstructor
@Builder
@Table(name="appointments")
public class Appointment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="public_id", nullable = false)
    private Long publicId;

    @Column(name="title", nullable = false)
    private String title;

    @Column(name="note")
    private String note;

    @Column(name = "start_at")
    private Instant startAt;

    @Column(name = "place_id")
    private Long placeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AppointmentsStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;


    @OneToMany(mappedBy = "appointment", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<AppointmentMember> members = new ArrayList<>();

    private Appointment(Long publicId,
                        String title,
                        String note,
                        Long placeId,
                        Instant startAt,
                        User createdBy) {
        this.publicId = publicId;
        this.title = title;
        this.note = note;
        this.placeId = placeId;
        this.startAt = startAt;
        this.status = AppointmentsStatus.PROPOSED;
        this.createdBy = createdBy;
    }

    public static Appointment create(Long publicId,
                                     String title,
                                     String note,
                                     Long placeId,
                                     Instant startAt,
                                     User host) {
        var appt = new Appointment(publicId, title, note, placeId, startAt, host);
        appt.addMember(host, MemberRole.HOST, RsvpStatus.ACCEPTED);
        return appt;
    }

    public AppointmentMember addMember(User user, MemberRole role, RsvpStatus rsvp){
        var m = AppointmentMember.create(this, user, role, rsvp);
        this.members.add(m);
        return m;
    }

    public boolean isHost(User user){
        return members.stream().anyMatch(m -> m.getUser().getId().equals(user.getId()) && m.getMemberRole() == MemberRole.HOST);
    }

    public void confirm(){
        if(this.status == AppointmentsStatus.CANCELED){
            throw new IllegalStateException("취소된 약속은 확정할 수 없습니다.");
        }
        this.status = AppointmentsStatus.CONFIRMED;
    }

    public void cancel(){
        this.status = AppointmentsStatus.CANCELED;
    }
}
