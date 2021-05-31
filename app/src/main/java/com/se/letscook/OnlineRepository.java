package com.se.letscook;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.se.letscook.models.Image;
import com.se.letscook.models.Recipe;
import com.se.letscook.network.BitmapConverter;
import com.se.letscook.network.PostRecipeRequest;
import com.se.letscook.network.PutRecipeRequest;
import com.se.letscook.network.RecipeResponse;
import com.se.letscook.network.RequestQueueHandler;
import com.se.letscook.network.UTF8StringRequest;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of RepositoryInterface
 */
public class OnlineRepository implements RepositoryInterface {

    private MutableLiveData<List<Recipe>> recipes;
    private MutableLiveData<Recipe> singleRecipe;
    private Context context;

    private static final String BASE_URL = "https://lets-cook-backend-zany-oryx-tj.cfapps.io/";
    private static final String RECIPES_URL = BASE_URL + "api/recipes/";
    private static final String IMAGES_URL_END = "/image";

    private OnlineRepository(Context context) {
        recipes = new MutableLiveData<>();
        singleRecipe = new MutableLiveData<>();
        this.context = context;
    }

    private static String getRecipeURL(String recipeId) {
        return RECIPES_URL + recipeId;
    }

    private static String getRecipeImageURL(String recipeId) {
        return getRecipeURL(recipeId) + IMAGES_URL_END;
    }

    static OnlineRepository getRepository(Context context) {
        return new OnlineRepository(context);
    }

    @Override
    public LiveData<List<Recipe>> getAllRecipesWithoutUpdating() {
        return recipes;
    }

    @Override
    public LiveData<List<Recipe>> getAllRecipes() {
        fetchAllRecipes();
        return recipes;
    }

    @Override
    public LiveData<Recipe> getRecipe(String id) {
        fetchSingleRecipe(id);
        return singleRecipe;
    }

    private void fetchAllRecipes() {
        // Instantiate the RequestQueue.
        Log.d("Debug", "FetchAllRecipes");
        RequestQueue queue = RequestQueueHandler.getInstance(context).getRequestQueue();
        String url = RECIPES_URL;

        // Request a string response from the provided URL.
        UTF8StringRequest stringRequest = new UTF8StringRequest(Request.Method.GET, url,
                response ->
                {
                    if (response == null || response.isEmpty()) {
                        return;
                    }
                    Type type = new TypeToken<List<Recipe>>() {
                    }.getType();
                    List<Recipe> recipeList = new Gson().fromJson(response, type);
                    recipes.postValue(recipeList);
                    // get images one by one
                    fetchAllImages(recipeList);
                }, error -> recipes.postValue(new ArrayList<>()));

        // Access the RequestQueue
        queue.add(stringRequest);
        queue.start();
    }

    private void fetchSingleRecipe(String id) {
        // Instantiate the RequestQueue.
        RequestQueue queue = RequestQueueHandler.getInstance(context).getRequestQueue();
        String url = getRecipeURL(id);

        // Request a string response from the provided URL.
        UTF8StringRequest stringRequest = new UTF8StringRequest(Request.Method.GET, url,
                response ->
                {
                    if (response == null || response.isEmpty()) {
                        return;
                    }
                    Recipe recipe = new Gson().fromJson(response, Recipe.class);
                    singleRecipe.postValue(recipe);
                    // get image
                    fetchSingleImage(recipe);
                },
                error -> singleRecipe.postValue(null));

        // Access the RequestQueue
        queue.add(stringRequest);
        queue.start();
    }

    private void fetchAllImages(List<Recipe> recipeList) {
        // Instantiate the RequestQueue.
        RequestQueue queue = RequestQueueHandler.getInstance(context).getRequestQueue();

        for (Recipe recipe : recipeList) {
            String url = getRecipeImageURL(recipe.getId());

            // Request a string response from the provided URL.
            UTF8StringRequest stringRequest = new UTF8StringRequest(Request.Method.GET, url,
                    response ->
                    {
                        if (response == null || response.isEmpty()) {
                            return;
                        }
                        Image imageString = new Gson().fromJson(response, Image.class);
                        Bitmap image = BitmapConverter.hexStringToBitmap(imageString.imageAsString);
                        recipe.setImage(image);
                        recipes.postValue(recipeList);
                    }, error ->
            {
                // Image stays null
            });

            // Access the RequestQueue
            queue.add(stringRequest);
        }
    }

