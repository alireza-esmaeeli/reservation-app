package com.azki.reservation.api.service;

import com.azki.reservation.api.data.entity.AvailableSlot;
import com.azki.reservation.api.data.repository.AvailableSlotRepository;
import com.azki.reservation.api.data.spec.AvailableSlotSpecs;
import com.azki.reservation.api.exception.AvailableSlotNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import static com.azki.reservation.api.data.entity.AvailableSlot.Fields.startTime;
import static org.springframework.data.domain.Sort.Order;
import static org.springframework.data.domain.Sort.by;
import static org.springframework.data.jpa.domain.Specification.where;

@Service
@RequiredArgsConstructor
public class DefaultAvailableSlotService implements AvailableSlotService {

    private final AvailableSlotRepository availableSlotRepository;

    @Override
    public AvailableSlot findFirstAvailableSlot() {
        var pageRequest = PageRequest
                .ofSize(1)
                .withSort(by(Order.desc(startTime)));

        var availableSlots = availableSlotRepository
                .findAll(where(AvailableSlotSpecs.isNotReserved()), pageRequest)
                .toList();

        if (availableSlots.isEmpty()) {
            throw new AvailableSlotNotFoundException();
        }

        return availableSlots.get(0);
    }
}
