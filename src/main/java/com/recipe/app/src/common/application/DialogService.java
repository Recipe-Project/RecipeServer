package com.recipe.app.src.common.application;

import com.recipe.app.src.common.domain.Dialog;
import com.recipe.app.src.common.exception.NotFoundNoticeException;
import com.recipe.app.src.common.infra.DialogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
