package com.recipe.app.src.userRecipePhoto.models;

import com.recipe.app.config.BaseEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@NoArgsConstructor(access = AccessLevel.PUBLIC) // Unit Test 를 위해 PUBLIC
@EqualsAndHashCode(callSuper = false)
@Data // from lombok
@Entity // 필수, Class 를 Database Table화 해주는 것이다
@Table(name = "UserRecipePhoto") // Table 이름을 명시해주지 않으면 class 이름을 Table 이름으로 대체한다.
public class UserRecipePhoto extends BaseEntity {
    @Id // PK를 의미하는 어노테이션
    @Column(name = "imgIdx", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer imgIdx;

    @Column(name="userRecipeIdx", nullable = false)
    private Integer userRecipeIdx;

    @Column(name = "photoUrl")
    private String photoUrl;

    @Column(name="status", nullable=false, length=10)
    private String status="ACTIVE";

    public UserRecipePhoto(Integer userRecipeIdx, String photoUrl){
        this.userRecipeIdx = userRecipeIdx;
        this.photoUrl = photoUrl;
    }
}
