package com.recipe.app.src.common.application.dto;

import com.recipe.app.src.common.domain.AppVersion;
import lombok.Getter;

@Getter
public class AppVersionResponse {
    private final String version;

    public AppVersionResponse(AppVersion appVersion) {
        this.version = appVersion.getVersion();
    }
}
