package com.example.guest.iamhere.activities;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CreateAccountActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private String TAG = CreateAccountActivity.class.getSimpleName();

    @Bind(R.id.createInputButton) Button createInputButton;
    @Bind(R.id.nameInputTextView) TextView nameInputTextView;
    @Bind(R.id.emailInputTextView) TextView emailInputTextView;
    @Bind(R.id.passwordInputTextView) TextView passwordInputTextView;
    @Bind(R.id.passwordConfirmInputTextView) TextView passwordConfirmInputTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        createInputButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == createInputButton){
            createAccount();
            Intent intent = new Intent(CreateAccountActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    public void createAccount(){
        String confirmPassword = passwordConfirmInputTextView.getText().toString().trim();
        String password = passwordInputTextView.getText().toString().trim();
        String email = emailInputTextView.getText().toString().trim();
        String name = nameInputTextView.getText().toString().trim();

        if(isValidEmail(email) && isValidName(name) && isValidPassword(password, confirmPassword)){
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                            if (!task.isSuccessful()) {
                                Toast.makeText(CreateAccountActivity.this, "Account creation was not successful", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

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

}
