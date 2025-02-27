package com.azki.reservation.api.data.entity;

import org.instancio.Instancio;
import org.instancio.support.DefaultRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;

@Testcontainers
@DataJpaTest
@DirtiesContext
class ReservationTest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mysqlContainer =
            new MySQLContainer<>("mysql:lts");

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void create_whenSlotReserved_shouldThrowException() {
        // given
        var user = Instancio.of(User.class)
                .ignore(field(User::getId))
                .ignore(field(User::getCreatedAt))
                .ignore(field("reservations"))
                .create();

        var availableSlot = Instancio.of(AvailableSlot.class)
                .ignore(field(AvailableSlot::getId))
                .ignore(field(AvailableSlot::getCreatedAt))
                .set(field(AvailableSlot::isReserved), true)
                .create();

        // then
        assertThatThrownBy(() -> new Reservation(availableSlot, user))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Slot is reserved");
    }

    @Test
    void create_whenSlotNotReserved_shouldReserveSlot() {
        // given
        var user = Instancio.of(User.class)
                .ignore(field(User::getId))
                .ignore(field(User::getCreatedAt))
                .ignore(field("reservations"))
                .create();

        var availableSlot = Instancio.of(AvailableSlot.class)
                .ignore(field(AvailableSlot::getId))
                .ignore(field(AvailableSlot::getCreatedAt))
                .set(field(AvailableSlot::isReserved), false)
                .create();

        entityManager.persist(availableSlot);
        entityManager.persist(user);
        entityManager.flush();

        // when
        var reservation = new Reservation(availableSlot, user);

        entityManager.persist(reservation);
        entityManager.flush();
        entityManager.clear();

        // then
        var persistedSlot = entityManager.find(AvailableSlot.class, availableSlot.getId());
        assertThat(persistedSlot.isReserved()).isTrue();
    }

    @Test
    void remove_whenSlotReserved_shouldFreeSlot() {
        // given
        var user = Instancio.of(User.class)
                .ignore(field(User::getId))
                .ignore(field(User::getCreatedAt))
                .ignore(field("reservations"))
                .create();

        var availableSlot = Instancio.of(AvailableSlot.class)
                .ignore(field(AvailableSlot::getId))
                .ignore(field(AvailableSlot::getCreatedAt))
                .set(field(AvailableSlot::isReserved), true)
                .create();

        var reservation = Instancio.of(Reservation.class)
                .ignore(field(AvailableSlot::getId))
                .ignore(field(AvailableSlot::getCreatedAt))
                .set(field(Reservation::getUser), user)
                .set(field(Reservation::getAvailableSlot), availableSlot)
                .create();

        entityManager.persist(availableSlot);
        entityManager.persist(user);
        entityManager.persist(reservation);
        entityManager.flush();

        // when
        entityManager.remove(reservation);
        entityManager.flush();
        entityManager.clear();

        // then
        var updatedSlot = entityManager.find(AvailableSlot.class, availableSlot.getId());
        assertThat(updatedSlot.isReserved()).isFalse();
    }

    @Test
    void create_shouldBeAddedToUserReservations() {

        // given
        var availableSlots = IntStream.range(0, 5)
                .mapToObj(i ->
                        Instancio.of(AvailableSlot.class)
                                .ignore(field(AvailableSlot::getId))
                                .ignore(field(AvailableSlot::getCreatedAt))
                                .set(field(AvailableSlot::isReserved), false)
                                .create()
                )
                .toList();

        final var user = Instancio.of(User.class)
                .ignore(field(User::getId))
                .ignore(field(User::getCreatedAt))
                .ignore(field("reservations"))
                .create();

        availableSlots.forEach(entityManager::persist);
        entityManager.persist(user);
        entityManager.flush();

        // when
        var reservations = availableSlots.stream()
                .map(availableSlot -> new Reservation(availableSlot, user))
                .toList();

        reservations.forEach(entityManager::persist);
        entityManager.flush();
        entityManager.clear();

        // then
        var persistedUser = entityManager.find(User.class, user.getId());
        assertThat(persistedUser.getReservations()).containsExactlyInAnyOrderElementsOf(reservations);
    }

    @Test
    void remove_whenInUserReservations_shouldRemoveFromUserReservations() {
        // given
        final var user = Instancio.of(User.class)
                .ignore(field(User::getId))
                .ignore(field(User::getCreatedAt))
                .ignore(field("reservations"))
                .create();

        var availableSlots = IntStream.range(0, 5)
                .mapToObj(i ->
                        Instancio.of(AvailableSlot.class)
                                .ignore(field(AvailableSlot::getId))
                                .ignore(field(AvailableSlot::getCreatedAt))
                                .set(field(AvailableSlot::isReserved), true)
                                .create()
                )
                .toList();

        var reservations = availableSlots.stream()
                .map(availableSlot ->
                        Instancio.of(Reservation.class)
                                .ignore(field(AvailableSlot::getId))
                                .ignore(field(AvailableSlot::getCreatedAt))
                                .set(field(Reservation::getUser), user)
                                .set(field(Reservation::getAvailableSlot), availableSlot)
                                .create()
                )
                .toList();

        entityManager.persist(user);
        availableSlots.forEach(entityManager::persist);
        reservations.forEach(entityManager::persist);
        entityManager.flush();

        // when
        var random = new DefaultRandom();
        var reservation = reservations.get(random.intRange(0, 4));
        entityManager.remove(reservation);
        entityManager.flush();
        entityManager.clear();

        // then
        var persistedUser = entityManager.find(User.class, user.getId());
        assertThat(persistedUser.getReservations()).doesNotContain(reservation);
    }
}