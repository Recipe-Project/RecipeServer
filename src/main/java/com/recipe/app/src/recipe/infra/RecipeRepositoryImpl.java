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
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;

import static com.recipe.app.src.common.utils.QueryUtils.ifIdIsNotNullAndGreaterThanZero;
import static com.recipe.app.src.common.utils.QueryUtils.matchAgainst;
import static com.recipe.app.src.common.utils.QueryUtils.matchSearchQuery;
import static com.recipe.app.src.common.utils.QueryUtils.relevanceScore;
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
    public List<Recipe> findByKeywordLimitOrderByCreatedAtDesc(SearchQuery query, Long lastRecipeId, Double lastRelevance, LocalDateTime lastCreatedAt, int size) {

        return queryFactory
                .selectFrom(recipe)
                .where(
                        recipe.hiddenYn.eq("N"),
                        keywordMatch(query),
                        relevanceCursor(query, lastRecipeId, lastRelevance,
                                () -> recipe.createdAt.lt(lastCreatedAt), () -> recipe.createdAt.eq(lastCreatedAt))
                )
                .orderBy(relevanceOrder(query, recipe.createdAt.desc()))
                .limit(size)
                .fetch();
    }

    @Override
    public Double findRelevanceScoreByRecipeId(SearchQuery query, Long recipeId) {

        // 커서로 쓸 "직전 페이지 마지막 레시피"의 현재 검색어 기준 점수를 구한다. (최신순의 createdAt findById 대응)
        // 점수 개념이 없는 1글자(ExactToken)/빈 쿼리는 null → 첫 페이지처럼 keyset 조건 없이 처리된다.
        if (recipeId == null || recipeId <= 0 || !(query instanceof SearchQuery.BooleanQuery b)) {
            return null;
        }

        return queryFactory
                .select(relevanceScore(recipe.searchTokens, b.query()))
                .from(recipe)
                .where(recipe.recipeId.eq(recipeId))
                .fetchOne();
    }

    @Override
    public List<Recipe> findByKeywordLimitOrderByRecipeScrapCntDesc(SearchQuery query, Long lastRecipeId, Double lastRelevance, long lastRecipeScrapCnt, int size) {

        return queryFactory
                .selectFrom(recipe)
                .where(
                        recipe.hiddenYn.eq("N"),
                        keywordMatch(query),
                        relevanceCursor(query, lastRecipeId, lastRelevance,
                                () -> recipe.scrapCnt.lt(lastRecipeScrapCnt), () -> recipe.scrapCnt.eq(lastRecipeScrapCnt))
                )
                .orderBy(relevanceOrder(query, recipe.scrapCnt.desc()))
                .limit(size)
                .fetch();
    }

    @Override
    public List<Recipe> findByKeywordLimitOrderByRecipeViewCntDesc(SearchQuery query, Long lastRecipeId, Double lastRelevance, long lastRecipeViewCnt, int size) {

        return queryFactory
                .selectFrom(recipe)
                .where(
                        recipe.hiddenYn.eq("N"),
                        keywordMatch(query),
                        relevanceCursor(query, lastRecipeId, lastRelevance,
                                () -> recipe.viewCnt.lt(lastRecipeViewCnt), () -> recipe.viewCnt.eq(lastRecipeViewCnt))
                )
                .orderBy(relevanceOrder(query, recipe.viewCnt.desc()))
                .limit(size)
                .fetch();
    }

    /**
     * "검색어 일치율(relevance) 대분류 → 소분류(secondary) → recipeId" 정렬의 keyset 커서.
     * BooleanQuery(2글자+)면 relevance 를 맨 앞에, 그 외(1글자 ExactToken 등)엔 relevance 계층 없이 기존 순수 keyset.
     * secondary 는 lastCreatedAt 이 null 일 수 있어 지연 평가(Supplier)로 받는다. lastRecipeId<=0(첫 페이지)이면 커서 없음.
     */
    private BooleanExpression relevanceCursor(SearchQuery query, Long lastRecipeId, Double lastRelevance,
                                              java.util.function.Supplier<BooleanExpression> secondaryLt,
                                              java.util.function.Supplier<BooleanExpression> secondaryEq) {

        if (lastRecipeId == null || lastRecipeId <= 0) {
            return null;
        }

        BooleanExpression idLt = recipe.recipeId.lt(lastRecipeId);

        if (query instanceof SearchQuery.BooleanQuery b && lastRelevance != null) {
            NumberExpression<Double> score = relevanceScore(recipe.searchTokens, b.query());
            return score.lt(lastRelevance)
                    .or(score.eq(lastRelevance).and(secondaryLt.get()))
                    .or(score.eq(lastRelevance).and(secondaryEq.get()).and(idLt));
        }

        return secondaryLt.get().or(secondaryEq.get().and(idLt));
    }

    /**
     * BooleanQuery 면 relevance DESC 를 맨 앞에 둔 정렬키, 그 외엔 기존 (secondary, recipeId) 정렬키를 반환.
     */
    private OrderSpecifier<?>[] relevanceOrder(SearchQuery query, OrderSpecifier<?> secondary) {

        if (query instanceof SearchQuery.BooleanQuery b) {
            return new OrderSpecifier<?>[]{relevanceScore(recipe.searchTokens, b.query()).desc(), secondary, recipe.recipeId.desc()};
        }

        return new OrderSpecifier<?>[]{secondary, recipe.recipeId.desc()};
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
