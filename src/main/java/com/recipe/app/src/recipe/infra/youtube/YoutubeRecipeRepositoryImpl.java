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
                        youtubeRecipe.youtubeRecipeId.lt(lastYoutubeRecipeId)
                                .or(youtubeRecipe.postDate.loe(lastYoutubeRecipePostDate))
                )
                .orderBy(youtubeRecipe.postDate.desc(), youtubeRecipe.youtubeRecipeId.desc())
                .limit(size)
                .fetch();
    }

    @Override
    public List<YoutubeRecipe> findByKeywordLimitOrderByYoutubeScrapCntDesc(String keyword, Long lastYoutubeRecipeId, long youtubeScrapCnt, int size) {

        return queryFactory
                .selectFrom(youtubeRecipe)
                .join(youtubeScrap).on(youtubeScrap.youtubeRecipeId.eq(youtubeRecipe.youtubeRecipeId))
                .where(
                        youtubeRecipe.title.contains(keyword)
                                .or(youtubeRecipe.description.contains(keyword)),
                        youtubeRecipe.youtubeRecipeId.lt(lastYoutubeRecipeId)
                                .or(youtubeScrap.count().loe(youtubeScrapCnt))
                )
                .orderBy(youtubeScrap.count().desc(), youtubeRecipe.youtubeRecipeId.desc())
                .limit(size)
                .fetch();
    }

    @Override
    public List<YoutubeRecipe> findByKeywordLimitOrderByYoutubeViewCntDesc(String keyword, Long lastYoutubeRecipeId, long youtubeViewCnt, int size) {

        return queryFactory
                .selectFrom(youtubeRecipe)
                .join(youtubeView).on(youtubeView.youtubeRecipeId.eq(youtubeRecipe.youtubeRecipeId))
                .where(
                        youtubeRecipe.title.contains(keyword)
                                .or(youtubeRecipe.description.contains(keyword)),
                        youtubeRecipe.youtubeRecipeId.lt(lastYoutubeRecipeId)
                                .or(youtubeView.count().loe(youtubeViewCnt))
                )
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
                        youtubeRecipe.youtubeRecipeId.lt(lastYoutubeRecipeId)
                                .or(youtubeScrap.createdAt.loe(scrapCreatedAt))
                )
                .orderBy(youtubeScrap.createdAt.desc(), youtubeRecipe.youtubeRecipeId.desc())
                .limit(size)
                .fetch();
    }
}
