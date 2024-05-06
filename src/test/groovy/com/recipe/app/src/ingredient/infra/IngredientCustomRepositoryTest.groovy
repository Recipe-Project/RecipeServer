package com.recipe.app.src.ingredient.infra

import com.recipe.app.src.ingredient.domain.Ingredient
import com.recipe.app.src.ingredient.domain.IngredientCategory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import spock.lang.Specification

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class IngredientCustomRepositoryTest extends Specification {

    @Autowired
    IngredientCategoryRepository ingredientCategoryRepository;
    @Autowired
    IngredientRepository ingredientRepository;

    def "검색어로 기본 재료 목록 조회"() {

        given:
        IngredientCategory ingredientCategory = IngredientCategory.builder()
                .ingredientCategoryName("과일")
                .build();
        ingredientCategoryRepository.save(ingredientCategory);

        List<Ingredient> ingredients = [
                Ingredient.builder()
                        .userId(1L)
                        .ingredientCategoryId(ingredientCategory.ingredientCategoryId)
                        .ingredientName("사과")
                        .ingredientIconId(10000)
                        .build(),
                Ingredient.builder()
                        .userId(1L)
                        .ingredientCategoryId(ingredientCategory.ingredientCategoryId)
                        .ingredientName("체리")
                        .ingredientIconId(10000)
                        .build(),
                Ingredient.builder()
                        .userId(1L)
                        .ingredientCategoryId(ingredientCategory.ingredientCategoryId)
                        .ingredientName("블루베리")
                        .ingredientIconId(10000)
                        .build(),
        ]
        ingredientRepository.saveAll(ingredients);

        when:
        List<Ingredient> response = ingredientRepository.findDefaultIngredientsByKeyword(1L, "리");

        then:
        response.size() == 2
    }

    def "기본 재료 목록 조회"() {

        given:
        IngredientCategory ingredientCategory = IngredientCategory.builder()
                .ingredientCategoryName("과일")
                .build();
        ingredientCategoryRepository.save(ingredientCategory);

        List<Ingredient> ingredients = [
                Ingredient.builder()
                        .userId(1L)
                        .ingredientCategoryId(ingredientCategory.ingredientCategoryId)
                        .ingredientName("사과")
                        .ingredientIconId(10000)
                        .build(),
                Ingredient.builder()
                        .userId(1L)
                        .ingredientCategoryId(ingredientCategory.ingredientCategoryId)
                        .ingredientName("포도")
                        .ingredientIconId(10000)
                        .build(),
                Ingredient.builder()
                        .userId(1L)
                        .ingredientCategoryId(ingredientCategory.ingredientCategoryId)
                        .ingredientName("귤")
                        .ingredientIconId(10000)
                        .build(),
        ]
        ingredientRepository.saveAll(ingredients);

        when:
        List<Ingredient> response = ingredientRepository.findDefaultIngredients(1L);

        then:
        response.size() == 3
    }
}
