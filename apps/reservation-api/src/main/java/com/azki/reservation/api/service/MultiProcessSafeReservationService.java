package com.azki.reservation.api.service;

import com.azki.reservation.api.data.entity.Reservation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.stereotype.Service;

import java.util.concurrent.locks.Lock;

@Primary
@Service
@Slf4j
public class MultiProcessSafeReservationService extends ReservationServiceDecorator {

    private final LockRegistry lockRegistry;

    public MultiProcessSafeReservationService(@Qualifier("defaultReservationService")
                                              ReservationService delegate,
                                              LockRegistry lockRegistry) {
        super(delegate);
        this.lockRegistry = lockRegistry;
    }

    @Override
    public Reservation reserveFirstAvailableSlot(String userEmail) {
        Lock lock = lockRegistry.obtain("reservationProcess");
        lock.lock();
        log.debug("Acquired lock {}", lock);
        try {
            return super.reserveFirstAvailableSlot(userEmail);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw ex;
        } finally {
            log.debug("Releasing lock");
            lock.unlock();
        }
    }
}
