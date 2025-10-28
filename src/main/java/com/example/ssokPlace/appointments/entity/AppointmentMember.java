package com.example.ssokPlace.appointments.entity;

import com.example.ssokPlace.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@AllArgsConstructor
@Builder
@Table(name="appointment_members")
@Access(AccessType.FIELD)
public class AppointmentMember {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING) @Column(name="member_role", nullable = false)
    private MemberRole memberRole;

    @Enumerated(EnumType.STRING) @Column(name="rsvp_status", nullable = false)
    private RsvpStatus rsvp;

    private AppointmentMember(Appointment appointment, User user, MemberRole memberRole, RsvpStatus rsvpStatus) {
        this.appointment = appointment;
        this.user = user;
        this.memberRole = memberRole;
        this.rsvp = rsvpStatus;
    }

    public static AppointmentMember create(Appointment appointment, User user, MemberRole memberRole, RsvpStatus rsvp) {
        return new AppointmentMember(appointment, user, memberRole, rsvp);
    }

    public void respond(RsvpStatus rsvp){
        if(this.memberRole == MemberRole.HOST){
            throw new IllegalStateException("호스트 RSVP는 변경할 수 없습니다.");
        }
        this.rsvp = rsvp;
    }
}
