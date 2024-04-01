package com.recipe.app.src.recipe.infra.youtube;

import com.recipe.app.common.infra.BaseRepositoryImpl;
import com.recipe.app.src.recipe.domain.youtube.YoutubeRecipe;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.recipe.app.src.recipe.domain.youtube.QYoutubeRecipe.youtubeRecipe;
import static com.recipe.app.src.recipe.domain.youtube.QYoutubeScrap.youtubeScrap;
import static com.recipe.app.src.recipe.domain.youtube.QYoutubeView.youtubeView;

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
    public List<YoutubeRecipe> findByKeywordLimitOrderByPostDateDesc(String keyword, Long lastYoutubeRecipeId, LocalDate lastYoutubeRecipePostDate, int size) {

        return queryFactory
                .selectFrom(youtubeRecipe)
                .where(
                        youtubeRecipe.title.contains(keyword)
                                .or(youtubeRecipe.description.contains(keyword)),
                        youtubeRecipe.postDate.lt(lastYoutubeRecipePostDate)
                                .or(youtubeRecipe.postDate.eq(lastYoutubeRecipePostDate)
                                        .and(youtubeRecipe.youtubeRecipeId.lt(lastYoutubeRecipeId)))
                )
                .orderBy(youtubeRecipe.postDate.desc(), youtubeRecipe.youtubeRecipeId.desc())
                .limit(size)
                .fetch();
    }

    @Override
    public List<YoutubeRecipe> findByKeywordLimitOrderByYoutubeScrapCntDesc(String keyword, Long lastYoutubeRecipeId, long youtubeScrapCnt, int size) {

        return queryFactory
                .selectFrom(youtubeRecipe)
                .leftJoin(youtubeScrap).on(youtubeScrap.youtubeRecipeId.eq(youtubeRecipe.youtubeRecipeId))
                .where(
                        youtubeRecipe.title.contains(keyword)
                                .or(youtubeRecipe.description.contains(keyword))
                )
                .groupBy(youtubeRecipe.youtubeRecipeId)
                .having(youtubeScrap.count().lt(youtubeScrapCnt)
                        .or(youtubeScrap.count().eq(youtubeScrapCnt)
                                .and(youtubeRecipe.youtubeRecipeId.lt(lastYoutubeRecipeId))))
                .orderBy(youtubeScrap.count().desc(), youtubeRecipe.youtubeRecipeId.desc())
                .limit(size)
                .fetch();
    }

    @Override
    public List<YoutubeRecipe> findByKeywordLimitOrderByYoutubeViewCntDesc(String keyword, Long lastYoutubeRecipeId, long youtubeViewCnt, int size) {

        return queryFactory
                .selectFrom(youtubeRecipe)
                .leftJoin(youtubeView).on(youtubeView.youtubeRecipeId.eq(youtubeRecipe.youtubeRecipeId))
                .where(
                        youtubeRecipe.title.contains(keyword)
                                .or(youtubeRecipe.description.contains(keyword))
                )
                .groupBy(youtubeRecipe.youtubeRecipeId)
                .having(youtubeView.count().lt(youtubeViewCnt)
                        .or(youtubeView.count().eq(youtubeViewCnt)
                                .and(youtubeRecipe.youtubeRecipeId.lt(lastYoutubeRecipeId))))
                .orderBy(youtubeView.count().desc(), youtubeRecipe.youtubeRecipeId.desc())
                .limit(size)
                .fetch();
    }

    @Override
    public List<YoutubeRecipe> findUserScrapYoutubeRecipesLimit(Long userId, Long lastYoutubeRecipeId, LocalDateTime scrapCreatedAt, int size) {

        return queryFactory
                .selectFrom(youtubeRecipe)
                .join(youtubeScrap).on(youtubeScrap.youtubeRecipeId.eq(youtubeRecipe.youtubeRecipeId), youtubeScrap.userId.eq(userId))
                .where(
                        youtubeScrap.createdAt.lt(scrapCreatedAt)
                                .or(youtubeScrap.createdAt.eq(scrapCreatedAt)
                                        .and(youtubeRecipe.youtubeRecipeId.lt(lastYoutubeRecipeId)))
                )
                .orderBy(youtubeScrap.createdAt.desc(), youtubeRecipe.youtubeRecipeId.desc())
                .limit(size)
                .fetch();
    }
}
