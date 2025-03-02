package com.azki.reservation.api.data.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true)
@Getter
@FieldNameConstants
public class User extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String username;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    @ToString.Exclude
    private String password;
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<Reservation> reservations = new HashSet<>();

    public User(User user) {
        super(user.getId(), user.getCreatedAt());
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.reservations = user.getReservations();
    }

    public Set<Reservation> getReservations() {
        return Collections.unmodifiableSet(reservations);
    }

    void addReservation(Reservation reservation) {
        reservations.add(reservation);
    }

    void removeReservation(Reservation reservation) {
        reservations.remove(reservation);
    }
}
