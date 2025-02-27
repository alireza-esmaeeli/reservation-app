package com.azki.reservation.api.controller;

import com.azki.reservation.api.dto.ReservationCreateDTO;
import com.azki.reservation.api.dto.ReservationQueryDTO;
import com.azki.reservation.api.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationQueryDTO> createReservation(@RequestBody ReservationCreateDTO dto) {
        var reservation = reservationService.reserveFirstAvailableSlot(dto.userEmail());
        return ResponseEntity
                .created(URI.create("/reservations/" + reservation.getId()).normalize())
                .body(new ReservationQueryDTO(
                        reservation.getId(),
                        reservation.getUser().getEmail(),
                        reservation.getAvailableSlot().getStartTime(),
                        reservation.getAvailableSlot().getEndTime())
                );
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.cancelReservation(id);
        return ResponseEntity.noContent().build();
    }
}
