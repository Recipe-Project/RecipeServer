package com.recipe.app.src.common.api;

import com.recipe.app.common.response.BaseResponse;
import com.recipe.app.src.common.application.AppVersionService;
import com.recipe.app.src.common.application.dto.AppVersionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.recipe.app.common.response.BaseResponse.success;


@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class AppVersionController {

    private final AppVersionService appVersionService;

    @GetMapping("/app/version")
    public BaseResponse<AppVersionDto> getAppVersion() {
        AppVersionDto data = new AppVersionDto(appVersionService.retrieveAppVersion());
        return success(data);
    }
}
