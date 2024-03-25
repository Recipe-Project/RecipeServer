package com.recipe.app.src.common.infra;

import com.recipe.app.src.common.domain.AppVersion;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface AppVersionRepositiory extends CrudRepository<AppVersion, Integer> {

    @Query(value = "select av from AppVersion av order by av.idx limit 1", nativeQuery = true)
    AppVersion findFirstAppVersionOrderByIdx();
}
