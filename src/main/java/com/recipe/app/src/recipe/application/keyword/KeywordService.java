package com.recipe.app.src.recipe.application.keyword;

import com.recipe.app.src.recipe.domain.keyword.Keyword;
import com.recipe.app.src.recipe.infra.keyword.KeywordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class KeywordService {

    private final KeywordRepository keywordRepository;

    public KeywordService(KeywordRepository keywordRepository) {
        this.keywordRepository = keywordRepository;
    }

    private static final int BEST_KEYWORD_COUNT = 10;

    @Transactional(readOnly = true)
    public List<String> retrieveRecipesBestKeyword() {
        List<String> recipeKeywords = keywordRepository.findAll().stream()
                .map(Keyword::getKeyword)
                .distinct()
                .collect(Collectors.toList());

        // 검색어가 없으면 빈 목록 (random.nextInt(0) 예외 방지)
        if (recipeKeywords.isEmpty()) {
            return List.of();
        }

        // 고유 검색어 수가 10개 미만이어도 끝나도록 목표치를 보유 수로 제한 (무한 루프 방지)
        int target = Math.min(BEST_KEYWORD_COUNT, recipeKeywords.size());

        Random random = new Random();
        List<String> keywords = new ArrayList<>();
        int cnt = 0;
        while (keywords.size() < target) {
            long seed = LocalDateTime.now().withMinute(cnt % 60).withSecond(0).withNano(0).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            random.setSeed(seed);

            String keyword = recipeKeywords.get(random.nextInt(recipeKeywords.size()));
            if (!keywords.contains(keyword)) {
                keywords.add(keyword);
            }
            cnt++;
        }

        return keywords;
    }
}
