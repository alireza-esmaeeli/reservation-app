package com.azki.reservation.api.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ReservationReadDTO(Long reservationId,
                                 String email,
                                 LocalDateTime startTime,
                                 LocalDateTime endTime) {
}
