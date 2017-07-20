package com.example.guest.iamhere.utilityClasses;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by mf on 7/20/17.
 */

public class Utilities {

    public static void addIdsToFirebase (String nodeName, ArrayList<String> ids){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(nodeName);
        String pushKey;
        for(int i = 0; i<ids.size(); i++){
            pushKey = ids.get(i);
            DatabaseReference subRef = ref.child(pushKey);
            subRef.setValue(true);
        }
    }

    public static ArrayList<String> removeItemFromArrayList (String key, ArrayList<String> aList){
        int originalSize = aList.size();
        for(int i = 0; i<originalSize; i++){
            String currentKey = aList.get(i);
            if (currentKey.equals(key)){
                aList.remove(i);
            }
        }
        return aList;
    }
}
