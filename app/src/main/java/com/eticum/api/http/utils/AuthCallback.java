package com.eticum.api.http.utils;

public interface AuthCallback {
    void onSuccess();
    void onFailure(Integer errorCode);
}
