package com.example.zacc.googleapiscratch;

import android.content.Intent;
import android.content.IntentSender;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.*;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.*;
import com.google.android.gms.drive.query.*;
import com.google.android.gms.drive.DriveApi.*;

import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

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

    public void Toaster(String result) {
        Toast.makeText(this, "Result:" + result, Toast.LENGTH_LONG).show();
        System.out.println("Toaster: " + result);
    }

    public void BtnQuery(View view){
        RunQuery();
    }

    public void RunQuery() {
        Toaster("Running Query...");
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.STARRED,false))
                .build();

        /*PendingResult<DriveApi.MetadataBufferResult> result = Drive.DriveApi.query(mGoogleApiClient, query);
        result.*/

        if (mGoogleApiClient.isConnected()) {
            // Invoke the query asynchronously with a callback method
            Drive.DriveApi.query(mGoogleApiClient, query).
            setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
                @Override
                public void onResult(DriveApi.MetadataBufferResult result) {
                    if (result.getStatus().isSuccess()) {
                        MetadataBuffer listOfMetadata = result.getMetadataBuffer();
                        if (listOfMetadata != null) {
                            for (Metadata metaDataFile : listOfMetadata) {
                                Toaster("The file" + metaDataFile.getTitle());
                            }
                        } else {
                            Toaster("Meta Data is null");
                        }
                    } else {
                        Toaster("Failed to load");
                    }
                }
            });

        }else{
            Toaster("Not connected to services");
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
        RunQuery();
    }

    @Override
    public void onConnectionSuspended(int i) {
        System.out.println("RESULT - Suspended("+i+")");
    }
}
