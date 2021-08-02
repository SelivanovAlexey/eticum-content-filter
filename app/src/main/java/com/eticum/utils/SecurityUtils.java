package com.eticum.utils;

import android.app.Activity;
import com.eticum.App;
import com.eticum.R;

import org.littleshoot.proxy.mitm.Authority;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import static com.eticum.Constants.*;

@UtilityClass
@Slf4j
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

    //TODO: TBD. Need to load eticum CA from the app to internal file storage
    public static void loadEticumCA(Activity context) {
    }

    @SneakyThrows
    public static boolean checkInstalledEticumCert() {
        KeyStore ks = KeyStore.getInstance("AndroidCAStore");
        if (ks != null) {
            ks.load(null, null);
            Enumeration<String> aliases = ks.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                X509Certificate cert = (X509Certificate) ks.getCertificate(alias);
                if (cert.getIssuerDN().getName().contains("CN=" + ETICUM_CA_ORGANIZATION_NAME))
                    return true;
            }
        }
        return false;
    }
}
