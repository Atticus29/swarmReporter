package fisherdynamic.swarmreporter1.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import fisherdynamic.swarmreporter1.R;
import fisherdynamic.swarmreporter1.SecretConstants;
import fisherdynamic.swarmreporter1.models.MessageEvent;
import fisherdynamic.swarmreporter1.models.SwarmNotification;
import fisherdynamic.swarmreporter1.models.SwarmReport;
import fisherdynamic.swarmreporter1.services.LocationService;
import fisherdynamic.swarmreporter1.utilityClasses.Utilities;
import fisherdynamic.swarmreporter1.viewHolders.FirebaseClaimViewHolder;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private Query swarmReportQuery;
    private FirebaseRecyclerAdapter mFirebaseAdapter;
    private Double currentLatitude;
    private Double currentLongitude;
    private String userName;
    private String userId;
    private String photoUrl;
    private String passedUserProfileURL;
    private FirebaseAuth auth;
    private View hView;
    private NavigationView navigationView;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private ArrayList<SwarmReport> swarmReports = new ArrayList<>();
    private String claimCheckKey;
    private FirebaseAuth.AuthStateListener authListener;
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 111; //TODO also in location// service. DRY
    private String TAG = MainActivity.class.getSimpleName();

    @Bind(R.id.claimRecyclerView) RecyclerView claimRecyclerView;
    @Bind(R.id.greetingTextView) TextView greetingTextView;
    @Bind(R.id.progressBarForRecyclerView) ProgressBar progressBarForRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, ">>>>onCreate called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawer);
        ButterKnife.bind(this);

//        View swarmReportMenuItem = findViewById(R.id.action_viewAvailableReports);
//        swarmReportMenuItem.setVisibility(View.GONE); //TODO try to make this work


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mSharedPreferences.edit();

        userName = mSharedPreferences.getString("userName", null);
        userId = mSharedPreferences.getString("userId", null);
        if (userName != null && userId != null && !userName.equals("") && !userId.equals("")) {
            greetingTextView.setText("Unclaimed swarms near " + userName + ":");
            clearCurrentUserNode(userId);
        }

        setUpBlankAdapter(); //TODO check whether this is necessary

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        hView = navigationView.getHeaderView(0);

        navigationView.setNavigationItemSelectedListener(this);

        ImageView profileImageView = (ImageView) hView.findViewById(R.id.profileImageView);
        photoUrl = mSharedPreferences.getString("photoUrl", null);
        if(photoUrl != null && !photoUrl.equals("")){
            Log.d(TAG, "photoUrl inside picasso is " + photoUrl);
            Picasso.with(this)
                    .load(photoUrl)
                    .resize(500,500)
                    .centerCrop()
                    .into(profileImageView);
        }

        TextView greetingNameTextView = (TextView) hView.findViewById(R.id.greetingNameTextView);
        if(userName != null){
            Log.d(TAG, "got into changing userName in textView");
            greetingNameTextView.setText(userName);
        }

        //Start location service//
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_FINE_LOCATION);
            return;
        }

        auth = FirebaseAuth.getInstance();
        Log.d(TAG, "is auth null? " + Boolean.toString(auth == null));

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = auth.getCurrentUser();
                for (UserInfo userInfo : user.getProviderData()) {
                    if (passedUserProfileURL == null && userInfo.getPhotoUrl() != null) {
                        passedUserProfileURL = userInfo.getPhotoUrl().toString();
                        ImageView profileImageView = (ImageView) hView.findViewById(R.id.profileImageView);
                        //TODO deal with either including user profile stuff or not
                        Picasso.with(hView.getContext())
                                .load(passedUserProfileURL)
                                .resize(SecretConstants.PIC_WIDTH, SecretConstants.PIC_HEIGHT)
                                .centerCrop()
                                .into(profileImageView);
                    }
                    if (userId == null && userInfo.getUid() != null) {
                        userId = userInfo.getUid();
                    }
                    if (userName == null && userInfo.getDisplayName() != null) {
                        userName = userInfo.getDisplayName();
                    }
                }
            }
        };

    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        currentLatitude = event.lat;
        currentLongitude = event.lng;
        ArrayList<String> children = new ArrayList<>();
        children.add(userId + "_current");
        setUpFirebaseAdapter(children);
    };


