package com.example.guest.iamhere.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guest.iamhere.R;
import com.example.guest.iamhere.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CreateAccountActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private String TAG = CreateAccountActivity.class.getSimpleName();
    private ProgressDialog mAuthProgressDialog;
    private FirebaseAuth.AuthStateListener authStateListener;

    private String userName;
    private String confirmPassword;
    private String email;
    private String password;
    private String phoneNumber;
    private Boolean contactOk;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private String pushId;
    private FirebaseAuth.AuthStateListener mAuthListener;



    @Bind(R.id.createInputButton) Button createInputButton;
    @Bind(R.id.nameInputTextView) TextView nameInputTextView;
    @Bind(R.id.emailInputTextView) TextView emailInputTextView;
    @Bind(R.id.passwordInputTextView) TextView passwordInputTextView;
    @Bind(R.id.passwordConfirmInputTextView) TextView passwordConfirmInputTextView;
    @Bind(R.id.phoneNumberTextView) TextView phoneNumberTextView;
    @Bind(R.id.switch1) Switch switch1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        ButterKnife.bind(this);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mSharedPreferences.edit();

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    Log.d("personal", "switch is Checked");
                    contactOk = true;
                } else {
                    Log.d("personal", "switch is not Checked");
                    contactOk = false;
                }
            }
        });
        createAuthStateListener();
        mAuth = FirebaseAuth.getInstance();
//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                if (user != null) {
//                } else {
//                }
//            }
//        };
        createInputButton.setOnClickListener(this);
        createAuthProgressDialog();
    }

    @Override
    public void onStart() {
        super.onStart();
//        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
//        if (mAuthListener != null) {
//            mAuth.removeAuthStateListener(mAuthListener);
//        }
    }

//    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//    }

    @Override
    public void onClick(View v) {
        if(v == createInputButton){
            createAccount();
        }
    }

    private void addToSharedPreferences(String key, String passedUserName) {
        mEditor.putString(key, passedUserName).apply();
    }

    public void createAccount(){

        mAuthProgressDialog.show();
        confirmPassword = passwordConfirmInputTextView.getText().toString().trim();
        password = passwordInputTextView.getText().toString().trim();
        email = emailInputTextView.getText().toString().trim();
        userName = nameInputTextView.getText().toString().trim();
        phoneNumber = phoneNumberTextView.getText().toString().trim();

        if(isValidEmail(email) && isValidName(userName) && isValidPassword(password, confirmPassword)){
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            mAuthProgressDialog.dismiss();
                            if (!task.isSuccessful()) {
                                Toast.makeText(CreateAccountActivity.this, "Account creation was not successful. Account may already exist.", Toast.LENGTH_SHORT).show();
                            } else if(task.isSuccessful()){
                                createFirebaseUserProfile(task.getResult().getUser());
                                pushId = task.getResult().getUser().getUid();
                                User currentUser = new User(email, userName, phoneNumber, contactOk);
                                Log.d("personal", "CreateAccountActivity pushId inside onComplete is " + pushId);
                                Log.d("personal", "CreateAccountActivity userName inside onComplete is " + userName);
                                FirebaseDatabase db = FirebaseDatabase.getInstance();
                                DatabaseReference ref = db
                                        .getReference("users")
                                        .child(pushId);
                                ref.setValue(currentUser);
                                addToSharedPreferences("userName", userName);
                                addToSharedPreferences("userId", pushId);
//                                login();

                            }
                        }
                    });
        }

    }

    public void login(){
        Log.d("personal", "CreateAccountActivity login entered");
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(CreateAccountActivity.this, "Authentication Failed",
                                    Toast.LENGTH_SHORT).show();
                        }else {

                            Intent intent = new Intent(CreateAccountActivity.this, MainActivity.class);
//                            intent.putExtra("userName", task.getResult().getUser().getDisplayName());
//                            intent.putExtra("userId", task.getResult().getUser().getUid());
                            Log.d("personal", "CreateAccountActivity userId is " + task.getResult().getUser().getUid());
                            startActivity(intent);
                        }
                    }
                });
    }

    private void createFirebaseUserProfile(final FirebaseUser user) {
        UserProfileChangeRequest addProfileName = new UserProfileChangeRequest.Builder()
                .setDisplayName(userName)
                .build();

        user.updateProfile(addProfileName)
                .addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(CreateAccountActivity.this, MainActivity.class);
//                            intent.putExtra("userName", userName);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
//                            Log.d("NameInsideOnComplete", userName);
//                            Log.d(TAG, "got into successful profile update");
//                            Log.d("user", user.getEmail());
                        }
                    }

                });
    }

    private boolean isValidEmail(String email) {
        boolean isGoodEmail =
                (email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches());
        if (!isGoodEmail) {
            emailInputTextView.setError("Please enter a valid email address");
            mAuthProgressDialog.dismiss();
            return false;
        }
        return isGoodEmail;
    }

    private boolean isValidName(String name) {
        if (name.equals("")) {
            nameInputTextView.setError("Please enter your name");
            mAuthProgressDialog.dismiss();
            return false;
        }
        return true;
    }

    private boolean isValidPassword(String password, String confirmPassword) {
        if (password.length() < 6) {
            passwordInputTextView.setError("Please create a password containing at least 6 characters");
            mAuthProgressDialog.dismiss();
            return false;
        } else if (!password.equals(confirmPassword)) {
            passwordInputTextView.setError("Passwords do not match");
            mAuthProgressDialog.dismiss();
            return false;
        }
        return true;
    }

    private void createAuthProgressDialog() {
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle("Loading...");
        mAuthProgressDialog.setMessage("Authenticating with Firebase...");
        mAuthProgressDialog.setCancelable(false);
    }

    public void createAuthStateListener(){
        authStateListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                }
            }
        };
    }

}
