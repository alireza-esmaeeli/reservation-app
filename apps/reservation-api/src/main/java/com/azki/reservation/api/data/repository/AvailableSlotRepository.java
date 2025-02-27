package com.azki.reservation.api.data.repository;

import com.azki.reservation.api.data.entity.AvailableSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AvailableSlotRepository
        extends JpaRepository<AvailableSlot, Long>, JpaSpecificationExecutor<AvailableSlot> {

    @Query("select a from AvailableSlot a where a.isReserved = false order by a.startTime limit 1")
    Optional<AvailableSlot> findFirstNotReserved();
}
