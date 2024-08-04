package com.recipe.app.src.recipe.domain.keyword;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "Keywords")
public class SearchKeyword {

    @Id
    @Column(name = "keyword", nullable = false, updatable = false)
    private String keyword;
}
