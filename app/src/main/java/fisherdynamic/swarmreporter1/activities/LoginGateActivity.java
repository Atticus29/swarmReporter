package fisherdynamic.swarmreporter1.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import fisherdynamic.swarmreporter1.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginGateActivity extends AppCompatActivity implements View.OnClickListener {
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private String userName;
    private String userId;


    @Bind(R.id.gateLoginButton) Button gateLoginButton;
    @Bind(R.id.gateRegisterButton) Button gateRegisterButton;
    @Bind(R.id.aboutButton) Button aboutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_gate);
        ButterKnife.bind(this);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mSharedPreferences.edit();

        userName = mSharedPreferences.getString("userName", null);
        userId = mSharedPreferences.getString("userId", null);
        if(userName != null && userId !=null && !userName.equals("") && !userId.equals("")){
            Intent intent = new Intent(LoginGateActivity.this, MainActivity.class);
            startActivity(intent);
        }

        gateLoginButton.setOnClickListener(this);
        gateRegisterButton.setOnClickListener(this);
        aboutButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == gateRegisterButton){
            Intent intent = new Intent(LoginGateActivity.this, CreateAccountActivity.class);
            startActivity(intent);
        } else if(v == gateLoginButton){
            Intent intent = new Intent(LoginGateActivity.this, LoginActivity.class);
            startActivity(intent);
        } else if (v == aboutButton){
            Intent intent = new Intent(LoginGateActivity.this, AboutActivity.class);
            startActivity(intent);
        }
    }
}
