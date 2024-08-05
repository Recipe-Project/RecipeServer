package com.recipe.app.src.etc.domain;

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
@Table(name = "BadWords")
public class BadWord {

    @Id
    @Column(name = "word", nullable = false, updatable = false)
    private String word;
}
