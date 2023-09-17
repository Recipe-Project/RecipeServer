package com.recipe.app.src.common.domain;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "BadWords")
public class BadWords {
    @Id
    @Column(name = "word", nullable = false, updatable = false)
    private String word;
}
