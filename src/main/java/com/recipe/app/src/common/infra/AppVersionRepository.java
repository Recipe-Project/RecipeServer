package com.recipe.app.src.common.infra;

import com.recipe.app.src.common.domain.AppVersion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppVersionRepository extends JpaRepository<AppVersion, Long>, AppVersionCustomRepository {
}
