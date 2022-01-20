package com.bmsce.studentachievements.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class SharedPreferenceManager {

    private static SharedPreferences sharedPreferences = null;
    public static final String SHARED_PREF_NAME = "USER_DATA";
    public static final String defaultValue = "error";

    private static final String TAG = "SharedPreferenceManager";

    public static void init(Context context) throws GeneralSecurityException, IOException {
        if(sharedPreferences == null) {
            MasterKey masterKey = new MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            sharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    SHARED_PREF_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
        }
    }

    public static String read(String key, String defValue) {
        return sharedPreferences.getString(key, defValue);
    }

    public static void write(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

}
