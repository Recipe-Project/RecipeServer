package com.recipe.app.src.keyword.domain;

import com.recipe.app.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "RecipeKeyword")
public class RecipeKeyword extends BaseEntity {
    @Id
    @Column(name = "idx", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    @Column(name = "keyword", nullable = false, length = 50)
    private String keyword;

    @Column(name = "status", nullable = false, length = 10)
    private String status = "ACTIVE";

    public RecipeKeyword(String keyword) {
        this.keyword = keyword;
    }
}
