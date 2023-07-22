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
@Table(name = "Notice")
public class Notice extends BaseEntity {
    @Id
    @Column(name = "idx", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx; // Integer -> Long 21억 지나면 터지니까 Long

    @Column(name = "title", nullable = false, length = 128)
    private String title;

    @Column(name = "content", nullable = false, length = 2000)
    private String content;

    @Column(name = "activeYn", nullable = false, length = 1)
    private String activeYn = "Y";

    public Notice(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
