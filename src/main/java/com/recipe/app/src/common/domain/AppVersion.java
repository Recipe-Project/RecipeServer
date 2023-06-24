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
@Table(name = "AppVersion")
public class AppVersion {
    @Id
    @Column(name = "idx", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idx;

    @Column(name = "version", nullable = false, length = 20)
    private String version;

    public AppVersion(String version) {
        this.version = version;
    }
}
