package com.recipe.app.src.userRecipe.domain;

import com.recipe.app.common.entity.BaseEntity;
import com.recipe.app.src.user.infra.UserEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import static com.recipe.app.common.response.BaseResponseStatus.EMPTY_CONTENT;
import static com.recipe.app.common.response.BaseResponseStatus.EMPTY_TITLE;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "UserRecipe")
public class UserRecipe extends BaseEntity {
    @Id
    @Column(name = "userRecipeIdx", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userRecipeIdx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userIdx", nullable = false)
    private UserEntity user;

    @Column(name = "thumbnail")
    private String thumbnail;

    @Column(name = "title", nullable = false, length = 45)
    private String title;

    @Column(name = "content", nullable = false, length = 1000)
    private String content;

    @Column(name = "status", nullable = false, length = 10)
    private String status = "ACTIVE";

    @OneToMany(mappedBy = "userRecipe", cascade = CascadeType.ALL)
    private List<UserRecipeIngredient> userRecipeIngredients = new ArrayList<>();

    public UserRecipe(UserEntity user, String thumbnail, String title, String content) {
        if (!StringUtils.hasText(title)) {
            throw new InvalidParameterException(EMPTY_TITLE.getMessage());
        }
        if (!StringUtils.hasText(content)) {
            throw new InvalidParameterException(EMPTY_CONTENT.getMessage());
        }
        this.user = user;
        this.thumbnail = thumbnail;
        this.title = title;
        this.content = content;
    }

    public void changeUserRecipe(String thumbnail, String title, String content) {
        this.thumbnail = thumbnail;
        this.title = title;
        this.content = content;
    }
}
