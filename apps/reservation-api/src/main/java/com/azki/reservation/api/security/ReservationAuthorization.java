package com.azki.reservation.api.security;

import com.azki.reservation.api.data.entity.Reservation;
import com.azki.reservation.api.data.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class ReservationAuthorization {

    public boolean decide(Reservation reservation) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        return reservation.getUser().equals(user);
    }
}
