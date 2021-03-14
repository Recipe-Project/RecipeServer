package com.recipe.app.src.scrapPublic.models;

import com.recipe.app.src.recipeInfo.models.RecipeInfo;
import com.recipe.app.src.user.models.User;
import lombok.*;
import javax.persistence.*;
import com.recipe.app.config.BaseEntity;

import java.sql.Date;

@NoArgsConstructor(access = AccessLevel.PUBLIC) // Unit Test 를 위해 PUBLIC
@EqualsAndHashCode(callSuper = false)
@Data // from lombok
@Entity // 필수, Class 를 Database Table화 해주는 것이다
@Table(name = "ScrapPublic") // Table 이름을 명시해주지 않으면 class 이름을 Table 이름으로 대체한다.
public class ScrapPublic extends BaseEntity {
    @Id // PK를 의미하는 어노테이션
    @Column(name = "idx", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    //@Column(name="userIdx", nullable = false)
    //private Integer userIdx;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userIdx", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publicIdx", nullable = false)
    private RecipeInfo recipeInfo;

    @Column(name="status", nullable=false, length=10)
    private String status="ACTIVE";

    public ScrapPublic(User user, RecipeInfo recipeInfo){
        this.user = user;
        this.recipeInfo = recipeInfo;
    }

}
