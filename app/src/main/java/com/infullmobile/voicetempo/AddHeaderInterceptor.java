package com.infullmobile.voicetempo;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AddHeaderInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        for (String header : request.headers().names()) {
            Log.e("HEADERS", "HEADER: " + header + " VALUE: " + request.headers().get(header));
        }
        return chain.proceed(request);
    }
}