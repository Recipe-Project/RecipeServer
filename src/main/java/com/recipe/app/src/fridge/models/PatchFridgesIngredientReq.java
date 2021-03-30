package com.recipe.app.src.fridge.models;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
public class PatchFridgesIngredientReq {
    private List<PatchFridgeList> patchFridgeList;
}
