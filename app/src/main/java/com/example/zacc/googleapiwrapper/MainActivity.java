package com.example.zacc.googleapiwrapper;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;

import com.example.zacc.googleapiwrapper.ZAPIWrapper.*;

public class MainActivity extends FragmentActivity{

    //Create the ZAPIClient and any zApis that you will be using
    ZAPIClient zClient;
    DriveZAPI zDrive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize client and zApis //Add the zApis to the client //Finally, register the client
        zClient = new ZAPIClient();
        zDrive = new DriveZAPI();
        zClient.AddAPI(zDrive);
        zClient.registerClient(this,this);

        //Immediately upon the zClient connecting, the "onCreate" method is called
        zClient.addCallback(zClient.new onCreateCallback() {
            @Override
            public void onCreate() {
                zDrive.CreateDocument(zClient, "Automatic zApi File", "zApi called immediately upon the activities on create", true);
            }
        });

    }

    //Here's an example of how you might make a zApi call through a button press
    public void BtnQuery(View view) {
        RunDriveRequest();
    }

    public void RunDriveRequest() {
        if (zClient.isConnected()) {
            zDrive.CreateDocument(zClient, "Automatic zApi File", "zApi called immediately upon the activities on create", true);
        }
    }
}
