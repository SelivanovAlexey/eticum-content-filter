package com.eticum.api.http;

import com.eticum.api.http.transport.request.AuthRequest;
import com.eticum.api.http.transport.request.GetURLInfoRequest;
import com.eticum.api.http.transport.request.KeepAliveRequest;
import com.eticum.api.http.transport.request.LogRequest;
import com.eticum.api.http.transport.response.AuthResponse;
import com.eticum.api.http.transport.response.GetURLInfoResponse;
import com.eticum.api.http.transport.response.KeepAliveResponse;
import com.eticum.api.http.transport.response.LogResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface APIInterface {

    @POST("/")
    Call<AuthResponse> auth(@Body AuthRequest request);

    @POST("/")
    Call<GetURLInfoResponse> getURLInfo(@Body GetURLInfoRequest request);

    @POST("/")
    Call<KeepAliveResponse> keepAlive(@Body KeepAliveRequest request);

    @POST("/")
    Call<LogResponse> log(@Body LogRequest request);
}
