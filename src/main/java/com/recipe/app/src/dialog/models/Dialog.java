package com.recipe.app.src.dialog.models;

import com.recipe.app.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PUBLIC) // Unit Test 를 위해 PUBLIC
@EqualsAndHashCode(callSuper = false)
@Data // from lombok
@Entity // 필수, Class 를 Database Table화 해주는 것이다
@Table(name = "Dialog") // Table 이름을 명시해주지 않으면 class 이름을 Table 이름으로 대체한다.
public class Dialog extends BaseEntity {
    @Id // PK를 의미하는 어노테이션
    @Column(name = "idx", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx; // Integer -> Long 21억 지나면 터지니까 Long

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
