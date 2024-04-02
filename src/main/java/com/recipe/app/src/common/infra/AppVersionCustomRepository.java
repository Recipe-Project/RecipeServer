package com.recipe.app.src.common.infra;

import com.recipe.app.src.common.domain.AppVersion;

public interface AppVersionCustomRepository {

    AppVersion findRecentAppVersion();
}
