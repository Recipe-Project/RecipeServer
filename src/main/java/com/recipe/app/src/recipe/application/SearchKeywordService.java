package com.recipe.app.src.recipe.application;

import com.recipe.app.src.recipe.domain.SearchKeyword;
import com.recipe.app.src.recipe.infra.keyword.SearchKeywordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class SearchKeywordService {

    private final SearchKeywordRepository searchKeywordRepository;

    public SearchKeywordService(SearchKeywordRepository searchKeywordRepository) {
        this.searchKeywordRepository = searchKeywordRepository;
    }

    @Transactional(readOnly = true)
    public List<String> retrieveRecipesBestKeyword() {
        Random random = new Random();
        List<String> recipeKeywords = searchKeywordRepository.findAll().stream()
                .map(SearchKeyword::getKeyword)
                .collect(Collectors.toList());

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