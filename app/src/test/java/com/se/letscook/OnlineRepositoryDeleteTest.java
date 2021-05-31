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
import com.se.letscook.network.RequestQueueHandler;
import com.se.letscook.network.UTF8StringRequest;

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
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.any;

@RunWith(PowerMockRunner.class)
@PrepareForTest({RequestQueueHandler.class, OnlineRepository.class})
public class OnlineRepositoryDeleteTest {

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

        // Save Mock-UTF8StringRequest with linked Data and return Mock-UTF8StringRequest
        PowerMockito.whenNew(UTF8StringRequest.class).withAnyArguments().then(invocation -> {
            UTF8StringRequest mockRequest = Mockito.mock(UTF8StringRequest.class);
            testHelper.saveRequest(mockRequest, invocation);
            return mockRequest;
        });

        // Add UTF8StringRequest to Queue (like normal RequestQueue.add(Request)) -> Handled in TestHelper
        Mockito.when(mockRequestQueue.add(any(UTF8StringRequest.class))).thenAnswer(invocation -> {
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
    public void correctApiCallForDelete() {
        testHelper.setReturnJson(new String[]{"{\"id\": \"1234\",\"message\": \"Deleted recipe with id [1234] in database\",\"status\": 200}"});
        OnlineRepository.getRepository(con).deleteRecipe("1234");

        // List of expected executed Requests
        ArrayList<OnlineRepositoryTestHelper.RequestData> executedRequests = new ArrayList<>();
        executedRequests.add(new OnlineRepositoryTestHelper.RequestData(Request.Method.DELETE, "https://lets-cook-backend-zany-oryx-tj.cfapps.io/api/recipes/1234"));

        testHelper.assertIfExecutedRequestsAreEqual(executedRequests);
    }

    @Test
    public void correctResponseForDelete() {
        // JSON returned, when Requests are executed
        testHelper.setReturnJson(new String[]{"{\"id\": \"1234\",\"message\": \"Deleted recipe with id [1234] in database\",\"status\": 200}"});

        Observer mockObserver = Mockito.mock(Observer.class);
        OnlineRepository.getRepository(con).deleteRecipe("1234").observeForever(mockObserver);

        // get response for Listener
        ArgumentCaptor<Boolean> actualResponse = ArgumentCaptor.forClass(Boolean.class);
        Mockito.verify(mockObserver).onChanged(actualResponse.capture());

        Assert.assertTrue(actualResponse.getValue());
    }
}
