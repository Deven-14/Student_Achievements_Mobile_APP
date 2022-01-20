package com.bmsce.studentachievements.Admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bmsce.studentachievements.R;
import com.bmsce.studentachievements.Student.AddAchievement;
import com.bmsce.studentachievements.Student.StudentSignIn;
import com.bmsce.studentachievements.Student.ViewAchievements;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class StudentAchievements extends AppCompatActivity implements View.OnClickListener {

    private TextView departmentsTextView;
    private MultiSelectOnClickListener departmentsListener;

    private TextView batchesTextView;
    private MultiSelectOnClickListener batchesListener;

    private Spinner fromAcademicYearSpinner, toAcademicYearSpinner;
    private Button viewStudentAchievementsBtn, createBatchBtn;

    ArrayList<String> selectedDepartments;
    ArrayList<String> selectedBatches;

    private static String TAG = "StudentAchievements";
    private String url = "https://trial-sabmsce.herokuapp.com/api/admin/studentAchievements";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_achievements);

//        String[] departments = getIntent().getStringArrayExtra("departments");
//        String[] batches = getIntent().getStringArrayExtra("batches");

        String[] batches = {
                "batch-2019-2023",
                "batch-2018-2022",
                "batch-2020-2024"
        };

        String[] departments = {"CE", "ME", "EE", "EC", "IM", "CS", "TE", "IS", "EI", "ML", "BT", "CH", "AS", "AM"};

        departmentsTextView = findViewById(R.id.departmentsTextView);
        departmentsListener = new MultiSelectOnClickListener("Choose the departments", this, departments, departmentsTextView);
        departmentsTextView.setOnClickListener(departmentsListener);

        batchesTextView = findViewById(R.id.batchesTextView);
        batchesListener = new MultiSelectOnClickListener("Choose the batches", this, batches, batchesTextView);
        batchesTextView.setOnClickListener(batchesListener);

        fromAcademicYearSpinner = findViewById(R.id.fromAcademicYearSpinner);
        setAdapterAndListenerToSpinner(fromAcademicYearSpinner, getFromAcademicYears(), fromAcademicYearSpinnerItemListener);
        toAcademicYearSpinner = findViewById(R.id.toAcademicYearSpinner);

        viewStudentAchievementsBtn = findViewById(R.id.viewStudentAchievementsBtn);
        viewStudentAchievementsBtn.setOnClickListener(this);

        createBatchBtn = findViewById(R.id.createBatchBtn);
        createBatchBtn.setOnClickListener(this);

    }

    private final AdapterView.OnItemSelectedListener fromAcademicYearSpinnerItemListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            TextView selectedItem = (TextView) parent.getChildAt(0);
            selectedItem.setTextColor(ContextCompat.getColor(StudentAchievements.this, R.color.white));
            selectedItem.setPadding(35, 0, 0, 0);

            setAdapterAndListenerToSpinner(toAcademicYearSpinner, getToAcademicYears(), toAcademicYearSpinnerItemListener);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private final AdapterView.OnItemSelectedListener toAcademicYearSpinnerItemListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if(fromAcademicYearSpinner.getSelectedItem().toString().compareTo("") == 0) {
                return;
            }
            TextView selectedItem = (TextView) parent.getChildAt(0);
            selectedItem.setTextColor(ContextCompat.getColor(StudentAchievements.this, R.color.white));
            selectedItem.setPadding(35, 0, 0, 0);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private String[] getFromAcademicYears() {
        int year = 2017;
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        String[] years = new String[currentYear - year];
        for(int i = year, j = 0; i < currentYear; ++i, ++j) {
            years[j] = Integer.toString(i);
        }
        return years;
    }

    private String[] getToAcademicYears() {
        int fromAcademicYear = Integer.parseInt((String)fromAcademicYearSpinner.getSelectedItem());
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        String[] years = new String[currentYear - fromAcademicYear];
        for(int i = fromAcademicYear + 1, j = 0; i <= currentYear; ++i, ++j) {
            years[j] = Integer.toString(i);
        }
        return years;
    }


    private void setAdapterAndListenerToSpinner(Spinner spinner, String[] data, AdapterView.OnItemSelectedListener itemSelectedListener) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(itemSelectedListener);
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.viewStudentAchievementsBtn) {
            viewStudentAchievements();
        } else if(v.getId() == R.id.createBatchBtn) {
            Intent intent = new Intent(this, CreateBatch.class);
            startActivity(intent);
        }
    }

    private String getToken() {
        return "";
    }

    private boolean isAllDataEntered(String token) {

        try {
            if (selectedBatches.size() <= 0 ||
                    selectedDepartments.size() <= 0 ||
                    fromAcademicYearSpinner.getSelectedItem().toString().compareTo("") == 0 ||
                    toAcademicYearSpinner.getSelectedItem().toString().compareTo("") == 0 ||
                    token.compareTo("") == 0) {
                return false;
            }
        } catch(NullPointerException exp) {
            return false;
        }
        return true;
    }

    private void viewStudentAchievements() {

        selectedDepartments = departmentsListener.getSelectedItemNames();
        selectedBatches = batchesListener.getSelectedItemNames();
        String token = getToken();

        if(isAllDataEntered(token)) {

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, onSuccess, onFailure) {

                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<String, String>();
//                    headers.put("Content-Type", "application/json");
//                    headers.put("Content-Type","application/x-www-form-urlencoded");
                    headers.put("x-access-token", token);
                    return headers;
                }

                @Override
                public String getUrl() {
                    String newUrl = url;
                    StringBuilder stringBuilder = new StringBuilder(url);

                    stringBuilder.append("?from_year=").append(fromAcademicYearSpinner.getSelectedItem().toString());
                    stringBuilder.append("&to_year=").append(toAcademicYearSpinner.getSelectedItem().toString());
                    for(int i = 0; i < selectedDepartments.size(); ++i) {
                        stringBuilder.append("&selected_departments=").append(selectedDepartments.get(i));
                    }
                    for(int i = 0; i < selectedBatches.size(); ++i) {
                        stringBuilder.append("&selected_batches=").append(selectedBatches.get(i));
                    }

                    newUrl = stringBuilder.toString();
                    return newUrl;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(request);

        } else {
            Toast.makeText(this, "Data Missing", Toast.LENGTH_SHORT).show();
        }
    }

    private final Response.Listener<JSONObject> onSuccess = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Toast.makeText(StudentAchievements.this, response.toString(), Toast.LENGTH_LONG).show();
            Log.i("response", response.toString());
        }
    };

    private final Response.ErrorListener onFailure = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(StudentAchievements.this, "Error", Toast.LENGTH_SHORT).show();
        }
    };
}

