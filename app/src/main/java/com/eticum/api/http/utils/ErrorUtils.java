package com.eticum.api.http.utils;

import com.eticum.App;
import com.eticum.R;

import com.eticum.api.http.model.Payload;
import com.eticum.api.http.transport.response.GenericResponse;
import com.eticum.utils.Optional;



public class ErrorUtils {

    @SuppressWarnings("unchecked")
    public static Payload parseError(Optional<? extends GenericResponse> response) {
        return response.map(GenericResponse::getPayload).orElse(Payload.builder().error(12).build());
    }

    public static String getErrorDescription(Payload payload){
        Integer payloadCode = payload.getError();
        int desc;
        switch (payloadCode){
            case 0:
                desc = R.string.auth_error_0;
                break;
            case 1:
                desc = R.string.auth_error_1;
                break;
            case 2:
                desc = R.string.auth_error_2;
                break;
            case 3:
                desc = R.string.auth_error_3;
                break;
            case 4:
                desc = R.string.auth_error_4;
                break;
            case 5:
                desc = R.string.auth_error_5;
                break;
            case 6:
                desc = R.string.auth_error_6;
                break;
            case 7:
                desc = R.string.auth_error_7;
                break;
            case 8:
                desc = R.string.auth_error_8;
                break;
            case 9:
                desc = R.string.auth_error_9;
                break;
            case 10:
                desc = R.string.auth_error_10;
                break;
            case 11:
                desc = R.string.auth_error_11;
                break;
            default:
                desc = R.string.auth_error_12;
                break;
        }
        return App.getContext().getString(desc);
    }
}
