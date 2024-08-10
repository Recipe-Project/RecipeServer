package com.recipe.app.src.etc.api;

import com.recipe.app.src.etc.application.dto.DialogResponse;
import com.recipe.app.src.etc.application.DialogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dialog")
public class DialogController {

    private final DialogService dialogService;

    public DialogController(DialogService dialogService) {
        this.dialogService = dialogService;
    }

    @GetMapping
    public DialogResponse getDialog() {

        return dialogService.findDialog();
    }
}
