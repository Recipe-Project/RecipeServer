package com.recipe.app.src.common.domain;

import com.recipe.app.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "Dialog")
public class Dialog extends BaseEntity {

    @Id
    @Column(name = "dialogId", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dialogId;

    @Column(name = "title", nullable = false, length = 128)
    private String title;

    @Column(name = "content", nullable = false, length = 2000)
    private String content;

    @Column(name = "link", length = 1000)
    private String link;

    @Column(name = "activeYn", nullable = false, length = 1)
    private String activeYn = "Y";
}
