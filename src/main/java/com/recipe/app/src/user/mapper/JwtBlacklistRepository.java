package com.recipe.app.src.user.mapper;

import com.recipe.app.src.user.domain.Jwt;
import org.springframework.data.repository.CrudRepository;

public interface JwtBlacklistRepository extends CrudRepository<Jwt, String> {
}
