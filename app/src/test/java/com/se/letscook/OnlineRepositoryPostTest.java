package com.se.letscook;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.se.letscook.models.Ingredient;
import com.se.letscook.models.IngredientUnit;
import com.se.letscook.models.Instruction;
import com.se.letscook.models.Recipe;
import com.se.letscook.network.PostRecipeRequest;
import com.se.letscook.network.RecipeResponse;
import com.se.letscook.network.RequestQueueHandler;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;

import static org.mockito.Matchers.any;

@RunWith(PowerMockRunner.class)
@PrepareForTest({RequestQueueHandler.class, OnlineRepository.class})
public class OnlineRepositoryPostTest {

    @Rule
    InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    private Context con;

    private OnlineRepositoryTestHelper testHelper;

    @Before
    public void setUp() throws Exception {
        con = Mockito.mock(Context.class);
        testHelper = new OnlineRepositoryTestHelper();

        // Get fake RequestQueue over fake RequestQueueHandler
        PowerMockito.mockStatic(RequestQueueHandler.class);

        RequestQueueHandler mockRequestQueueHandler = Mockito.mock(RequestQueueHandler.class);
        RequestQueue mockRequestQueue = Mockito.mock(RequestQueue.class);

        PowerMockito.when(RequestQueueHandler.getInstance(any())).thenReturn(mockRequestQueueHandler);
        Mockito.when(mockRequestQueueHandler.getRequestQueue()).thenReturn(mockRequestQueue);

        // Save Mock-PostRecipeRequest with linked Data and return Mock-PostRecipeRequest
        PowerMockito.whenNew(PostRecipeRequest.class).withAnyArguments().then(invocation -> {
            PostRecipeRequest mockRequest = Mockito.mock(PostRecipeRequest.class);
            testHelper.saveRequest(mockRequest, invocation);
            return mockRequest;
        });

        // Add PostRecipeRequest to Queue (like normal RequestQueue.add(Request)) -> Handled in TestHelper
        Mockito.when(mockRequestQueue.add(any(PostRecipeRequest.class))).thenAnswer(invocation -> {
            testHelper.addToQueue(invocation.getArgument(0));
            return null;
        });

        // Start RequestQueue (like normal RequestQueue.start() -> Handled in TestHelper
        Mockito.doAnswer(invocation -> {
            testHelper.executeQueue();
            return null;
        }).when(mockRequestQueue).start();
    }

    @Test
    public void correctApiCallForPostRecipe() {
        testHelper.setReturnResponse(new RecipeResponse[]{RecipeResponse.UNDEFINED});

        ArrayList<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient(3, IngredientUnit.GRAMM, "Salz"));
        ArrayList<Instruction> instructions = new ArrayList<>();
        instructions.add(new Instruction("Anweisung"));
        Recipe recipe = new Recipe("Titel", 2, "Beschreibung", ingredients, instructions, null);

        OnlineRepository.getRepository(con).postRecipe(recipe);

        // List of expected executed Requests
        ArrayList<OnlineRepositoryTestHelper.RequestData> executedRequests = new ArrayList<>();
        executedRequests.add(new OnlineRepositoryTestHelper.RequestData(Request.Method.POST, "https://lets-cook-backend-zany-oryx-tj.cfapps.io/api/recipes/", recipe));

        testHelper.assertIfExecutedRequestsAreEqual(executedRequests);
    }

    @Test
    public void correctEnumForPostRecipe() {
        // JSON returned, when Requests are executed
        testHelper.setReturnResponse(new RecipeResponse[]{RecipeResponse.SUCCESS});

        ArrayList<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient(3, IngredientUnit.GRAMM, "Salz"));
        ArrayList<Instruction> instructions = new ArrayList<>();
        instructions.add(new Instruction("Anweisung"));
        Recipe recipe = new Recipe("Titel", 2, "Beschreibung", ingredients, instructions, null);

        Observer mockObserver = Mockito.mock(Observer.class);
        OnlineRepository.getRepository(con).postRecipe(recipe).observeForever(mockObserver);

        // get response for Listener
        ArgumentCaptor<RecipeResponse> actualResponse = ArgumentCaptor.forClass(RecipeResponse.class);
        Mockito.verify(mockObserver).onChanged(actualResponse.capture());

        //Sorting to compare Lists with default .equals Method.
        RecipeResponse actual = actualResponse.getValue();

        Assert.assertEquals(RecipeResponse.SUCCESS, actual);
    }

}
