package com.bmsce.studentachievements.Admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bmsce.studentachievements.R;
import com.bmsce.studentachievements.Student.AddAchievement;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class CreateBatch extends AppCompatActivity implements View.OnClickListener{

    private ListView batchesListView;
    private Spinner batchesSpinner;
    private Button createBatchBtn;

    private final static String TAG = "CreateBatch";
    private final static String url = "https://trial-sabmsce.herokuapp.com/api/student/createBatch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_batch);

//        String[] batches = getIntent().getStringArrayExtra("batches");

        batchesListView = findViewById(R.id.batchesListView);
        batchesSpinner = findViewById(R.id.batchesSpinner);
        createBatchBtn = findViewById(R.id.createBatchBtn);

        createBatchBtn.setOnClickListener(this);

        displayBatches();
    }

    private String[] getCreatableBatches(String[] batches) {
        ArrayList<String> creatableBatches = new ArrayList<>();
        HashSet<String> batchesSet = new HashSet<>(Arrays.asList(batches));
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for(int i = 2018, j = 0; i <= currentYear; ++i, ++j) {
            String batch = "batch-" + i + "-" + (i+4);
            if(!batchesSet.contains(batch)) {
                creatableBatches.add(batch);
            }
        }
        String[] finalBatches = new String[creatableBatches.size()];
        return creatableBatches.toArray(finalBatches);
    }

    private void displayBatches() {

        String[] batches = {
                "batch-2018-2022",
                "batch-2019-2023",
                "batch-2020-2024"
        };
//        SharedPref.getBatches();

        ArrayAdapter<String> batchesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, batches);
        batchesListView.setAdapter(batchesAdapter);

        setAdapterAndListenerToSpinner(batchesSpinner, getCreatableBatches(batches), batchesSpinnerItemListener);
    }

    private final AdapterView.OnItemSelectedListener batchesSpinnerItemListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            TextView selectedItem = (TextView) parent.getChildAt(0);
            selectedItem.setTextColor(ContextCompat.getColor(CreateBatch.this, R.color.white));
            selectedItem.setPadding(35, 0, 0, 0);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private void setAdapterAndListenerToSpinner(Spinner spinner, String[] data, AdapterView.OnItemSelectedListener itemSelectedListener) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(itemSelectedListener);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.createBatchBtn) {
            createBatch();
        }
    }

    private boolean isAllDataEntered(String token) {
        try {
            if (batchesSpinner.getSelectedItem().toString().compareTo("") == 0 || token.compareTo("") == 0) {
                return false;
            }
        } catch(NullPointerException exp) {
            return false;
        }
        return true;
    }

    private String getToken() {
        return "";
    }

    private void createBatch() {
        String token = getToken();
        if(isAllDataEntered(token)) {

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, getBody(), onSuccess, onFailure) {
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
            body.put("batch_year", batchesSpinner.getSelectedItem().toString().split("-")[1]);
        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
        }
        return body;

    }

    private Response.Listener<JSONObject> onSuccess = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Toast.makeText(CreateBatch.this, response.toString(), Toast.LENGTH_SHORT).show();
            Log.i("response", response.toString());
        }
    };

    private Response.ErrorListener onFailure = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(CreateBatch.this, "Error", Toast.LENGTH_SHORT).show();
        }
    };

}