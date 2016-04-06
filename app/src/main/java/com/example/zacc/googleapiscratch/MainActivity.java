package com.example.zacc.googleapiscratch;

import android.content.Intent;
import android.content.IntentSender;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.*;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.*;
import com.google.android.gms.drive.DriveApi.*;
import com.google.android.gms.drive.DriveFolder.*;


import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import java.io.*;

public class MainActivity extends FragmentActivity
        implements ConnectionCallbacks, OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create a GoogleApiClient instance
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .build();
        mGoogleApiClient.registerConnectionCallbacks(this);
        mGoogleApiClient.registerConnectionFailedListener(this);
        mGoogleApiClient.connect();
        // ...

    }

    final private ResultCallback<DriveContentsResult> driveContentsCallback = new
            ResultCallback<DriveContentsResult>() {
                @Override
                public void onResult(DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Toaster("Error while trying to create new file contents");
                        return;
                    }
                    final DriveContents driveContents = result.getDriveContents();

                    // Perform I/O off the UI thread.
                    new Thread() {
                        @Override
                        public void run() {
                            // write content to DriveContents
                            OutputStream outputStream = driveContents.getOutputStream();
                            Writer writer = new OutputStreamWriter(outputStream);
                            try {
                                writer.write("Hello World!");
                                writer.close();
                            } catch (IOException e) {
                                //Log.e(TAG, e.getMessage());
                            }

                            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                    .setTitle("New file")
                                    .setMimeType("text/plain")
                                    .setStarred(true).build();

                            // create a file in root folder
                            Drive.DriveApi.getRootFolder(mGoogleApiClient)
                                    .createFile(mGoogleApiClient, changeSet, driveContents)
                                    .setResultCallback(fileCallback);
                        }
                    }.start();
                }
            };

    final private ResultCallback<DriveFileResult> fileCallback = new
            ResultCallback<DriveFileResult>() {
                @Override
                public void onResult(DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Toaster("Error while trying to create the file");
                        return;
                    }
                    Toaster("Created a file with content: " + result.getDriveFile().getDriveId());
                }
            };



    public void RunDriveRequest() {

        if (mGoogleApiClient.isConnected()){
            Drive.DriveApi.newDriveContents(mGoogleApiClient)
                    .setResultCallback(driveContentsCallback);
        }else{
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //Throw errors here
        Toaster("ERROR(" + connectionResult + ")");

        if (connectionResult.hasResolution()){
            Toaster("Attempting to resolve");
            try {
                connectionResult.startResolutionForResult(this,connectionResult.getErrorCode());
                Toaster("resolution successful");
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
                Toaster("Failed to resolve");
            }
        }else{
            Toaster("No available solution");
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_OK){
            mGoogleApiClient.connect();
        }else{
            Toaster("Attempted to resolve; Resolution not OK");

        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Toaster("Connected to services");
        RunDriveRequest();
    }

    @Override
    public void onConnectionSuspended(int i) {
        System.out.println("RESULT - Suspended("+i+")");
    }

    public void Toaster(String result) {
        Toast.makeText(this, "Result:" + result, Toast.LENGTH_LONG).show();
        System.out.println("Toaster: " + result);
    }

    public void BtnQuery(View view){
        RunDriveRequest();
    }
}
