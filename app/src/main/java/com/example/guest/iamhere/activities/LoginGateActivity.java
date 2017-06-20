package com.example.guest.iamhere.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.guest.iamhere.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginGateActivity extends AppCompatActivity implements View.OnClickListener {
    @Bind(R.id.gateLoginButton) Button gateLoginButton;
    @Bind(R.id.gateRegisterButton) Button gateRegisterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_gate);
        ButterKnife.bind(this);
        gateLoginButton.setOnClickListener(this);
        gateRegisterButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == gateRegisterButton){
            Intent intent = new Intent(LoginGateActivity.this, CreateAccountActivity.class);
            startActivity(intent);
        } else if(v == gateLoginButton){
            Intent intent = new Intent(LoginGateActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }
}
