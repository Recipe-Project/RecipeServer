package com.recipe.app.src.appVersion.application.dto;

import com.recipe.app.src.appVersion.domain.AppVersion;
import lombok.Getter;

@Getter
public class AppVersionResponse {
    private final String version;

    public AppVersionResponse(AppVersion appVersion) {
        this.version = appVersion.getVersion();
    }
}
