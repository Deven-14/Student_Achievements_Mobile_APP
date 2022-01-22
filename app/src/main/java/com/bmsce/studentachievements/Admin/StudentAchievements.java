package com.bmsce.studentachievements.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bmsce.studentachievements.MainActivity;
import com.bmsce.studentachievements.R;
import com.bmsce.studentachievements.SharedPreferences.SharedPreferenceManager;
import com.bmsce.studentachievements.Student.AddAchievement;
import com.bmsce.studentachievements.Student.StudentSignIn;
import com.bmsce.studentachievements.Student.ViewAchievements;
import com.bmsce.studentachievements.Token.AccessToken;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StudentAchievements extends AppCompatActivity implements View.OnClickListener {

    private TextView departmentsTextView;
    private MultiSelectOnClickListener departmentsListener;

    private TextView batchesTextView;
    private MultiSelectOnClickListener batchesListener;

    private Spinner fromAcademicYearSpinner, toAcademicYearSpinner;
    private Button viewStudentAchievementsBtn, createBatchBtn;

    ArrayList<String> selectedDepartments;
    ArrayList<String> selectedBatches;
    private static final String TAG = StudentAchievements.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.arsenic)));
        getSupportActionBar().setTitle("Student Achievements");
        setContentView(R.layout.activity_student_achievements);

        Set<String> batchesSet = SharedPreferenceManager.read("batches", new HashSet<String>());
        String[] batches = new String[batchesSet.size()];
        batchesSet.toArray(batches);
        Arrays.sort(batches);
        Set<String> departmentsSet = SharedPreferenceManager.read("departments", new HashSet<String>());
        String[] departments = new String[departmentsSet.size()];
        departmentsSet.toArray(departments);

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


    private boolean isAllDataEntered(String token) {

        return selectedBatches.size() != 0
                && selectedDepartments.size() != 0
                && fromAcademicYearSpinner.getSelectedItem().toString().compareTo("") != 0
                && toAcademicYearSpinner.getSelectedItem().toString().compareTo("") != 0
                && token.compareTo(SharedPreferenceManager.DEFAULT_VALUE) != 0;
    }

    private void viewStudentAchievements() {

        selectedDepartments = departmentsListener.getSelectedItemNames();
        selectedBatches = batchesListener.getSelectedItemNames();
        String token = AccessToken.getAccessToken(getApplicationContext());

        if(isAllDataEntered(token)) {

            InputStreamVolleyRequest request = new InputStreamVolleyRequest(Request.Method.GET, getUrl(), onSuccess, onFailure, getHeaders(token));
            RequestQueue requestQueue = Volley.newRequestQueue(this, new HurlStack());
            requestQueue.add(request);

        } else {
            Toast.makeText(this, "Data Missing", Toast.LENGTH_SHORT).show();
        }
    }

    private final Response.Listener<byte[]> onSuccess = new Response.Listener<byte[]>() {
        @Override
        public void onResponse(byte[] response) {
            try {
                if (response!=null) {
                    File newFile = null;
                    if (isExternalStorageWritable() && isExternalStorageReadable()) {
                        String downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                        newFile = new File(downloadsDir, "studentAchievements_" + Calendar.getInstance().getTimeInMillis() + ".xlsx");
                    }
                    FileOutputStream outputStream;
//                    String name="1234.xlsx";
//                    outputStream = openFileOutput(name, Context.MODE_PRIVATE);
                    outputStream = new FileOutputStream(newFile);
                    outputStream.write(response);
                    outputStream.close();
                    Toast.makeText(StudentAchievements.this, "Download complete.", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Log.d("KEY_ERROR", "UNABLE TO DOWNLOAD FILE");
                Toast.makeText(StudentAchievements.this, "File not created", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    };

    private final Response.ErrorListener onFailure = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(StudentAchievements.this, "Download Failed", Toast.LENGTH_SHORT).show();
            error.printStackTrace();
        }
    };

    private HashMap<String, String> getHeaders(String token) {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("x-access-token", token);
        return headers;
    }

    private String getUrl() {
        StringBuilder stringBuilder = new StringBuilder(Admin.getStudentAchievementsUri());

        stringBuilder.append("?from_year=").append(fromAcademicYearSpinner.getSelectedItem().toString());
        stringBuilder.append("&to_year=").append(toAcademicYearSpinner.getSelectedItem().toString());
        for(int i = 0; i < selectedDepartments.size(); ++i) {
            stringBuilder.append("&selected_departments=").append(selectedDepartments.get(i));
        }
        for(int i = 0; i < selectedBatches.size(); ++i) {
            stringBuilder.append("&selected_batches=").append(selectedBatches.get(i));
        }

        return stringBuilder.toString();
    }

    /* Checks if external storage is available for read and write */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }
}

