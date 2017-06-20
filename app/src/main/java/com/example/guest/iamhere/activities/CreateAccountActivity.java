package com.example.guest.iamhere.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.guest.iamhere.R;

import butterknife.ButterKnife;

public class CreateAccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        ButterKnife.bind(this);
    }
}
