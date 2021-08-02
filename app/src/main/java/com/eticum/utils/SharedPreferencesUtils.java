package com.eticum.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.eticum.App;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import static androidx.security.crypto.MasterKey.DEFAULT_MASTER_KEY_ALIAS;

@Slf4j
@UtilityClass
public class SharedPreferencesUtils {
    private SharedPreferences sharedPreferences;

    @SneakyThrows
    public SharedPreferences generateStore(Context ctx) {
        KeyGenParameterSpec spec = new KeyGenParameterSpec.Builder(
                DEFAULT_MASTER_KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build();

        MasterKey mKey = new MasterKey.Builder(ctx)
                .setKeyGenParameterSpec(spec)
                .build();

        return sharedPreferences = EncryptedSharedPreferences.create(
                ctx,
                "app_prefs",
                mKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
    }

    public static boolean isLoggedIn() {
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }

    public static void setLoggedIn() {
        sharedPreferences.edit().putBoolean("isLoggedIn", true).apply();
    }

    public static void removeLoggedIn() {
        sharedPreferences.edit().putBoolean("isLoggedIn", false).apply();
    }

    public static boolean isVpnRunning() {
        return sharedPreferences.getBoolean("isVpnRunning", false);
    }

    public static void setVpnRunning() {
        sharedPreferences.edit().putBoolean("isVpnRunning", true).apply();
    }

    public static void removeVpnRunning() {
        sharedPreferences.edit().putBoolean("isVpnRunning", false).apply();
    }


    public static boolean isPassedInitialSetup() {
        return sharedPreferences.getBoolean("isPassedInitialSetup", false);
    }

    public static void setPassedInitialSetup() {
        sharedPreferences.edit().putBoolean("isPassedInitialSetup", true).apply();
    }

    public static void removePassedInitialSetup() {
        sharedPreferences.edit().putBoolean("isPassedInitialSetup", false).apply();
    }

    public static void setAccountPasswordHash(String password) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(password.getBytes());
            sharedPreferences.edit().putString("accountPasswordHash", Arrays.toString(messageDigest.digest())).apply();
        } catch (NoSuchAlgorithmException ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    public static boolean checkAccountPasswordHash(String password) {
        MessageDigest messageDigest;
        boolean result = false;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(password.getBytes());
            result = Arrays.toString(messageDigest.digest()).equals(sharedPreferences.getString("accountPasswordHash", null));
        } catch (NoSuchAlgorithmException ex) {
            log.error(ex.getMessage(), ex);
        }
        return result;
    }

    public static void setProfileHash(String s) {
        sharedPreferences.edit().putString("profileHash", s).apply();
    }

    public static String getProfileHash() {
        return sharedPreferences.getString("profileHash", "");
    }
}
