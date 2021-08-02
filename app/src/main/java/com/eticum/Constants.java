package com.eticum;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public interface Constants {
    int LOCAL_PROXY_PORT = 8089;

    String ETICUM_CA_ORGANIZATION_NAME = "Eticum Personal CA";
    String ETICUM_CA_COMMON_NAME = "eticum.com";
    String ETICUM_CA_ALIAS = "eticum";

    long KEEP_ALIVE_INTERVAL = 120000L;

    long HTTP_TIMEOUT = 3000L;

    List<Integer> errorList = Collections.unmodifiableList(Arrays.asList(
            // Eticum API errors
            R.string.auth_error_1,
            R.string.auth_error_2,
            R.string.auth_error_3,
            R.string.auth_error_4,
            R.string.auth_error_5,
            R.string.auth_error_6,
            R.string.auth_error_7,
            R.string.auth_error_8,
            R.string.auth_error_9,
            R.string.auth_error_10,
            R.string.auth_error_11,
            // Custom errors
            R.string.auth_error_12));

    Integer DEFAULT_HTTP_ERROR = R.string.auth_error_99;
}
