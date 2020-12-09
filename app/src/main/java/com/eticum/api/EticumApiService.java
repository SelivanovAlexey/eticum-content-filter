package com.eticum.api;

import android.net.Uri;

import com.eticum.App;
import com.eticum.api.http.APIInterface;
import com.eticum.api.http.HttpClient;
import com.eticum.api.http.model.Info;
import com.eticum.api.http.model.LogItem;
import com.eticum.api.http.model.Payload;
import com.eticum.api.http.transport.request.AuthRequest;
import com.eticum.api.http.transport.request.GetURLInfoRequest;
import com.eticum.api.http.transport.request.KeepAliveRequest;
import com.eticum.api.http.transport.request.LogRequest;
import com.eticum.api.http.transport.response.AuthResponse;
import com.eticum.api.http.transport.response.GetURLInfoResponse;
import com.eticum.api.http.transport.response.KeepAliveResponse;
import com.eticum.api.http.transport.response.LogResponse;
import com.eticum.api.http.utils.AuthCallback;
import com.eticum.api.http.utils.ErrorUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import com.eticum.api.http.utils.KeepAliveCallback;
import com.eticum.filter.FilterInfoHolder;
import com.eticum.services.EticumVpnService;
import com.eticum.utils.Optional;
import com.eticum.utils.VpnUtils;

import lombok.extern.slf4j.Slf4j;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * This class contains methods which are represent calls to Eticum API.
 */

@Slf4j
public class EticumApiService {

    private final static APIInterface apiInterface = HttpClient.getClient().create(APIInterface.class);

    private static final FilterInfoHolder filterInfoHolder = FilterInfoHolder.get();

    /**
     * Authentication by access token
     */
    public static void doAuth(AuthCallback authCallback) {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setRefreshToken(getRefreshToken());
        doAuthExecute(authRequest, authCallback);
    }

    /**
     * Authentication by login/password
     *
     * @param login    user's email
     * @param password user's password
     */

    public static void doAuth(String login, String password, AuthCallback authCallback) {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setLogin(login);
        authRequest.setPassword(password);
        doAuthExecute(authRequest, authCallback);
    }

