package com.azki.reservation.api.controller;

import com.azki.reservation.api.dto.ReservationCreateDTO;
import com.azki.reservation.api.dto.ReservationReadDTO;
import com.azki.reservation.api.service.ReservationService;
import com.azki.reservation.api.util.ReservationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private static final ReservationMapper MAPPER = ReservationMapper.INSTANCE;

    @PostMapping(produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationReadDTO create(@RequestBody ReservationCreateDTO createDTO) {
        var reservation = reservationService.reserveFirstAvailableSlot(createDTO.email());
        return MAPPER.mapToReadDTO(reservation);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        reservationService.cancelReservation(id);
    }
}
