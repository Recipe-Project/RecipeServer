package com.recipe.app.src.userRecipe.models;

import com.recipe.app.common.entity.BaseEntity;
import com.recipe.app.src.user.domain.User;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@NoArgsConstructor(access = AccessLevel.PUBLIC) // Unit Test 를 위해 PUBLIC
@EqualsAndHashCode(callSuper = false)
@Data // from lombok
@Entity // 필수, Class 를 Database Table화 해주는 것이다
@Table(name = "UserRecipe") // Table 이름을 명시해주지 않으면 class 이름을 Table 이름으로 대체한다.
public class UserRecipe extends BaseEntity {
    @Id // PK를 의미하는 어노테이션
    @Column(name = "userRecipeIdx", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userRecipeIdx;

    //@Column(name="userIdx", nullable = false)
    //private Integer userIdx;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userIdx", nullable = false)
    private User user;

    @Column(name = "thumbnail")
    private String thumbnail;

    @Column(name = "title", nullable = false, length = 45)
    private String title;

    @Column(name = "content",nullable = false, length = 1000)
    private String content;


    @Column(name="status", nullable=false, length=10)
    private String status="ACTIVE";

    public UserRecipe(User user, String thumbnail, String title, String content){
        this.user = user;
        this.thumbnail = thumbnail;
        this.title = title;
        this.content = content;
    }
}
