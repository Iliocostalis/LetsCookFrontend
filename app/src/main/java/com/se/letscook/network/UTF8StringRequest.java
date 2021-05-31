package com.se.letscook.network;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * Like a normal StringRequest, just with utf-8 encoding
 */
public class UTF8StringRequest extends Request<String>
{

    private Response.Listener<String> mListener;

    public UTF8StringRequest(int method, String url, Response.Listener<String> mListener,
                             Response.ErrorListener errorListener)
    {
        super(method, url, errorListener);
        this.mListener = mListener;
    }

    @Override
    protected void deliverResponse(String response)
    {
        this.mListener.onResponse(response);
    }

    /**
     * Handle the response of the request
     * Overwritten to enable utf-8 responses
     * @param response the response of the request, from the Volley-Framework
     * @return the string-response in utf-8 encoding
     */
    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response)
    {
        String parsed = "";

        String encoding = HttpHeaderParser.parseCharset(response.headers);

        try
        {
            parsed = new String(response.data, encoding);
            byte[] bytes = parsed.getBytes(encoding);
            parsed = new String(bytes, StandardCharsets.UTF_8);

            return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
        }
        catch (UnsupportedEncodingException e)
        {
            return Response.error(new ParseError(e));
        }
    }

}
