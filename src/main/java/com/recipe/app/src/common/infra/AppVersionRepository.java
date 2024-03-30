package com.recipe.app.src.common.infra;

import com.recipe.app.src.common.domain.AppVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AppVersionRepository extends JpaRepository<AppVersion, Long> {

    @Query(value = "select av from AppVersion av order by av.idx limit 1", nativeQuery = true)
    AppVersion findFirstAppVersionOrderByIdx();
}
