package com.recipe.app.src.user.infra;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "JwtBlacklist")
public class JwtEntity {
    @Id
    @Column(name = "jwt", nullable = false, updatable = false)
    private String jwt;

    @CreatedDate
    @Column(name = "createdAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public JwtEntity(String jwt) {
        this.jwt = jwt;
    }

    public static JwtEntity fromModel(String jwt) {
        JwtEntity jwtEntity = new JwtEntity();
        jwtEntity.jwt = jwt;
        return jwtEntity;
    }
}
