package com.se.letscook;

import com.android.volley.ClientError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.se.letscook.models.Recipe;
import com.se.letscook.network.PostRecipeRequest;
import com.se.letscook.network.RecipeResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PostRecipeRequestTest {

    @Test
    public void someErrorOccured(){
        Response.Listener listener = Mockito.mock(Response.Listener.class);
        PostRecipeRequest postRequest = new PostRecipeRequest("myUrl", new Recipe(), listener);
        postRequest.deliverError(new VolleyError());

        Mockito.verify(listener).onResponse(RecipeResponse.ERROR);
    }

    @Test
    public void timeoutOccured(){
        Response.Listener listener = Mockito.mock(Response.Listener.class);
        PostRecipeRequest postRequest = new PostRecipeRequest("myUrl", new Recipe(), listener);
        postRequest.deliverError(new TimeoutError());

        Mockito.verify(listener).onResponse(RecipeResponse.TIMEOUT);
    }

    @Test
    public void descriptionMissing(){
        Response.Listener listener = Mockito.mock(Response.Listener.class);
        PostRecipeRequest postRequest = new PostRecipeRequest("myUrl", new Recipe(), listener);
        postRequest.deliverError(new ClientError(new NetworkResponse(new String("{\"type\":\"MethodArgumentNotValidException\",\"errors\":[\"description is empty or null\",\"title is empty or null\"],\"status\":400}").getBytes())));

        Mockito.verify(listener).onResponse(RecipeResponse.MISSING_DESCRIPTION);
    }

    @Test
    public void titleMissing(){
        Response.Listener listener = Mockito.mock(Response.Listener.class);
        PostRecipeRequest postRequest = new PostRecipeRequest("myUrl", new Recipe(), listener);
        postRequest.deliverError(new ClientError(new NetworkResponse(new String("{\"type\":\"MethodArgumentNotValidException\",\"errors\":[\"title is empty or null\"],\"status\":400}").getBytes())));

        Mockito.verify(listener).onResponse(RecipeResponse.MISSING_TITLE);
    }

    @Test
    public void peopleMissing(){
        Response.Listener listener = Mockito.mock(Response.Listener.class);
        PostRecipeRequest postRequest = new PostRecipeRequest("myUrl", new Recipe(), listener);
        postRequest.deliverError(new ClientError(new NetworkResponse(new String("{\"type\":\"MethodArgumentNotValidException\",\"errors\":[\"people is null\"],\"status\":400}").getBytes())));

        Mockito.verify(listener).onResponse(RecipeResponse.MISSING_PEOPLE);
    }

    @Test
    public void peopleInvalid(){
        Response.Listener listener = Mockito.mock(Response.Listener.class);
        PostRecipeRequest postRequest = new PostRecipeRequest("myUrl", new Recipe(), listener);
        postRequest.deliverError(new ClientError(new NetworkResponse(new String("{\"type\":\"MethodArgumentNotValidException\",\"errors\":[\"people is 0\"],\"status\":400}").getBytes())));

        Mockito.verify(listener).onResponse(RecipeResponse.INVALID_PEOPLE);
    }

    @Test
    public void ingredientsMissing(){
        Response.Listener listener = Mockito.mock(Response.Listener.class);
        PostRecipeRequest postRequest = new PostRecipeRequest("myUrl", new Recipe(), listener);
        postRequest.deliverError(new ClientError(new NetworkResponse(new String("{\"type\":\"MethodArgumentNotValidException\",\"errors\":[\"ingredients is null\"],\"status\":400}").getBytes())));

        Mockito.verify(listener).onResponse(RecipeResponse.MISSING_INGREDIENTS);
    }

    @Test
    public void instructionsMissing(){
        Response.Listener listener = Mockito.mock(Response.Listener.class);
        PostRecipeRequest postRequest = new PostRecipeRequest("myUrl", new Recipe(), listener);
        postRequest.deliverError(new ClientError(new NetworkResponse(new String("{\"type\":\"MethodArgumentNotValidException\",\"errors\":[\"instructions is null\"],\"status\":400}").getBytes())));

        Mockito.verify(listener).onResponse(RecipeResponse.MISSING_INSTRUCTIONS);
    }
}
