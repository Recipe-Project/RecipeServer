package com.recipe.app.src.appVersion;

import com.recipe.app.src.appVersion.models.AppVersion;
import org.springframework.data.repository.CrudRepository;

public interface AppVersionRepositiory extends CrudRepository<AppVersion, Integer> {
    AppVersion findByIdx(Integer idx);
}
