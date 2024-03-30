package com.recipe.app.src.common.api;

import com.recipe.app.common.response.BaseResponse;
import com.recipe.app.src.common.application.AppVersionService;
import com.recipe.app.src.common.application.dto.AppVersionResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.recipe.app.common.response.BaseResponse.success;


@RestController
@RequestMapping("/app/version")
public class AppVersionController {

    private final AppVersionService appVersionService;

    public AppVersionController(AppVersionService appVersionService) {
        this.appVersionService = appVersionService;
    }

    @GetMapping
    public BaseResponse<AppVersionResponse> getAppVersion() {

        return success(appVersionService.findAppVersion());
    }
}
