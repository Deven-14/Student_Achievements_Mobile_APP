package com.bmsce.studentachievements;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.bmsce.studentachievements.Admin.StudentAchievements;
import com.bmsce.studentachievements.Admin.Admin;
import com.bmsce.studentachievements.SharedPreferences.SharedPreferenceManager;
import com.bmsce.studentachievements.Student.StudentSignIn;
import com.bmsce.studentachievements.Student.ViewAchievements;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private GoogleSignInClient mGoogleSignInClient;
    private static String TAG = "MainActivity";
    private ActivityResultLauncher<Intent> someActivityResultLauncher;
    private Button signOutButton;
    private SignInButton signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        setContentView(R.layout.activity_main);

        // Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Set the dimensions of the sign-in button.
        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(this);

        signOutButton = findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(this);

        someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            activityResultCallback
        );

    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        try {
            if(SharedPreferenceManager.readIsSignedIn(getApplicationContext()) && account != null) {
                alreadySignedIn(account);
            } else if (account != null){
                signOut();
            }
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            signOut();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.sign_out_button:
                signOut();
                break;
        }
    }

    private ActivityResultCallback<ActivityResult> activityResultCallback = new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
            if (result.getResultCode() == MainActivity.RESULT_OK) {
                // The Task returned from this call is always completed, no need to attach a listener.
                Intent data = result.getData();
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);
            }
        }
    };

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        someActivityResultLauncher.launch(signInIntent);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            if (account != null) {
                signInButton.setVisibility(View.GONE);
                signOutButton.setVisibility(View.VISIBLE);
                SignInIntoApp(account);
            }

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void signOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                try {
                    SharedPreferenceManager.writeIsSignedInFalse(getApplicationContext());
                    signInButton.setVisibility(View.VISIBLE);
                    signOutButton.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Signed Out", Toast.LENGTH_SHORT).show();
                } catch (GeneralSecurityException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void SignInIntoApp(GoogleSignInAccount account) {
        if(Admin.isAdmin(account.getEmail())) {
            Admin admin = new Admin(getApplicationContext(), account);
            admin.verify();
        } else {
            Log.i(MainActivity.TAG, account.getDisplayName());
            Intent intent = new Intent(this, StudentSignIn.class);
            intent.putExtra("gAccount", account);
            startActivity(intent);
        }
    }

    private void alreadySignedIn(GoogleSignInAccount account) {
        Intent intent;
        if(Admin.isAdmin(account.getEmail())) {
            intent = new Intent(getApplicationContext(), StudentAchievements.class);
        } else {
            intent = new Intent(this, ViewAchievements.class);
        }
        intent.putExtra("gAccount", account);
        startActivity(intent);
    }
}