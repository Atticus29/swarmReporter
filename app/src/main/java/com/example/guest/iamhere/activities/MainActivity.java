package com.example.guest.iamhere.activities;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.guest.iamhere.R;
import com.example.guest.iamhere.models.SwarmReport;
import com.example.guest.iamhere.viewHolders.FirebaseClaimViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private String TAG = MainActivity.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationRequest mLocationRequest;
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 111;
    private String city;
    private Query swarmReportQuery;
    private FirebaseRecyclerAdapter mFirebaseAdapter;
    private Double currenLatitude;
    private Double currentLongitude;
    private String userName;
    private String userId;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;

    @Bind(R.id.claimRecyclerView) RecyclerView claimRecyclerView;
    @Bind(R.id.greetingTextView) TextView greetingTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawer);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String passedUserName = getIntent().getStringExtra("userName");
        greetingTextView.setText("Unclaimed swarms near, " + passedUserName + ":");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        auth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.d("authStateChangedCheck", "does this even happen?");
                FirebaseUser user = auth.getCurrentUser();
                if (user != null) {
                    Log.d("state change", "got here");
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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(com.example.guest.iamhere.activities.MainActivity.this, LoginGateActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            logout();
            return true;
        }

        if (id == R.id.action_newReport){
            Intent intent = new Intent(MainActivity.this, NewSwarmReportActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_FINE_LOCATION);
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(location == null){
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }else {
            handleNewLocation(location);
        }
    }

    @Override
    public void onRequestPermissionsResult (int requestCode, String permissions[], int[] grantResults){
        switch(requestCode){
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    if(location == null){
                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                    }else {
                        handleNewLocation(location);
                    }
                }
            }
        }
    }

    private void handleNewLocation(Location location){
        currenLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();

        Geocoder gcd = new Geocoder(MainActivity.this, Locale.getDefault());
        try{
            List<Address> addresses = gcd.getFromLocation(currenLatitude, currentLongitude, 1);
            if (addresses.size() > 0)
            {
                city = addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea() ;

                if(currenLatitude != null && currentLongitude !=null){
                    swarmReportQuery = FirebaseDatabase.getInstance()
                            .getReference(city)
                            .orderByChild("claimed")
                            .equalTo(false);
                    setUpFirebaseAdapter();
                }
            }
            else
            {
                city = "unknown";
            }
        } catch(IOException e){
            e.printStackTrace();
        }

    }

    private void setUpFirebaseAdapter() {
        mFirebaseAdapter = new FirebaseRecyclerAdapter<SwarmReport, FirebaseClaimViewHolder>
                (SwarmReport.class, R.layout.claim_item, FirebaseClaimViewHolder.class,
                        swarmReportQuery) {

            @Override
            protected void populateViewHolder(FirebaseClaimViewHolder viewHolder,
                                              SwarmReport model, int position) {
                viewHolder.bindClaimerLatLong(currenLatitude, currentLongitude);
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                viewHolder.bindCurrentUserNameAndId(user.getDisplayName(), user.getUid());
                viewHolder.bindSwarmReport(model);
            }
        };
        claimRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        claimRecyclerView.setLayoutManager(linearLayoutManager);
        claimRecyclerView.setAdapter(mFirebaseAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(claimRecyclerView.getContext(),
                linearLayoutManager.getOrientation());
        dividerItemDecoration.setDrawable(getDrawable(R.drawable.recycler_view_divider));
        claimRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFirebaseAdapter.cleanup();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Location services suspended. Please reconnect");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if(connectionResult.hasResolution()){
            try{
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch(IntentSender.SendIntentException e){
                e.printStackTrace();
            }
        } else{
            Log.d(TAG, "Location services failed with code " + connectionResult.getErrorCode());
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mGoogleApiClient.isConnected()){
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

}
