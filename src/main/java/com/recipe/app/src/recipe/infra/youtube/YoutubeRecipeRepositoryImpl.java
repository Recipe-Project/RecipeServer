package com.recipe.app.src.recipe.infra.youtube;

import com.recipe.app.common.infra.BaseRepositoryImpl;
import com.recipe.app.src.recipe.domain.youtube.YoutubeRecipe;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.recipe.app.common.utils.QueryUtils.ifIdIsNotNullAndGreaterThanZero;
import static com.recipe.app.src.recipe.domain.youtube.QYoutubeRecipe.youtubeRecipe;
import static com.recipe.app.src.recipe.domain.youtube.QYoutubeScrap.youtubeScrap;

public class YoutubeRecipeRepositoryImpl extends BaseRepositoryImpl implements YoutubeRecipeCustomRepository {

    public YoutubeRecipeRepositoryImpl(EntityManager em) {
        super(em);
    }

    @Override
    public Long countByKeyword(String keyword) {

        return queryFactory
                .select(youtubeRecipe.count())
                .from(youtubeRecipe)
                .where(
                        youtubeRecipe.title.contains(keyword)
                                .or(youtubeRecipe.description.contains(keyword))
                )
                .fetchOne();
    }

    @Override
    public List<YoutubeRecipe> findByKeywordLimit(String keyword, int size) {

        return queryFactory
                .selectFrom(youtubeRecipe)
                .where(
                        youtubeRecipe.title.contains(keyword)
                                .or(youtubeRecipe.description.contains(keyword))
                )
                .limit(size)
                .fetch();
    }

    @Override
    public List<YoutubeRecipe> findByKeywordLimitOrderByPostDateDesc(String keyword, Long lastYoutubeRecipeId, LocalDate lastYoutubeRecipePostDate, int size) {

        return queryFactory
                .selectFrom(youtubeRecipe)
                .where(
                        youtubeRecipe.title.contains(keyword)
                                .or(youtubeRecipe.description.contains(keyword)),
                        ifIdIsNotNullAndGreaterThanZero((youtubeRecipeId, postDate) -> youtubeRecipe.postDate.lt(postDate)
                                        .or(youtubeRecipe.postDate.eq(postDate)
                                                .and(youtubeRecipe.youtubeRecipeId.lt(youtubeRecipeId))),
                                lastYoutubeRecipeId, lastYoutubeRecipePostDate)
                )
                .orderBy(youtubeRecipe.postDate.desc(), youtubeRecipe.youtubeRecipeId.desc())
                .limit(size)
                .fetch();
    }

    @Override
    public List<YoutubeRecipe> findByKeywordLimitOrderByYoutubeScrapCntDesc(String keyword, Long lastYoutubeRecipeId, long lastYoutubeScrapCnt, int size) {

        return queryFactory
                .selectFrom(youtubeRecipe)
                .where(
                        youtubeRecipe.title.contains(keyword)
                                .or(youtubeRecipe.description.contains(keyword)),
                        ifIdIsNotNullAndGreaterThanZero((youtubeRecipeId, youtubeScrapCnt) -> youtubeRecipe.scrapCnt.lt(youtubeScrapCnt)
                                        .or(youtubeRecipe.scrapCnt.eq(youtubeScrapCnt)
                                                .and(youtubeRecipe.youtubeRecipeId.lt(youtubeRecipeId))),
                                lastYoutubeRecipeId, lastYoutubeScrapCnt)
                )
                .orderBy(youtubeRecipe.scrapCnt.desc(), youtubeRecipe.youtubeRecipeId.desc())
                .limit(size)
                .fetch();
    }

    @Override
    public List<YoutubeRecipe> findByKeywordLimitOrderByYoutubeViewCntDesc(String keyword, Long lastYoutubeRecipeId, long lastYoutubeViewCnt, int size) {

        return queryFactory
                .selectFrom(youtubeRecipe)
                .where(
                        youtubeRecipe.title.contains(keyword)
                                .or(youtubeRecipe.description.contains(keyword)),
                        ifIdIsNotNullAndGreaterThanZero((youtubeRecipeId, youtubeViewCnt) -> youtubeRecipe.viewCnt.lt(youtubeViewCnt)
                                        .or(youtubeRecipe.viewCnt.eq(youtubeViewCnt)
                                                .and(youtubeRecipe.youtubeRecipeId.lt(youtubeRecipeId))),
                                lastYoutubeRecipeId, lastYoutubeViewCnt)
                )
                .orderBy(youtubeRecipe.viewCnt.desc(), youtubeRecipe.youtubeRecipeId.desc())
                .limit(size)
                .fetch();
    }

    @Override
    public List<YoutubeRecipe> findUserScrapYoutubeRecipesLimit(Long userId, Long lastYoutubeRecipeId, LocalDateTime lastScrapCreatedAt, int size) {

        return queryFactory
                .selectFrom(youtubeRecipe)
                .join(youtubeScrap).on(youtubeScrap.youtubeRecipeId.eq(youtubeRecipe.youtubeRecipeId), youtubeScrap.userId.eq(userId))
                .where(
                        ifIdIsNotNullAndGreaterThanZero((youtubeRecipeId, scrapCreatedAt) -> youtubeScrap.createdAt.lt(scrapCreatedAt)
                                        .or(youtubeScrap.createdAt.eq(scrapCreatedAt)
                                                .and(youtubeRecipe.youtubeRecipeId.lt(youtubeRecipeId))),
                                lastYoutubeRecipeId, lastScrapCreatedAt))
                .orderBy(youtubeScrap.createdAt.desc(), youtubeRecipe.youtubeRecipeId.desc())
                .limit(size)
                .fetch();
    }
}
