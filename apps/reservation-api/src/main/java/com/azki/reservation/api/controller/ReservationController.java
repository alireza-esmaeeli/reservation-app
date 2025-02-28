package com.azki.reservation.api.controller;

import com.azki.reservation.api.dto.ReservationCreateDTO;
import com.azki.reservation.api.dto.ReservationQueryDTO;
import com.azki.reservation.api.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping(produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationQueryDTO createReservation(@RequestBody ReservationCreateDTO dto) {
        var reservation = reservationService.reserveFirstAvailableSlot(dto.userEmail());
        return new ReservationQueryDTO(
                reservation.getId(),
                reservation.getUser().getEmail(),
                reservation.getAvailableSlot().getStartTime(),
                reservation.getAvailableSlot().getEndTime()
        );
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReservation(@PathVariable Long id) {
        reservationService.cancelReservation(id);
    }
}
