package com.harish.buyhatkesmschallenge.views;

import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.gson.Gson;
import com.harish.buyhatkesmschallenge.R;
import com.harish.buyhatkesmschallenge.adapters.UsersRecyclerAdapter;
import com.harish.buyhatkesmschallenge.models.SMSObject;
import com.harish.buyhatkesmschallenge.models.SMSUserObject;
import com.harish.buyhatkesmschallenge.smsutils.SMSUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private SMSUtils smsUtils;
    private static final int SMS_PERMISSION_CODE = 1001;
    private static final int RESOLVE_CONNECTION_REQUEST_CODE = 1003;
    private static final int REQUEST_CODE_CREATOR = 1005;
    private RecyclerView recyclerView;
    private ImageView imgvBackUp;
    private ImageView imgvSearch;
    private ImageView imgvMessage;
    private UsersRecyclerAdapter usersRecyclerAdapter;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }


    private void init() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        imgvBackUp = (ImageView) findViewById(R.id.backup);
        imgvSearch = (ImageView) findViewById(R.id.search);
        imgvMessage = (ImageView) findViewById(R.id.message);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        smsUtils = new SMSUtils(getApplicationContext());
       // checkPermissions();
        clickListeners();
    }
    private void clickListeners()
    {
        imgvMessage.setOnClickListener(this);
        imgvSearch.setOnClickListener(this);
        imgvBackUp.setOnClickListener(this);
    }

    private void initOrUpdateRecyclerView() {
        HashMap<String, ArrayList<SMSObject>> hashMap = smsUtils.getSMS();
        List<String> userList = new ArrayList<String>(hashMap.keySet());

        List<SMSUserObject> smsUserObjectList = new ArrayList<>();
        for (String user :
                userList) {
            SMSUserObject smsUserObject = new SMSUserObject();
            smsUserObject.setName(user);

            smsUserObject.setLastMsg(hashMap.get(user).get(0).getMsg());
            smsUserObject.setId(hashMap.get(user).get(0).getId());
            smsUserObjectList.add(smsUserObject);
        }

        Collections.sort(smsUserObjectList,new MsgComparator());

            usersRecyclerAdapter = new UsersRecyclerAdapter(this, smsUserObjectList, hashMap);
            recyclerView.setAdapter(usersRecyclerAdapter);
    }

    /**
     * Create a new file and save it to Drive.
     */
    public static final String TAG = "drive--->";

    private void saveFileToDrive() {
        // Start by creating a new contents, and setting a callback.
        Log.i(TAG, "Creating new contents.");


        Drive.DriveApi.newDriveContents(mGoogleApiClient)
                .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {

                    @Override
                    public void onResult(DriveApi.DriveContentsResult result) {
                        // If the operation was not successful, we cannot do anything
                        // and must
                        // fail.
                        if (!result.getStatus().isSuccess()) {
                            Log.i(TAG, "Failed to create new contents.");
                            return;
                        }
                        // Otherwise, we can write our data to the new contents.
                        Log.i(TAG, "New contents created.");
                        // Get an output stream for the contents.
                        OutputStream outputStream = result.getDriveContents().getOutputStream();

                        try {
                            // outputStream.write(bitmapStream.toByteArray());

                            byte[] b = convertOjbectToString().getBytes(Charset.forName("UTF-8"));
                            outputStream.write(b);
                        } catch (IOException e1) {
                            Log.i(TAG, "Unable to write file contents.");
                        }
                        // Create the initial metadata - MIME type and title.

                        String time= String.valueOf(System.currentTimeMillis());
                        MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                                .setMimeType("application/json").setTitle("buyhatke_messages_backup_"+time+".json").build();
                        ;
                        // Create an intent for the file chooser, and start it.
                        IntentSender intentSender = Drive.DriveApi
                                .newCreateFileActivityBuilder()
                                .setInitialMetadata(metadataChangeSet)
                                .setInitialDriveContents(result.getDriveContents())
                                .build(mGoogleApiClient);
                        try {
                            startIntentSenderForResult(
                                    intentSender, REQUEST_CODE_CREATOR, null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "Failed to launch file chooser.");
                        }
                    }
                });
    }


    private String convertOjbectToString()
    {
        Gson gson = new Gson();
        return gson.toJson(smsUtils.getAllSMSForBackUp());
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.READ_SMS") == PackageManager.PERMISSION_GRANTED) {
            Log.d("permission", "granted");
            initOrUpdateRecyclerView();

        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.READ_SMS"}, SMS_PERMISSION_CODE);
//            Log.d("permission", "not granted");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case SMS_PERMISSION_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    initOrUpdateRecyclerView();
                } else {
                    // permission denied, boo! Disable the
                }
                return;
            }

        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        if(isConnectedToInternet()) {
            mGoogleApiClient.connect();
        }else {
            Toast.makeText(MainActivity.this, "no internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {


        Log.d("drive------->","onConnectionFailed");

        if (connectionResult.hasResolution()) {

            Log.d("drive------->","hasResolution");
            try {
                connectionResult.startResolutionForResult(this, RESOLVE_CONNECTION_REQUEST_CODE);
            } catch (IntentSender.SendIntentException e) {
                // Unable to resolve, message user appropriately
                Log.d("drive------->","hasResolution-->"+e.getMessage());
            }

        } else {
            Log.d("drive------->","getErrorDialog");

            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
        }
    }
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case RESOLVE_CONNECTION_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    mGoogleApiClient.connect();
                }
            case REQUEST_CODE_CREATOR:
                break;
        }
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Log.d("drive------->","connected");

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("drive------->","onConnectionSuspended");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.message:
                startActivity(new Intent(MainActivity.this,UserSMSActivity.class));
                break;
            case R.id.search:
                startActivity(new Intent(MainActivity.this,MessagesSearchActivity.class));
                break;
            case R.id.backup:
                saveFileToDrive();
                break;
        }
    }

    public class MsgComparator implements Comparator<SMSUserObject> {

        @Override
        public int compare(SMSUserObject p1, SMSUserObject p2) {
            if (p1.getId() < p2.getId()) {
                return 1;
            } else if (p1.getId() > p2.getId()) {
                return -1;
            }
            return 0;
        }
    }
    @Override
    public void onResume()
    {
        checkPermissions();
        super.onResume();
    }

    public boolean isConnectedToInternet() {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {


            return true;

        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {


            return false;
        }
        return false;
    }


}
