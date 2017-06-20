package com.example.guest.iamhere.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.guest.iamhere.R;

import butterknife.ButterKnife;

public class LoginGateActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_gate);
        ButterKnife.bind(this);
    }

    @Override
    public void onClick(View v) {

    }
}
