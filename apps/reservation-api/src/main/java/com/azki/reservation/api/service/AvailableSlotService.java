package com.azki.reservation.api.service;

import com.azki.reservation.api.data.entity.AvailableSlot;

public interface AvailableSlotService {
    AvailableSlot findFirstAvailableSlot();
}
