package com.example.guest.iamhere.activities;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guest.iamhere.R;
import com.example.guest.iamhere.SecretConstants;
import com.example.guest.iamhere.models.SwarmReport;
import com.example.guest.iamhere.services.GeoCodingService;
import com.example.guest.iamhere.viewHolders.FirebaseClaimViewHolder;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

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
    private String passedUserProfileURL;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private View hView;
    private NavigationView navigationView;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private GeoFire geoFire;
    private GeoQuery geoQuery;
    private ArrayList<String> swarmReportIds = new ArrayList<>();
    private SwarmReport retrievedModel;
    private ValueEventListener allRefListener;
    private DatabaseReference allRef;

    @Bind(R.id.claimRecyclerView) RecyclerView claimRecyclerView;
    @Bind(R.id.greetingTextView) TextView greetingTextView;
    @Bind(R.id.progressBarForRecyclerView) ProgressBar progressBarForRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawer);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mSharedPreferences.edit();

        String passedUserName = getIntent().getStringExtra("userName");
        String passedUid = getIntent().getStringExtra("userId");

        if(passedUserName != null && passedUid != null){
            addToSharedPreferences("userName", passedUserName);
            addToSharedPreferences("userId", passedUid);
        } else{
            greetingTextView.setText("");
        }

        userName = mSharedPreferences.getString("userName", null);
        userId = mSharedPreferences.getString("userId", null);
        if(userName != null && userId != null){
            greetingTextView.setText("Unclaimed swarms near " + passedUserName + ":");
        }

        setUpBlankAdapter();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        hView = navigationView.getHeaderView(0);

        navigationView.setNavigationItemSelectedListener(this);

        auth = FirebaseAuth.getInstance();
        Log.d("personal", "is auth null? " + Boolean.toString(auth == null));

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = auth.getCurrentUser();
                for (UserInfo userInfo : user.getProviderData()) {
                    Log.d("personal", "is userInfo null? " + Boolean.toString(userInfo == null));
                    if (passedUserProfileURL == null && userInfo.getPhotoUrl() != null) {
                        Log.d("personal", "things are null and not null");
                        passedUserProfileURL = userInfo.getPhotoUrl().toString();
                        Log.d("personal", "photoUrl is " + passedUserProfileURL);
                        ImageView profileImageView = (ImageView) hView.findViewById(R.id.profileImageView);
                        Picasso.with(hView.getContext())
                                .load(passedUserProfileURL)
                                .resize(300, 300)
                                .centerCrop()
                                .into(profileImageView);
                    }
                    if (userId == null && userInfo.getUid() != null) {
                        userId = userInfo.getUid();
                        Log.d("personal", "userId is " + userId);
                    }
                    if (userName == null && userInfo.getDisplayName() != null) {
                        userName = userInfo.getDisplayName();
                        Log.d("personal", "userName is " + userName);
                    }
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

    private void addToSharedPreferences(String key, String passedUserName) {
        mEditor.putString(key, passedUserName).apply();
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
            if(userName != null && userId != null){
                intent.putExtra("userName", userName);
                intent.putExtra("userId", userId);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Unable to retrieve username and id", Toast.LENGTH_SHORT).show();
            }

        }

        if(id == R.id.action_myClaims){
            Intent intent = new Intent(MainActivity.this, MyClaimedSwarmsActivity.class);
            if(userName != null && userId != null){
                intent.putExtra("userName", userName);
                intent.putExtra("userId", userId);
                startActivity(intent);
            } else{
                Toast.makeText(this, "Unable to retrieve username and id", Toast.LENGTH_SHORT).show();
            }
        }

        if(id == R.id.action_myReportedSwarms){
            Intent intent = new Intent(MainActivity.this, MyReportedSwarmsActivity.class);
            if(userName != null && userId != null){
                intent.putExtra("userName", userName);
                intent.putExtra("userId", userId);
                startActivity(intent);
            } else{
                Toast.makeText(this, "Unable to retrieve username and id", Toast.LENGTH_SHORT).show();
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("personal", "got into onConnected");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_FINE_LOCATION);
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation (mGoogleApiClient);
        if(location == null){
            Log.d("personal", "location null");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }else {
            Log.d("personal", "location not null");
            handleNewLocation(location);
        }
    }

    @Override
    public void onRequestPermissionsResult (int requestCode, String permissions[], int[] grantResults){
        Log.d("personal", "got into permissionResults");
        switch(requestCode){
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.d("personal", "permission was granted");
                    Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    if(location == null){
                        Log.d("personal", "location is null inside permission");
                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                    }else {
                        Log.d("personal", "about to call new location");
                        handleNewLocation(location);
                    }
                }
            }
        }
    }

    private void handleNewLocation(Location location){
        Log.d("personal", "got to new location");
        currenLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
        Log.d("personal", "lat is " + Double.toString(currenLatitude) + " and longitude is " + Double.toString(currentLongitude));
        Log.d("personal", "geo coder present? " + Boolean.toString(Geocoder.isPresent()));
        if(currenLatitude != null && currentLongitude !=null){
            setUpGeoFire();
        }

        Geocoder gcd = new Geocoder(MainActivity.this, Locale.getDefault());
        try{
            List<Address> addresses = gcd.getFromLocation(currenLatitude, currentLongitude, 1);
            if (addresses.size() > 0)
            {
                city = addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea() ;

                if(currenLatitude != null && currentLongitude !=null){
                    Log.d("personal", "Got lat and long");
                    Log.d("personal", "lat in handleNewLocation is " + Double.toString(currenLatitude));
                    Log.d("personal", "long in handleNewLocation is " + Double.toString(currentLongitude));
                    Log.d("personal", "query radius is " + Double.toString(SecretConstants.QUERY_RADIUS));
                    ArrayList<String> children = new ArrayList<String>();
                    children.add(city);

//                    setUpFirebaseAdapter(children);
                }
            }
            else
            {
                city = "unknown";
                Log.d("personal", "couldn't get an address from the location");
            }
        } catch(IOException e){
            Log.d("personal", "getFromLocation didn't work");
            e.printStackTrace();

        }

    }

    private void setUpGeoFire(){
        DatabaseReference geoFireRef = FirebaseDatabase.getInstance().getReference().child("geofire");
        geoFire = new GeoFire(geoFireRef);
        Log.d("personal", "is geoFire null? " + Boolean.toString(geoFire == null));
        geoQuery = geoFire.queryAtLocation(new GeoLocation(currenLatitude, currentLongitude), SecretConstants.QUERY_RADIUS);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Log.d("personal", "onKeyEntered entered");
                Log.d("personal", "is key null? " + Boolean.toString(key == null));
                Log.d("personal",String.format("Key %s entered the search area at [%f,%f]", key, location.latitude, location.longitude));
                swarmReportIds.add(key);
            }

            @Override
            public void onKeyExited(String key) {
                Log.d("personal", "onKeyExited entered");
                Log.d("personal",String.format("Key %s is no longer in the search area", key));
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                Log.d("personal", "onKeyMoved entered");
                Log.d("personal", String.format("Key %s moved within the search area to [%f,%f]", key, location.latitude, location.longitude));
            }

            @Override
            public void onGeoQueryReady() {

                Log.d("personal", "All initial data has been loaded and events have been fired!");
//                DatabaseReference userCurrentLocationRef = FirebaseDatabase.getInstance().getReference(Double.toString(currenLatitude) + ", " + Double.toString(currentLongitude));
                for(int i =0; i<swarmReportIds.size(); i++){
                    Log.d("personal", "swarmId: " + swarmReportIds.get(i));
//                    userCurrentLocationRef
                }
                ArrayList<String> children = new ArrayList<String>();
                children.add("all");
                setUpFirebaseAdapter(children);

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                System.err.println("There was an error with this query: " + error);
            }
        });
    }

    private void setUpFirebaseAdapter(final ArrayList<String> children) {
        this.runOnUiThread(new Runnable() { //TODO maybe get rid of this
            @Override
            public void run() {
                swarmReportQuery = FirebaseDatabase.getInstance().getReference(children.get(0));
                for(int i =1; i<children.size(); i++){
                    swarmReportQuery = swarmReportQuery.getRef().child(children.get(i));
                }
//                DatabaseReference swarmReportQueryAsReferenceForGeoFire = (DatabaseReference) swarmReportQuery;
                swarmReportQuery = swarmReportQuery
                        .orderByChild("claimed")
                        .equalTo(false);
                mFirebaseAdapter = new FirebaseRecyclerAdapter<SwarmReport, FirebaseClaimViewHolder>
                        (SwarmReport.class, R.layout.claim_item, FirebaseClaimViewHolder.class,
                                swarmReportQuery) {

                    @Override
                    protected void populateViewHolder(final FirebaseClaimViewHolder viewHolder,
                                                      SwarmReport model, int position) {
                        viewHolder.bindClaimerLatLong(currenLatitude, currentLongitude);
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        viewHolder.bindCurrentUserNameAndId(user.getDisplayName(), user.getUid());
                        Log.d("personal", "position is " + Integer.toString(position));
                        Log.d("personal", "swarmReportIds length is " + Integer.toString(swarmReportIds.size()));
                        if(position < swarmReportIds.size()){
                            String targetKey = swarmReportIds.get(position);
                            allRef = FirebaseDatabase.getInstance().getReference("all").child(targetKey);
                            allRefListener = allRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    SwarmReport retrievedModel = dataSnapshot.getValue(SwarmReport.class);
                                    viewHolder.bindSwarmReport(retrievedModel);
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                        }

                    }
                };
                setUpBlankAdapter();
                Log.d("personal", "got past setUpBlankAdapter");
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(claimRecyclerView.getContext(),
                        new LinearLayoutManager(MainActivity.this).getOrientation());
                dividerItemDecoration.setDrawable(getDrawable(R.drawable.recycler_view_divider));
                claimRecyclerView.addItemDecoration(dividerItemDecoration);
                progressBarForRecyclerView.setVisibility(View.GONE);
            }
        });
    }

//    public static void removeValueEventListener(HashMap<DatabaseReference, ValueEventListener> hashMap) {
//        for (Map.Entry<DatabaseReference, ValueEventListener> entry : hashMap.entrySet()) {
//            DatabaseReference databaseReference = entry.getKey();
//            ValueEventListener valueEventListener = entry.getValue();
//            databaseReference.removeEventListener(valueEventListener);
//        }
//    }

    private void setUpBlankAdapter(){
        Log.d("personal", "got here setUpBlankAdapater");
        claimRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        claimRecyclerView.setLayoutManager(linearLayoutManager);
        claimRecyclerView.setAdapter(mFirebaseAdapter);
        claimRecyclerView.setVisibility(View.VISIBLE);
        Log.d("personal", "setUpBlankAdapter done with function");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mFirebaseAdapter != null){
            mFirebaseAdapter.cleanup();
        }
//        allRef.removeEventListener(allRefListener);
//        removeValueEventListener(hashMap);

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("personal", "Location services suspended. Please reconnect");

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
            Log.d("personal", "Location services failed with code " + connectionResult.getErrorCode());
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
        this.geoQuery.removeAllListeners();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("personal", "location changed");
        handleNewLocation(location);
    }

}
