package com.se.letscook;

import android.support.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.Gson;
import com.se.letscook.models.Recipe;
import com.se.letscook.network.PostRecipeRequest;
import com.se.letscook.network.PutRecipeRequest;
import com.se.letscook.network.RecipeRequest;
import com.se.letscook.network.RecipeResponse;
import com.se.letscook.network.UTF8StringRequest;

import junit.framework.Assert;

import org.mockito.invocation.InvocationOnMock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

class OnlineRepositoryTestHelper {

    private boolean queueStarted;
    private HashMap<Request, HelpRequest> requestMap;
    private ArrayList<Request> requestQueue;
    private ArrayList<RequestData> executedRequests;

    private ArrayList<String> returnJson;
    private ArrayList<RecipeResponse> returnResponse;


    OnlineRepositoryTestHelper(){
        queueStarted = false;

        requestMap = new HashMap<>();
        requestQueue = new ArrayList<>();
        executedRequests = new ArrayList<>();

        this.returnJson = new ArrayList<>();
        this.returnResponse = new ArrayList<>();
    }

    // JSON returned, when Requests are executed
    void setReturnJson(String[] responses) {
        returnJson = new ArrayList<>();
        Collections.addAll(returnJson, responses);
    }
    void setReturnResponse(RecipeResponse[] responses){
        returnResponse = new ArrayList<>();
        Collections.addAll(returnResponse, responses);
    }

    // Save Request-Data with corresponding Mock
    void saveRequest(Request mockRequest, InvocationOnMock invocationStringRequest){
        HelpRequest helpReq = null;
        if(mockRequest instanceof UTF8StringRequest){
            helpReq = new HelpStringRequest(invocationStringRequest.getArgument(0), invocationStringRequest.getArgument(1), invocationStringRequest.getArgument(2), invocationStringRequest.getArgument(3));
        }else if(mockRequest instanceof RecipeRequest){
            int method = -1;
            if(mockRequest instanceof PostRecipeRequest){
                method = Request.Method.POST;
            }else if(mockRequest instanceof PutRecipeRequest){
                method = Request.Method.PUT;
            }
            helpReq = new HelpRecipeRequest(method, invocationStringRequest.getArgument(0), invocationStringRequest.getArgument(2), invocationStringRequest.getArgument(1));
        }
        requestMap.put(mockRequest, helpReq);
    }

    void addToQueue(Request fakeStringRequest){
        requestQueue.add(fakeStringRequest);
        if(queueStarted){
            executeQueue();
        }
    }

    void executeQueue(){
        queueStarted = true;
        ArrayList<HelpRequest> toExecute = new ArrayList<>();

        for (Request request: requestQueue) {
            toExecute.add(requestMap.get(request));
            requestMap.remove(request);
        }

        requestQueue.clear();

        for (HelpRequest request: toExecute) {
            if(request instanceof HelpStringRequest){
                request.listener.onResponse(returnJson.remove(0));
                executedRequests.add(new RequestData(request.verb, request.url));
            } else if(request instanceof HelpRecipeRequest){
                request.listener.onResponse(returnResponse.remove(0));
                executedRequests.add(new RequestData(request.verb, request.url, ((HelpRecipeRequest) request).body));
            }
        }
    }

    void assertIfExecutedRequestsAreEqual(ArrayList<RequestData> expectedRequests){
        Collections.sort(expectedRequests);
        Collections.sort(executedRequests);
        Gson gson = new Gson();
        Assert.assertEquals(gson.toJson(expectedRequests), gson.toJson(executedRequests));
    }

    static abstract class HelpRequest{
        int verb;
        String url;
        Response.Listener listener;
    }

    static class HelpStringRequest extends HelpRequest{
        Response.ErrorListener errorListener;

        HelpStringRequest(int verb, String url, Response.Listener listener, Response.ErrorListener errorListener){
            this.verb = verb;
            this.url = url;
            this.listener = listener;
            this.errorListener = errorListener;
        }
    }

    static class HelpRecipeRequest extends HelpRequest{
        Recipe body;

        HelpRecipeRequest(int verb, String url, Response.Listener listener, Recipe body){
            this.verb = verb;
            this.url = url;
            this.listener = listener;
            this.body = body;
        }
    }

    static class RequestData implements Comparable<RequestData>{

        int verb;
        String url;
        Recipe body;

        RequestData(int verb, String url, Recipe body){
            this.verb = verb;
            this.url = url;
            this.body = body;
        }

        RequestData(int verb, String url){
            this(verb, url, null);
        }

        @Override
        public int compareTo(@NonNull RequestData requestDataToCompare) {
            return this.url.compareTo(requestDataToCompare.url);
        }
    }
}
