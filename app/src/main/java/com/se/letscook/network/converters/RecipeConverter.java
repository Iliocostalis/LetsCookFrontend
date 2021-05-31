package com.se.letscook.network.converters;

import com.se.letscook.models.Recipe;
import com.se.letscook.network.BitmapConverter;
import com.se.letscook.network.gsons.GsonRecipe;

/**
 * Utils-Class to convert a Recipe to a GsonRecipe
 */
public class RecipeConverter
{

    private RecipeConverter()
    {
        // Privaet Constructor for Utils class
    }

    public static GsonRecipe toGson(Recipe recipe)
    {
        String imageString = null;
        if(recipe.getImage() != null)
        {
            imageString = BitmapConverter.bitmapToHexString(recipe.getImage());
        }
        return new GsonRecipe(recipe.getId(), recipe.getTitle(), recipe.getAmountPeople(),
                recipe.getDescription(), recipe.getIngredients(), recipe.getInstructions(),
                imageString);
    }

}
