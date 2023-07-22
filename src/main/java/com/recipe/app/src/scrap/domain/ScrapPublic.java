package com.recipe.app.src.scrap.domain;

import com.recipe.app.common.entity.BaseEntity;
import com.recipe.app.src.recipe.domain.RecipeInfo;
import com.recipe.app.src.user.domain.User;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "ScrapPublic")
public class ScrapPublic extends BaseEntity {
    @Id
    @Column(name = "idx", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userIdx", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publicIdx", nullable = false)
    private RecipeInfo recipeInfo;

    @Column(name = "status", nullable = false, length = 10)
    private String status = "ACTIVE";

    public ScrapPublic(User user, RecipeInfo recipeInfo) {
        this.user = user;
        this.recipeInfo = recipeInfo;
    }

}
