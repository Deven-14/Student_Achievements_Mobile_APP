package com.bmsce.studentachievements.Student;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bmsce.studentachievements.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class StudentSignUp extends AppCompatActivity implements View.OnClickListener {

    private ImageView image;
    private TextView name, email;
    private EditText usn, phone;
    private Button studentSignUpBtn, oldAccountBtn;
    private GoogleSignInAccount account;
    private static String TAG = StudentSignUp.class.toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_sign_up);

        account = getIntent().getParcelableExtra("gAccount");
        image = findViewById(R.id.image);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        usn = findViewById(R.id.usn);
        phone = findViewById(R.id.phone);
        studentSignUpBtn = findViewById(R.id.student_sign_up_btn);
        oldAccountBtn = findViewById(R.id.old_account_btn);

        Glide.with(this).load(account.getPhotoUrl()).into(image);
        name.setText(account.getDisplayName());
        email.setText(account.getEmail());

        studentSignUpBtn.setOnClickListener(this);
        oldAccountBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.student_sign_up_btn:
                signUp();
                break;
            case R.id.old_account_btn:
                finish();
                break;
            default:
                Toast.makeText(this, "No Functionality Present", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isAllDataEntered() {
        String email = account.getEmail();
        String name = account.getDisplayName();
        return email != null && email.compareTo("") != 0
                && name != null && name.compareTo("") != 0
                && usn.getText().toString().compareTo("") != 0
                && phone.getText().toString().compareTo("") != 0;
    }

    private void signUp() {
        if(isAllDataEntered()) {

            //request should only be written here, coz we are calling getBody() when we are creating the request object...
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Student.getSignUpUri(), getBody(), onSuccess, onFailure);
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(request);

        } else {
            Toast.makeText(this, "Data Missing.", Toast.LENGTH_SHORT).show();
        }
    }

    private JSONObject getBody() {

        JSONObject body = new JSONObject();
        try {
            body.put("name", account.getDisplayName());
            body.put("email", account.getEmail());
            body.put("usn", usn.getText().toString());
            body.put("phone", usn.getText().toString());
        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
        }
        return body;
    }

    private Response.Listener<JSONObject> onSuccess = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
//            Log.i("response", response.toString());
            try {
                Student.addDataToPreferences(getApplicationContext(), account, response.getJSONObject("userData"));
                Intent intent = new Intent(StudentSignUp.this, ViewAchievements.class);
                startActivity(intent);
            } catch (GeneralSecurityException | IOException | JSONException e) {
                e.printStackTrace();
                Toast.makeText(StudentSignUp.this, "Something Went Wrong!, SignUp Again", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    };

    private Response.ErrorListener onFailure = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(StudentSignUp.this, "Something Went Wrong!, SignUp Again", Toast.LENGTH_SHORT).show();
            finish();
        }
    };
}