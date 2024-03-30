package com.recipe.app.src.common.application;

import com.recipe.app.src.common.application.dto.DialogResponse;
import com.recipe.app.src.common.exception.NotFoundNoticeException;
import com.recipe.app.src.common.infra.DialogRepository;
import org.springframework.stereotype.Service;

@Service
public class DialogService {
    private final DialogRepository dialogRepository;

    public DialogService(DialogRepository dialogRepository) {
        this.dialogRepository = dialogRepository;
    }

    public DialogResponse findDialog() {

        return DialogResponse.from(dialogRepository.findFirstByActiveYn("Y")
                .orElseThrow(NotFoundNoticeException::new));
    }
}
