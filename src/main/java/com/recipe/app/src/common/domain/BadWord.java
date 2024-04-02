package com.recipe.app.src.common.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "BadWords")
public class BadWord {

    @Id
    @Column(name = "word", nullable = false, updatable = false)
    private String word;
}
