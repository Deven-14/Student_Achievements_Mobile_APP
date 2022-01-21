package com.bmsce.studentachievements.Admin;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bmsce.studentachievements.SharedPreferences.SharedPreferenceManager;
import com.bmsce.studentachievements.Student.StudentSignIn;
import com.bmsce.studentachievements.Student.ViewAchievements;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class Admin {

    private Context context;
    private GoogleSignInAccount account;

    public Admin(Context context, GoogleSignInAccount account) {
        this.context = context;
        this.account = account;
    }

    public static boolean isAdmin(String email) {
        return (! Pattern.matches("^\\w+\\.\\w+\\d{2}(.*)", email));
    }

    private static void addDataToPreferences(Context context, GoogleSignInAccount account, JSONObject data) throws GeneralSecurityException, IOException, JSONException {
        SharedPreferenceManager.init(context);
        SharedPreferenceManager.write("name", account.getDisplayName());
        SharedPreferenceManager.write("email", account.getEmail());
        SharedPreferenceManager.write("token", data.getJSONObject("admin").getString("token"));
        SharedPreferenceManager.write("refreshToken", data.getJSONObject("admin").getString("refreshToken"));
        SharedPreferenceManager.write("departments", fromJSONArrayToStringSet(data.getJSONArray("departments")));
        SharedPreferenceManager.write("batches", fromJSONArrayToStringSet(data.getJSONArray("all_batches")));
    }

    private static String getVerifyUri() {
        return "https://trial-sabmsce.herokuapp.com/api/auth/admin/verify";
    }

    public static String getStudentAchievementsUri() {
        return "https://trial-sabmsce.herokuapp.com/api/admin/studentAchievements";
    }

    public static String getCreateBatchUri() {
        return "https://trial-sabmsce.herokuapp.com/api/student/createBatch";
    }

    public static String getRefreshTokenUri() {
        return "https://trial-sabmsce.herokuapp.com/api/auth/admin/refreshToken";
    }

    private static HashMap<String, String> getRefreshTokenDetails() {
        HashMap<String, String> map = new HashMap<>();
        map.put("email", "devenparamaj.ise@bmsce.ac.in");
        map.put("refreshToken", "");
        return map;
    }
    
    public void verify() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Admin.getVerifyUri(), getBody(), onSuccess, onFailure);
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }

    private JSONObject getBody() {

        JSONObject body = new JSONObject();
        try {
            body.put("name", "Deven Prakash Paramaj");
            body.put("email", "devenparamaj.ise@bmsce.ac.in");
//            body.put("name", account.getDisplayName());
//            body.put("email", account.getEmail());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return body;

    }

    private Response.Listener<JSONObject> onSuccess = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
//            Toast.makeText(context, "Success, Welcome Admin", Toast.LENGTH_LONG).show();
            Log.i("response", response.toString());
            Intent intent = new Intent(context, StudentAchievements.class);
            intent.setFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK
            );

            try {
                Admin.addDataToPreferences(context, account, response);
                SharedPreferenceManager.writeIsSignedInTrue(context);
                context.startActivity(intent);
            } catch (GeneralSecurityException | IOException | JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, "Error, logIn Again", Toast.LENGTH_SHORT).show();
            }

        }
    };

    private Response.ErrorListener onFailure = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(context, "Error, logIn Again", Toast.LENGTH_SHORT).show();
        }
    };

    private static Set<String> fromJSONArrayToStringSet(JSONArray array) throws JSONException {
        HashSet<String> set = new HashSet<>();
        for(int i = 0; i < array.length(); ++i) {
            set.add(array.getString(i));
        }
        return set;
    }
    
}
