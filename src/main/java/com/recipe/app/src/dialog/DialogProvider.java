package com.recipe.app.src.dialog;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.src.dialog.models.GetDialogRes;
import com.recipe.app.src.dialog.models.Dialog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.recipe.app.common.response.BaseResponseStatus.NOT_FOUND_NOTICE;

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
        Dialog dialog = dialogRepository.findFirstByActiveYn("Y");

        if(dialog == null)
            throw new BaseException(NOT_FOUND_NOTICE);

        return new GetDialogRes(dialog.getIdx(), dialog.getTitle(), dialog.getContent(), dialog.getLink());
    }
}
