package com.eticum.api.http.utils;

import com.eticum.App;
import com.eticum.Constants;
import com.eticum.R;

import com.eticum.api.http.model.Payload;
import com.eticum.api.http.transport.response.GenericResponse;
import com.eticum.utils.Optional;


public class ErrorUtils {

    public static Payload parseError(Optional<? extends GenericResponse> response) {
        return response.map(GenericResponse::getPayload).orElse(Payload.builder().error(Constants.DEFAULT_HTTP_ERROR).build());
    }

    public static String getErrorDescription(Payload payload) {
        Integer payloadCode = payload.getError();
        return payloadCode > Constants.errorList.size() ? App.getContext().getString(Constants.DEFAULT_HTTP_ERROR) :
                App.getContext().getString(Constants.errorList.get(--payloadCode));
    }
}
