package com.infullmobile.voicetempo;

import com.google.gson.Gson;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Utils {

    private static long HTTP_READ_WRITE_TIMEOUT = 10;
    private static String ENDPOINT = "https://infullmobile.atlassian.net/rest/";

    private static OkHttpClient okClient = null;
    private static TempoService tempoService = null;

    private static OkHttpClient getOkHttpClient() {
        if (okClient == null)
            okClient = new OkHttpClient()
                    .newBuilder()
                    .readTimeout(HTTP_READ_WRITE_TIMEOUT, TimeUnit.SECONDS)
                    .connectTimeout(HTTP_READ_WRITE_TIMEOUT, TimeUnit.SECONDS)
                    .addNetworkInterceptor(new AddHeaderInterceptor())
                    .addInterceptor(getLogging())
                    .build();
        return okClient;
    }

    public static TempoService getTempoService() {
        if (tempoService == null)
            tempoService = new Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl(ENDPOINT)
                    .client(getOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create(new Gson()))
                    .build()
                    .create(TempoService.class);
        return tempoService;
    }

    private static HttpLoggingInterceptor getLogging() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }
}