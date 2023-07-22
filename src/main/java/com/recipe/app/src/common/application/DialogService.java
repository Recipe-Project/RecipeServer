package com.recipe.app.src.common.application;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.src.common.application.dto.DialogDto;
import com.recipe.app.src.common.domain.Dialog;
import com.recipe.app.src.common.exception.NotFoundNoticeException;
import com.recipe.app.src.common.mapper.DialogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.recipe.app.common.response.BaseResponseStatus.NOT_FOUND_NOTICE;

@Service
public class DialogService {
    private final DialogRepository dialogRepository;

    @Autowired
    public DialogService(DialogRepository dialogRepository) {
        this.dialogRepository = dialogRepository;
    }

    public Dialog retrieveDialog() {
        return dialogRepository.findFirstByActiveYn("Y").orElseThrow(NotFoundNoticeException::new);
    }
}
