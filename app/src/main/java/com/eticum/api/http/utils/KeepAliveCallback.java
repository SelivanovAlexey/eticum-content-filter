package com.eticum.api.http.utils;

import com.eticum.api.http.model.Profile;

public interface KeepAliveCallback {
    void onApiConfigChanged(Profile profile);
}
