package com.recipe.app.src.recipe.domain.keyword;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 인기 검색어(추천 검색어) 후보 풀. Keywords 테이블에 매핑된다.
 * 실제 사용자 검색 로그는 {@link SearchKeyword} 에 별도로 쌓인다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "Keywords")
public class Keyword {

    @Id
    @Column(name = "keyword", nullable = false, updatable = false)
    private String keyword;
}
