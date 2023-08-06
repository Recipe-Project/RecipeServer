package com.recipe.app.src.user.infra;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserJpaRepository extends CrudRepository<UserEntity, Long> {
    Optional<UserEntity> findBySocialId(String socialId);
}
