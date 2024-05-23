package com.recipe.app.src.common.application;

import com.recipe.app.src.common.domain.BadWord;
import com.recipe.app.src.common.exception.BadWordException;
import com.recipe.app.src.common.infra.BadWordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BadWordService {

    private final BadWordRepository badWordRepository;

    public BadWordService(BadWordRepository badWordRepository) {
        this.badWordRepository = badWordRepository;
    }

    @Transactional(readOnly = true)
    public void checkBadWords(String keyword) {

        List<BadWord> badWords = badWordRepository.findAll();

        for (BadWord badWord : badWords) {
            if (keyword.contains(badWord.getWord())) {
                throw new BadWordException(badWord.getWord());
            }
        }
    }
}
