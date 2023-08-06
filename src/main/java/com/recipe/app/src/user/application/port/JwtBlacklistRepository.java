package com.recipe.app.src.user.application.port;

import java.util.Optional;

public interface JwtBlacklistRepository {
    Optional<String> findById(String id);

    void save(String jwt);
}
