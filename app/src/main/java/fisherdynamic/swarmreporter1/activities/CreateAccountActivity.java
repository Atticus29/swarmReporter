package fisherdynamic.swarmreporter1.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
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
    private static ProgressDialog mAuthProgressDialog;
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
    private static EditText emailInputTextView;
    private static TextView nameInputTextView;
    private static TextView phoneNumberTextView;


    @Bind(R.id.createInputButton) Button createInputButton;
//    @Bind(R.id.nameInputTextView) EditText nameInputTextView;
//    @Bind(R.id.emailInputTextView) EditText emailInputTextView;
    @Bind(R.id.passwordInputTextView) EditText passwordInputTextView;
    @Bind(R.id.passwordConfirmInputTextView) EditText passwordConfirmInputTextView;
//    @Bind(R.id.phoneNumberTextView) EditText phoneNumberTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        ButterKnife.bind(this);
        emailInputTextView = (EditText) findViewById(R.id.emailInputTextView);
        nameInputTextView = (TextView) findViewById(R.id.nameInputTextView);
        phoneNumberTextView = (TextView) findViewById(R.id.phoneNumberTextView);

        passwordInputTextView.setTypeface(nameInputTextView.getTypeface());
        passwordConfirmInputTextView.setTypeface(nameInputTextView.getTypeface());
        phoneNumberTextView.setTypeface(nameInputTextView.getTypeface());

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mSharedPreferences.edit();
        phoneNumberTextView.setEnabled(true);


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
    }

    private void addToSharedPreferences(String key, String passedUserName) {
        mEditor.putString(key, passedUserName).apply();
    }

    public void createAccount() {


        confirmPassword = passwordConfirmInputTextView.getText().toString().trim();
        password = passwordInputTextView.getText().toString().trim();
        email = emailInputTextView.getText().toString().trim();
        userName = nameInputTextView.getText().toString().trim();
        phoneNumber = phoneNumberTextView.getText().toString().trim();
        if(phoneNumber == null || phoneNumber.equals("")){
            contactOk = false;
        } else{
            contactOk = true;
        }

        if (isValidName(userName) && isValidEmail(email) && isValidPhoneNumber(phoneNumber) && isValidPassword(password, confirmPassword)) {
            mAuthProgressDialog.show();
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

    private static boolean isValidEmail(String email) {
        boolean isGoodEmail =
                (email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches());
        if (!isGoodEmail) {
            emailInputTextView.setError("Please enter a valid email address");
            mAuthProgressDialog.dismiss();
            return false;
        }
        return isGoodEmail;
    }

    private static boolean isValidName(String name) {
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

    public static boolean isValidPhoneNumber (String phoneNumber){
        boolean returnVal = false;
        //PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber)
        if(Patterns.PHONE.matcher(phoneNumber).matches() && phoneNumber.length() > 6 && phoneNumber.length() < 13 || phoneNumber == null || phoneNumber.equals("")){
            returnVal = true;
        }
        if(returnVal == false){
//            Toast.makeText(CreateAccountActivity.this, "Invalid phone number", Toast.LENGTH_SHORT).show();
            phoneNumberTextView.setError("Invalid phone number");
        }
        return returnVal;
    }
}
