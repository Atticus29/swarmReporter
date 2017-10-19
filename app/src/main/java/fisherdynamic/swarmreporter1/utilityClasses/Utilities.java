package fisherdynamic.swarmreporter1.utilityClasses;

import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import fisherdynamic.swarmreporter1.activities.CreateAccountActivity;
import fisherdynamic.swarmreporter1.models.SwarmReport;
import fisherdynamic.swarmreporter1.models.User;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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

    public static void installUserInDatabase(String confirmPassword, String password, String email, String userName, String phoneNumber, String pushId, Boolean contactOk) {
        //usage: installUserInDatabase(passwordConfirmInputTextView.getText().toString().trim(),passwordInputTextView.getText().toString().trim(),  emailInputTextView.getText().toString().trim(), nameInputTextView.getText().toString().trim(), phoneNumberTextView.getText().toString().trim(), //ATTN pushId, //ATTN contactOk);
        if (contactOk == null) {
            contactOk = false;
        }
        User currentUser = new User(email, userName, phoneNumber, contactOk);
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db
                .getReference("users")
                .child(pushId);
        ref.setValue(currentUser);
    }

    public static boolean isValidPhoneNumber (String phoneNumber){
        boolean returnVal = false;
        //PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber)
        if(Patterns.PHONE.matcher(phoneNumber).matches() && phoneNumber.length() > 6 && phoneNumber.length() < 13 || phoneNumber == null || phoneNumber.equals("")){
            returnVal = true;
        }
        return returnVal;
    }

    private static boolean isValidName(String name) {
        if (name.equals("")) {
            //TODO nameInputTextView.setError("Please enter your name");
            //TODO mAuthProgressDialog.dismiss();
            return false;
        }
        return true;
    }

    private boolean isValidPassword(String password, String confirmPassword) {
        if (password.length() < 6) {
            //TODO passwordInputTextView.setError("Please create a password containing at least 6 characters");
            //TODO mAuthProgressDialog.dismiss();
            return false;
        } else if (!password.equals(confirmPassword)) {
            //TODO passwordInputTextView.setError("Passwords do not match");
            //TODO mAuthProgressDialog.dismiss();
            return false;
        }
        return true;
    }

    private static boolean isValidEmail(String email) {
        boolean isGoodEmail =
                (email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches());
        if (!isGoodEmail) {
            //TODO emailInputTextView.setError("Please enter a valid email address");
            //TODO mAuthProgressDialog.dismiss();
            return false;
        }
        return isGoodEmail;
    }
}
