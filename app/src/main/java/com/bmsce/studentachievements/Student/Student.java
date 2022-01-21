package com.bmsce.studentachievements.Student;

import android.content.Context;
import android.util.Log;

import com.bmsce.studentachievements.SharedPreferences.SharedPreferenceManager;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;

public class Student {

    public static void addDataToPreferences(Context context, GoogleSignInAccount account, JSONObject data) throws GeneralSecurityException, IOException, JSONException {
        SharedPreferenceManager.init(context);
        SharedPreferenceManager.write("name", account.getDisplayName());
        SharedPreferenceManager.write("email", account.getEmail());
        SharedPreferenceManager.write("usn", data.getString("usn"));
        SharedPreferenceManager.write("presentYear", String.valueOf(data.getInt("presentYear")));
        SharedPreferenceManager.write("token", data.getString("token"));
        SharedPreferenceManager.write("refreshToken", data.getString("refreshToken"));
        Log.i("Student", "Saved data successfully");
    }

    public static String getRefreshTokenUri() {
        return "https://trial-sabmsce.herokuapp.com/api/auth/Student/refreshToken";
    }

    public static String getSignInUri() {
        return "https://trial-sabmsce.herokuapp.com/api/auth/student/signin";
    }

    public static String getSignUpUri() {
        return "https://trial-sabmsce.herokuapp.com/api/auth/student/signup";
    }

    public static String getViewAchievementsUri() {
        return "https://trial-sabmsce.herokuapp.com/api/student/viewAchievements";
    }

    public static String getAddAchievementUri() {
        return "https://trial-sabmsce.herokuapp.com/api/student/addAchievement";
    }

    public static HashMap<String, String> getRefreshTokenDetails(Context context) throws GeneralSecurityException, IOException {
        SharedPreferenceManager.init(context);
        HashMap<String, String> map = new HashMap<>();
        map.put("email", SharedPreferenceManager.read("email", SharedPreferenceManager.DEFAULT_VALUE));
        map.put("usn", SharedPreferenceManager.read("usn", SharedPreferenceManager.DEFAULT_VALUE));
        map.put("refreshToken", SharedPreferenceManager.read("refreshToken", SharedPreferenceManager.DEFAULT_VALUE));
        return map;
    }

}
