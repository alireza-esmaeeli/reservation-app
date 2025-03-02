package com.azki.reservation.api.data.repository;

import com.azki.reservation.api.data.entity.Reservation;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository
        extends JpaRepository<Reservation, Long>, JpaSpecificationExecutor<Reservation> {

    @PreAuthorize("@reservationAuthorization.decide(#entity)")
    @Override
    void delete(@NonNull Reservation entity);
}
