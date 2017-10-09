package fisherdynamic.swarmreporter1.services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import fisherdynamic.swarmreporter1.R;
import fisherdynamic.swarmreporter1.SecretConstants;
import fisherdynamic.swarmreporter1.activities.MainActivity;
import fisherdynamic.swarmreporter1.activities.ResolverActivity;
import fisherdynamic.swarmreporter1.models.SwarmNotification;
import fisherdynamic.swarmreporter1.models.SwarmReport;
import fisherdynamic.swarmreporter1.utilityClasses.Utilities;
import fisherdynamic.swarmreporter1.viewHolders.FirebaseClaimViewHolder;

public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private GoogleApiClient mGoogleApiClient;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationRequest mLocationRequest;
    private Double currentLatitude;
    private Double currentLongitude;
    private GeoFire geoFire;
    private GeoQuery geoQuery;
    private String claimCheckKey;
    private ArrayList<String> swarmReportIds = new ArrayList<>();
    private String userId;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private FirebaseRecyclerAdapter mFirebaseAdapter;
    private Context serviceContext = null;
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 111; //TODO also in MainActivity. DRY


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("personal", "got into onConnected");

        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            Log.d("personal", "location null");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            Log.d("personal", "location not null");
            handleNewLocation(location);
        }
    }

    private void handleNewLocation(Location location) {
        Log.d("personal", "got to handleNewLocation");
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
        Log.d("personal", "lat from handleNewLocation is " + currentLatitude.toString());
        Log.d("personal", "long from handleNewLocation is " + currentLongitude.toString());
        if (currentLatitude != null && currentLongitude != null) {
            setUpGeoFire();
            sendToActivity(currentLatitude, currentLongitude);
        }
    }

    private void setUpGeoFire() {
        DatabaseReference geoFireRef = FirebaseDatabase.getInstance().getReference("geofire");
        geoFire = new GeoFire(geoFireRef);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(currentLatitude, currentLongitude), SecretConstants.QUERY_RADIUS);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Log.d("personal", "all of the onKeyEntered entered");
                Log.d("personal", String.format("Key %s entered the search area at [%f,%f]", key, location.latitude, location.longitude));
                claimCheckKey = key;
                swarmReportIds.add(claimCheckKey);
                clearCurrentUserNode(userId);
                Utilities.transferSwarmReportsFromAllToNewNode(userId + "_current", swarmReportIds);
                Log.d("personal", "all of the onKeyEntered stuff happened");
                if(serviceContext != null){
                    SwarmNotification swarmNotification = new SwarmNotification("New swarm", "New swarm", "A new swarm has been reported in your area", serviceContext);
                } else {
                    Log.d("personal", "serviceContext was null. Bummer!");
                }
            }

            @Override
            public void onKeyExited(String key) {
                Log.d("personal", "onKeyExited entered");
                Log.d("personal", String.format("Key %s is no longer in the search area", key));
                Utilities.removeItemFromArrayList(key, swarmReportIds);
                clearCurrentUserNode(userId);
                Utilities.transferSwarmReportsFromAllToNewNode(userId + "_current", swarmReportIds);
                Log.d("personal", "onKeyExited stuff all happened");
            }


            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                clearCurrentUserNode(userId);
                swarmReportIds = Utilities.removeItemFromArrayList(key, swarmReportIds);
                Utilities.transferSwarmReportsFromAllToNewNode(userId + "_current", swarmReportIds);
            }

            @Override
            public void onGeoQueryReady() {
                Log.d("personal", "All initial data has been loaded and events have been fired!");
                Utilities.transferSwarmReportsFromAllToNewNode(userId + "_current", swarmReportIds);
                ArrayList<String> children = new ArrayList<>();
                children.add(userId + "_current");
                //setUpFirebaseAdapter equivalent
//                setUpFirebaseAdapter(children);
                //TODO check for asynchronicity issues when the swarm count is very high
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                System.err.println("There was an error with this query: " + error);
            }
        });
    }

    public void sendToActivity(Double currentLatitude, Double currentLongitude){
        Log.d("personal", "got to sendToActivity");
        Intent intent = new Intent("locationServiceUpdates");
//        Bundle bundle = new Bundle();
        intent.putExtra("ServiceLatitudeUpdate", currentLatitude.toString());
        intent.putExtra("ServiceLongitudeUpdate", currentLongitude.toString());
//        bundle.putParcelable("ServiceLatitudeUpdate", currentLatitude);
//        bundle.putParcelable("ServiceLongitudeUpdate", currentLongitude);
//        intent.putExtra("locationUpdate", bundle);
        if(serviceContext != null){
            LocalBroadcastManager.getInstance(serviceContext).sendBroadcast(intent);
        } else{
            Log.d("personal", "didn't broadcast the location updates because serviceContext is null");
        }
    }

    public void clearCurrentUserNode(String userId){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(userId + "_current");
        ref.removeValue();
        Log.d("personal", "removed references in userName_current");
    }

    @Override
    public void onConnectionSuspended(int i) {
        if(serviceContext != null){
            Toast.makeText(serviceContext, "Location services suspended. Please reconnect", Toast.LENGTH_SHORT).show();
        } else {
            Log.d("personal", "connection suspended, but serviceContext was null");
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("personal", "connection failed");
        Intent i = new Intent(this, ResolverActivity.class);
        i.putExtra(ResolverActivity.CONNECT_RESULT_KEY, connectionResult);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("personal", "onLocationChanged entered");
        handleNewLocation(location);
    }

    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        serviceContext = this;
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)
                .setFastestInterval(1 * 1000);

        mGoogleApiClient.connect();
    }

//    @Override
//    public int onStartCommand(Intent intent, @IntDef(value = {Service.START_FLAG_REDELIVERY, Service.START_FLAG_RETRY}, flag = true) int flags, int startId) {
//        Toast.makeText(this, "Location service started", Toast.LENGTH_SHORT).show();
//        return super.onStartCommand(intent, flags, startId);
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        Toast.makeText(this, "Location service destroyed", Toast.LENGTH_SHORT).show();
    }
}
