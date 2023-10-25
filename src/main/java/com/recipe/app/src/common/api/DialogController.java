package com.recipe.app.src.common.api;

import com.recipe.app.common.response.BaseResponse;
import com.recipe.app.src.common.application.dto.DialogDto;
import com.recipe.app.src.common.application.DialogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.recipe.app.common.response.BaseResponse.success;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dialog")
public class DialogController {
    private final DialogService dialogService;

    @GetMapping("")
    public BaseResponse<DialogDto> getDialog() {
        DialogDto data = new DialogDto(dialogService.retrieveDialog());

        return success(data);
    }
}