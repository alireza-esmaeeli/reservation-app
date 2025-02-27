package com.azki.reservation.api.data.repository;

import com.azki.reservation.api.data.entity.AvailableSlot;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.instancio.Assign.valueOf;
import static org.instancio.Select.field;

@Testcontainers
@DataJpaTest
class AvailableSlotRepositoryTest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mysqlContainer =
            new MySQLContainer<>("mysql:lts");

    @Autowired
    private AvailableSlotRepository sut;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldReturnAllAvailableSlots() {
        var availableSlots = IntStream.range(0, 5)
                .mapToObj(i ->
                        Instancio.of(AvailableSlot.class)
                                .ignore(field(AvailableSlot::getId))
                                .ignore(field(AvailableSlot::getCreatedAt))
                                .set(field(AvailableSlot::isReserved), false)
                                .assign(valueOf(AvailableSlot::getStartTime)
                                        .to(field(AvailableSlot::getEndTime))
                                        .as((LocalDateTime start) -> start.plusHours(1)))
                                .create()
                )
                .toList();
        availableSlots.forEach(entityManager::persist);
        availableSlots.forEach(System.out::println);
        entityManager.flush();

        AvailableSlot availableSlot = sut.findFirstNotReserved().orElseThrow();
        System.out.println("availableSlot = " + availableSlot);
    }
}