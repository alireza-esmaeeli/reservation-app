package com.azki.reservation.api.service;

import com.azki.reservation.api.data.entity.Reservation;
import com.azki.reservation.api.data.repository.AvailableSlotRepository;
import com.azki.reservation.api.data.repository.ReservationRepository;
import com.azki.reservation.api.data.repository.UserRepository;
import com.azki.reservation.api.exception.AvailableSlotNotFoundException;
import com.azki.reservation.api.exception.ReservationNotFoundException;
import com.azki.reservation.api.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultReservationService implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final AvailableSlotRepository availableSlotRepository;

    @Transactional
    @Override
    public Reservation reserveFirstAvailableSlot(String userEmail) {

        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(UserNotFoundException::new);
        log.debug("Found user {}", user.getId());

        var availableSlot = availableSlotRepository.findFirstNotReserved()
                .orElseThrow(AvailableSlotNotFoundException::new);
        log.debug("Found available slot {}", availableSlot.getId());

        Reservation reservation = new Reservation(availableSlot, user);
        log.debug("Created reservation of available slot {} for user {}", availableSlot.getId(), user.getId());

        reservationRepository.save(reservation);
        log.debug("Saved reservation {}", reservation.getId());

        return reservation;
    }

    @Override
    public void cancelReservation(Long reservationId) {
        var reservation = reservationRepository.findById(reservationId)
                .orElseThrow(ReservationNotFoundException::new);

        reservationRepository.delete(reservation);
    }
}
