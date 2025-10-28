package com.example.ssokPlace.appointments.repository;

import com.example.ssokPlace.appointments.entity.AppointmentMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppointmentMemberRepository extends JpaRepository<AppointmentMember, Long> {
    Optional<AppointmentMember> findByAppointment_PublicIdAndUser_Id(Long apptPublicId, Long userId);
}
