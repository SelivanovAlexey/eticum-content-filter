package com.eticum.utils;

import android.app.Activity;
import android.content.Intent;
import android.security.KeyChain;

import com.eticum.App;
import com.eticum.Constants;
import com.eticum.R;

import org.littleshoot.proxy.mitm.Authority;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

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

    @SneakyThrows
    public static void installEticumCA(Activity context) {
        val ks = SecurityUtils.loadKeyStore(authority());
        val cert = ks.getCertificate(authority().alias());
        Intent intent = KeyChain.createInstallIntent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(KeyChain.EXTRA_CERTIFICATE, cert.getEncoded());
        intent.putExtra(KeyChain.EXTRA_NAME, Constants.ETICUM_CA_ORGANIZATION_NAME);
        context.startActivityForResult(intent, 0xf00);
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
                if (cert.getIssuerDN().getName().contains("CN=" + ETICUM_CA_ORGANIZATION_NAME)) return true;
            }
        }
        return false;
    }
}
