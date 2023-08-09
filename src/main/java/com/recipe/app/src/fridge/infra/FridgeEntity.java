package com.recipe.app.src.fridge.infra;

import com.recipe.app.common.entity.BaseEntity;
import com.recipe.app.src.fridge.domain.Fridge;
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
@Table(name = "Fridge")
public class FridgeEntity extends BaseEntity {
    @Id
    @Column(name = "fridgeId", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fridgeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredientId", nullable = false)
    private IngredientEntity ingredient;

    @Column(name = "expiredAt")
    private LocalDate expiredAt;

    @Column(name = "quantity", nullable = false)
    private float quantity;

    @Column(name = "unit")
    private String unit;

    public static FridgeEntity fromModel(Fridge fridge) {
        FridgeEntity fridgeEntity = new FridgeEntity();
        fridgeEntity.fridgeId = fridge.getFridgeId();
        fridgeEntity.user = UserEntity.fromModel(fridge.getUser());
        fridgeEntity.ingredient = IngredientEntity.fromModel(fridge.getIngredient());
        fridgeEntity.expiredAt = fridge.getExpiredAt();
        fridgeEntity.quantity = fridge.getQuantity();
        fridgeEntity.unit = fridge.getUnit();
        return fridgeEntity;
    }

    public Fridge toModel() {
        return Fridge.builder()
                .fridgeId(fridgeId)
                .user(user.toModel())
                .ingredient(ingredient.toModel())
                .expiredAt(expiredAt)
                .quantity(quantity)
                .unit(unit)
                .build();
    }
}