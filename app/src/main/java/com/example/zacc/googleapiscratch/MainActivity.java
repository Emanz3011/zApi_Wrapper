package com.example.zacc.googleapiscratch;

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
import com.google.android.gms.drive.*;
import com.google.android.gms.drive.query.*;

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
        mGoogleApiClient.connect();

        // ...

    }

    public void Toaster(String result) {
        Toast.makeText(this, "Result:" + result, Toast.LENGTH_LONG).show();
        System.out.println("Toaster: " + result);
    }

    public void RunQuery(View view) {
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
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //Throw errors here
        Toaster("ERROR ERROR ERROR");
    }

    @Override
    public void onConnected(Bundle bundle) {
        Toaster("Connected to services");
    }

    @Override
    public void onConnectionSuspended(int i) {
        System.out.println("RESULT - Suspended("+i+")");
    }
}
