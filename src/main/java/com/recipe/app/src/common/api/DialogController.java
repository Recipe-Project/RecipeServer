package com.recipe.app.src.common.api;

import com.recipe.app.common.response.BaseResponse;
import com.recipe.app.src.common.application.dto.DialogResponse;
import com.recipe.app.src.common.application.DialogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.recipe.app.common.response.BaseResponse.success;

@RestController
@RequestMapping("/dialog")
public class DialogController {

    private final DialogService dialogService;

    public DialogController(DialogService dialogService) {
        this.dialogService = dialogService;
    }

    @GetMapping("")
    public BaseResponse<DialogResponse> getDialog() {

        return success(dialogService.findDialog());
    }
}
