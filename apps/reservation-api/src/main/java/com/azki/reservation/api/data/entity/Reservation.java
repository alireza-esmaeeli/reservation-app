package com.azki.reservation.api.data.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reservations")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseEntity {

    @OneToOne(optional = false, cascade = CascadeType.MERGE)
    private AvailableSlot availableSlot;
    @ManyToOne(optional = false)
    private User user;

    public Reservation(@NonNull AvailableSlot availableSlot,
                       @NonNull User user) {

        if (availableSlot.isReserved()) {
            throw new IllegalStateException("Slot is reserved");
        }

        availableSlot.setReserved(true);
        user.addReservation(this);

        this.availableSlot = availableSlot;
        this.user = user;
    }

    @PreRemove
    void preRemove() {
        availableSlot.setReserved(false);
        user.removeReservation(this);
    }
}
