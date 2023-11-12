package com.recipe.app.src.recipe.infra.keyword;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "Keywords")
public class SearchKeywordEntity {
    @Id
    @Column(name = "keyword", nullable = false, updatable = false)
    private String keyword;

}
