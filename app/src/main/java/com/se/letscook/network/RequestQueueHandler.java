package com.se.letscook.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Implemented as a Singleton to make sure there is always just one RequestQueue
 */
public class RequestQueueHandler {
        private static RequestQueueHandler instance;
        private RequestQueue requestQueue;
        private Context ctx;

        private RequestQueueHandler(Context context) {
            this.ctx = context;
            requestQueue = getRequestQueue();
        }

        public static synchronized RequestQueueHandler getInstance(Context context) {
            if (instance == null) {
                instance = new RequestQueueHandler(context);
            }
            return instance;
        }

        public RequestQueue getRequestQueue() {
            if (requestQueue == null) {
                // getApplicationContext() is key, it keeps you from leaking the
                // Activity or BroadcastReceiver if someone passes one in.
                requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
            }
            return requestQueue;
        }

        public <T> void addToRequestQueue(Request<T> req) {
            getRequestQueue().add(req);
        }
}
