package com.recipe.app.src.etc.application;

import com.recipe.app.src.etc.application.dto.DialogResponse;
import com.recipe.app.src.etc.exception.NotFoundNoticeException;
import com.recipe.app.src.etc.infra.DialogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DialogService {
    private final DialogRepository dialogRepository;

    public DialogService(DialogRepository dialogRepository) {
        this.dialogRepository = dialogRepository;
    }

    @Transactional(readOnly = true)
    public DialogResponse findDialog() {

        return DialogResponse.from(dialogRepository.findFirstByActiveYn("Y")
                .orElseThrow(NotFoundNoticeException::new));
    }
}
