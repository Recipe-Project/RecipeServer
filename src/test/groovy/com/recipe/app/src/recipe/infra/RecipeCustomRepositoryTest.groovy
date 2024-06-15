package com.recipe.app.src.recipe.infra

import com.recipe.app.src.recipe.domain.Recipe
import com.recipe.app.src.recipe.domain.RecipeIngredient
import com.recipe.app.src.recipe.domain.RecipeLevel
import com.recipe.app.src.recipe.domain.RecipeScrap
import com.recipe.app.src.recipe.domain.RecipeView
import com.recipe.app.src.user.domain.User
import com.recipe.app.src.user.infra.UserRepository
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
class RecipeCustomRepositoryTest extends Specification {

    @Autowired
    UserRepository userRepository;
    @Autowired
    RecipeIngredientRepository recipeIngredientRepository;
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
        recipeRepository.saveAll(recipes);

        List<RecipeIngredient> recipeIngredients = [
                RecipeIngredient.builder()
                        .recipeId(recipes.get(0).recipeId)
                        .ingredientName("재료")
                        .build(),
                RecipeIngredient.builder()
                        .recipeId(recipes.get(1).recipeId)
                        .ingredientName("재료")
                        .build(),
                RecipeIngredient.builder()
                        .recipeId(recipes.get(2).recipeId)
                        .ingredientName("테스트")
                        .build(),
        ]
        recipeIngredientRepository.saveAll(recipeIngredients);

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
        recipeRepository.saveAll(recipes);

        List<RecipeIngredient> recipeIngredients = [
                RecipeIngredient.builder()
                        .recipeId(recipes.get(0).recipeId)
                        .ingredientName("재료")
                        .build(),
                RecipeIngredient.builder()
                        .recipeId(recipes.get(1).recipeId)
                        .ingredientName("재료")
                        .build(),
                RecipeIngredient.builder()
                        .recipeId(recipes.get(2).recipeId)
                        .ingredientName("테스트")
                        .build(),
        ]
        recipeIngredientRepository.saveAll(recipeIngredients);

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
        recipeRepository.saveAll(recipes);

        List<RecipeIngredient> recipeIngredients = [
                RecipeIngredient.builder()
                        .recipeId(recipes.get(0).recipeId)
                        .ingredientName("재료")
                        .build(),
                RecipeIngredient.builder()
                        .recipeId(recipes.get(1).recipeId)
                        .ingredientName("재료")
                        .build(),
                RecipeIngredient.builder()
                        .recipeId(recipes.get(2).recipeId)
                        .ingredientName("테스트")
                        .build(),
        ]
        recipeIngredientRepository.saveAll(recipeIngredients);

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
        recipeRepository.saveAll(recipes);

        List<RecipeIngredient> recipeIngredients = [
                RecipeIngredient.builder()
                        .recipeId(recipes.get(0).recipeId)
                        .ingredientName("재료")
                        .build(),
                RecipeIngredient.builder()
                        .recipeId(recipes.get(1).recipeId)
                        .ingredientName("재료")
                        .build(),
                RecipeIngredient.builder()
                        .recipeId(recipes.get(2).recipeId)
                        .ingredientName("테스트")
                        .build(),
        ]
        recipeIngredientRepository.saveAll(recipeIngredients);

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
        recipeRepository.saveAll(recipes);

        List<RecipeIngredient> recipeIngredients = [
                RecipeIngredient.builder()
                        .recipeId(recipes.get(0).recipeId)
                        .ingredientName("테스트")
                        .build(),
                RecipeIngredient.builder()
                        .recipeId(recipes.get(1).recipeId)
                        .ingredientName("재료")
                        .build(),
                RecipeIngredient.builder()
                        .recipeId(recipes.get(2).recipeId)
                        .ingredientName("테스트")
                        .build(),
        ]
        recipeIngredientRepository.saveAll(recipeIngredients);

        when:
        List<Recipe> response = recipeRepository.findRecipesInFridge(["테스트"]);

        then:
        response.size() == 2
        response.recipeId == [recipes.get(0).recipeId, recipes.get(2).recipeId]
    }
}
