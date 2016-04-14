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
    //Static properties to be shared across multiple instances of ZAPIClient
    static private GoogleApiClient mGoogleApiClient;
    static private boolean registered = false;
    static private ArrayList<ZAPI> attachedZapis;
    static private ArrayList<onCreateCallback> attachedCallbacks;

    //Initialize the ZAPIClient
    public ZAPIClient(){
        attachedZapis = new ArrayList<>();
        attachedCallbacks = new ArrayList<>();
    }


    public GoogleApiClient getClient(){
        return mGoogleApiClient;
    }

    public boolean isConnected() {
        if (mGoogleApiClient.isConnected())
            return true;
        return false;
    }

    //Allows coders to add and override callbacks,
    //this way specific code can be run as soon as the ZAPIclient connects
    public void addCallback(onCreateCallback c) {
        attachedCallbacks.add(c);
    }

    public class onCreateCallback {
        public void onCreate() {
        }
    }

    private void clientConnected() {
        Toaster("Api onConnects being called");
        for (onCreateCallback c : attachedCallbacks) {
            c.onCreate();
        }
    }

    //Allows coders to add APIs to the list of attached APIs so that those APIs can be properly
    //initialized when the client is created.
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

    //All APIs will implement this interface to add the correct build instructions when the client
    //is created
    interface ZAPI{
        Builder buildInstructions(Builder originalBuilder, Context context, FragmentActivity fragmentActivity);
    }

    //This creates the google client and runs all the build instructions of attached APIs.
    //Finally, the google client attempts to connect in a new thread.
    //When the client connects, all attached callbacks will be run.
    public void registerClient(Context context, FragmentActivity fragmentActivity) {
        Builder clientBuilder = new Builder(context)
                .enableAutoManage(fragmentActivity,
                        this /* OnConnectionFailedListener */);
        for(ZAPI zAPI: attachedZapis){
            clientBuilder = zAPI.buildInstructions(clientBuilder,context,fragmentActivity);
        }
        mGoogleApiClient = clientBuilder.build();
        mGoogleApiClient.connect();
        new Thread() {
            @Override
            public void run() {
                //wait till finished connecting
                while (mGoogleApiClient.isConnecting()) {
                }
                clientConnected();
            }
        }.start();
        registered = true;
    }

    //Handles failures in attempts to connect the google client
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