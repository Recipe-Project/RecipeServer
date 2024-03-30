package com.recipe.app.src.common.application;

import com.recipe.app.src.common.application.dto.AppVersionResponse;
import com.recipe.app.src.common.infra.AppVersionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppVersionService {
    private final AppVersionRepository appVersionRepository;

    public AppVersionService(AppVersionRepository appVersionRepository) {
        this.appVersionRepository = appVersionRepository;
    }

    @Transactional
    public AppVersionResponse findAppVersion() {

        return new AppVersionResponse(appVersionRepository.findFirstAppVersionOrderByIdx());
    }
}
