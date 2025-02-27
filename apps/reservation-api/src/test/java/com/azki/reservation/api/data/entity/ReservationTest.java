package com.azki.reservation.api.data.entity;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

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
}