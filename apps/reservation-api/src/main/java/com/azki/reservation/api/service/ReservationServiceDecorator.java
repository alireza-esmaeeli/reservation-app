package com.azki.reservation.api.service;

import com.azki.reservation.api.data.entity.Reservation;

public abstract class ReservationServiceDecorator implements ReservationService {

    private final ReservationService delegate;

    protected ReservationServiceDecorator(ReservationService delegate) {
        this.delegate = delegate;
    }

    @Override
    public Reservation reserveFirstAvailableSlot(String userEmail) {
        return delegate.reserveFirstAvailableSlot(userEmail);
    }

    @Override
    public void cancelReservation(Long reservationId) {
        delegate.cancelReservation(reservationId);
    }
}