    private void fetchSingleImage(Recipe recipe) {
        // Instantiate the RequestQueue.
        RequestQueue queue = RequestQueueHandler.getInstance(context).getRequestQueue();

        String url = getRecipeImageURL(recipe.getId());

        // Request a string response from the provided URL.
        UTF8StringRequest stringRequest = new UTF8StringRequest(Request.Method.GET, url,
                response ->
                {
                    if (response == null || response.isEmpty()) {
                        return;
                    }
                    Image imageString = new Gson().fromJson(response, Image.class);
                    Bitmap image = BitmapConverter.hexStringToBitmap(imageString.imageAsString);
                    recipe.setImage(image);
                    singleRecipe.postValue(recipe);
                }, error ->
        {
            // Image stays null
        });

        // Access the RequestQueue
        queue.add(stringRequest);
    }

    @Override
    public LiveData<Bitmap> fetchSingleImage(String id) {
        MutableLiveData<Bitmap> liveData = new MutableLiveData<>();

        // Instantiate the RequestQueue.
        RequestQueue queue = RequestQueueHandler.getInstance(context).getRequestQueue();

        String url = getRecipeImageURL(id);

        // Request a string response from the provided URL.
        UTF8StringRequest stringRequest = new UTF8StringRequest(Request.Method.GET, url,
                response ->
                {
                    Image imageString = new Gson().fromJson(response, Image.class);
                    Bitmap bitmap = BitmapConverter.hexStringToBitmap(imageString.imageAsString);
                    liveData.postValue(bitmap);
                },
                error -> liveData.postValue(null));

        // Access the RequestQueue
        queue.add(stringRequest);
        queue.start();
        return liveData;
    }

    @Override
    public LiveData<RecipeResponse> postRecipe(Recipe recipe) {
        MutableLiveData<RecipeResponse> responseLiveData = new MutableLiveData<>();

        // Instantiate the RequestQueue.
        RequestQueue requestQueue = RequestQueueHandler.getInstance(context).getRequestQueue();

        // post recipe and request a RecipeResponse response for the provided URL.
        PostRecipeRequest postRequest = new PostRecipeRequest(RECIPES_URL, recipe,
                response -> responseLiveData.postValue(response));

        // Access the RequestQueue
        requestQueue.add(postRequest);
        requestQueue.start();
        return responseLiveData;
    }

    @Override
    public LiveData<RecipeResponse> putRecipe(Recipe recipe) {
        MutableLiveData<RecipeResponse> responseLiveData = new MutableLiveData<>();

        // Instantiate the RequestQueue.
        RequestQueue requestQueue = RequestQueueHandler.getInstance(context).getRequestQueue();

        // put recipe and request a RecipeResponse response for the provided URL.
        PutRecipeRequest putRequest = new PutRecipeRequest(RECIPES_URL, recipe,
                response -> responseLiveData.postValue(response));

        // Access the RequestQueue
        requestQueue.add(putRequest);
        requestQueue.start();
        return responseLiveData;
    }

    @Override
    public LiveData<Boolean> deleteRecipe(String id) {
        MutableLiveData<Boolean> responseLiveData = new MutableLiveData<>();

        // Instantiate the RequestQueue.
        RequestQueue queue = RequestQueueHandler.getInstance(context).getRequestQueue();
        String url = getRecipeURL(id);

        // Request a string response from the provided URL.
        UTF8StringRequest stringRequest = new UTF8StringRequest(Request.Method.DELETE, url,
                response -> responseLiveData.postValue(true),
                error -> responseLiveData.postValue(false));

        // Access the RequestQueue
        queue.add(stringRequest);
        queue.start();
        return responseLiveData;
    }


}
