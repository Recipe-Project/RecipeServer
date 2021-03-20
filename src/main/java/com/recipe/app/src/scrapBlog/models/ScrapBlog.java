package com.recipe.app.src.scrapBlog.models;

import com.recipe.app.src.user.models.User;
import lombok.*;
import javax.persistence.*;
import com.recipe.app.config.BaseEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PUBLIC) // Unit Test 를 위해 PUBLIC
@EqualsAndHashCode(callSuper = false)
@Data // from lombok
@Entity // 필수, Class 를 Database Table화 해주는 것이다
@Table(name = "ScrapBlog") // Table 이름을 명시해주지 않으면 class 이름을 Table 이름으로 대체한다.
public class ScrapBlog extends BaseEntity {
    @Id // PK를 의미하는 어노테이션
    @Column(name = "idx", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    //@Column(name="userIdx", nullable = false)
    //private Integer userIdx;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userIdx", nullable = false)
    private User user;

    @Column(name = "title", nullable = false, length = 45)
    private String title;

    @Column(name = "thumbnail", nullable = false)
    private String thumbnail;

    @Column(name = "blogUrl", nullable = false)
    private String blogUrl;

    @Column(name = "description", nullable = false, length=200)
    private String description;

    @Column(name = "bloggerName", nullable = false, length=45)
    private String bloggerName;

    @Column(name = "postDate", nullable = false)
    private Date postDate;

    @Column(name="status", nullable=false, length=10)
    private String status="ACTIVE";

    public ScrapBlog(User user, String title, String thumbnail, String blogUrl, String description, String bloggerName, Date postDate){
        this.user = user;
        this.title = title;
        this.thumbnail = thumbnail;
        this.blogUrl = blogUrl;
        this.description = description;
        this.bloggerName = bloggerName;
        this.postDate = postDate;
    }

}

