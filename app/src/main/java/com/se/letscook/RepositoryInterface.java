package com.se.letscook;

import android.arch.lifecycle.LiveData;
import android.graphics.Bitmap;

import com.se.letscook.models.Recipe;
import com.se.letscook.network.RecipeResponse;

import java.util.List;

/**
 * Interface to interact with the backend-server
 */
public interface RepositoryInterface
{

    LiveData<List<Recipe>> getAllRecipesWithoutUpdating();

    LiveData<List<Recipe>> getAllRecipes();

    LiveData<Recipe> getRecipe(String id);

    LiveData<Bitmap> fetchSingleImage(String id);

    LiveData<RecipeResponse> postRecipe(Recipe recipe);

    LiveData<RecipeResponse> putRecipe(Recipe recipe);

    LiveData<Boolean> deleteRecipe(String id);
}
