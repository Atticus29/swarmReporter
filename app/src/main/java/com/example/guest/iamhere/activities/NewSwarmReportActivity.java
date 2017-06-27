package com.example.guest.iamhere.activities;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guest.iamhere.R;
import com.example.guest.iamhere.models.SwarmReport;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NewSwarmReportActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private String TAG = NewSwarmReportActivity.class.getSimpleName();
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private String size;
    private String accessibility;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private GoogleApiClient mGoogleApiClient;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationRequest mLocationRequest;
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 111;
    private String city;
    private String userName;
    private String userId;
    private Double currenLatitude;
    private Double currentLongitude;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    @Bind(R.id.reportSwarmButton) Button reportSwarmButton;
    @Bind(R.id.baseball) RadioButton baseball;
    @Bind(R.id.football) RadioButton football;
    @Bind(R.id.basketball) RadioButton basketball;
    @Bind(R.id.beachball) RadioButton beachball;
    @Bind(R.id.tallLadder) RadioButton tallLadder;
    @Bind(R.id.ladder) RadioButton ladder;
    @Bind(R.id.reach) RadioButton reach;
    @Bind(R.id.hasLadder) RadioButton hasLadder;
    @Bind(R.id.locationTextView) TextView locationTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_swarm_report);
        ButterKnife.bind(this);
        reportSwarmButton.setOnClickListener(this);
        auth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    userName = user.getDisplayName();
                    userId = user.getUid();
                } else {

                }
            }
        };
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)
                .setFastestInterval(1 * 1000);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_FINE_LOCATION);
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            handleNewLocation(location);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    if (location == null) {
                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                    } else {
                        handleNewLocation(location);
                    }
                }
            }
        }
    }

    private void handleNewLocation(Location location) {
        currenLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
        Geocoder gcd = new Geocoder(NewSwarmReportActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(currenLatitude, currentLongitude, 1);
            if (addresses.size() > 0) {
                city = addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea();
                Log.d(TAG, city);
                locationTextView.setText("Looks like you're in: " + city + ". We'll register your swarm there.");
                reportSwarmButton.setVisibility(View.VISIBLE);
            } else {
                city = "unknown";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Location services suspended. Please reconnect");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "Location services failed with code " + connectionResult.getErrorCode());
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == reportSwarmButton) {
            size = getSize();
            accessibility = getAccessibility();
            if (size != null && accessibility != null) {
                Calendar calendar = Calendar.getInstance();
                java.util.Date now = calendar.getTime();
                java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());
                String timeString = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(currentTimestamp);

                SwarmReport newSwarmReport = new SwarmReport(currenLatitude, currentLongitude, city, userName, userId, size, timeString, accessibility);
                database = FirebaseDatabase.getInstance();
                ref = database.getReference(city);
                DatabaseReference pushRef = ref.push();
                String pushId = pushRef.getKey();
                newSwarmReport.setReportId(pushId);
                pushRef.setValue(newSwarmReport);

                Intent intent = new Intent(NewSwarmReportActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(NewSwarmReportActivity.this, "Please select size and accessability", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String getSize() {
        String size = null;
        if (baseball.isChecked()) {
            size = "baseball";
        } else if (football.isChecked()) {
            size = "football";
        } else if (basketball.isChecked()) {
            size = "basketball";
        } else if (beachball.isChecked()) {
            size = "beachball";
        }
        return size;
    }

    public String getAccessibility() {
        String accessibility = null;
        if (reach.isChecked()) {
            accessibility = "reach";
        } else if (ladder.isChecked()) {
            accessibility = "ladder";

        } else if (hasLadder.isChecked()) {
            accessibility = "hasLadder";
        } else if (tallLadder.isChecked()) {
            accessibility = "tallLadder";
        }
        return accessibility;
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }
}
