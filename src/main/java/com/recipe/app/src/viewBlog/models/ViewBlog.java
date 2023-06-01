package com.recipe.app.src.viewBlog.models;

import com.recipe.app.src.user.domain.User;
import lombok.*;
import javax.persistence.*;
import com.recipe.app.common.entity.BaseEntity;

@NoArgsConstructor(access = AccessLevel.PUBLIC) // Unit Test 를 위해 PUBLIC
@EqualsAndHashCode(callSuper = false)
@Data // from lombok
@Entity // 필수, Class 를 Database Table화 해주는 것이다
@Table(name = "ViewBlog") // Table 이름을 명시해주지 않으면 class 이름을 Table 이름으로 대체한다.
public class ViewBlog extends BaseEntity {
    @Id // PK를 의미하는 어노테이션
    @Column(name = "idx", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    //@Column(name="userIdx", nullable = false)
    //private Integer userIdx;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userIdx", nullable = false)
    private User user;

    @Column(name = "blogUrl", nullable = false, length = 45)
    private String blogUrl;

    public ViewBlog(User user, String blogUrl){
        this.user = user;
        this.blogUrl = blogUrl;
    }
}
