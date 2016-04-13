package com.example.zacc.googleapiwrapper.ZAPIWrapper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.*;

import java.util.ArrayList;

public class ZAPIClient implements OnConnectionFailedListener {
    static private GoogleApiClient mGoogleApiClient;
    static private boolean registered = false;
    static private ArrayList<ZAPI> attachedZapis;

    public ZAPIClient(){
        attachedZapis = new ArrayList<>();
    }

    public GoogleApiClient getClient(){
            return mGoogleApiClient;
    }

    public void AddAPI(ZAPI zAPI){
        if (!registered) {
            //Check for copies of api
            for (ZAPI lzapi: attachedZapis){
                if (lzapi.getClass().getName().equals(zAPI.getClass().getName())){
                    Toaster("This API has already been added.");
                    return;
                }
            }
            attachedZapis.add(zAPI);
        }
        else
        Toaster("The client has already been built and registered.");
    }

    interface ZAPI{
        Builder buildInstructions(Builder originalBuilder, Context context, FragmentActivity fragmentActivity);
    }

    public void registerClient(Context context, FragmentActivity fragmentActivity) {
        Builder clientBuilder = new Builder(context)
                .enableAutoManage(fragmentActivity,
                        this /* OnConnectionFailedListener */);
        for(ZAPI zAPI: attachedZapis){
            clientBuilder = zAPI.buildInstructions(clientBuilder,context,fragmentActivity);
        }
        mGoogleApiClient = clientBuilder.build();
        registered = true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toaster("ERROR(" + connectionResult + ")");

        if (connectionResult.hasResolution()) {
            Toaster("Attempting to resolve");
            try {
                connectionResult.startResolutionForResult(new Activity() {
                    @Override
                    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                        Toaster("Attempting to resolve...");
                        super.onActivityResult(requestCode, resultCode, data);
                        if (requestCode == RESULT_OK) {
                            mGoogleApiClient.connect();
                        } else {
                            Toaster("Attempted to resolve; Resolution not OK");

                        }
                    }
                }, connectionResult.getErrorCode());
                Toaster("resolution successful");
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
                Toaster("Failed to resolve");
            }
        } else {
            Toaster("No available solution");
        }
    }

    //Cleans up debugging and error handling
    private void Toaster(String result) {
        System.out.println("ZAPICLIENT Toaster: " + result);
    }
}