package com.recipe.app.src.recipe.infra;

import com.querydsl.jpa.JPAExpressions;
import com.recipe.app.src.common.infra.BaseRepositoryImpl;
import com.recipe.app.src.recipe.domain.Recipe;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.recipe.app.src.common.utils.SearchKeywordNormalizer.SearchQuery;
import com.querydsl.core.types.dsl.BooleanExpression;

import static com.recipe.app.src.common.utils.QueryUtils.ifIdIsNotNullAndGreaterThanZero;
import static com.recipe.app.src.common.utils.QueryUtils.matchAgainst;
import static com.recipe.app.src.common.utils.QueryUtils.matchSearchQuery;
import static com.recipe.app.src.recipe.domain.QRecipe.recipe;
import static com.recipe.app.src.recipe.domain.QRecipeIngredient.recipeIngredient;
import static com.recipe.app.src.recipe.domain.QRecipeScrap.recipeScrap;

public class RecipeRepositoryImpl extends BaseRepositoryImpl implements RecipeCustomRepository {

    public RecipeRepositoryImpl(EntityManager em) {
        super(em);
    }

    @Override
    public Optional<Recipe> findRecipeDetail(Long recipeId, Long userId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(recipe)
                .where(recipe.recipeId.eq(recipeId)
                        .and(recipe.hiddenYn.eq("N")
                                // userId == null(비로그인)이면 본인 비공개 조건을 제외 → 공개글만. (Querydsl 은 or(null) 을 무시)
                                .or(userId != null ? recipe.hiddenYn.eq("Y").and(recipe.userId.eq(userId)) : null))
                )
                .fetchOne());
    }

    @Override
    public Long countByKeyword(SearchQuery query) {
        return queryFactory
                .select(recipe.recipeId.countDistinct())
                .from(recipe)
                .where(
                        recipe.hiddenYn.eq("N"),
                        keywordMatch(query)
                )
                .fetchOne();
    }

    @Override
    public List<Recipe> findByKeywordLimitOrderByCreatedAtDesc(SearchQuery query, Long lastRecipeId, LocalDateTime lastCreatedAt, int size) {

        return queryFactory
                .selectFrom(recipe)
                .where(
                        recipe.hiddenYn.eq("N"),
                        keywordMatch(query),
                        ifIdIsNotNullAndGreaterThanZero((recipeId, createdAt) -> recipe.createdAt.lt(createdAt)
                                        .or(recipe.createdAt.eq(createdAt)
                                                .and(recipe.recipeId.lt(recipeId))),
                                lastRecipeId, lastCreatedAt)
                )
                .orderBy(recipe.createdAt.desc(), recipe.recipeId.desc())
                .limit(size)
                .fetch();
    }

    @Override
    public List<Recipe> findByKeywordLimitOrderByRecipeScrapCntDesc(SearchQuery query, Long lastRecipeId, long lastRecipeScrapCnt, int size) {

        return queryFactory
                .selectFrom(recipe)
                .where(
                        recipe.hiddenYn.eq("N"),
                        keywordMatch(query),
                        ifIdIsNotNullAndGreaterThanZero((recipeId, recipeScrapCnt) -> recipe.scrapCnt.lt(recipeScrapCnt)
                                        .or(recipe.scrapCnt.eq(recipeScrapCnt)
                                                .and(recipe.recipeId.lt(recipeId))),
                                lastRecipeId, lastRecipeScrapCnt)
                )
                .orderBy(recipe.scrapCnt.desc(), recipe.recipeId.desc())
                .limit(size)
                .fetch();
    }

    @Override
    public List<Recipe> findByKeywordLimitOrderByRecipeViewCntDesc(SearchQuery query, Long lastRecipeId, long lastRecipeViewCnt, int size) {

        return queryFactory
                .selectFrom(recipe)
                .where(
                        recipe.hiddenYn.eq("N"),
                        keywordMatch(query),
                        ifIdIsNotNullAndGreaterThanZero((recipeId, recipeViewCnt) -> recipe.viewCnt.lt(recipeViewCnt)
                                        .or(recipe.viewCnt.eq(recipeViewCnt)
                                                .and(recipe.recipeId.lt(recipeId))),
                                lastRecipeId, lastRecipeViewCnt)
                )
                .orderBy(recipe.viewCnt.desc(), recipe.recipeId.desc())
                .limit(size)
                .fetch();
    }

    private BooleanExpression keywordMatch(SearchQuery query) {
        return matchSearchQuery(recipe.searchTokens, query)
                .or(JPAExpressions.selectOne()
                        .from(recipeIngredient)
                        .where(recipeIngredient.recipe.recipeId.eq(recipe.recipeId)
                                .and(matchSearchQuery(recipeIngredient.searchTokens, query)))
                        .exists());
    }

    @Override
    public List<Recipe> findUserScrapRecipesLimit(Long userId, Long lastRecipeId, LocalDateTime lastScrapCreatedAt, int size) {

        return queryFactory
                .selectFrom(recipe)
                .join(recipeScrap).on(recipe.recipeId.eq(recipeScrap.recipeId), recipeScrap.userId.eq(userId))
                .where(
                        ifIdIsNotNullAndGreaterThanZero((recipeId, scrapCreatedAt) -> recipeScrap.createdAt.lt(scrapCreatedAt)
                                        .or(recipeScrap.createdAt.eq(scrapCreatedAt)
                                                .and(recipe.recipeId.lt(recipeId))),
                                lastRecipeId, lastScrapCreatedAt),
                        recipe.hiddenYn.eq("N")
                )
                .orderBy(recipeScrap.createdAt.desc(), recipe.recipeId.desc())
                .limit(size)
                .fetch();
    }

    @Override
    public List<Recipe> findLimitByUserId(Long userId, Long lastRecipeId, int size) {

        return queryFactory
                .selectFrom(recipe)
                .where(
                        recipe.userId.eq(userId),
                        ifIdIsNotNullAndGreaterThanZero(recipe.recipeId::lt, lastRecipeId)
                )
                .orderBy(recipe.recipeId.desc())
                .limit(size)
                .fetch();
    }

    @Override
    public List<Recipe> findRecipesInFridge(Collection<String> ingredientNames) {

        if (ingredientNames == null || ingredientNames.isEmpty()) return List.of();

        // OR BOOLEAN MODE 쿼리. "+" 없이 공백 구분이면 토큰 중 하나만 매치되어도 hit.
        String boolQuery = ingredientNames.stream()
                .filter(Objects::nonNull)
                .map(s -> s.toLowerCase().trim())
                .filter(s -> !s.isEmpty())
                .distinct()
                .collect(Collectors.joining(" "));

        if (boolQuery.isEmpty()) return List.of();

        return queryFactory
                .selectFrom(recipe)
                .join(recipeIngredient).on(recipe.recipeId.eq(recipeIngredient.recipe.recipeId))
                .where(
                        matchAgainst(recipeIngredient.searchTokens, boolQuery),
                        recipe.hiddenYn.eq("N")
                )
                .groupBy(recipe.recipeId)
                .fetch();
    }
}
