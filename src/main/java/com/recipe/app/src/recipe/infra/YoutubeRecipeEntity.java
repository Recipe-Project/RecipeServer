package com.recipe.app.src.recipe.infra;

import com.recipe.app.common.entity.BaseEntity;
import com.recipe.app.src.recipe.domain.YoutubeRecipe;
import com.recipe.app.src.user.infra.UserEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "YoutubeRecipe")
public class YoutubeRecipeEntity extends BaseEntity {

    @Id
    @Column(name = "youtubeRecipeId", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long youtubeRecipeId;

    @Column(name = "title", nullable = false, length = 128)
    private String title;

    @Column(name = "description", nullable = false, length = 200)
    private String description;

    @Column(name = "thumbnailImgUrl", nullable = false)
    private String thumbnailImgUrl;

    @Column(name = "postDate", nullable = false)
    private LocalDate postDate;

    @Column(name = "channelName", nullable = false)
    private String channelName;

    @Column(name = "youtubeId", nullable = false, length = 16)
    private String youtubeId;

    @OneToMany(mappedBy = "youtubeRecipe", cascade = CascadeType.ALL)
    private List<YoutubeScrapEntity> youtubeScraps = new ArrayList<>();

    @OneToMany(mappedBy = "youtubeRecipe", cascade = CascadeType.ALL)
    private List<YoutubeViewEntity> youtubeViews = new ArrayList<>();

    public static YoutubeRecipeEntity fromModel(YoutubeRecipe youtubeRecipe) {
        YoutubeRecipeEntity youtubeRecipeEntity = new YoutubeRecipeEntity();
        youtubeRecipeEntity.youtubeRecipeId = youtubeRecipe.getYoutubeRecipeId();
        youtubeRecipeEntity.title = youtubeRecipe.getTitle();
        youtubeRecipeEntity.description = youtubeRecipe.getDescription();
        youtubeRecipeEntity.thumbnailImgUrl = youtubeRecipe.getThumbnailImgUrl();
        youtubeRecipeEntity.postDate = youtubeRecipe.getPostDate();
        youtubeRecipeEntity.channelName = youtubeRecipe.getChannelName();
        youtubeRecipeEntity.youtubeId = youtubeRecipe.getYoutubeId();
        return youtubeRecipeEntity;
    }

    public YoutubeRecipe toModel() {
        return YoutubeRecipe.builder()
                .youtubeRecipeId(youtubeRecipeId)
                .title(title)
                .description(description)
                .thumbnailImgUrl(thumbnailImgUrl)
                .postDate(postDate)
                .channelName(channelName)
                .youtubeId(youtubeId)
                .scrapUsers(youtubeScraps.stream()
                        .map(YoutubeScrapEntity::getUser)
                        .map(UserEntity::toModel)
                        .collect(Collectors.toList()))
                .viewUsers(youtubeViews.stream()
                        .map(YoutubeViewEntity::getUser)
                        .map(UserEntity::toModel)
                        .collect(Collectors.toList()))
                .build();
    }
}