    /**
     * API call to send auth request.
     * Each client application should begin interacting through the API
     * with an authentication request. Authentication is done by tokens.
     * If the validity of the transmitted accessToken has expired, then
     * based on the current refreshToken, a new accessToken is issued.
     * If the accessToken has not expired, it’s extended. Initially, when
     * there are no saved tokens in the application, you must use a username
     * and password for authentication.
     *
     * @param request
     */
    private static void doAuthExecute(AuthRequest request, AuthCallback authCallback) {
        log.debug("Send auth");
        Call<AuthResponse> call = apiInterface.auth(request);
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(@NotNull Call<AuthResponse> call, @NotNull Response<AuthResponse> response) {
                Optional<AuthResponse> responseBody = Optional.ofNullable(response.body());

                responseBody
                        .map(AuthResponse::getStatus)
                        .filter(v -> v.equals("authOK"))
                        .ifPresentOrElse(authOK -> {
                            responseBody
                                    .map(AuthResponse::getAccessToken)
                                    .ifPresent(EticumApiService::setAccessToken);
                            responseBody
                                    .map(AuthResponse::getRefreshToken)
                                    .ifPresent(EticumApiService::setRefreshToken);
                            responseBody
                                    .map(AuthResponse::getUser)
                                    .ifPresent(filterInfoHolder::setUser);
                            responseBody
                                    .map(AuthResponse::getProfile)
                                    .ifPresent(filterInfoHolder::setProfile);
                            authCallback.onSuccess();
                        }, () -> {
                            Payload errorPayload = ErrorUtils.parseError(responseBody);
                            authCallback.onFailure(errorPayload.getError());
                            log.error("Authentication error: {}", ErrorUtils.getErrorDescription(errorPayload));
                        });
            }

            @Override
            public void onFailure(@NotNull Call<AuthResponse> call, @NotNull Throwable t) {
                call.cancel();
                log.error("An exception occurred during exchange with server on doAuth call", t);
            }
        });
    }

    public static void setAccessToken(String accessToken) {
        App.getPreferences().edit().putString("accessToken", accessToken).apply();
    }

    public static void setRefreshToken(String refreshToken) {
        App.getPreferences().edit().putString("refreshToken", refreshToken).apply();
    }

    public static String getAccessToken() {
        return App.getPreferences().getString("accessToken", null);
    }

    private static String getRefreshToken() {
        return App.getPreferences().getString("refreshToken", null);
    }

    /**
     * API call to send keep-alive request.
     * Keep-alive requests are executed from filtering applications
     * every 120 seconds to obtain a valid filtering profile.
     */
    public static void doKeepAlive(KeepAliveCallback callback) {
        log.debug("Send keepAlive");
        KeepAliveRequest keepAliveRequest = new KeepAliveRequest(getAccessToken(), Payload.builder().apps(VpnUtils.getInstalledApplications()).build());
        Call<KeepAliveResponse> call = apiInterface.keepAlive(keepAliveRequest);
        call.enqueue(new Callback<KeepAliveResponse>() {
            @Override
            public void onResponse(@NotNull Call<KeepAliveResponse> call, @NotNull Response<KeepAliveResponse> response) {
                Optional<KeepAliveResponse> responseBody = Optional.ofNullable(response.body());
                responseBody
                        .map(KeepAliveResponse::getStatus)
                        .filter(v -> v.equals("onlineOK"))
                        .ifPresentOrElse(onlineOK ->
                                        responseBody
                                                .map(KeepAliveResponse::getProfile)
                                                .ifPresent(profile -> {
                                                    if (VpnUtils.isApiConfigChanged(profile, filterInfoHolder.getProfile()))
                                                        callback.onApiConfigChanged(profile);
                                                    filterInfoHolder.setProfile(profile);
                                                }),
                                () -> {
                                    Payload errorPayload = ErrorUtils.parseError(responseBody);
                                    log.error("KeepAlive error: {}", ErrorUtils.getErrorDescription(errorPayload));
                                }
                        );
            }

            @Override
            public void onFailure(@NotNull Call<KeepAliveResponse> call, @NotNull Throwable t) {
                call.cancel();
                log.error("An exception occurred during exchange with server on doKeepAlive call", t);
            }
        });
    }

    /**
     * API call to send URL information request.
     * Requests for URL information are made from secure applications before
     * deciding whether to grant or deny access to the requested URL.
     * <p>
     * Should be synchronized
     *
     * @param url
     */
    public Info doGetURLInfo(Uri url) {
        log.debug("Send info");
        if (url != null) {
            GetURLInfoRequest getURLInfoRequest = new GetURLInfoRequest(getAccessToken(), url.toString());
            Call<GetURLInfoResponse> call = apiInterface.getURLInfo(getURLInfoRequest);
            GetURLInfoResponse responseBody;
            try {
                responseBody = call.execute().body();
                if (responseBody != null) {
                    if (responseBody.getStatus() != null && responseBody.getStatus().equals("infoError")) {
                        log.debug("some error occurs during obtaining URL info");
                        return null;
                    } else
                        return responseBody.getInfo();
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                call.cancel();
            }
        } else log.error("attempt to obtaining info of null url");
        return null;
    }

    /**
     * API call to send log request.
     * Log sending requests are performed by the filtering application
     * as necessary: by time or by the number of log entries.
     *
     * @param itemList
     */
    public void doLog(List<LogItem> itemList) {
        log.debug("Send log");
        LogRequest logRequest = new LogRequest(getAccessToken(), itemList);
        Call<LogResponse> call = apiInterface.log(logRequest);
        call.enqueue(new Callback<LogResponse>() {
            @Override
            public void onResponse(@NotNull Call<LogResponse> call, @NotNull Response<LogResponse> response) {
                Optional<LogResponse> responseBody = Optional.ofNullable(response.body());
                responseBody
                        .map(LogResponse::getStatus)
                        .filter(v -> v.equals("logOK"))
                        .ifPresentOrElse(val -> log.debug("Successful sending history to the server"),
                                () -> {
                                    log.debug("An error occurred during send logs to the server");
                                    Payload errorPayload = ErrorUtils.parseError(responseBody);
                                    log.error("Log error: {}", ErrorUtils.getErrorDescription(errorPayload));
                                });
            }

            @Override
            public void onFailure(@NotNull Call<LogResponse> call, @NotNull Throwable t) {
                call.cancel();
                log.error("An exception occurred during exchange with server on doLog call", t);
            }
        });
    }
}
