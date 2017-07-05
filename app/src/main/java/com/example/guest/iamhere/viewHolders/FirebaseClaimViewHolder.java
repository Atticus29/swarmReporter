package com.example.guest.iamhere.viewHolders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.guest.iamhere.R;
import com.example.guest.iamhere.SecretConstants;
import com.example.guest.iamhere.models.SwarmReport;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class FirebaseClaimViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener, OnMapReadyCallback {
    private String TAG = FirebaseClaimViewHolder.class.getSimpleName();
    private Double claimerLatitude;
    private Double claimerLongitude;
    private SwarmReport currentSwarmReport;
    private String userName;
    private String userId;
    private GoogleMap mMap;

    View mView;
    Context mContext;
    Button claimButton;
    ImageView mapImageView;

    public FirebaseClaimViewHolder(View itemView) {
        super(itemView);
        this.mView = itemView;
        this.mContext = itemView.getContext();
        claimButton = (Button) itemView.findViewById(R.id.claimSwarmButton);
        mapImageView = (ImageView) itemView.findViewById(R.id.mapImageView);
    }

    public void bindSwarmReport(SwarmReport swarmReport){
        currentSwarmReport = swarmReport;
        TextView timeStampTextView = (TextView) mView.findViewById(R.id.timeStampTextView);
        TextView distanceTextView = (TextView) mView.findViewById(R.id.distanceTextView);
        TextView sizeTextView = (TextView) mView.findViewById(R.id.sizeTextView);
        TextView accessibilityTextView = (TextView) mView.findViewById(R.id.accessibilityTextView);
        Button claimSwarmButton = (Button) mView.findViewById(R.id.claimSwarmButton);
        claimSwarmButton.setOnClickListener(this);

        timeStampTextView.setText("Reported on: " + swarmReport.getReportTimestamp());

        //TODO get current location lat and long and put here

        distanceTextView.setText("Located " + calculateDistanceAsString(claimerLatitude, swarmReport.getLatitude(), claimerLongitude, swarmReport.getLongitude(),0.0, 0.0) + " miles away in " + swarmReport.getCity());
        sizeTextView.setText("Size: The size of a " + swarmReport.getSize());
        accessibilityTextView.setText("Accessibility: " + swarmReport.getAccessibility());

        ImageView swarmImage = (ImageView) mView.findViewById(R.id.swarmImage);
        if(swarmReport.getImageString() == null){
            swarmReport.setImageString("https://coxshoney.com/wp-content/uploads/bee_swarm_man.jpg");
        }
        dropImageIntoView(swarmReport.getImageString(), mContext, swarmImage);

        String testUrl = "https://maps.googleapis.com/maps/api/staticmap?size=400x400&key=" + SecretConstants.STATIC_MAP_API_KEY + "&markers=size:mid%7Ccolor:red%7C" + Double.toString(claimerLatitude) +"," + Double.toString(claimerLongitude) + "&visible=" + Double.toString(claimerLatitude) +"," + Double.toString(claimerLongitude) + "%7C" + Double.toString(swarmReport.getLatitude()) +"," + Double.toString(swarmReport.getLongitude()) + " &markers=size:mid%7Ccolor:yellow%7Clabel:S%7C" + Double.toString(swarmReport.getLatitude()) +"," + Double.toString(swarmReport.getLongitude());
        dropImageIntoView(testUrl, mContext, mapImageView);
    }
    public void bindClaimerLatLong(Double latitude, Double longitude){
        claimerLatitude = latitude;
        claimerLongitude = longitude;
    }

    public void bindCurrentUserNameAndId(String passedUserName, String passedUserId){
        userName = passedUserName;
        userId = passedUserId;
    }

    public void dropImageIntoView(String imageURL, Context context, ImageView imageView){
        Log.d("personal", "got into dropImageIntoView");
        Log.d("personal", "imageURL is " + imageURL);
        if(imageURL != null){
            if(!imageURL.contains("http")){
                try{
                    Bitmap imageBitmap = decodeFromFirebaseBase64(imageURL);
                    imageView.setImageBitmap(imageBitmap);
                } catch(IOException e){
                    e.printStackTrace();
                }
            } else{
                Picasso.with(context)
                        .load(imageURL)
                        .resize(300, 300)
                        .centerCrop()
                        .into(imageView);
            }
        }
    }

    public static Bitmap decodeFromFirebaseBase64(String image) throws IOException {
        byte[] decodedByteArray = android.util.Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }

    @Override
    public void onClick(View v) {
        if(v == claimButton){
            currentSwarmReport.setClaimed(true);
            DatabaseReference claimerRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(userId)
                    .child("claimedSwarms")
                    .child(currentSwarmReport.getReportId());
            claimerRef.setValue(currentSwarmReport);

            DatabaseReference updateReportedSwarmsRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(currentSwarmReport.getReporterId())
                    .child("reportedSwarms")
                    .child(currentSwarmReport.getReportId())
                    .child("claimed");
            updateReportedSwarmsRef.setValue(true);

            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference(currentSwarmReport.getCity())
                    .child(currentSwarmReport.getReportId())
                    .child("claimed");
            ref.setValue(true);

            DatabaseReference claimantNameRef = FirebaseDatabase.getInstance()
                    .getReference(currentSwarmReport.getCity())
                    .child(currentSwarmReport.getReportId())
                    .child("claimantName");
            claimantNameRef.setValue(userName);

            DatabaseReference claimantIdRef = FirebaseDatabase.getInstance()
                    .getReference(currentSwarmReport.getCity())
                    .child(currentSwarmReport.getReportId())
                    .child("claimantId");
            claimantIdRef.setValue(userId);
        }
    }

    public String calculateDistanceAsString(Double currentLatitude, Double claimLatitude, Double currentLongitude, Double claimLongitude, Double elevation1, Double elevation2){
        //TODO you can update with elevation later

        Double distanceInMeters = new Double(3.14);
        Double distanceInMiles = new Double(3.14);

        final int R = 6371; // Radius of the earth

        Double latDistance = Math.toRadians(claimLatitude - currentLatitude);
        Double lonDistance = Math.toRadians(claimLongitude - currentLongitude);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(currentLatitude)) * Math.cos(Math.toRadians(claimLatitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        Double distance = R * c * 1000; // convert to meters

        Double height = elevation1 - elevation2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);


        if((Double) Math.sqrt(distance) != null){
            distanceInMeters = Math.sqrt(distance);
            distanceInMiles = convertToMiles(distanceInMeters);
        }
        return String.format("%.0f", distanceInMeters);
    }

    public Double convertToMiles(Double distanceInMeters){
        return round(distanceInMeters*0.000621371192,2);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        MapsInitializer.initialize(mContext);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        updateMapContents(sydney);
    }

    protected void updateMapContents(LatLng coordinates) {
        mMap.clear();
        // Update the mapView feature data and camera position.
        mMap.addMarker(new MarkerOptions().position(coordinates));

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(coordinates, 10f);
        mMap.moveCamera(cameraUpdate);
    }
}
