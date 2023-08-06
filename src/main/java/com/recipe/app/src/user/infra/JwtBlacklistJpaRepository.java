package com.recipe.app.src.user.infra;

import org.springframework.data.repository.CrudRepository;

public interface JwtBlacklistJpaRepository extends CrudRepository<JwtEntity, String> {
}
