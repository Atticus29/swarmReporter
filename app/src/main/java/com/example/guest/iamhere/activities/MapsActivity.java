package com.example.guest.iamhere.activities;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.example.guest.iamhere.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MapsActivity extends FragmentActivity {

    @Bind(R.id.mapImageViewFull)
    ImageView mapImageViewFull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.guest.iamhere.R.layout.activity_maps);
        ButterKnife.bind(this);
        String mapUrl = getIntent().getStringExtra("mapURL");
        Log.d("personal", "mapURL is " + mapUrl);

        if(mapUrl != null){
            Picasso.with(this)
                    .load(mapUrl)
                    .resize(600, 600)
                    .centerCrop()
                    .into(mapImageViewFull);
        }
    }
}
