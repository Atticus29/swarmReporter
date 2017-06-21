package com.example.guest.iamhere.viewHolders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.guest.iamhere.R;
import com.example.guest.iamhere.models.SwarmReport;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;

public class FirebaseClaimViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener {
    private static final int MAX_WIDTH = 200;
    private static final int MAX_HEIGHT = 200;

    View mView;
    Context mContext;

    public FirebaseClaimViewHolder(View itemView) {
        super(itemView);
        this.mView = itemView;
        this.mContext = itemView.getContext();
    }

    public void bindSwarmReport(SwarmReport swarmReport){
        TextView timeStampTextView = (TextView) mView.findViewById(R.id.timeStampTextView);
        TextView distanceTextView = (TextView) mView.findViewById(R.id.distanceTextView);
        TextView sizeTextView = (TextView) mView.findViewById(R.id.sizeTextView);
        TextView accessibilityTextView = (TextView) mView.findViewById(R.id.accessibilityTextView);
        Button claimSwarmButton = (Button) mView.findViewById(R.id.claimSwarmButton);

        timeStampTextView.setText("Reported " + swarmReport.getReportTimestamp());
        Log.d("lat", Double.toString(swarmReport.getLatitude()));
        Log.d("long", Double.toString(swarmReport.getLongitude()));
        distanceTextView.setText("Located " + calculateDistanceAsString(3.0,swarmReport.getLatitude(), 3.0, swarmReport.getLongitude(),0.0, 0.0) + "meters away");
        sizeTextView.setText("Size: The size of a " + swarmReport.getSize());
        accessibilityTextView.setText("Accessibility: " + swarmReport.getAccessibility());
        claimSwarmButton.setOnClickListener(this); //not sure whether this will work here
    }

    @Override
    public void onClick(View v) {
//        if(v == claimSwarmButton){
//
//        }
    }

    public String calculateDistanceAsString(Double currentLatitude, Double claimLatitude, Double currentLongitude, Double claimLongitude, Double elevation1, Double elevation2){

        //TODO you can update with elevation later

        Double distanceInMeters = 0.0;
        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(claimLatitude - currentLatitude);
        double lonDistance = Math.toRadians(claimLongitude - currentLongitude);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(currentLatitude)) * Math.cos(Math.toRadians(claimLatitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = elevation1 - elevation2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);


        if((Double) Math.sqrt(distance) != null){
            distanceInMeters = Math.sqrt(distance);
        }
        return String.format("%.0f", distanceInMeters);
    }
}
