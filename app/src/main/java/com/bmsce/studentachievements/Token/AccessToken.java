package com.bmsce.studentachievements.Token;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bmsce.studentachievements.MainActivity;
import com.bmsce.studentachievements.SharedPreferences.SharedPreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;

public class AccessToken {

    private final static String TAG = "AccessToken";
    private Context context;

    public static String getAccessToken(Context context) {
        try {
            SharedPreferenceManager.init(context);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            return SharedPreferenceManager.DEFAULT_VALUE;
        }
        return SharedPreferenceManager.read("token", SharedPreferenceManager.DEFAULT_VALUE);
    }

    private static void addAccessTokenToPreferences(Context context, String token) throws GeneralSecurityException, IOException {
        SharedPreferenceManager.init(context);
        SharedPreferenceManager.write("token", token);
    }

    private JsonObjectRequest getNewAccessTokenRequest(Context context, HashMap<String, String> bodyMap, String url) {
        this.context = context;
        JSONObject body = new JSONObject(bodyMap);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, body, onSuccess, onFailure);
        return request;
    }

    private final Response.Listener<JSONObject> onSuccess = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Log.i("response", response.toString());
            try {
                AccessToken.addAccessTokenToPreferences(context, response.getString("token"));
            } catch (JSONException | GeneralSecurityException | IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "Something Went Wrong, Try Again!", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private final Response.ErrorListener onFailure = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.i("error", error.toString()); // if token expired comes then login again error should be thrown
            Toast.makeText(context, "Refresh Token Expired, LogIn Again", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent( context, MainActivity.class );
            intent.setFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK
            );
            context.startActivity( intent );
        }
    };

}
