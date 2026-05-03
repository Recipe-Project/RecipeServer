package com.recipe.app.src.ingredient.domain;

import com.google.common.base.Preconditions;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "IngredientSynonym")
public class IngredientSynonym {

    @Id
    @Column(name = "synonymId", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long synonymId;

    @Column(name = "groupId", nullable = false)
    private Long groupId;

    @Column(name = "name", nullable = false, length = 64, unique = true)
    private String name;

    @Builder
    public IngredientSynonym(Long synonymId, Long groupId, String name) {

        Preconditions.checkNotNull(groupId, "동의어 그룹 아이디를 입력해주세요.");
        Preconditions.checkArgument(StringUtils.hasText(name), "동의어 단어를 입력해주세요.");

        this.synonymId = synonymId;
        this.groupId = groupId;
        this.name = name;
    }
}
