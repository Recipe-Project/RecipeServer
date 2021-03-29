package com.recipe.app.src.scrapYoutube.models;

import com.recipe.app.src.user.models.User;
import lombok.*;
import javax.persistence.*;
import com.recipe.app.config.BaseEntity;

import java.util.Date;

@NoArgsConstructor(access = AccessLevel.PUBLIC) // Unit Test 를 위해 PUBLIC
@EqualsAndHashCode(callSuper = false)
@Data // from lombok
@Entity // 필수, Class 를 Database Table화 해주는 것이다
@Table(name = "ScrapYoutube") // Table 이름을 명시해주지 않으면 class 이름을 Table 이름으로 대체한다.
public class ScrapYoutube extends BaseEntity {
    @Id // PK를 의미하는 어노테이션
    @Column(name = "idx", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    //@Column(name="userIdx", nullable = false)
    //private Integer userIdx;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userIdx", nullable = false)
    private User user;

    @Column(name = "youtubeIdx", nullable = false)
    private Integer youtubeIdx;

    @Column(name = "title", nullable = false, length = 45)
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

    @Column(name="status", nullable=false, length=10)
    private String status="ACTIVE";

    public ScrapYoutube(User user,  Integer youtubeIdx, String title, String thumbnail, String youtubeUrl, String postDate, String channelName,String playTime){
        this.user = user;
        this.youtubeIdx = youtubeIdx;
        this.title = title;
        this.thumbnail = thumbnail;
        this.youtubeUrl = youtubeUrl;
        this.postDate = postDate;
        this.channelName = channelName;
        this.playTime = playTime;
    }

}
