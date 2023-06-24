package com.recipe.app.src.common.application;

import com.recipe.app.src.common.domain.AppVersion;
import com.recipe.app.src.common.mapper.AppVersionRepositiory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppVersionService {
    private final AppVersionRepositiory appVersionRepositiory;

    @Transactional
    public AppVersion retrieveAppVersion() {
        return appVersionRepositiory.findFirstAppVersionOrderByIdx();
    }
}
