package com.azki.reservation.api.service;

import com.azki.reservation.api.data.entity.Reservation;
import com.azki.reservation.api.data.repository.AvailableSlotRepository;
import com.azki.reservation.api.data.repository.ReservationRepository;
import com.azki.reservation.api.data.repository.UserRepository;
import com.azki.reservation.api.exception.AvailableSlotNotFoundException;
import com.azki.reservation.api.exception.ReservationNotFoundException;
import com.azki.reservation.api.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultReservationService implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final AvailableSlotRepository availableSlotRepository;

    @Override
    public Reservation reserveFirstAvailableSlot(String userEmail) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(UserNotFoundException::new);

        var availableSlot = availableSlotRepository.findFirstNotReserved()
                .orElseThrow(AvailableSlotNotFoundException::new);

        var reservation = new Reservation(availableSlot, user);
        reservationRepository.save(reservation);
        return reservation;
    }

    @Override
    public void cancelReservation(Long reservationId) {
        var reservation = reservationRepository.findById(reservationId)
                .orElseThrow(ReservationNotFoundException::new);

        reservationRepository.delete(reservation);
    }
}
