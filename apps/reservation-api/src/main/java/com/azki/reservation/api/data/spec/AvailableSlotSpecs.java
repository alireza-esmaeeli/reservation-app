package com.azki.reservation.api.data.spec;

import com.azki.reservation.api.data.entity.AvailableSlot;
import org.springframework.data.jpa.domain.Specification;

public abstract class AvailableSlotSpecs {
    public static Specification<AvailableSlot> isNotReserved() {
        return (root, query, cb) -> cb.equal(root.get(AvailableSlot.Fields.isReserved), false);
    }
}