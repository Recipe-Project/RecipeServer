package com.recipe.app.src.scrap.domain;

import com.recipe.app.common.entity.BaseEntity;
import com.recipe.app.common.exception.BaseException;
import com.recipe.app.src.user.infra.UserEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import javax.persistence.*;

import static com.recipe.app.common.response.BaseResponseStatus.*;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "ScrapYoutube")
public class ScrapYoutube extends BaseEntity {
    @Id
    @Column(name = "idx", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private UserEntity user;

    @Column(name = "youtubeId", nullable = false, length = 45)
    private String youtubeId;

    @Column(name = "title", nullable = false, columnDefinition = "TEXT")
    private String title;

    @Column(name = "thumbnail", nullable = false)
    private String thumbnail;

    @Column(name = "youtubeUrl", nullable = false)
    private String youtubeUrl;

    @Column(name = "postDate", nullable = false, length = 20)
    private String postDate;

    @Column(name = "channelName", nullable = false, length = 30)
    private String channelName;

    @Column(name = "playTime", nullable = false, length = 20)
    private String playTime;

    @Column(name = "status", nullable = false, length = 10)
    private String status = "ACTIVE";

    public ScrapYoutube(UserEntity user, String youtubeId, String title, String thumbnail, String youtubeUrl, String postDate, String channelName, String playTime) {
        if (!StringUtils.hasText(title)) {
            throw new BaseException(EMPTY_TITLE);
        }
        if (!StringUtils.hasText(thumbnail)) {
            throw new BaseException(EMPTY_THUMBNAIL);
        }
        if (!StringUtils.hasText(youtubeUrl)) {
            throw new BaseException(EMPTY_YOUTUBEURL);
        }
        if (!StringUtils.hasText(postDate)) {
            throw new BaseException(EMPTY_POST_DATE);
        }
        if (!StringUtils.hasText(channelName)) {
            throw new BaseException(EMPTY_CHANNEL_NAME);
        }
        if (!StringUtils.hasText(youtubeId)) {
            throw new BaseException(EMPTY_YOUTUBEIDX);
        }
        if (!StringUtils.hasText(playTime)) {
            throw new BaseException(EMPTY_PLAY_TIME);
        }
        if (!thumbnail.matches("([^\\s]+(\\.(?i)(jpg|png|gif|pdf))$)")) {
            throw new BaseException(INVALID_THUMBNAIL);
        }
        if (!youtubeUrl.matches("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")) {
            throw new BaseException(INVALID_YOUTUBE_URL);
        }
        if (!postDate.matches("^\\d{4}\\.(0[1-9]|1[012])\\.(0[1-9]|[12][0-9]|3[01])$")) {
            throw new BaseException(INVALID_DATE);
        }

        this.user = user;
        this.youtubeId = youtubeId;
        this.title = title;
        this.thumbnail = thumbnail;
        this.youtubeUrl = youtubeUrl;
        this.postDate = postDate;
        this.channelName = channelName;
        this.playTime = playTime;
    }

}
