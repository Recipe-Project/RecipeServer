package com.recipe.app.src.user.domain;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "JwtBlacklist")
public class JwtBlacklist {
    @Id
    @Column(name = "jwt", nullable = false, updatable = false)
    private String jwt;

    @CreatedDate
    @Column(name = "createdAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public JwtBlacklist(String jwt) {

        Preconditions.checkArgument(StringUtils.hasText(jwt), "블랙리스트에 등록할 JWT를 입력해주세요.");

        this.jwt = jwt;
    }
}
