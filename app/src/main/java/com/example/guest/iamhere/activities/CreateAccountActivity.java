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



    @Bind(R.id.createInputButton) Button createInputButton;
    @Bind(R.id.nameInputTextView) TextView nameInputTextView;
    @Bind(R.id.emailInputTextView) TextView emailInputTextView;
    @Bind(R.id.passwordInputTextView) TextView passwordInputTextView;
    @Bind(R.id.passwordConfirmInputTextView) TextView passwordConfirmInputTextView;
    @Bind(R.id.phoneNumberTextView) TextView phoneNumberTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        ButterKnife.bind(this);
        createAuthStateListener();
        mAuth = FirebaseAuth.getInstance();
        createInputButton.setOnClickListener(this);
        createAuthProgressDialog();
    }

    @Override
    public void onClick(View v) {
        if(v == createInputButton){
            createAccount();
        }
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
                                Toast.makeText(CreateAccountActivity.this, "Account creation was not successful", Toast.LENGTH_SHORT).show();
                            } else if(task.isSuccessful()){
                                createFirebaseUserProfile(task.getResult().getUser());
                                String pushId = task.getResult().getUser().getUid();
                                User currentUser = new User(email, userName, phoneNumber);
                                Log.d("phone number", currentUser.getPhoneNumber());
                                //TODO after you know this works, see if you can resolve the pushId issue
                                FirebaseDatabase db = FirebaseDatabase.getInstance();
                                DatabaseReference ref = db
                                        .getReference("users");
//                                        .child(pushId);
                                ref.push().setValue(currentUser);

                            }
                        }
                    });
        }

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
                            intent.putExtra("userName", userName);
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
            return false;
        }
        return isGoodEmail;
    }

    private boolean isValidName(String name) {
        if (name.equals("")) {
            nameInputTextView.setError("Please enter your name");
            return false;
        }
        return true;
    }

    private boolean isValidPassword(String password, String confirmPassword) {
        if (password.length() < 6) {
            passwordInputTextView.setError("Please create a password containing at least 6 characters");
            return false;
        } else if (!password.equals(confirmPassword)) {
            passwordInputTextView.setError("Passwords do not match");
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
