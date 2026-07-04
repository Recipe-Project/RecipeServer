package com.recipe.app.src.recipe.domain.keyword;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 사용자 검색 로그. 검색이 일어날 때마다 1건씩 쌓인다 (원본 append-log).
 * 인기 검색어 승격/집계는 이 로그를 나중에 가공해서 처리한다. (지금은 쌓기만)
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "SearchKeyword")
public class SearchKeyword {

    @Id
    @Column(name = "searchKeywordId", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long searchKeywordId;

    @Column(name = "keyword", nullable = false)
    private String keyword;

    @Column(name = "userId")
    private Long userId;

    @CreatedDate
    @Column(name = "createdAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public SearchKeyword(Long searchKeywordId, String keyword, Long userId) {
        this.searchKeywordId = searchKeywordId;
        this.keyword = keyword;
        this.userId = userId;
    }
}
