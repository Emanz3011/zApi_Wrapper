package com.example.zacc.googleapiwrapper;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;

import com.example.zacc.googleapiwrapper.ZAPIWrapper.*;

public class MainActivity extends FragmentActivity{

    ZAPIClient zClient;
    DriveZAPI zDrive;

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
        zDrive.CreateDocument(zClient, "*New* zAPI File", "I created a file from zAPI", true);
    }

    public void BtnQuery(View view) {
        RunDriveRequest();
    }
}
