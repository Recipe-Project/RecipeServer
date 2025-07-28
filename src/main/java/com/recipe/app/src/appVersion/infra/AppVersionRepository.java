package com.recipe.app.src.appVersion.infra;

import com.recipe.app.src.appVersion.domain.AppVersion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppVersionRepository extends JpaRepository<AppVersion, Long>, AppVersionCustomRepository {
}
