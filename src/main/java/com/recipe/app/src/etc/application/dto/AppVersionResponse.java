package com.recipe.app.src.etc.application.dto;

import com.recipe.app.src.etc.domain.AppVersion;
import lombok.Getter;

@Getter
public class AppVersionResponse {
    private final String version;

    public AppVersionResponse(AppVersion appVersion) {
        this.version = appVersion.getVersion();
    }
}
