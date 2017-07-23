package com.example.guest.iamhere.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guest.iamhere.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{
    @Bind(R.id.emailTextView) TextView emailTextView;
    @Bind(R.id.passwordTextView) TextView passwordTextView;
    @Bind(R.id.loginButton) Button loginButton;
    @Bind(R.id.google_sign_in_button) SignInButton signInButton;
    @Bind(R.id.orText) TextView orText;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String TAG = LoginActivity.class.getSimpleName();
    private ProgressDialog authProgressDialog;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        //Disable google authentication for the time being because the phone number stuff is difficult to do when they log in through google
        signInButton.setVisibility(View.GONE);
        orText.setVisibility(View.GONE);

        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(this);
        createAuthProgressDialog();
        loginButton.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                } else {
                }
            }
        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    public void login(){
        authProgressDialog.show();
        String email = emailTextView.getText().toString().trim();
        String password = passwordTextView.getText().toString().trim();
        if (email.equals("")) {
            emailTextView.setError("Please enter your email");
            return;
        }
        if(!isValidEmail(email)){
            emailTextView.setError("Please enter valid email address");
            return;
        }
        if (password.equals("")) {
            passwordTextView.setError("Password cannot be blank");
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                        authProgressDialog.dismiss();
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication Failed",
                                    Toast.LENGTH_SHORT).show();
                        }else {

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("userName", task.getResult().getUser().getDisplayName());
                            intent.putExtra("userId", task.getResult().getUser().getUid());
                            startActivity(intent);
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == loginButton){
            login();
        } else if (v == signInButton){
            signIn();
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("personal", "status code " + Integer.toString(result.getStatus().getStatusCode()));
        Log.d("personal", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            updateUI(true);
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            try{
                    intent.putExtra("userName", acct.getDisplayName().toString());
                    Log.d("personal handleResult", acct.getDisplayName().toString());
                    intent.putExtra("userId", acct.getId().toString());
                    Log.d("personal userId handle", acct.getId().toString());
                    Log.d("personal", "photo URL handleSignInResult is " + acct.getPhotoUrl().toString());
                    intent.putExtra("photoUrl", acct.getPhotoUrl().toString());
                    startActivity(intent);
            } catch(Exception e){
                    e.printStackTrace();
                Toast.makeText(this, "Couldn't get userName, etc.", Toast.LENGTH_SHORT).show();
                }
        } else {
            updateUI(false);
        }
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
        } else {

        }
    }

    private boolean isValidEmail(String email) {
        boolean isGoodEmail =
                (email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches());
        if (!isGoodEmail) {
            emailTextView.setError("Please enter a valid email address");
            return false;
        }
        return isGoodEmail;
    }

    private void createAuthProgressDialog() {
        authProgressDialog = new ProgressDialog(this);
        authProgressDialog.setTitle("Loading...");
        authProgressDialog.setMessage("Authenticating with Firebase...");
        authProgressDialog.setCancelable(false);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }
}
