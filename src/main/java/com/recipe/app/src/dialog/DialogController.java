package com.recipe.app.src.dialog;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.common.response.BaseResponse;
import com.recipe.app.src.dialog.models.GetDialogRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.recipe.app.common.response.BaseResponse.success;

@RestController
@RequestMapping("/dialog")
public class DialogController {
    private final DialogProvider dialogProvider;

    @Autowired
    public DialogController(DialogProvider dialogProvider) {
        this.dialogProvider = dialogProvider;
    }

    /**
     * 다이얼로그 조회 API
     * [GET] /dialog
     * @return BaseResponse<GetDialogRes>
     */
    @GetMapping("")
    public BaseResponse<GetDialogRes> getDialog() {
        GetDialogRes getDialogRes = dialogProvider.retrieveDialog();
        return success(getDialogRes);
    }
}
