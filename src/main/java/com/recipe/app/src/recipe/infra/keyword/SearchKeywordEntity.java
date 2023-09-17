package com.recipe.app.src.recipe.infra.keyword;

import com.recipe.app.src.user.infra.UserEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "SearchKeyword")
public class SearchKeywordEntity {
    @Id
    @Column(name = "searchKeywordId", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long searchKeywordId;

    @Column(name = "keyword", nullable = false, length = 64)
    private String keyword;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private UserEntity user;

    @CreatedDate
    @Column(name = "createdAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static SearchKeywordEntity fromModel(String keyword, UserEntity user) {
        SearchKeywordEntity searchKeywordEntity = new SearchKeywordEntity();
        searchKeywordEntity.keyword = keyword;
        searchKeywordEntity.user = user;
        return searchKeywordEntity;
    }
}
