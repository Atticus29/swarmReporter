package fisherdynamic.swarmreporter1.activities;

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
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import fisherdynamic.swarmreporter1.R;
import fisherdynamic.swarmreporter1.models.User;
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
    @Bind(R.id.nameInputTextView) EditText nameInputTextView;
    @Bind(R.id.emailInputTextView) EditText emailInputTextView;
    @Bind(R.id.passwordInputTextView) EditText passwordInputTextView;
    @Bind(R.id.passwordConfirmInputTextView) EditText passwordConfirmInputTextView;
    @Bind(R.id.phoneNumberTextView) EditText phoneNumberTextView;
    @Bind(R.id.switch1) Switch switch1;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        ButterKnife.bind(this);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mSharedPreferences.edit();
        phoneNumberTextView.setEnabled(false);

        switch1.setChecked(false);
        switch1.setOnClickListener(this);


        createAuthStateListener();
        mAuth = FirebaseAuth.getInstance();
        createInputButton.setOnClickListener(this);
        createAuthProgressDialog();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        if (v == createInputButton) {
            createAccount();
        }
        if (v == switch1){
            if(switch1.isChecked()){
                Log.d("personal", "switch1 is checked entered");
                switch1.setChecked(false);
                Log.d("personal", "switch1 actual status is " + Boolean.toString(switch1.isChecked()));
                switch1.toggle();
                contactOk = true;
                phoneNumberTextView.setEnabled(true);
                Log.d("personal", "got to the end of switch1 is checked");
            } else{
                Log.d("personal", "switch1 is not checked");
                switch1.setChecked(true);
                switch1.toggle();
                Log.d("personal", "switch1 actual status is " + Boolean.toString(switch1.isChecked()));
                contactOk = false;
                phoneNumberTextView.setEnabled(false);
                Log.d("personal", "got to the end of switch1 is not checked");
            }
        }
    }

    private void addToSharedPreferences(String key, String passedUserName) {
        mEditor.putString(key, passedUserName).apply();
    }

    public void createAccount() {

        mAuthProgressDialog.show();
        confirmPassword = passwordConfirmInputTextView.getText().toString().trim();
        password = passwordInputTextView.getText().toString().trim();
        email = emailInputTextView.getText().toString().trim();
        userName = nameInputTextView.getText().toString().trim();
        phoneNumber = phoneNumberTextView.getText().toString().trim();

        if (isValidEmail(email) && isValidName(userName) && isValidPassword(password, confirmPassword)) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            mAuthProgressDialog.dismiss();
                            if (!task.isSuccessful()) {
                                Toast.makeText(CreateAccountActivity.this, "Account creation was not successful. Account may already exist.", Toast.LENGTH_SHORT).show();
                            } else if (task.isSuccessful()) {
                                createFirebaseUserProfile(task.getResult().getUser());
                                pushId = task.getResult().getUser().getUid();
                                if (contactOk == null) {
                                    contactOk = false;
                                }
                                Log.d("personal", "contactOk is " + Boolean.toString(contactOk));
                                User currentUser = new User(email, userName, phoneNumber, contactOk);
                                FirebaseDatabase db = FirebaseDatabase.getInstance();
                                DatabaseReference ref = db
                                        .getReference("users")
                                        .child(pushId);
                                ref.setValue(currentUser);
                                addToSharedPreferences("userName", userName);
                                addToSharedPreferences("userId", pushId);

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
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
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

    public void createAuthStateListener() {
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                }
            }
        };
    }

}
