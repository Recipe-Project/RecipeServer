package com.recipe.app.src.etc.infra;

import com.recipe.app.src.etc.domain.AppVersion;

public interface AppVersionCustomRepository {

    AppVersion findRecentAppVersion();
}
