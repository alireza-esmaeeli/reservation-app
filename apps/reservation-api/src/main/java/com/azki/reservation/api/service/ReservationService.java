package com.azki.reservation.api.service;

import com.azki.reservation.api.data.entity.Reservation;

public interface ReservationService {
    Reservation reserveFirstAvailableSlot(String userEmail);

    void cancelReservation(Long reservationId);
}
