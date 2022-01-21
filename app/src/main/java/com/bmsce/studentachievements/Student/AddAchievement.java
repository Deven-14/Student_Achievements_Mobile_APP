package com.bmsce.studentachievements.Student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bmsce.studentachievements.MainActivity;
import com.bmsce.studentachievements.R;
import com.bmsce.studentachievements.SharedPreferences.SharedPreferenceManager;
import com.bmsce.studentachievements.Token.AccessToken;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

public class AddAchievement extends AppCompatActivity implements View.OnClickListener {

    private Spinner yearOfAchievement, nameOfEvent, level;
    private EditText detailsOfEvent;
    private RadioGroup awardRg;
    private Button submit;
    private final static String TAG = AddAchievement.class.toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.arsenic)));
        getSupportActionBar().setTitle("Add Achievement");
        setContentView(R.layout.activity_add_achievement);

        yearOfAchievement = findViewById(R.id.yearOfAchievement);
        setAdapterAndListenerToSpinner(yearOfAchievement, getPossibleYearOfAchievements(), spinnerItemListener);

        nameOfEvent = findViewById(R.id.nameOfEvent);
        setAdapterAndListenerToSpinner(nameOfEvent, getNameOfEvents(), spinnerItemListener);

        level = findViewById(R.id.level);
        setAdapterAndListenerToSpinner(level, getLevels(), spinnerItemListener);

        detailsOfEvent = findViewById(R.id.detailsOfEvent);

        awardRg = findViewById(R.id.awardRg);

        submit = findViewById(R.id.submitBtn);
        submit.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.sign_out_action_bar_btn) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK
                            | Intent.FLAG_ACTIVITY_NEW_TASK
            );
            try {
                SharedPreferenceManager.writeIsSignedInFalse(getApplicationContext());
            } catch (GeneralSecurityException | IOException e) {
                e.printStackTrace();
            }
            startActivity(intent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void setAdapterAndListenerToSpinner(Spinner spinner, String[] data, AdapterView.OnItemSelectedListener itemSelectedListener) {
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.planets_array, android.R.layout.simple_spinner_item);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(itemSelectedListener);
    }

    private String[] getLevels() {
        return new String[] {
                "college",
                "district",
                "state",
                "national",
                "international"
        };
    }

    private String[] getNameOfEvents() {
        return new String[] {
                "Quiz Competition",
                "Coding Competition/Hackathons",
                "IEEE Event",
                "Any Technical Events",
                "PhaseShift",
                "HackerRank Challenges/Competition",
                "Paper or Publications",
                "Other"
        };
    }

    private String[] getPossibleYearOfAchievements() {
        try {
            SharedPreferenceManager.init(getApplicationContext());
            int year = Integer.parseInt(SharedPreferenceManager.read("presentYear", "0"));
            String[] years = new String[year];
            for (int i = 1; i <= year; ++i) {
                years[i-1] = String.valueOf(i);
            }
            return years;
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            return new String[] {};
        }
    }

    private AdapterView.OnItemSelectedListener spinnerItemListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            TextView selectedItem = (TextView) parent.getChildAt(0);
            selectedItem.setTextColor(ContextCompat.getColor(AddAchievement.this, R.color.white));
            selectedItem.setPadding(35, 0, 0, 0);

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.submitBtn) {
            addAchievement();
        }
    }

    private boolean isAllDataEntered(String token) {
        return nameOfEvent.getSelectedItem().toString().compareTo("") != 0
                && yearOfAchievement.getSelectedItem().toString().compareTo("") != 0
                && level.getSelectedItem().toString().compareTo("") != 0
                && detailsOfEvent.getText().toString().compareTo("") != 0
                && awardRg.getCheckedRadioButtonId() != -1
                && token != null && token.compareTo(SharedPreferenceManager.DEFAULT_VALUE) != 0;
    }

    private void addAchievement() {
        String token = AccessToken.getAccessToken(getApplicationContext());
        if(isAllDataEntered(token)) {

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Student.getAddAchievementUri(), getBody(), onSuccess, onFailure) {
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
            Toast.makeText(this, "Data Missing", Toast.LENGTH_SHORT).show();
        }
    }

    private JSONObject getBody() {

        JSONObject body = new JSONObject();
        try {
            body.put("yearOfAchievement", yearOfAchievement.getSelectedItem().toString());
            body.put("nameOfEvent", nameOfEvent.getSelectedItem().toString());
            body.put("detailsOfEvent", detailsOfEvent.getText());
            body.put("level", level.getSelectedItem().toString());
            body.put("award", ((RadioButton)findViewById(awardRg.getCheckedRadioButtonId())).getText());
        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
        }
        return body;

    }

    private Response.Listener<JSONObject> onSuccess = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Toast.makeText(AddAchievement.this, "Added Achievement", Toast.LENGTH_SHORT).show();
//            Log.i("response", response.toString());
            Intent returnIntent = new Intent();
            setResult(AddAchievement.RESULT_OK, returnIntent);
            finish();
        }
    };

    private Response.ErrorListener onFailure = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(AddAchievement.this, "Error Adding Achievement, Try Again!", Toast.LENGTH_SHORT).show();
            Intent returnIntent = new Intent();
            setResult(AddAchievement.RESULT_CANCELED, returnIntent);
            finish();
        }
    };

}