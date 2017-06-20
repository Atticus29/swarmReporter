package com.example.guest.iamhere.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import com.example.guest.iamhere.R;
import com.example.guest.iamhere.models.SwarmReport;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NewSwarmReportActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = NewSwarmReportActivity.class.getSimpleName();
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private String size;
    private String accessibility;

    @Bind(R.id.reportSwarmButton) Button reportSwarmButton;
    @Bind(R.id.baseball) RadioButton baseball;
    @Bind(R.id.football) RadioButton football;
    @Bind(R.id.basketball) RadioButton basketball;
    @Bind(R.id.beachball) RadioButton beachball;
    @Bind(R.id.tallLadder) RadioButton tallLadder;
    @Bind(R.id.ladder) RadioButton ladder;
    @Bind(R.id.reach) RadioButton reach;
    @Bind(R.id.hasLadder) RadioButton hasLadder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_swarm_report);
        ButterKnife.bind(this);
        reportSwarmButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == reportSwarmButton){
            size = getSize();
            accessibility = getAccessibility();
            Log.d(TAG, size);
            Log.d(TAG, accessibility);

            Calendar calendar = Calendar.getInstance();
            java.util.Date now = calendar.getTime();
            java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());
            String timeString = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(currentTimestamp);
            Log.d(TAG, timeString);
//            SwarmReport newSwarmReport(location, city, userName, userUid, size, timeString, accessibility);
            database = FirebaseDatabase.getInstance();
//            ref = database.getReference(city);

//            ref.push(newSwarmReport);
        }
    }

    public String getSize(){
        String size = null;
        if(baseball.isChecked()){
            size = "baseball";
        } else if (football.isChecked()){
            size = "football";
        } else if(basketball.isChecked()){
            size = "basketball";
        } else if(beachball.isChecked()){
            size = "beachball";
        }
        return size;
    }

    public String getAccessibility(){
        String accessibility = null;
        if(reach.isChecked()){
            accessibility = "reach";
        } else if (ladder.isChecked()){
            accessibility = "ladder";

        } else if(hasLadder.isChecked()){
            accessibility = "hasLadder";
        } else if(tallLadder.isChecked()){
            accessibility = "tallLadder";
        }
        return accessibility;
    }
}
