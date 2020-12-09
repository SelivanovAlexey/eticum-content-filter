package com.eticum.utils;

import com.eticum.App;
import com.eticum.R;

import org.littleshoot.proxy.mitm.Authority;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import static com.eticum.Constants.*;

public class SecurityUtils {

    private static final String KEY_STORE_TYPE = "PKCS12";

    public static Authority authority() {
        return new Authority(App.getContext().getFilesDir(),
                ETICUM_CA_ALIAS,
                ETICUM_CA_ALIAS.toCharArray(),
                ETICUM_CA_COMMON_NAME,
                ETICUM_CA_ORGANIZATION_NAME,
                "",
                ETICUM_CA_ORGANIZATION_NAME,
                "");
    }

    public static KeyStore loadKeyStore(Authority authority) throws GeneralSecurityException,
            IOException {
        KeyStore ks = KeyStore.getInstance(KEY_STORE_TYPE);
        InputStream is = App.getContext().getResources().openRawResource(R.raw.eticum);
        ks.load(is, authority.password());
        return ks;
    }
}
