package com.azki.reservation.api.service;

import com.azki.reservation.api.data.entity.User;

public interface UserService {
    User findByEmail(String email);
}
