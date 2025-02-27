package com.azki.reservation.api.exception;

public class ReservationNotFoundException extends ResourceNotFoundException {
    public ReservationNotFoundException() {
        super("Reservation not found");
    }

    public ReservationNotFoundException(String message) {
        super(message);
    }
}
