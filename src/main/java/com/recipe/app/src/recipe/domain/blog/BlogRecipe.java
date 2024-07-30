package com.recipe.app.src.recipe.domain.blog;

import com.google.common.base.Preconditions;
import com.recipe.app.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "BlogRecipe")
public class BlogRecipe extends BaseEntity {

    @Id
    @Column(name = "blogRecipeId", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long blogRecipeId;

    @Column(name = "blogUrl", nullable = false)
    private String blogUrl;

    @Column(name = "blogThumbnailImgUrl")
    private String blogThumbnailImgUrl;

    @Column(name = "title", nullable = false, length = 128)
    private String title;

    @Column(name = "description", nullable = false, length = 200)
    private String description;

    @Column(name = "publishedAt", nullable = false)
    private LocalDate publishedAt;

    @Column(name = "blogName", nullable = false, length = 45)
    private String blogName;

    @Column(name = "scrapCnt", nullable = false)
    private long scrapCnt;

    @Column(name = "viewCnt", nullable = false)
    private long viewCnt;

    @Builder
    public BlogRecipe(Long blogRecipeId, String blogUrl, String blogThumbnailImgUrl, String title, String description, LocalDate publishedAt, String blogName, long scrapCnt, long viewCnt) {

        Preconditions.checkArgument(StringUtils.hasText(blogUrl), "블로그 URL을 입력해주세요.");
        Preconditions.checkArgument(StringUtils.hasText(title), "블로그 레시피 제목을 입력해주세요.");
        Preconditions.checkArgument(StringUtils.hasText(description), "블로그 레시피 설명을 입력해주세요.");
        Preconditions.checkArgument(StringUtils.hasText(blogName), "블로그명을 입력해주세요.");
        Objects.requireNonNull(publishedAt, "블로그 레시피 게시 날짜를 입력해주세요.");

        this.blogRecipeId = blogRecipeId;
        this.blogUrl = blogUrl;
        this.blogThumbnailImgUrl = blogThumbnailImgUrl;
        this.title = title;
        this.description = description;
        this.publishedAt = publishedAt;
        this.blogName = blogName;
        this.scrapCnt = scrapCnt;
        this.viewCnt = viewCnt;
    }

    public void plusScrapCnt() {
        this.scrapCnt++;
    }

    public void minusScrapCnt() {
        this.scrapCnt--;
    }

    public void plusViewCnt() {
        this.viewCnt++;
    }

    public void changeThumbnail(String blogThumbnailUrl) {
        this.blogThumbnailImgUrl = blogThumbnailUrl;
    }
}
