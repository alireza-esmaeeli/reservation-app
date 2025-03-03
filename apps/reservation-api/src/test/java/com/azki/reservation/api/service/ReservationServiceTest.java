package com.azki.reservation.api.service;

import com.azki.reservation.api.data.entity.AvailableSlot;
import com.azki.reservation.api.data.entity.Reservation;
import com.azki.reservation.api.data.entity.User;
import com.azki.reservation.api.data.repository.AvailableSlotRepository;
import com.azki.reservation.api.data.repository.UserRepository;
import com.redis.testcontainers.RedisContainer;
import lombok.extern.slf4j.Slf4j;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Assign.valueOf;
import static org.instancio.Select.field;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext
@Slf4j
class ReservationServiceTest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mysqlContainer =
            new MySQLContainer<>("mysql:lts");

    @Container
    @ServiceConnection
    static RedisContainer redisContainer =
            new RedisContainer("redis:7.4.2-alpine");

    @Autowired
    private ReservationService sut;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AvailableSlotRepository availableSlotRepository;

    private static final ExecutorService executorService = Executors.newFixedThreadPool(8);

    @AfterAll
    static void tearDown() {
        executorService.shutdown();
    }

    @Test
    void reserveFirstAvailableSlot_whenSameNumberOfSlotsAndUsers_reservesAllSlots()
            throws InterruptedException {

        var now = LocalDateTime.now();

        var availableSlots = IntStream.range(0, 8)
                .mapToObj(i ->
                        Instancio.of(AvailableSlot.class)
                                .ignore(field(User::getId))
                                .ignore(field(User::getCreatedAt))
                                .set(field(AvailableSlot::isReserved), false)
                                .generate(field(AvailableSlot::getStartTime),
                                        gen -> gen.temporal().localDateTime().range(now, now.plusHours(8)))
                                .assign(valueOf(AvailableSlot::getStartTime)
                                        .to(field(AvailableSlot::getEndTime))
                                        .as((LocalDateTime start) -> start.plusHours(1)))
                                .create()
                )
                .collect(Collectors.toList());

        var users = IntStream.range(0, 8)
                .mapToObj(i ->
                        Instancio.of(User.class)
                                .ignore(field(User::getId))
                                .ignore(field(User::getCreatedAt))
                                .ignore(field("reservations"))
                                .generate(field(User::getEmail), gen -> gen.net().email())
                                .create()
                )
                .collect(Collectors.toList());

        availableSlotRepository.saveAllAndFlush(availableSlots);
        userRepository.saveAllAndFlush(users);

        List<Reservation> reservations = new ArrayList<>();
        var countDownLatch = new CountDownLatch(8);

        List<Callable<Reservation>> tasks =
                users.stream()
                        .map(user -> (Callable<Reservation>) () -> {
                            log.info("Starting reservation for user {}", user.getId());
                            try {
                                Reservation reservation = sut.reserveFirstAvailableSlot(user.getEmail());
                                reservations.add(reservation);
                                return reservation;
                            } finally {
                                countDownLatch.countDown();
                            }
                        })
                        .collect(Collectors.toList());

        executorService.invokeAll(tasks);

        countDownLatch.await();

        List<AvailableSlot> reservedSlots = reservations.stream()
                .map(Reservation::getAvailableSlot)
                .toList();


        assertThat(reservedSlots).hasSameElementsAs(availableSlots);
    }

    @Test
    void reserveFirstAvailableSlot_whenSameUserTryForAllSlots_reservesAllSlots()
            throws InterruptedException {

        var now = LocalDateTime.now();

        var availableSlots = IntStream.range(0, 8)
                .mapToObj(i ->
                        Instancio.of(AvailableSlot.class)
                                .ignore(field(User::getId))
                                .ignore(field(User::getCreatedAt))
                                .set(field(AvailableSlot::isReserved), false)
                                .generate(field(AvailableSlot::getStartTime),
                                        gen -> gen.temporal().localDateTime().range(now, now.plusHours(8)))
                                .assign(valueOf(AvailableSlot::getStartTime)
                                        .to(field(AvailableSlot::getEndTime))
                                        .as((LocalDateTime start) -> start.plusHours(1)))
                                .create()
                )
                .collect(Collectors.toList());

        var user = Instancio.of(User.class)
                .ignore(field(User::getId))
                .ignore(field(User::getCreatedAt))
                .ignore(field("reservations"))
                .generate(field(User::getEmail), gen -> gen.net().email())
                .create();

        availableSlotRepository.saveAllAndFlush(availableSlots);
        userRepository.saveAndFlush(user);

        List<Reservation> reservations = new ArrayList<>();
        var countDownLatch = new CountDownLatch(8);

        List<Callable<Reservation>> tasks =
                IntStream.range(0, 8)
                        .mapToObj(i -> (Callable<Reservation>) () -> {
                            log.info("Starting reservation for user {}", user.getId());
                            try {
                                Reservation reservation = sut.reserveFirstAvailableSlot(user.getEmail());
                                reservations.add(reservation);
                                return reservation;
                            } finally {
                                countDownLatch.countDown();
                            }
                        })
                        .collect(Collectors.toList());


        executorService.invokeAll(tasks);

        countDownLatch.await();

        List<AvailableSlot> reservedSlots = reservations.stream()
                .map(Reservation::getAvailableSlot)
                .toList();

        assertThat(reservedSlots).hasSameElementsAs(availableSlots);
    }
}