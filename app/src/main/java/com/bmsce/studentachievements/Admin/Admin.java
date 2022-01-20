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
import java.util.regex.Pattern;

public class Admin {

    private Context context;
    private GoogleSignInAccount account;
    private String url = "https://trial-sabmsce.herokuapp.com/api/auth/admin/verify";

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
        SharedPreferenceManager.write("token", data.getString("token"));
        SharedPreferenceManager.write("refreshToken", data.getString("refreshToken"));
        // set string of departments and batches
    }

    private static String getRefreshTokenUrl() {
        return "https://trial-sabmsce.herokuapp.com/api/auth/admin/refreshToken";
    }

    private static HashMap<String, String> getRefreshTokenDetails() {
        String error = "error";
        HashMap<String, String> map = new HashMap<>();
        map.put("refreshToken", "");
        map.put("email", "devenparamaj.ise@bmsce.ac.in");
        map.put("error", error);
        return map;
    }
    
    public void verify() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, getBody(), onSuccess, onFailure);
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }

    private JSONObject getBody() {

        JSONObject body = new JSONObject();
        try {
            body.put("name", "Deven Prakash Paramaj");
            body.put("email", "devenparamaj.ise@bmsce.ac.in");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return body;

    }

    private Response.Listener<JSONObject> onSuccess = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Toast.makeText(context, "Success, Welcome Admin", Toast.LENGTH_LONG).show();
            Log.i("response", response.toString());
            Intent intent = new Intent(context, StudentAchievements.class);
            try {
                String[] departments = fromJSONArrayToStringArray(response.getJSONArray("departments"));
                String[] batches = fromJSONArrayToStringArray(response.getJSONArray("all_batches"));

                String token = response.getJSONObject("admin").getString("token");
                String refreshToken = response.getJSONObject("admin").getString("refreshToken");

                intent.putExtra("departments", departments);
                intent.putExtra("batches", batches);
                intent.putExtra("gAccount", account);

                context.startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, "Error, login again", Toast.LENGTH_SHORT).show();
            }

        }
    };

    private Response.ErrorListener onFailure = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
        }
    };

    private String[] fromJSONArrayToStringArray(JSONArray array) throws JSONException {
        String[] list = new String[array.length()];
        for(int i = 0; i < array.length(); ++i) {
            list[i] = array.getString(i);
        }
        return list;
    }
    
}
