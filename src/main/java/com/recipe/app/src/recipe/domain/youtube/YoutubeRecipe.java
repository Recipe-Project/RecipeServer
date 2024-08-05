package com.recipe.app.src.recipe.domain.youtube;

import com.google.common.base.Preconditions;
import com.recipe.app.src.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "YoutubeRecipe")
public class YoutubeRecipe extends BaseEntity {

    @Id
    @Column(name = "youtubeRecipeId", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long youtubeRecipeId;

    @Column(name = "title", nullable = false, length = 128)
    private String title;

    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "thumbnailImgUrl", nullable = false)
    private String thumbnailImgUrl;

    @Column(name = "postDate", nullable = false)
    private LocalDate postDate;

    @Column(name = "channelName", nullable = false)
    private String channelName;

    @Column(name = "youtubeId", nullable = false, length = 16)
    private String youtubeId;

    @Column(name = "scrapCnt", nullable = false)
    private long scrapCnt;

    @Column(name = "viewCnt", nullable = false)
    private long viewCnt;

    @Builder
    public YoutubeRecipe(Long youtubeRecipeId, String title, String description, String thumbnailImgUrl, LocalDate postDate, String channelName, String youtubeId, long scrapCnt, long viewCnt) {

        Preconditions.checkArgument(StringUtils.hasText(title), "유튜브 레시피 제목을 입력해주세요.");
        Preconditions.checkArgument(StringUtils.hasText(thumbnailImgUrl), "유튜브 레시피 썸네일 URL을 입력해주세요.");
        Preconditions.checkArgument(StringUtils.hasText(channelName), "유튜브 채널명을 입력해주세요.");
        Preconditions.checkArgument(StringUtils.hasText(youtubeId), "유튜브 아이디를 입력해주세요.");
        Objects.requireNonNull(postDate, "유튜브 게시 날짜를 입력해주세요.");

        this.youtubeRecipeId = youtubeRecipeId;
        this.title = title;
        this.description = description;
        this.thumbnailImgUrl = thumbnailImgUrl;
        this.postDate = postDate;
        this.channelName = channelName;
        this.youtubeId = youtubeId;
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
}
