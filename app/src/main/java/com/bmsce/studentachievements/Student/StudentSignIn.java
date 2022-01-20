package com.bmsce.studentachievements.Student;

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
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
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

public class StudentSignIn extends AppCompatActivity implements View.OnClickListener {

    private ImageView image;
    private TextView name, email;
    private EditText usn;
    private Button studentSignInBtn, newAccountBtn;
    private GoogleSignInAccount account;
    private static String TAG = StudentSignIn.class.toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_sign_in);
//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.royal_blue)));

        account = getIntent().getParcelableExtra("gAccount");
        image = findViewById(R.id.image);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        usn = findViewById(R.id.usn);
        studentSignInBtn = findViewById(R.id.student_sign_in_btn);
        newAccountBtn = findViewById(R.id.new_account_btn);

        try{
            Toast.makeText(this, account.getPhotoUrl().toString(), Toast.LENGTH_SHORT).show();
        } catch(NullPointerException e) {
            Toast.makeText(this, "no photo url" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        Glide.with(this).load(account.getPhotoUrl()).into(image);
        name.setText(account.getDisplayName());
        email.setText(account.getEmail());

        studentSignInBtn.setOnClickListener(this);
        newAccountBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.student_sign_in_btn:
                signIn();
                break;
            case R.id.new_account_btn:
                Intent intent = new Intent(this, StudentSignUp.class);
                intent.putExtra("gAccount", account);
                startActivity(intent);
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
                && usn.getText().toString().compareTo("") != 0;
    }

    private void signIn() {
        if(isAllDataEntered()) {

            //request should only be written here, coz we are calling getBody() when we are creating the request object...
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Student.getSignInUri(), getBody(), onSuccess, onFailure);
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(request);

        } else {
            Toast.makeText(this, "Data Missing", Toast.LENGTH_SHORT).show();
        }
    }

    private JSONObject getBody() {

        JSONObject body = new JSONObject();
        try {
            body.put("name", account.getDisplayName());
            body.put("email", account.getEmail());
            body.put("usn", usn.getText().toString());
        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
            return null;
        }
        return body;

    }

    private Response.Listener<JSONObject> onSuccess = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
//            Log.i("response", response.toString());
            try {
                Student.addDataToPreferences(getApplicationContext(), account, response.getJSONObject("userData"));
                Intent intent = new Intent(StudentSignIn.this, ViewAchievements.class);
                startActivity(intent);
            } catch (GeneralSecurityException | IOException | JSONException e) {
                e.printStackTrace();
                Toast.makeText(StudentSignIn.this, "Something Went Wrong!, LogIn Again", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    };

    private Response.ErrorListener onFailure = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(StudentSignIn.this, "Something Went Wrong!, LogIn Again", Toast.LENGTH_SHORT).show();
            finish();
        }
    };

}