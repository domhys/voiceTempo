package com.infullmobile.voicetempo;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface TempoService {

    @POST("tempo-timesheets/3/worklogs")
    Single<Object> postWorklog(@Body SendLogRequest request, @Header("Authorization") String header);
}
