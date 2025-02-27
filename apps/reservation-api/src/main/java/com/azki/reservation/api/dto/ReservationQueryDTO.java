package com.azki.reservation.api.dto;

import java.time.LocalDateTime;

public record ReservationQueryDTO(Long id,
                                  String userEmail,
                                  LocalDateTime startTime,
                                  LocalDateTime endTime) {
}
