package com.recipe.app.src.recipe.infra;

import com.recipe.app.common.entity.BaseEntity;
import com.recipe.app.src.recipe.domain.BlogRecipe;
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
@Table(name = "BlogRecipe")
public class BlogRecipeEntity extends BaseEntity {

    @Id
    @Column(name = "blogRecipeId", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long blogRecipeId;

    @Column(name = "blogUrl", nullable = false)
    private String blogUrl;

    @Column(name = "blogThumbnailImgUrl", nullable = false)
    private String blogThumbnailImgUrl;

    @Column(name = "title", nullable = false, length = 128)
    private String title;

    @Column(name = "description", nullable = false, length = 200)
    private String description;

    @Column(name = "publishedAt", nullable = false)
    private LocalDate publishedAt;

    @Column(name = "blogName", nullable = false, length = 45)
    private String blogName;

    @OneToMany(mappedBy = "blogRecipe", cascade = CascadeType.ALL)
    private List<BlogScrapEntity> blogScraps = new ArrayList<>();

    @OneToMany(mappedBy = "blogRecipe", cascade = CascadeType.ALL)
    private List<BlogViewEntity> blogViews = new ArrayList<>();

    public BlogRecipe toModel() {
        return BlogRecipe.builder()
                .blogRecipeId(blogRecipeId)
                .blogUrl(blogUrl)
                .blogThumbanilImgUrl(blogThumbnailImgUrl)
                .title(title)
                .description(description)
                .publishedAt(publishedAt)
                .blogName(blogName)
                .scrapUsers(blogScraps.stream()
                        .map(BlogScrapEntity::getUser)
                        .map(UserEntity::toModel)
                        .collect(Collectors.toList()))
                .viewUsers(blogViews.stream()
                        .map(BlogViewEntity::getUser)
                        .map(UserEntity::toModel)
                        .collect(Collectors.toList()))
                .build();
    }

    public static BlogRecipeEntity fromModel(BlogRecipe blogRecipe) {
        BlogRecipeEntity blogRecipeEntity = new BlogRecipeEntity();
        blogRecipeEntity.blogRecipeId = blogRecipe.getBlogRecipeId();
        blogRecipeEntity.blogUrl = blogRecipe.getBlogUrl();
        blogRecipeEntity.blogThumbnailImgUrl = blogRecipe.getBlogThumbanilImgUrl();
        blogRecipeEntity.title = blogRecipe.getTitle();
        blogRecipeEntity.description = blogRecipe.getDescription();
        blogRecipeEntity.publishedAt = blogRecipe.getPublishedAt();
        blogRecipeEntity.blogName = blogRecipe.getBlogName();
        return blogRecipeEntity;
    }
}
