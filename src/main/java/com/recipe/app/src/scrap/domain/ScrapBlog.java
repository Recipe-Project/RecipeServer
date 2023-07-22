package com.recipe.app.src.scrap.domain;

import com.recipe.app.common.entity.BaseEntity;
import com.recipe.app.common.exception.BaseException;
import com.recipe.app.src.user.domain.User;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.recipe.app.common.response.BaseResponseStatus.*;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "ScrapBlog")
public class ScrapBlog extends BaseEntity {
    @Id
    @Column(name = "idx", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userIdx", nullable = false)
    private User user;

    @Column(name = "title", nullable = false, length = 45)
    private String title;

    @Column(name = "thumbnail", nullable = false)
    private String thumbnail;

    @Column(name = "blogUrl", nullable = false)
    private String blogUrl;

    @Column(name = "description", nullable = false, length = 200)
    private String description;

    @Column(name = "bloggerName", nullable = false, length = 45)
    private String bloggerName;

    @Column(name = "postDate", nullable = false)
    private LocalDate postDate;

    @Column(name = "status", nullable = false, length = 10)
    private String status = "ACTIVE";

    public ScrapBlog(User user, String title, String thumbnail, String blogUrl, String description, String bloggerName, String postDate) {
        if (!StringUtils.hasText(title)) {
            throw new BaseException(POST_SCRAP_BLOG_EMPTY_TITLE);
        }
        if (!StringUtils.hasText(thumbnail)) {
            throw new BaseException(POST_SCRAP_BLOG_EMPTY_THUMBNAIL);
        }
        if (!StringUtils.hasText(blogUrl)) {
            throw new BaseException(POST_SCRAP_BLOG_EMPTY_BLOGURL);
        }
        if (!StringUtils.hasText(description)) {
            throw new BaseException(POST_SCRAP_BLOG_EMPTY_DESCRIPTION);
        }
        if (!StringUtils.hasText(bloggerName)) {
            throw new BaseException(POST_SCRAP_BLOG_EMPTY_BLOGGER_NAME);
        }
        if (!StringUtils.hasText(postDate)) {
            throw new BaseException(POST_SCRAP_BLOG_EMPTY_POST_DATE);
        }

        this.user = user;
        this.title = title;
        this.thumbnail = thumbnail;
        this.blogUrl = blogUrl;
        this.description = description;
        this.bloggerName = bloggerName;
        this.postDate = LocalDate.parse(postDate, DateTimeFormatter.ofPattern("yyyy.M.d"));
    }

}

