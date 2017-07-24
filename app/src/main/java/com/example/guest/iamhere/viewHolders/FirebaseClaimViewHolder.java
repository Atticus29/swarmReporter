package com.example.guest.iamhere.viewHolders;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.guest.iamhere.R;
import com.example.guest.iamhere.SecretConstants;
import com.example.guest.iamhere.activities.MapActivity;
import com.example.guest.iamhere.models.SwarmReport;
import com.example.guest.iamhere.models.User;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
    private String staticMapURL;
    private User currentReporter;
    private SwarmReport myClaimSwarmReport;

    View mView;
    Context mContext;
    Button claimButton;
    Button cancelSwarmClaimButtonMyReportedSwarms;
    Button cancelMyClaimButton;
    ImageView mapImageView;
    TextView contactTextViewMyReportedSwarms;
    TextView contactReporterTextViewMyClaims;

    public FirebaseClaimViewHolder(View itemView) {
        super(itemView);
        this.mView = itemView;
        this.mContext = itemView.getContext();
        claimButton = (Button) itemView.findViewById(R.id.claimSwarmButton);
        //TODO maybe add buttons and contactTextViewMyReportedSwarms here? Test without first
        mapImageView = (ImageView) itemView.findViewById(R.id.mapImageView);
    }

    public void bindSwarmReportForMyReportedSwarms(SwarmReport swarmReport){
        ImageView swarmImageMyReportedSwarms = (ImageView) mView.findViewById(R.id.swarmImageMyReportedSwarms);
        dropImageIntoView(swarmReport.getImageString(), mContext, swarmImageMyReportedSwarms);

        TextView timeStampTextViewMyReportedSwarms = (TextView) mView.findViewById(R.id.timeStampTextViewMyReportedSwarms);
        timeStampTextViewMyReportedSwarms.setText("Reported on: " + swarmReport.getReportTimestamp());

        TextView claimantTextViewMyReportedSwarms = (TextView) mView.findViewById(R.id.claimantTextViewMyReportedSwarms);
        if(swarmReport.isClaimed()){
            if(swarmReport.getClaimantId() != null){
                claimantTextViewMyReportedSwarms.setText("Claimed by: " + swarmReport.getClaimantName());
            } else{
                claimantTextViewMyReportedSwarms.setText("");
            }

        } else{
            claimantTextViewMyReportedSwarms.setText("This swarm has not yet been claimed");
        }


        TextView contactTextViewMyReportedSwarms = (TextView) mView.findViewById(R.id.contactTextViewMyReportedSwarms);
        if(swarmReport.isClaimed()){
            contactTextViewMyReportedSwarms.setVisibility(View.VISIBLE);
            contactTextViewMyReportedSwarms.setText("Contact the claimant");
            contactTextViewMyReportedSwarms.setOnClickListener(this);

        } else{
            Log.d("personal", "swarmIs Unclaimed so contact option not made available");
        }

        TextView sizeTextViewMyReportedSwarms = (TextView) mView.findViewById(R.id.sizeTextViewMyReportedSwarms);
        sizeTextViewMyReportedSwarms.setText("Size: The size of a " + swarmReport.getSize());

        TextView accessibilityTextViewMyReportedSwarms = (TextView) mView.findViewById(R.id.accessibilityTextViewMyReportedSwarms);
        accessibilityTextViewMyReportedSwarms.setText("Accessibility: " + swarmReport.getAccessibility());

        Button cancelSwarmClaimButtonMyReportedSwarms = (Button) mView.findViewById(R.id.cancelSwarmClaimButtonMyReportedSwarms);
        cancelSwarmClaimButtonMyReportedSwarms.setOnClickListener(this);
    }

    public void bindSwarmReportForMyClaims(SwarmReport swarmReport){
        myClaimSwarmReport = swarmReport;
        cancelMyClaimButton = (Button) mView.findViewById(R.id.cancelMyClaimButton);
        cancelMyClaimButton.setOnClickListener(this);

        TextView accessibilityTextViewMyClaims = (TextView) mView.findViewById(R.id.accessibilityTextViewMyClaims);
        accessibilityTextViewMyClaims.setText("Accessibility: " + swarmReport.getAccessibility());

        TextView sizeTextViewMyClaims = (TextView) mView.findViewById(R.id.sizeTextViewMyClaims);
        sizeTextViewMyClaims.setText("The size of a: " + swarmReport.getSize());

        TextView reportedByTextViewMyClaims = (TextView) mView.findViewById(R.id.reportedByTextViewMyClaims);
        reportedByTextViewMyClaims.setText("Reported by: " + swarmReport.getReporterName());

        contactReporterTextViewMyClaims = (TextView) mView.findViewById(R.id.contactReporterTextViewMyClaims);

        contactReporterTextViewMyClaims.setOnClickListener(this);

        String userPushId = swarmReport.getReporterId();
        DatabaseReference currentReporterRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userPushId);

        currentReporterRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentReporter = dataSnapshot.getValue(User.class);
                if(currentReporter.getContactOk()){
                    contactReporterTextViewMyClaims.setText("Call " + currentReporter.getUserName());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        TextView timeStampTextViewMyClaims = (TextView) mView.findViewById(R.id.timeStampTextViewMyClaims);
        timeStampTextViewMyClaims.setText("Reported on: " + swarmReport.getReportTimestamp());

        ImageView swarmImageMyClaims = (ImageView) mView.findViewById(R.id.swarmImageMyClaims);
        dropImageIntoView(swarmReport.getImageString(), mContext, swarmImageMyClaims);

    }

    public void bindSwarmReport(SwarmReport swarmReport){
        currentSwarmReport = swarmReport;
        TextView timeStampTextView = (TextView) mView.findViewById(R.id.timeStampTextView);
        TextView distanceTextView = (TextView) mView.findViewById(R.id.claimantTextView);
        TextView sizeTextView = (TextView) mView.findViewById(R.id.sizeTextView);
        TextView accessibilityTextView = (TextView) mView.findViewById(R.id.accessibilityTextView);
        Button claimSwarmButton = (Button) mView.findViewById(R.id.claimSwarmButton);
        claimSwarmButton.setOnClickListener(this);
        mapImageView.setOnClickListener(this);

        timeStampTextView.setText("Reported on: " + swarmReport.getReportTimestamp());

        //TODO get current location lat and long and put here

        distanceTextView.setText("Located " + calculateDistanceAsString(claimerLatitude, swarmReport.getLatitude(), claimerLongitude, swarmReport.getLongitude(),0.0, 0.0) + " miles away.");
        sizeTextView.setText("Size: The size of a " + swarmReport.getSize());
        accessibilityTextView.setText("Accessibility: " + swarmReport.getAccessibility());

        ImageView swarmImage = (ImageView) mView.findViewById(R.id.swarmImage);
        if(swarmReport.getImageString() == null){
            swarmReport.setImageString("https://coxshoney.com/wp-content/uploads/bee_swarm_man.jpg");
        }
        dropImageIntoView(swarmReport.getImageString(), mContext, swarmImage);

        staticMapURL = "https://maps.googleapis.com/maps/api/staticmap?size=400x400&key=" + SecretConstants.STATIC_MAP_API_KEY + "&markers=size:mid%7Ccolor:red%7C" + Double.toString(claimerLatitude) +"," + Double.toString(claimerLongitude) + "&visible=" + Double.toString(claimerLatitude) +"," + Double.toString(claimerLongitude) + "%7C" + Double.toString(swarmReport.getLatitude()) +"," + Double.toString(swarmReport.getLongitude()) + " &markers=size:mid%7Ccolor:yellow%7Clabel:S%7C" + Double.toString(swarmReport.getLatitude()) +"," + Double.toString(swarmReport.getLongitude());
        dropImageIntoView(staticMapURL, mContext, mapImageView);
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
                        .resize(SecretConstants.PIC_WIDTH, SecretConstants.PIC_HEIGHT)
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
            currentSwarmReport.setClaimantId(userId);
            currentSwarmReport.setClaimantName(userName);

            DatabaseReference reportedRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(currentSwarmReport.getReporterId())
                    .child("reportedSwarms")
                    .child(currentSwarmReport.getReportId());
            reportedRef.setValue(currentSwarmReport);

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
                    .getReference(userId+"_current")
                    .child(currentSwarmReport.getReportId())
                    .child("claimed");
            ref.setValue(true);

            DatabaseReference claimantNameRef = FirebaseDatabase.getInstance()
                    .getReference(userId+"_current")
                    .child(currentSwarmReport.getReportId())
                    .child("claimantName");
            claimantNameRef.setValue(userName);

            DatabaseReference claimantIdRef = FirebaseDatabase.getInstance()
                    .getReference(userId+"_current")
                    .child(currentSwarmReport.getReportId())
                    .child("claimantId");
            claimantIdRef.setValue(userId);

            DatabaseReference allClaimantClaimedRef = FirebaseDatabase.getInstance()
                    .getReference("all")
                    .child(currentSwarmReport.getReportId())
                    .child("claimed");
            allClaimantClaimedRef.setValue(true);

            DatabaseReference allClaimantNameRef = FirebaseDatabase.getInstance()
                    .getReference("all")
                    .child(currentSwarmReport.getReportId())
                    .child("claimantName");
            allClaimantNameRef.setValue(userName);

            DatabaseReference allClaimantIdRef = FirebaseDatabase.getInstance()
                    .getReference("all")
                    .child(currentSwarmReport.getReportId())
                    .child("claimantId");
            allClaimantIdRef.setValue(userId);
        }
        if(v == mapImageView){
            Intent intent = new Intent(mContext, MapActivity.class);
            intent.putExtra("mapURL", staticMapURL);
            mContext.startActivity(intent);
        }

        if(v == contactReporterTextViewMyClaims){
            dialPhoneNumber(currentReporter.getPhoneNumber());

        }
        if(v == cancelMyClaimButton){
            Log.d("personal", "myClaim cancelMyClaimButton clicked");
            String claimantNameTemp = myClaimSwarmReport.getClaimantName();
            String claimantIdTemp = myClaimSwarmReport.getClaimantId();
            myClaimSwarmReport.setClaimantName(null);
            myClaimSwarmReport.setClaimantId(null);
            myClaimSwarmReport.setClaimed(false);

            //Removes the claim from the user who claimed its list AND resets claim status in reporter, city, and all back to false
            DatabaseReference allClaimantIdRef = FirebaseDatabase.getInstance()
                    .getReference("all")
                    .child(myClaimSwarmReport.getReportId());
            allClaimantIdRef.setValue(myClaimSwarmReport);

            DatabaseReference reportedSwarmRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(userId)
                    .child("reportedSwarms")
                    .child(myClaimSwarmReport.getReportId());
            reportedSwarmRef.setValue(myClaimSwarmReport);



            DatabaseReference currentUserLocationClaimantIdRef = FirebaseDatabase.getInstance()
                    .getReference(userId+"_current")
                    .child(myClaimSwarmReport.getReportId());
            currentUserLocationClaimantIdRef.setValue(myClaimSwarmReport);

            DatabaseReference reporterClaimantIdRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(myClaimSwarmReport.getReporterId())
                    .child("reportedSwarms")
                    .child(myClaimSwarmReport.getReportId());
            reporterClaimantIdRef.setValue(myClaimSwarmReport);

            DatabaseReference claimantClaimantIdRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(claimantIdTemp)
                    .child("claimedSwarms")
                    .child(myClaimSwarmReport.getReportId());
            claimantClaimantIdRef.removeValue(); //TODO check that this works
        }
        if(v == cancelSwarmClaimButtonMyReportedSwarms){
            //Deletes the swarmReport from everywhere in the DB
            DatabaseReference allClaimantIdRef = FirebaseDatabase.getInstance()
                    .getReference("all")
                    .child(currentSwarmReport.getReportId());
            allClaimantIdRef.removeValue();

            DatabaseReference currentUserLocationClaimantIdRef = FirebaseDatabase.getInstance()
                    .getReference(userId+"_current")
                    .child(currentSwarmReport.getReportId());
            currentUserLocationClaimantIdRef.removeValue();

            DatabaseReference reporterClaimantIdRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(currentSwarmReport.getReporterId())
                    .child("reportedSwarms")
                    .child(currentSwarmReport.getReportId());
            reporterClaimantIdRef.removeValue();

            DatabaseReference claimantClaimantIdRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(currentSwarmReport.getClaimantId())
                    .child("claimedSwarms")
                    .child(currentSwarmReport.getReportId());
            claimantClaimantIdRef.removeValue(); //TODO check that this works
        }
        if(v == contactTextViewMyReportedSwarms){
            DatabaseReference dbRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(currentSwarmReport.getClaimantId())
                    .child("phoneNumber");
            dbRef.addValueEventListener(new ValueEventListener() {
                String phoneNumber = "";
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    phoneNumber = dataSnapshot.getValue().toString();
                    Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                    mContext.startActivity(phoneIntent);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("personal", "contact number event listener got cancelled");
                }
            });
        }
    }

    public void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        mContext.startActivity(intent);
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
        if (distanceInMiles < 1.0){
            return " < 1";
        } else{
            return String.format("%.0f", distanceInMiles);
        }
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
        mMap.addMarker(new MarkerOptions().position(coordinates));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(coordinates, 10f);
        mMap.moveCamera(cameraUpdate);
    }
}
