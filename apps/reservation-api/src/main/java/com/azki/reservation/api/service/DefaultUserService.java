package com.azki.reservation.api.service;

import com.azki.reservation.api.data.entity.User;
import com.azki.reservation.api.data.repository.UserRepository;
import com.azki.reservation.api.data.spec.UserSpecs;
import com.azki.reservation.api.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;

    @Override
    public User findByEmail(String email) {
        return userRepository.findOne(Specification.where(UserSpecs.hasEmail(email)))
                .orElseThrow(UserNotFoundException::new);
    }
}
