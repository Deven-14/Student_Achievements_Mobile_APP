package com.bmsce.studentachievements.Token;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class AccessToken {

    private final static String TAG = "AccessToken";

    private static void addAccessTokenToPreferences(String token) {

    }

//    private boolean isAllDataPresent(HashMap<String, String> body) {
//        String email = body.get("email");
//        String usn = body.get("usn");
//        String refreshToken = body.get("refreshToken");
//        String error = body.get("error");
//        return error != null && email != null && email.compareTo(error) != 0
//                && usn != null && usn.compareTo(error) != 0
//                && refreshToken != null && refreshToken.compareTo(error) != 0;
//    }

    private JsonObjectRequest getRequest(HashMap<String, String> bodyMap, String url) {
        JSONObject body = new JSONObject(bodyMap);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, body, onSuccess, onFailure);
        return request;
    }

    private final Response.Listener<JSONObject> onSuccess = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Log.i("response", response.toString());
            try {
                AccessToken.addAccessTokenToPreferences(response.getString("token"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private final Response.ErrorListener onFailure = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.i("error", error.toString()); // if token expired comes then login again error should be thrown
        }
    };

}
