package com.example.guest.iamhere.services;

import android.util.Log;

import com.example.guest.iamhere.SecretConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GeoCodingService {
    public static void getCity(String latitude, String longitude, Callback callback){
        OkHttpClient client = new OkHttpClient.Builder().build();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(SecretConstants.BASE_GEOCODE_URL).newBuilder();
        urlBuilder.addQueryParameter(SecretConstants.GEOCODE_ADDRESS, latitude + "," + longitude);
        String url = urlBuilder.build().toString() + SecretConstants.GEOCODE_SENSOR;
        Log.d("finalURL", url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static String processResults(Response response){
        Log.d("processResults", "made it here");
        String returnVal = null;
        try{
            String jsonData = response.body().string();
            if(response.isSuccessful()){
                Log.d("successfulResponse", "got in0");
                JSONObject geoJSON = new JSONObject(jsonData);
                Log.d("geoJSON as string", geoJSON.toString());
                JSONObject resultsJSON = geoJSON.getJSONObject("results");
                Log.d("successfulResponse", "got in2");
                resultsJSON = resultsJSON.getJSONObject("address_components");
                Log.d("successfulResponse", "got in3");
                returnVal = resultsJSON.getString("long_name");
                Log.d("successfulResponse", "got in4");
                Log.d("inside", returnVal);
            }
        } catch(IOException e){
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("returnVal", returnVal);
        return returnVal;
    }
}
