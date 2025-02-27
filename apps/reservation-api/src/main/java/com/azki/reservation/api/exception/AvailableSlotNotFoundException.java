package com.azki.reservation.api.exception;

public class AvailableSlotNotFoundException extends ResourceNotFoundException {
    public AvailableSlotNotFoundException() {
        super("Available slot not found");
    }

    public AvailableSlotNotFoundException(String message) {
        super(message);
    }
}
