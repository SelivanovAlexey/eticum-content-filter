package com.eticum.api;

import com.eticum.App;
import com.eticum.Constants;
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
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.eticum.api.http.utils.KeepAliveCallback;
import com.eticum.filter.FilterInfoHolder;
import com.eticum.utils.NetworkUtils;
import com.eticum.utils.Optional;
import com.eticum.utils.SharedPreferencesUtils;
import com.eticum.utils.VpnUtils;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
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
        enqueue(apiInterface.auth(request), new Callback<AuthResponse>() {
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
                            responseBody
                                    .map(AuthResponse::getProfileHash)
                                    .ifPresent(SharedPreferencesUtils::setProfileHash);
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
                // No internet
                if (t instanceof UnknownHostException) authCallback.onFailure(12);
                else authCallback.onFailure(99);
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
        enqueue(apiInterface.keepAlive(keepAliveRequest), new Callback<KeepAliveResponse>() {
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
                                                    callback.onSuccess();
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
    public static Info doGetURLInfo(URI url) {
        log.debug("Send info");
        GetURLInfoRequest getURLInfoRequest = new GetURLInfoRequest(getAccessToken(), url.toString());
        Call<GetURLInfoResponse> call = apiInterface.getURLInfo(getURLInfoRequest);
        GetURLInfoResponse response = null;

        try {
            response = call.execute().body();
        } catch (IOException e) {
            call.cancel();
            log.error("An exception occurred during exchange with server on doGetURLInfo call", e);
        }

        Optional<GetURLInfoResponse> optResponseBody = Optional.ofNullable(response);
        Info resultInfo = optResponseBody
                .map(GetURLInfoResponse::getInfo)
                .orElseGet(() -> {
                    Payload errorPayload = ErrorUtils.parseError(optResponseBody);
                    log.error("GetURLInfo error: {}", ErrorUtils.getErrorDescription(errorPayload));
                    return null;
                });

        return Optional.ofNullable(resultInfo)
                .filter(i -> i.getStatus() == null)
                .orElse(Info.builder()
                        .age(0)
                        .categories(new ArrayList<>())
                        .build());
    }

    /**
     * API call to send log request.
     * Log sending requests are performed by the filtering application
     * as necessary: by time or by the number of log entries.
     *
     * @param itemList
     */
    public static void doLog(List<LogItem> itemList) {
        log.debug("Send log");
        LogRequest logRequest = new LogRequest(getAccessToken(), itemList);
        enqueue(apiInterface.log(logRequest), new Callback<LogResponse>() {
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

    // A filter to handling network errors
    private static <T> void enqueue(Call<T> call, Callback<T> callback) {
        val host = call.request().url().host();
        new Thread(() -> {
            if (!NetworkUtils.DNSResolver.isDNSReachable(host, Constants.HTTP_TIMEOUT)) {
                callback.onFailure(call, new UnknownHostException(host));
            } else call.enqueue(callback);
        }).start();
    }


}
