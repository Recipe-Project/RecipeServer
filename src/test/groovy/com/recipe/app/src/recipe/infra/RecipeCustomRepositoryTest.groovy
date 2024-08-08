package com.recipe.app.src.recipe.infra

import com.recipe.app.src.recipe.domain.Recipe
import com.recipe.app.src.recipe.domain.RecipeIngredient
import com.recipe.app.src.recipe.domain.RecipeLevel
import com.recipe.app.src.recipe.domain.RecipeScrap
import com.recipe.app.src.user.domain.User
import com.recipe.app.src.user.infra.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.cloud.openfeign.FeignAutoConfiguration
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import spock.lang.Specification

@ActiveProfiles("test")
@DataJpaTest
@ImportAutoConfiguration(classes = FeignAutoConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class RecipeCustomRepositoryTest extends Specification {

    @Autowired
    UserRepository userRepository;
    @Autowired
    RecipeRepository recipeRepository;
    @Autowired
    RecipeScrapRepository recipeScrapRepository;

    private List<User> users;

    void setup() {
        users = [
                User.builder()
                        .socialId("naver_1")
                        .nickname("테스터1")
                        .build(),
                User.builder()
                        .socialId("naver_2")
                        .nickname("테스터2")
                        .build(),
        ]
        userRepository.saveAll(users);
    }

    def "레시피 상세 조회 시 공개인 경우 성공"() {

        given:
        List<Recipe> recipes = [
                Recipe.builder()
                        .recipeNm("제목")
                        .introduction("테스트설명")
                        .level(RecipeLevel.NORMAL)
                        .userId(users.get(1).userId)
                        .isHidden(false)
                        .build(),
                Recipe.builder()
                        .recipeNm("제목")
                        .introduction("설명")
                        .level(RecipeLevel.NORMAL)
                        .userId(users.get(0).userId)
                        .isHidden(true)
                        .build(),
        ]

        RecipeIngredient.builder()
                .recipe(recipes.get(0))
                .ingredientName("재료")
                .build()
        RecipeIngredient.builder()
                .recipe(recipes.get(1))
                .ingredientName("재료")
                .build()

        recipeRepository.saveAll(recipes)

        when:
        Optional<Recipe> recipe = recipeRepository.findRecipeDetail(recipes.get(0).getRecipeId(), users.get(0).getUserId())

        then:
        recipe.isPresent()
        recipe.get().recipeId == recipes.get(0).recipeId
        recipe.get().recipeNm == recipes.get(0).recipeNm
        recipe.get().introduction == recipes.get(0).introduction
        recipe.get().level == recipes.get(0).level
        recipe.get().userId == recipes.get(0).userId
        !recipe.get().isHidden()
        recipe.get().ingredients.size() == 1
    }

    def "레시피 상세 조회 시 비공개인 경우 본인이 작성한 경우에만 성공"() {

        given:
        List<Recipe> recipes = [
                Recipe.builder()
                        .recipeNm("제목")
                        .introduction("테스트설명")
                        .level(RecipeLevel.NORMAL)
                        .userId(users.get(1).userId)
                        .isHidden(false)
                        .build(),
                Recipe.builder()
                        .recipeNm("제목")
                        .introduction("설명")
                        .level(RecipeLevel.NORMAL)
                        .userId(users.get(0).userId)
                        .isHidden(true)
                        .build(),
        ]

        RecipeIngredient.builder()
                .recipe(recipes.get(0))
                .ingredientName("재료")
                .build()
        RecipeIngredient.builder()
                .recipe(recipes.get(1))
                .ingredientName("재료")
                .build()

        recipeRepository.saveAll(recipes)

        when:
        Optional<Recipe> recipe = recipeRepository.findRecipeDetail(recipes.get(1).getRecipeId(), users.get(0).getUserId())

        then:
        recipe.isPresent()
        recipe.get().recipeId == recipes.get(1).recipeId
        recipe.get().recipeNm == recipes.get(1).recipeNm
        recipe.get().introduction == recipes.get(1).introduction
        recipe.get().level == recipes.get(1).level
        recipe.get().userId == recipes.get(1).userId
        recipe.get().isHidden()
        recipe.get().ingredients.size() == 1

    }

    def "레시피 상세 조회 시 비공개인 경우 본인이 작성하지 않은 경우 실패"() {

        given:
        List<Recipe> recipes = [
                Recipe.builder()
                        .recipeNm("제목")
                        .introduction("테스트설명")
                        .level(RecipeLevel.NORMAL)
                        .userId(users.get(1).userId)
                        .isHidden(false)
                        .build(),
                Recipe.builder()
                        .recipeNm("제목")
                        .introduction("설명")
                        .level(RecipeLevel.NORMAL)
                        .userId(users.get(0).userId)
                        .isHidden(true)
                        .build(),
        ]

        RecipeIngredient.builder()
                .recipe(recipes.get(0))
                .ingredientName("재료")
                .build()
        RecipeIngredient.builder()
                .recipe(recipes.get(1))
                .ingredientName("재료")
                .build()

        recipeRepository.saveAll(recipes)

        when:
        Optional<Recipe> recipe = recipeRepository.findRecipeDetail(recipes.get(1).getRecipeId(), users.get(1).getUserId())

        then:
        !recipe.isPresent()
    }

    def "검색어로 레시피 갯수 조회"() {

        given:
        List<Recipe> recipes = [
                Recipe.builder()
                        .recipeNm("테스트제목")
                        .introduction("설명")
                        .level(RecipeLevel.NORMAL)
                        .userId(users.get(0).userId)
                        .isHidden(false)
                        .build(),
                Recipe.builder()
                        .recipeNm("제목")
                        .introduction("테스트설명")
                        .level(RecipeLevel.NORMAL)
                        .userId(users.get(0).userId)
                        .isHidden(false)
                        .build(),
                Recipe.builder()
                        .recipeNm("제목")
                        .introduction("설명")
                        .level(RecipeLevel.NORMAL)
                        .userId(users.get(0).userId)
                        .isHidden(false)
                        .build(),
        ]

        RecipeIngredient.builder()
                .recipe(recipes.get(0))
                .ingredientName("재료")
                .build()
        RecipeIngredient.builder()
                .recipe(recipes.get(1))
                .ingredientName("재료")
                .build()
        RecipeIngredient.builder()
                .recipe(recipes.get(2))
                .ingredientName("테스트")
                .build()

        recipeRepository.saveAll(recipes);

        when:
        long response = recipeRepository.countByKeyword("테스트");

        then:
        response == 3
    }

    def "검색어로 레시피 목록 조회 - 레시피 최신 등록 순 정렬"() {

        given:
        List<Recipe> recipes = [
                Recipe.builder()
                        .recipeNm("테스트제목")
                        .introduction("설명")
                        .level(RecipeLevel.NORMAL)
                        .userId(users.get(0).userId)
                        .isHidden(false)
                        .build(),
                Recipe.builder()
                        .recipeNm("제목")
                        .introduction("테스트설명")
                        .level(RecipeLevel.NORMAL)
                        .userId(users.get(0).userId)
                        .isHidden(false)
                        .build(),
                Recipe.builder()
                        .recipeNm("제목")
                        .introduction("설명")
                        .level(RecipeLevel.NORMAL)
                        .userId(users.get(0).userId)
                        .isHidden(false)
                        .build(),
        ]

        RecipeIngredient.builder()
                .recipe(recipes.get(0))
                .ingredientName("재료")
                .build()
        RecipeIngredient.builder()
                .recipe(recipes.get(1))
                .ingredientName("재료")
                .build()
        RecipeIngredient.builder()
                .recipe(recipes.get(2))
                .ingredientName("테스트")
                .build()

        recipeRepository.saveAll(recipes);

        when:
        List<Recipe> response = recipeRepository.findByKeywordLimitOrderByCreatedAtDesc("테스트", 0L, recipes.createdAt.max().plusMinutes(1), 3);

        then:
        response.size() == 3
        response.get(0).recipeId == recipes.get(2).recipeId
        response.get(1).recipeId == recipes.get(1).recipeId
        response.get(2).recipeId == recipes.get(0).recipeId
    }

    def "검색어로 레시피 목록 조회 - 스크랩 수 많은 순 정렬"() {

        given:

        List<Recipe> recipes = [
                Recipe.builder()
                        .recipeNm("테스트제목")
                        .introduction("설명")
                        .level(RecipeLevel.NORMAL)
                        .userId(users.get(0).userId)
                        .isHidden(false)
                        .scrapCnt(2L)
                        .build(),
                Recipe.builder()
                        .recipeNm("제목")
                        .introduction("테스트설명")
                        .level(RecipeLevel.NORMAL)
                        .userId(users.get(0).userId)
                        .isHidden(false)
                        .scrapCnt(0L)
                        .build(),
                Recipe.builder()
                        .recipeNm("제목")
                        .introduction("설명")
                        .level(RecipeLevel.NORMAL)
                        .userId(users.get(0).userId)
                        .isHidden(false)
                        .scrapCnt(2L)
                        .build(),
        ]

        RecipeIngredient.builder()
                .recipe(recipes.get(0))
                .ingredientName("재료")
                .build()
        RecipeIngredient.builder()
                .recipe(recipes.get(1))
                .ingredientName("재료")
                .build()
        RecipeIngredient.builder()
                .recipe(recipes.get(2))
                .ingredientName("테스트")
                .build()

        recipeRepository.saveAll(recipes);

        when:
        List<Recipe> response = recipeRepository.findByKeywordLimitOrderByRecipeScrapCntDesc("테스트", recipes.get(2).recipeId, 2, 3);

        then:
        response.size() == 2
        response.get(0).recipeId == recipes.get(0).recipeId
        response.get(1).recipeId == recipes.get(1).recipeId
    }

    def "검색어로 레시피 목록 조회 - 조회 수 많은 순 정렬"() {

        given:
        List<Recipe> recipes = [
                Recipe.builder()
                        .recipeNm("테스트제목")
                        .introduction("설명")
                        .level(RecipeLevel.NORMAL)
                        .userId(users.get(0).userId)
                        .isHidden(false)
                        .viewCnt(2L)
                        .build(),
                Recipe.builder()
                        .recipeNm("제목")
                        .introduction("테스트설명")
                        .level(RecipeLevel.NORMAL)
                        .userId(users.get(0).userId)
                        .isHidden(false)
                        .viewCnt(0L)
                        .build(),
                Recipe.builder()
                        .recipeNm("제목")
                        .introduction("설명")
                        .level(RecipeLevel.NORMAL)
                        .userId(users.get(0).userId)
                        .isHidden(false)
                        .viewCnt(2L)
                        .build(),
        ]

        RecipeIngredient.builder()
                .recipe(recipes.get(0))
                .ingredientName("재료")
                .build()
        RecipeIngredient.builder()
                .recipe(recipes.get(1))
                .ingredientName("재료")
                .build()
        RecipeIngredient.builder()
                .recipe(recipes.get(2))
                .ingredientName("테스트")
                .build()

        recipeRepository.saveAll(recipes);

        when:
        List<Recipe> response = recipeRepository.findByKeywordLimitOrderByRecipeViewCntDesc("테스트", recipes.get(2).recipeId, 2, 3);

        then:
        response.size() == 2
        response.get(0).recipeId == recipes.get(0).recipeId
        response.get(1).recipeId == recipes.get(1).recipeId
    }

    def "유저가 스크랩한 레시피 목록 조회"() {

        given:
        List<Recipe> recipes = [
                Recipe.builder()
                        .recipeNm("테스트제목")
                        .introduction("설명")
                        .level(RecipeLevel.NORMAL)
                        .userId(users.get(0).userId)
                        .isHidden(false)
                        .build(),
                Recipe.builder()
                        .recipeNm("제목")
                        .introduction("테스트설명")
                        .level(RecipeLevel.NORMAL)
                        .userId(users.get(0).userId)
                        .isHidden(false)
                        .build(),
                Recipe.builder()
                        .recipeNm("제목")
                        .introduction("설명")
                        .level(RecipeLevel.NORMAL)
                        .userId(users.get(0).userId)
                        .isHidden(false)
                        .build(),
        ]
        recipeRepository.saveAll(recipes);

        List<RecipeScrap> recipeScraps = [
                RecipeScrap.builder()
                        .userId(users.get(0).userId)
                        .recipeId(recipes.get(0).recipeId)
                        .build(),
                RecipeScrap.builder()
                        .userId(users.get(0).userId)
                        .recipeId(recipes.get(2).recipeId)
                        .build(),
        ]
        recipeScrapRepository.saveAll(recipeScraps);

        when:
        List<Recipe> response = recipeRepository.findUserScrapRecipesLimit(users.get(0).userId, 0L, recipeScraps.createdAt.max().plusMinutes(1), 3);

        then:
        response.size() == 2
        response.get(0).recipeId == recipes.get(2).recipeId
        response.get(1).recipeId == recipes.get(0).recipeId
    }

    def "유저가 등록한 레시피 목록 조회"() {

        given:
        List<Recipe> recipes = [
                Recipe.builder()
                        .recipeNm("테스트제목")
                        .introduction("설명")
                        .level(RecipeLevel.NORMAL)
                        .userId(users.get(0).userId)
                        .isHidden(false)
                        .build(),
                Recipe.builder()
                        .recipeNm("제목")
                        .introduction("테스트설명")
                        .level(RecipeLevel.NORMAL)
                        .userId(users.get(0).userId)
                        .isHidden(false)
                        .build(),
                Recipe.builder()
                        .recipeNm("제목")
                        .introduction("설명")
                        .level(RecipeLevel.NORMAL)
                        .userId(users.get(0).userId)
                        .isHidden(false)
                        .build(),
        ]
        recipeRepository.saveAll(recipes);

        when:
        List<Recipe> response = recipeRepository.findLimitByUserId(users.get(0).userId, recipes.get(2).recipeId, 3);

        then:
        response.size() == 2
        response.get(0).recipeId == recipes.get(1).recipeId
        response.get(1).recipeId == recipes.get(0).recipeId
    }

    def "냉장고에 있는 재료가 있는 레시피 목록 조회"() {

        given:
        List<Recipe> recipes = [
                Recipe.builder()
                        .recipeNm("테스트제목")
                        .introduction("설명")
                        .level(RecipeLevel.NORMAL)
                        .userId(users.get(0).userId)
                        .isHidden(false)
                        .build(),
                Recipe.builder()
                        .recipeNm("제목")
                        .introduction("테스트설명")
                        .level(RecipeLevel.NORMAL)
                        .userId(users.get(0).userId)
                        .isHidden(false)
                        .build(),
                Recipe.builder()
                        .recipeNm("제목")
                        .introduction("설명")
                        .level(RecipeLevel.NORMAL)
                        .userId(users.get(0).userId)
                        .isHidden(false)
                        .build(),
        ]


        RecipeIngredient.builder()
                .recipe(recipes.get(0))
                .ingredientName("테스트")
                .build()
        RecipeIngredient.builder()
                .recipe(recipes.get(1))
                .ingredientName("재료")
                .build()
        RecipeIngredient.builder()
                .recipe(recipes.get(2))
                .ingredientName("테스트")
                .build()

        recipeRepository.saveAll(recipes);

        when:
        List<Recipe> response = recipeRepository.findRecipesInFridge(["테스트"]);

        then:
        response.size() == 2
        response.recipeId == [recipes.get(0).recipeId, recipes.get(2).recipeId]
    }
}
