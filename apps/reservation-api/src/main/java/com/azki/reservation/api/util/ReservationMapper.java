package com.azki.reservation.api.util;

import com.azki.reservation.api.data.entity.Reservation;
import com.azki.reservation.api.dto.ReservationReadDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ReservationMapper {
    ReservationMapper INSTANCE = Mappers.getMapper(ReservationMapper.class);

    @Mapping(target = "reservationId", source = "id")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "startTime", source = "availableSlot.startTime")
    @Mapping(target = "endTime", source = "availableSlot.endTime")
    ReservationReadDTO mapToReadDTO(Reservation reservation);
}
