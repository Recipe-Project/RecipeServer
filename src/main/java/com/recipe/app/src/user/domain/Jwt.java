package com.recipe.app.src.user.domain;

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
@Table(name = "JwtBlacklist")
public class Jwt extends BaseEntity {
    @Id
    @Column(name = "jwt", nullable = false, updatable = false)
    private String jwt;
}
