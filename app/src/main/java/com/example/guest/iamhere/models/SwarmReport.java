package com.example.guest.iamhere.models;

import com.google.android.gms.maps.model.LatLng;

import java.security.Timestamp;

public class SwarmReport {
    private Double latitude;
    private Double longitude;
    private String reporterName;
    private String reporterId;
    private String reportId;
    private String size;
    private boolean isClaimed;
    private String reportTimestamp;
    private boolean wasRetrived;
    private String accessibility;
    private String claimantName;
    private String claimantId;
    private String imageString;
    private String description;
    private String geofireCode;

    public SwarmReport(){}

    public SwarmReport(Double latitude, Double longitude, String reporterName, String reporterId, String size, String reportTimestamp, String accessibility, String description) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.reporterName = reporterName;
        this.reporterId = reporterId;
        this.size = size;
        this.reportTimestamp = reportTimestamp;
        this.accessibility = accessibility;
        this.imageString = "https://coxshoney.com/wp-content/uploads/bee_swarm_man.jpg";
        this.description = description;
    }

    public void setGeofireCode(String geofireCode) {
        this.geofireCode = geofireCode;
    }

    public String getGeofireCode() {
        return geofireCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    public void setReporterId(String reporterId) {
        this.reporterId = reporterId;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setReportTimestamp(String reportTimestamp) {
        this.reportTimestamp = reportTimestamp;
    }

    public void setAccessibility(String accessibility) {
        this.accessibility = accessibility;
    }

    public void setImageString(String imageString) {
        this.imageString = imageString;
    }

    public String getImageString() {
        return imageString;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getReportId() {
        return reportId;
    }

    public String getClaimantName() {
        return claimantName;
    }

    public String getClaimantId() {
        return claimantId;
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


    public void setWasRetrived(boolean wasRetrived) {
        this.wasRetrived = wasRetrived;
    }
}
