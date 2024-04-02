package com.recipe.app.src.recipe.domain.keyword;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "Keywords")
public class SearchKeyword {

    @Id
    @Column(name = "keyword", nullable = false, updatable = false)
    private String keyword;
}
