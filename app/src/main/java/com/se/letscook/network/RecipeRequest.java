package com.se.letscook.network;

import com.android.volley.ClientError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.google.gson.Gson;
import com.se.letscook.models.MethodArgumentNotValidError;
import com.se.letscook.models.Recipe;
import com.se.letscook.network.converters.RecipeConverter;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

/**
 * abstract class to handle recipe-requests
 * includes encoding, error-handling and request-body-handling
 */
public abstract class RecipeRequest extends Request<RecipeResponse>
{

    private Recipe mRecipe;
    private Response.Listener<RecipeResponse> mListener;

    public RecipeRequest(int method, String url, Recipe recipe,
                         Response.Listener<RecipeResponse> listener)
    {
        super(method, url, onVolleyErrorListener(listener));
        this.mRecipe = recipe;
        this.mListener = listener;
    }

    public Recipe getRecipe()
    {
        return this.mRecipe;
    }

    @Override
    public String getBodyContentType()
    {
        return "application/json; charset=utf-8";
    }

    @Override
    public byte[] getBody()
    {
        String recipeString = new Gson().toJson(RecipeConverter.toGson(this.mRecipe));
        return recipeString == null ? null : recipeString.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    protected void deliverResponse(RecipeResponse response)
    {
        mListener.onResponse(response);
    }

    /**
     * Handles the failure of the request
     * We try to detect the error-type and return it as a normal RecipeResponse to the normal listener
     * If we do not know the error-type, it is a general RecipeResponse.ERROR
     * @param listener the normal listener
     * @return the error-handling as a lambda-method
     */
    private static Response.ErrorListener onVolleyErrorListener(Response.Listener<RecipeResponse> listener)
    {
        return error ->
        {
            RecipeResponse response = RecipeResponse.ERROR;
            if (error instanceof TimeoutError)
            {
                response = RecipeResponse.TIMEOUT;
            }else if(error instanceof ClientError){
                try {
                    JSONObject jsonResponse = new JSONObject(new String(error.networkResponse.data));
                    if(jsonResponse.get("type").equals("MethodArgumentNotValidException")){
                        MethodArgumentNotValidError methodArgumentNotValidError = new Gson().fromJson(jsonResponse.toString(), MethodArgumentNotValidError.class);
                        switch(methodArgumentNotValidError.getErrors()[0]){
                            case "description is empty or null":
                                response = RecipeResponse.MISSING_DESCRIPTION;
                                break;
                            case "title is empty or null":
                                response = RecipeResponse.MISSING_TITLE;
                                break;
                            case "people is null":
                                response = RecipeResponse.MISSING_PEOPLE;
                                break;
                            case "people is 0":
                                response = RecipeResponse.INVALID_PEOPLE;
                                break;
                            case "ingredients is null":
                                response = RecipeResponse.MISSING_INGREDIENTS;
                                break;
                            case "instructions is null":
                                response = RecipeResponse.MISSING_INSTRUCTIONS;
                                break;
                            default:
                                // still undefined
                        }
                    }
                } catch (JSONException e) {
                    // no valid Json -> Undefined
                }
            }
            listener.onResponse(response);
        };
    }

}
