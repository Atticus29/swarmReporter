package com.example.guest.iamhere.models;

import com.google.android.gms.maps.model.LatLng;

import java.security.Timestamp;

public class SwarmReport {
    private Double latitude;
    private Double longitude;
    private String city;
    private String reporterName;
    private String reporterId;
    private String size;
    private boolean isClaimed;
    private String claimedBy;
    private String reportTimestamp;
    private boolean wasRetrived;
    private String accessibility;
    private String claimantName;
    private String claimantId;

    public SwarmReport(){}

    public SwarmReport(Double latitude, Double longitude, String city, String reporterName, String reporterId, String size, String reportTimestamp, String accessibility) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.city = city;
        this.reporterName = reporterName;
        this.reporterId = reporterId;
        this.size = size;
        this.reportTimestamp = reportTimestamp;
        this.accessibility = accessibility;
    }

    public void setClaimantName(String claimantName) {
        this.claimantName = claimantName;
    }

    public void setClaimantId(String claimantId) {
        this.claimantId = claimantId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getCity() {
        return city;
    }

    public String getReporterName() {
        return reporterName;
    }

    public String getReporterId() {
        return reporterId;
    }

    public String getSize() {
        return size;
    }

    public boolean isClaimed() {
        return isClaimed;
    }

    public String getClaimedBy() {
        return claimedBy;
    }

    public String getReportTimestamp() {
        return reportTimestamp;
    }

    public boolean isWasRetrived() {
        return wasRetrived;
    }

    public String getAccessibility() {
        return accessibility;
    }

    public void setClaimed(boolean claimed) {
        isClaimed = claimed;
    }

    public void setClaimedBy(String claimedBy) {
        this.claimedBy = claimedBy;
    }

    public void setWasRetrived(boolean wasRetrived) {
        this.wasRetrived = wasRetrived;
    }
}
