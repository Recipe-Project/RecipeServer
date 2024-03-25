package com.recipe.app.src.common.application;

import com.recipe.app.src.common.domain.BadWords;
import com.recipe.app.src.common.exception.BadWordException;
import com.recipe.app.src.common.infra.BadWordsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BadWordService {

    private final BadWordsRepository badWordsRepository;

    public void checkBadWords(String keyword) {
        List<String> badWords = badWordsRepository.findByWordContaining(keyword).stream()
                .map(BadWords::getWord)
                .collect(Collectors.toList());
        if (badWords.size() > 0)
            throw new BadWordException(badWords.toString());
    }
}
