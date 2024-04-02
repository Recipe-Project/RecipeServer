package com.recipe.app.src.user.infra;

import com.recipe.app.src.user.domain.JwtBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JwtBlacklistRepository extends JpaRepository<JwtBlacklist, String> {
}
