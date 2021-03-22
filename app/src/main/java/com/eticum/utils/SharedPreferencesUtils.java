package com.eticum.utils;

import android.content.SharedPreferences;

import com.eticum.App;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class SharedPreferencesUtils {
    private static final SharedPreferences sharedPreferences = App.getPreferences();

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
}
