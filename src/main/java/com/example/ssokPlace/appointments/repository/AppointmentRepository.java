package com.example.ssokPlace.appointments.repository;

import com.example.ssokPlace.appointments.entity.Appointment;
import com.example.ssokPlace.appointments.entity.AppointmentsStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    Optional<Appointment> findByPublicId(Long publicId);

    @Query("""
        select a from Appointment a
        where (:status is null or a.status = :status)
          and (:from is null or a.startAt >= :from)
          and (:to   is null or a.startAt <  :to)
    """)
    Page<Appointment> search(AppointmentsStatus status, Instant from, Instant to, Pageable pageable);
}
