package com.example.zacc.googleapiwrapper.ZAPIWrapper;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Created by Zacc on 2016-04-12.
 */
public class DriveZAPI implements ZAPIClient.ZAPI{

    @Override
    public GoogleApiClient.Builder buildInstructions(GoogleApiClient.Builder originalBuilder, Context context, FragmentActivity fragmentActivity) {
        originalBuilder.addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE);
        return originalBuilder;
    }

    public void CreateDocument(ZAPIClient client, String fileTitle, String fileContents, boolean isStarred){
        if (client.getClient().isConnected()) {
            Drive.DriveApi.newDriveContents(client.getClient())
                    .setResultCallback(driveContentsCallback(client.getClient(), fileTitle, fileContents,isStarred));
        } else {
            client.getClient().connect();
        }
    }

    //This function returns a new callback class.
    //The new callback class essentially waits for your phone to request access from google docs.
    //Specifically, the callback is waiting to see if you can create new "driveContents",
    //imagine asking your computer if you could make a new .png file.
    //One things to note is that it does not place the file on your drive account just yet,
    //It is merely allowing you to generate and edit a file's data on your phone
    //The fileCallback is what waits upon the actual addition of the file to your drive account
    private ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback(final GoogleApiClient client, final String sTitle, final String sFileText, final boolean isStarred) {
        return new
                ResultCallback<DriveApi.DriveContentsResult>() {
                    @Override
                    public void onResult(DriveApi.DriveContentsResult result) {
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
                                    writer.write(sFileText);
                                    writer.close();
                                } catch (IOException e) {
                                    //Log.e(TAG, e.getMessage());
                                }

                                MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                        .setTitle(sTitle)
                                        .setMimeType("text/plain")
                                        .setStarred(isStarred).build();

                                // create a file in root folder
                                Drive.DriveApi.getRootFolder(client)
                                        .createFile(client, changeSet, driveContents)
                                        .setResultCallback(fileCallback());
                            }
                        }.start();
                    }
                };
    }

    private ResultCallback<DriveFolder.DriveFileResult> fileCallback() {
        return new
                ResultCallback<DriveFolder.DriveFileResult>() {
                    @Override
                    public void onResult(DriveFolder.DriveFileResult result) {
                        if (!result.getStatus().isSuccess()) {
                            Toaster("Error while trying to create the file");
                            return;
                        }
                        Toaster("Created a file with content at: " + result.getDriveFile().getDriveId());
                    }
                };
    }

    //Cleans up debugging and error handling
    private void Toaster(String result) {
        System.out.println("DriveZAPI Toaster: " + result);
    }
}
