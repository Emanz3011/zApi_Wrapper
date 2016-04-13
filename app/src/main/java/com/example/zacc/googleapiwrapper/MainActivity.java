package com.example.zacc.googleapiwrapper;

import android.content.Intent;
import android.content.IntentSender;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.View;

import com.example.zacc.googleapiwrapper.ZAPIWrapper.DriveZAPI;
import com.example.zacc.googleapiwrapper.ZAPIWrapper.ZAPIClient;
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

public class MainActivity extends FragmentActivity{

    private ZAPIClient zClient;
    private DriveZAPI zDrive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        zClient = new ZAPIClient();
        zDrive = new DriveZAPI();
        zClient.AddAPI(zDrive);
        zClient.registerClient(this,this);
    }

    public void RunDriveRequest() {
        zDrive.CreateDocument(zClient,"My File","I created a file from ZAPI",true);
    }

    public void BtnQuery(View view) {
        RunDriveRequest();
    }
}
