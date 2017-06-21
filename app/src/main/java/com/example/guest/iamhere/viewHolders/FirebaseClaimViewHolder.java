package com.example.guest.iamhere.viewHolders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.guest.iamhere.R;
import com.example.guest.iamhere.models.SwarmReport;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

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
        distanceTextView.setText("Distance from your current location: " + "m"); //+ Double.toString(calculateDistance(swarmReport.getLocation())) +
        sizeTextView.setText("Size: The size of a " + swarmReport.getSize());
        accessibilityTextView.setText("Accessibility: " + swarmReport.getAccessibility());
        claimSwarmButton.setOnClickListener(this); //not sure whether this will work here
    }

    public Double calculateDistance(LatLng claimerLocation, LatLng reporterLocation){
        Double distanceInMeters = 0.0;
        //...
        return distanceInMeters;
    }

    @Override
    public void onClick(View v) {
//        if(v == claimSwarmButton){
//
//        }
    }
}
