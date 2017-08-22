package com.example.guest.iamhere.utilityClasses;

import android.util.Log;

import com.example.guest.iamhere.models.SwarmReport;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by mf on 7/20/17.
 */

public class Utilities {

    public static SwarmReport returnSwarm = null;

    public static void printArrayListContents (ArrayList<String> aL){
        for(int i=0; i<aL.size(); i++){
            Log.d("personal", aL.get(i));
        }
    }

    public static void addIdsToFirebase (String nodeName, ArrayList<String> ids){
        Log.d("personal", "nodeName is " + nodeName);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(nodeName);
        String pushKey;
        for(int i = 0; i<ids.size(); i++){
            pushKey = ids.get(i);
            DatabaseReference subRef = ref.child(pushKey);
            subRef.setValue(true);
        }
    }

    public static SwarmReport getUnclaimedSwarmObjectWithId (String id) throws Exception{
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("all_unclaimed");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                returnSwarm = dataSnapshot.getValue(SwarmReport.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        if (returnSwarm == null){
            throw new Exception("returnSwarm wasn't located in getUnclaimedSwarmObjectWithId");
        } else{
            return returnSwarm;
        }

    }

    public static void changeSwarmReportAtNodePathTo(ArrayList<String> nodes, SwarmReport newSwarmReport){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(nodes.get(0));
        for (int i = 1; i < nodes.size(); i++) {
            ref = ref.getRef().child(nodes.get(i));
        }
        ref.setValue(newSwarmReport);
    }

    public static void removeSwarmReportAtNodePath(ArrayList<String> nodes){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(nodes.get(0));
        for (int i = 1; i < nodes.size(); i++) {
            ref = ref.getRef().child(nodes.get(i));
        }
        ref.removeValue();
    }

    public static void transferSwarmReportsFromAllToNewNode (String nodeName, ArrayList<String> ids){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(nodeName);
        String pushKey;
        for(int i = 0; i<ids.size(); i++){
            pushKey = ids.get(i);
            final DatabaseReference subRef = ref.child(pushKey);
            DatabaseReference allRef = FirebaseDatabase.getInstance().getReference("all_unclaimed").child(pushKey);
            allRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    SwarmReport currentSwarmReport = dataSnapshot.getValue(SwarmReport.class);
                    subRef.setValue(currentSwarmReport);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public static ArrayList<String> removeItemFromArrayList (String key, ArrayList<String> aList){
        //TODO test this to ensure it works in all cases
        while(aList.remove(key)) {}
//        int originalSize = aList.size();
//        for(int i = 0; i<originalSize; i++){
//            String currentKey = aList.get(i);
//            if (currentKey.equals(key)){
//                aList.remove(i);
//            }
//        }
        return aList;
    }

    public static void establishSwarmReportInGeoFire(SwarmReport swarmReport){
        DatabaseReference geoFireRef = FirebaseDatabase.getInstance()
                .getReference("geofire");
        GeoFire geoFire = new GeoFire(geoFireRef);
        geoFire.setLocation(swarmReport.getReportId(), new GeoLocation(swarmReport.getLatitude(), swarmReport.getLongitude()));
    }

    public static void updateSwarmReportWithItsGeoFireCode(SwarmReport swarmReport, String userId){
        final SwarmReport innerSwarmReportObject = swarmReport;
        final String innerUserId = userId;
        DatabaseReference geoCodeRetrievalRef = FirebaseDatabase.getInstance()
                .getReference("geofire").child(swarmReport.getReportId());
        geoCodeRetrievalRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    try{
                        String geoCode = ds.getValue(String.class);
                        innerSwarmReportObject.setGeofireCode(geoCode);
                        Log.d("personal", "geofireCode is: " + innerSwarmReportObject.getGeofireCode());
                        ArrayList<String> path = new ArrayList<>();
                        path.add("users/" + innerUserId + "/reportedSwarms/" + innerSwarmReportObject.getReportId());
                        Utilities.changeSwarmReportAtNodePathTo(path, innerSwarmReportObject);

                        path = new ArrayList<>();
                        path.add("all_unclaimed/" + innerSwarmReportObject.getReportId());
                        Utilities.changeSwarmReportAtNodePathTo(path, innerSwarmReportObject);
                    } catch(Exception e){
                        e.printStackTrace();
                        continue;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
