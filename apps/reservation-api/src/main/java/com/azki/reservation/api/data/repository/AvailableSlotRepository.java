package com.azki.reservation.api.data.repository;

import com.azki.reservation.api.data.entity.AvailableSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AvailableSlotRepository
        extends JpaRepository<AvailableSlot, Long>, JpaSpecificationExecutor<AvailableSlot> {
}
