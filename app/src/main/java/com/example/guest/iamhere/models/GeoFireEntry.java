package com.example.guest.iamhere.models;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by mf on 8/18/17.
 */

//TODO you should be able to remove this class

public class GeoFireEntry {

    private String geoCode;
    private Double latitude;
    private Double longitude;

    public GeoFireEntry(String geoCode, Double latitude, Double longitude) {
        this.geoCode = geoCode;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public GeoFireEntry(SwarmReport swarmReport) {
        this.geoCode = swarmReport.getGeofireCode();
        this.latitude = swarmReport.getLatitude();
        this.longitude = swarmReport.getLongitude();
    }

    public String getGeoCode() {
        return geoCode;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setGeoCode(String geoCode) {
        this.geoCode = geoCode;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void makeEntryInFirebase(String reportId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("geofire").child(reportId);
        ref.child("g").setValue(this.geoCode);
        ref.child("l").child("0").setValue(this.latitude);
        ref.child("l").child("1").setValue(this.longitude);
    }
}
