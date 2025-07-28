package com.recipe.app.src.appVersion.infra;

import com.recipe.app.src.appVersion.domain.AppVersion;

public interface AppVersionCustomRepository {

    AppVersion findRecentAppVersion();
}
