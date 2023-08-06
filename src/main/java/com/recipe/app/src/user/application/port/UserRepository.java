package com.recipe.app.src.user.application.port;

import com.recipe.app.src.user.domain.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findBySocialId(String socialId);

    Optional<User> findById(Long id);

    User save(User user);

    void delete(User user);
}
