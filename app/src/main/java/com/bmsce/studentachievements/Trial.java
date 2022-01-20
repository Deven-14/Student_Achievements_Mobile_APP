package com.bmsce.studentachievements;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bmsce.studentachievements.Achievement.Achievement;
import com.bmsce.studentachievements.Achievement.AchievementsRecViewAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Trial extends AppCompatActivity {

    private RecyclerView achievementsRecView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trial);

//        EditText usnTrial = findViewById(R.id.usnTrial);
//        Toast.makeText(this, usnTrial.getText().toString(), Toast.LENGTH_SHORT).show();
//        Log.i("Trial", usnTrial.getText().toString());
////        Toast.makeText(this, usnTrial.getText().toString().compareTo(""), Toast.LENGTH_SHORT).show();
//        Integer i = usnTrial.getText().toString().compareTo("");
//        Log.i("Trial", i.toString());

        achievementsRecView = findViewById(R.id.achievementsRecView);

        ArrayList<Achievement> achievements = new ArrayList<>();

        AchievementsRecViewAdapter adapter = new AchievementsRecViewAdapter(this, achievements);

        achievementsRecView.setAdapter(adapter);
        achievementsRecView.setLayoutManager(new LinearLayoutManager(this));

//        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
//        requestQueue.add(request);
    }

    private String url = "https://trial-sabmsce.herokuapp.com/student/viewAchievements";

    private JSONObject getData() {

        JSONObject userData = new JSONObject();
        try {
            userData.put("name", "Deven Prakash Paramaj");
            userData.put("email", "devenparamaj.is19@bmsce.ac.in");
            userData.put("usn", "1BM19IS048");
            userData.put("image", "abc");
            userData.put("spreadsheetId", "1f54CNAEZ-0hFInttaunyALRDpNGqB8Mbl6BHq25KLPY");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject1 = new JSONObject();
        try {
            jsonObject1.put("userData", userData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject1;
    }

    private JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, getData(),
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Toast.makeText(Trial.this, response.toString(), Toast.LENGTH_LONG).show();
                    Log.i("response", response.toString());
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(Trial.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
    );
}