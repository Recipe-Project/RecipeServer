package com.recipe.app.src.common.domain;

import com.recipe.app.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "Dialog")
public class Dialog extends BaseEntity {
    @Id
    @Column(name = "idx", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idx;

    @Column(name = "title", nullable = false, length = 128)
    private String title;

    @Column(name = "content", nullable = false, length = 2000)
    private String content;

    @Column(name = "link", nullable = true, length = 1000)
    private String link;

    @Column(name = "activeYn", nullable = false, length = 1)
    private String activeYn = "Y";

    public Dialog(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
