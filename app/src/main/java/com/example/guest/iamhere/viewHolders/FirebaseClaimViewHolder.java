package com.example.guest.iamhere.viewHolders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.guest.iamhere.R;
import com.example.guest.iamhere.models.SwarmReport;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseClaimViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener {
    private String TAG = FirebaseClaimViewHolder.class.getSimpleName();
    private Double claimerLatitude;
    private Double claimerLongitude;
    private SwarmReport currentSwarmReport;
    private String userName;
    private String userId;

    View mView;
    Context mContext;
    Button claimButton;

    public FirebaseClaimViewHolder(View itemView) {
        super(itemView);
        this.mView = itemView;
        this.mContext = itemView.getContext();
        claimButton = (Button) itemView.findViewById(R.id.claimSwarmButton);
    }

    public void bindSwarmReport(SwarmReport swarmReport){
        currentSwarmReport = swarmReport;
        TextView timeStampTextView = (TextView) mView.findViewById(R.id.timeStampTextView);
        TextView distanceTextView = (TextView) mView.findViewById(R.id.distanceTextView);
        TextView sizeTextView = (TextView) mView.findViewById(R.id.sizeTextView);
        TextView accessibilityTextView = (TextView) mView.findViewById(R.id.accessibilityTextView);
        Button claimSwarmButton = (Button) mView.findViewById(R.id.claimSwarmButton);

        timeStampTextView.setText("Reported " + swarmReport.getReportTimestamp());
        //TODO get current location lat and long and put here
        distanceTextView.setText("Located " + calculateDistanceAsString(claimerLatitude, swarmReport.getLatitude(), claimerLongitude, swarmReport.getLongitude(),0.0, 0.0) + " meters away in " + swarmReport.getCity());
        sizeTextView.setText("Size: The size of a " + swarmReport.getSize());
        accessibilityTextView.setText("Accessibility: " + swarmReport.getAccessibility());
        claimSwarmButton.setOnClickListener(this); //not sure whether this will work here
    }
    public void bindClaimerLatLong(Double latitude, Double longitude){
        claimerLatitude = latitude;
        claimerLongitude = longitude;
    }

    public void bindCurrentUserNameAndId(String passedUserName, String passedUserId){
        userName = passedUserName;
        userId = passedUserId;
    }

    @Override
    public void onClick(View v) {
        if(v == claimButton){
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
        }
        return String.format("%.0f", distanceInMeters);
    }
}
