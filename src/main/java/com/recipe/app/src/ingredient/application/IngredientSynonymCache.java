package com.recipe.app.src.ingredient.application;

import com.recipe.app.src.ingredient.domain.IngredientSynonym;
import com.recipe.app.src.ingredient.infra.IngredientSynonymRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * IngredientSynonym 테이블의 동의어 그룹을 메모리 캐시로 들고있다가 단어 -> 그룹 전체 단어 집합을 반환한다.
 * 운영 중 DB 에 새 동의어를 추가했다면 인스턴스 재시작 또는 reload() 호출 필요.
 */
@Slf4j
@Component
public class IngredientSynonymCache {

    private final IngredientSynonymRepository ingredientSynonymRepository;
    private volatile Map<String, Set<String>> expansionMap = Collections.emptyMap();

    public IngredientSynonymCache(IngredientSynonymRepository ingredientSynonymRepository) {
        this.ingredientSynonymRepository = ingredientSynonymRepository;
    }

    @PostConstruct
    public void reload() {

        List<IngredientSynonym> all = ingredientSynonymRepository.findAll();
        Map<Long, Set<String>> byGroup = all.stream()
                .collect(Collectors.groupingBy(
                        IngredientSynonym::getGroupId,
                        Collectors.mapping(IngredientSynonym::getName, Collectors.toUnmodifiableSet())
                ));

        Map<String, Set<String>> map = new HashMap<>();
        for (Set<String> group : byGroup.values()) {
            for (String name : group) {
                map.put(name, group);
            }
        }
        this.expansionMap = Map.copyOf(map);

        log.info("IngredientSynonymCache loaded - groups={}, words={}", byGroup.size(), all.size());
    }

    /**
     * 입력 단어들과 그 동의어들을 모두 합친 Set 반환. 동의어 그룹에 없는 단어는 자기 자신만 들어감.
     */
    public Set<String> expand(Collection<String> names) {

        if (names == null || names.isEmpty()) return Set.of();

        Set<String> result = new HashSet<>();
        for (String name : names) {
            if (name == null) continue;
            result.add(name);
            Set<String> group = expansionMap.get(name);
            if (group != null) result.addAll(group);
        }
        return result;
    }
}
