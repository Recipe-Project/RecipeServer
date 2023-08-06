package com.recipe.app.src.fridgeBasket.mapper;

import com.recipe.app.common.entity.BaseEntity;
import com.recipe.app.src.ingredient.infra.IngredientEntity;
import com.recipe.app.src.user.infra.UserEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "FridgeBasket")
public class FridgeBasketEntity extends BaseEntity {

    @Id
    @Column(name = "fridgeBasketId", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fridgeBasketId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredientId", nullable = false)
    private IngredientEntity ingredient;

    @Column(name = "expiredAt")
    private LocalDate expiredAt;

    @Column(name = "quantity", nullable = false)
    private float quantity = 1;

    @Column(name = "unit")
    private String unit;
}