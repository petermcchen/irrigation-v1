package com.accton.iot.irrigationv1.management.user.api;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class UserResponseInterceptor implements Interceptor
{
    private final String TAG = "UserResponseInterceptor";
    private final boolean DEBUG = false;
    private int responseCode = -1; // error check

    @Override
    public Response intercept(Chain chain) throws IOException
    {
        Request request = chain.request();
        Response response = chain.proceed(request);

        if(DEBUG)
            Log.d(TAG, "intercept response headers: " + response.headers() + ", code: " + response.code());

        responseCode = response.code();

        return response;
    }

    public int getSuccessCode()
    {
        if(DEBUG)
            Log.d(TAG, "getSuccessCode, code: " + responseCode);

        return responseCode;
    }
}