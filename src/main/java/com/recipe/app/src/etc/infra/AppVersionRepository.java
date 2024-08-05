package com.recipe.app.src.etc.infra;

import com.recipe.app.src.etc.domain.AppVersion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppVersionRepository extends JpaRepository<AppVersion, Long>, AppVersionCustomRepository {
}
