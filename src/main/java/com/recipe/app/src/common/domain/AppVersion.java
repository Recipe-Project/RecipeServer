package com.recipe.app.src.common.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "AppVersion")
public class AppVersion {

    @Id
    @Column(name = "appVersionId", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appVersionId;

    @Column(name = "version", nullable = false, length = 20)
    private String version;
}
