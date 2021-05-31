package com.se.letscook.network;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.se.letscook.models.Recipe;

/**
 * Own Request to edit a Recipe via PUT
 */
public class PutRecipeRequest extends RecipeRequest
{

    public PutRecipeRequest(String url, Recipe recipe, Response.Listener<RecipeResponse> listener)
    {
        super(Method.PUT, url, recipe, listener);
    }

    /**
     * Handle the response of the request
     * In our case: RecipeResponse.SUCCESS if the request succeeded with a 200 Status-Code
     * @param response the response of the request, from the Volley-Framework
     * @return a response of Type RecipeResponse
     */
    @Override
    protected Response<RecipeResponse> parseNetworkResponse(NetworkResponse response)
    {
        RecipeResponse recipeResponse = RecipeResponse.UNDEFINED;
        if(response.statusCode == 200)
        {
            recipeResponse = RecipeResponse.SUCCESS;
        }
        return Response.success(recipeResponse, HttpHeaderParser.parseCacheHeaders(response));
    }


}
