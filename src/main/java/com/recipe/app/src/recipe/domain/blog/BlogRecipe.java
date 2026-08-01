package com.recipe.app.src.recipe.domain.blog;

import com.google.common.base.Preconditions;
import com.recipe.app.src.common.entity.BaseEntity;
import com.recipe.app.src.common.utils.KoreanTokenizer;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

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

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "publishedAt", nullable = false)
    private LocalDate publishedAt;

    @Column(name = "blogName", nullable = false)
    private String blogName;

    @Column(name = "scrapCnt", nullable = false)
    private long scrapCnt;

    @Column(name = "viewCnt", nullable = false)
    private long viewCnt;

    @Column(name = "searchTokens", columnDefinition = "TEXT")
    private String searchTokens;

    // 제목(title)만 토큰화. 검색 정렬에서 "제목 매칭 우선" 계층에 사용.
    @Column(name = "titleSearchTokens", columnDefinition = "TEXT")
    private String titleSearchTokens;

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

    @PrePersist
    @PreUpdate
    private void refreshSearchTokens() {
        String source = (title != null ? title : "") + " " + (description != null ? description : "");
        this.searchTokens = KoreanTokenizer.tokenize(source);
        this.titleSearchTokens = KoreanTokenizer.tokenize(title != null ? title : "");
    }
}
