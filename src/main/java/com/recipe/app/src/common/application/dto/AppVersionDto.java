package com.recipe.app.src.common.application.dto;

import com.recipe.app.src.common.domain.AppVersion;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppVersionDto {
    private String version;

    public AppVersionDto(AppVersion appVersion) {
        this.version = appVersion.getVersion();
    }
}
