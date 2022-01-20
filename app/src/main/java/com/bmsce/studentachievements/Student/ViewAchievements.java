package com.bmsce.studentachievements.Student;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bmsce.studentachievements.Achievement.Achievement;
import com.bmsce.studentachievements.Achievement.AchievementsRecViewAdapter;
import com.bmsce.studentachievements.MainActivity;
import com.bmsce.studentachievements.R;
import com.bmsce.studentachievements.SharedPreferences.SharedPreferenceManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewAchievements extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView achievementsRecView;
    private ArrayList<Achievement> achievements;
    private FloatingActionButton addAchievementBtn;
    private static final String TAG = ViewAchievements.class.toString();

    //this has to be in class scope above addAchievementResultLauncher
    private ActivityResultCallback<ActivityResult> activityResultCallback = new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == MainActivity.RESULT_OK) {
                getAchievements();
                Toast.makeText(ViewAchievements.this, "success", Toast.LENGTH_SHORT).show();
            }
//            Toast.makeText(ViewAchievements.this, "failure", Toast.LENGTH_SHORT).show();

        }
    };

    //this has to be in class scope
    private ActivityResultLauncher<Intent> addAchievementActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            activityResultCallback
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_achievements);

        achievementsRecView = findViewById(R.id.achievementsRecView);
        addAchievementBtn = findViewById(R.id.addAchievementBtn);

        addAchievementBtn.setOnClickListener(this);

        getAchievements();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.addAchievementBtn) {

            Intent intent = new Intent(this, AddAchievement.class);
            addAchievementActivityResultLauncher.launch(intent);

        }
    }

    private String getToken() {
        try {
            SharedPreferenceManager.init(getApplicationContext());
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            return SharedPreferenceManager.defaultValue;
        }
        return SharedPreferenceManager.read("token", SharedPreferenceManager.defaultValue);
    }

    private boolean isTokenPresent(String token) {
        return token.compareTo(SharedPreferenceManager.defaultValue) != 0;
    }

    private void getAchievements() {
        String token = getToken();
        if(isTokenPresent(token)) {

            //request should only be written here, coz we are calling getBody() when we are creating the request object...
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Student.getViewAchievementsUri(), getBody(), onSuccess, onFailure) {
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    //headers.put("Content-Type", "application/json");
                    headers.put("x-access-token", token);
                    return headers;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(request);

        } else {
            Toast.makeText(this, "Invalid User", Toast.LENGTH_SHORT).show();
        }
    }

    private JSONObject getBody() {
        return null;
    }

    private Response.Listener<JSONObject> onSuccess = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
//            Log.i("response", response.toString());
            try {
                displayAchievements(response.getJSONArray("achievements"));
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(ViewAchievements.this, "Something went Wrong!, Reload", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private Response.ErrorListener onFailure = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(ViewAchievements.this, "Error Fetching Achievements", Toast.LENGTH_SHORT).show();
        }
    };

    void displayAchievements(JSONArray achievementsJSONArray) {
        achievements = new ArrayList<>();
        try {

            for(int i = 0; i < achievementsJSONArray.length(); ++i) {
                JSONObject achievementJSON = achievementsJSONArray.getJSONObject(i);
                achievements.add(new Achievement(achievementJSON));
            }

            AchievementsRecViewAdapter adapter = new AchievementsRecViewAdapter(this, achievements);
            achievementsRecView.setAdapter(adapter);
            achievementsRecView.setLayoutManager(new LinearLayoutManager(this));

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(ViewAchievements.this, "Something went Wrong!, Reload2", Toast.LENGTH_SHORT).show();
        }

    }

}