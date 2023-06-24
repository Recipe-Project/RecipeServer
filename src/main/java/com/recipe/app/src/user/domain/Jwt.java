package com.recipe.app.src.user.domain;

import com.recipe.app.common.entity.BaseEntity;
import lombok.*;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "JwtBlacklist")
public class Jwt extends BaseEntity {
    @Id
    @Column(name = "jwt", nullable = false, updatable = false)
    private String jwt;
}
