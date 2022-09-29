package com.recipe.app.src.dialog;

import com.recipe.app.config.BaseException;
import com.recipe.app.src.dialog.models.GetDialogRes;
import com.recipe.app.src.dialog.models.Dialog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.recipe.app.config.BaseResponseStatus.FAILED_TO_GET_NOTICE;
import static com.recipe.app.config.BaseResponseStatus.NOT_FOUND_NOTICE;

@Service
public class DialogProvider {
    private final DialogRepository dialogRepository;

    @Autowired
    public DialogProvider(DialogRepository dialogRepository) {
        this.dialogRepository = dialogRepository;
    }

    /**
     * 다이얼로그 조회 API
     * @param
     * @return GetDialogRes
     * @throws BaseException
     */
    public GetDialogRes retrieveDialog() throws BaseException {
        Dialog dialog;
        try {
            dialog = dialogRepository.findFirstByActiveYn("Y");
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_NOTICE);
        }
        if(dialog == null)
            throw new BaseException(NOT_FOUND_NOTICE);
        return new GetDialogRes(dialog.getTitle(), dialog.getContent(), dialog.getLink());
    }
}
