package com.recipe.app.src.recipe.infra;

import com.recipe.app.src.recipe.domain.RecipeLevel;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class RecipeLevelPersistConverter implements AttributeConverter<RecipeLevel, String> {


    @Override
    public String convertToDatabaseColumn(RecipeLevel attribute) {

        if (attribute == null) {
            throw new IllegalArgumentException("RecipeLevel이 null입니다.");
        }

        return attribute.getCode();
    }

    @Override
    public RecipeLevel convertToEntityAttribute(String dbData) {

        if (dbData == null || dbData.isBlank()) {
            throw new IllegalArgumentException("RecipeLevel이 비어있습니다.");
        }

        return RecipeLevel.findRecipeLevelByCode(dbData);
    }
}
