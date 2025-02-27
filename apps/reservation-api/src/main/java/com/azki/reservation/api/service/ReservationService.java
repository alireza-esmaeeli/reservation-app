package com.azki.reservation.api.service;

public interface ReservationService {
    void reserveFirstAvailableSlot(String userEmail);

    void cancelReservation(Long reservationId);
}
