package com.recipe.app.src.etc.application;

import com.recipe.app.src.etc.application.dto.AppVersionResponse;
import com.recipe.app.src.etc.infra.AppVersionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppVersionService {
    private final AppVersionRepository appVersionRepository;

    public AppVersionService(AppVersionRepository appVersionRepository) {
        this.appVersionRepository = appVersionRepository;
    }

    @Transactional(readOnly = true)
    public AppVersionResponse findAppVersion() {

        return new AppVersionResponse(appVersionRepository.findRecentAppVersion());
    }
}
