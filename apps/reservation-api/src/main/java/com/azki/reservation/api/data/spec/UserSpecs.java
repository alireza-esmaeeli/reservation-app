package com.azki.reservation.api.data.spec;

import com.azki.reservation.api.data.entity.User;
import org.springframework.data.jpa.domain.Specification;

public abstract class UserSpecs {
    public static Specification<User> hasEmail(String email) {
        return (root, query, cb) -> cb.equal(root.get(User.Fields.email), email);
    }

    public static Specification<User> hasUsername(String username) {
        return (root, query, cb) -> cb.equal(root.get(User.Fields.username), username);
    }
}
