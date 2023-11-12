package com.recipe.app.src.recipe.application;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.src.recipe.application.port.SearchKeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchKeywordService {

    private final SearchKeywordRepository searchKeywordRepository;

    public List<String> retrieveRecipesBestKeyword() throws BaseException {
        Random random = new Random();
        List<String> recipeKeywords = searchKeywordRepository.findAll();

        List<String> keywords = new ArrayList<>();

        int cnt = 0;
        while (keywords.size() < 10) {
            long seed = LocalDateTime.now().withMinute(cnt).withSecond(0).withNano(0).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            random.setSeed(seed);

            int idx = random.nextInt(recipeKeywords.size());
            String keyword = recipeKeywords.get(idx);
            if (!keywords.contains(keyword)) {
                keywords.add(keyword);
            }
            cnt++;
        }

        return keywords;
    }
}