//        @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        Log.d(TAG, "got into permissionResults");
//        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Log.d(TAG, "permission was granted");
//                    startLocationService(); //TODO I think necessary? replacing:
//                }
//            }
//        }
//    }

    public void clearCurrentUserNode(String userId){
        Log.d(TAG, "clearCurrentUserNode called");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(userId + "_current");
        ref.removeValue();
        Log.d(TAG, "removed references in userName_current");
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

    private void addToSharedPreferences(String key, String passedUserName) {
        mEditor.putString(key, passedUserName).apply();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            Utilities.logoutWithContextAndSharedPreferences(this, mEditor);
            finish();
            return true;
        }

        if (id == R.id.action_viewAvailableReports){ //&& !TAG.equals("MainActivity")
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        if (id == R.id.action_newReport) { //  && !TAG.equals("NewSwarmReportActivity")
            Intent intent = new Intent(this, NewSwarmReportActivity.class);
            startActivity(intent);
        }

        if (id == R.id.action_myClaims) { //  && !TAG.equals("MyClaimedSwarmsActivity")
            Intent intent = new Intent(this, MyClaimedSwarmsActivity.class);
            startActivity(intent);
        }

        if (id == R.id.action_myReportedSwarms) { //  && !TAG.equals("MyReportedSwarmsActivity")
            Intent intent = new Intent(this, MyReportedSwarmsActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setUpFirebaseAdapter(final ArrayList<String> children) {
        Log.d(TAG, "setUpFirebaseAdapter method entered");
        Log.d(TAG, "first child is " + children.get(0));

        DatabaseReference keyRef = FirebaseDatabase.getInstance().getReference(children.get(0)); //TODO edit here

        for (int i = 1; i < children.size(); i++) {
            keyRef = keyRef.getRef().child(children.get(i));
        }

        mFirebaseAdapter = new FirebaseRecyclerAdapter<SwarmReport, FirebaseClaimViewHolder>
                (SwarmReport.class, R.layout.claim_item, FirebaseClaimViewHolder.class,
                        keyRef) {

            @Override
            protected void populateViewHolder(final FirebaseClaimViewHolder viewHolder,
                                              SwarmReport model, int position) {
                viewHolder.bindClaimerLatLong(currentLatitude, currentLongitude);
                viewHolder.bindCurrentUserNameAndId(userName, userId);
                viewHolder.bindSwarmReport(model);
            }
        };
        Log.d(TAG, "got past setting up the firebaserecycler adapter in setUpFirebaseAdapter method of main activity");
        setUpBlankAdapter(); //TODO check whether necessary
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(claimRecyclerView.getContext(),
                new LinearLayoutManager(MainActivity.this).getOrientation());
        dividerItemDecoration.setDrawable(getDrawable(R.drawable.recycler_view_divider));
        claimRecyclerView.addItemDecoration(dividerItemDecoration);
        Log.d(TAG, "got right up to making the progressBar invisible setUpFirebaseAdapter method of main activity");
        progressBarForRecyclerView.setVisibility(View.GONE);
    }

    private void setUpBlankAdapter() {
        claimRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        claimRecyclerView.setLayoutManager(linearLayoutManager);
        claimRecyclerView.setAdapter(mFirebaseAdapter);
        claimRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, ">>>>onDestroy called");
        super.onDestroy();
        if (mFirebaseAdapter != null) {
            mFirebaseAdapter.cleanup();
        }
    }


    @Override
    public void onStart() {
        Log.d(TAG, ">>>>onStart called");
        super.onStart();
        startLocationService();
        EventBus.getDefault().register(this);

    }

    @Override
    public void onStop() {
        Log.d(TAG, ">>>>onStop called");
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onPause() {
        Log.d(TAG, ">>>>onPause called");
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.d(TAG, ">>>>onResume called");
        Log.d(TAG, "userId is: " + userId);
        Log.d(TAG, "userName is: " + userName);
        super.onResume();
    }

    public void startLocationService(){
        Intent intent = new Intent(this, LocationService.class);
        startService(intent);
    }

}
