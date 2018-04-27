package com.pathfinder;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.pathfinder.Models.BreadCrumb;
import com.pathfinder.Models.Route;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

//activity that records a route, dropping breadcrumbs as and when the user instructs.
public class RecordingActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_drop_breadcrumb, btn_save_and_exit;
    List<BreadCrumb> breadCrumbs = new ArrayList();

    //recording related variables
    String fileLocationAudio= Environment.getExternalStorageDirectory()+ File.separator +"PathFinder"+ File.separator+"Audio"+File.separator;
    String fileLocationRoutes= Environment.getExternalStorageDirectory()+ File.separator +"PathFinder"+ File.separator+"Routes"+File.separator;

    boolean isRecording = false;
    boolean isSaving=false;
    MediaRecorderHelper mediaRecorderHelper;
    Location location=null;
    TextToSpeech tts;
    boolean ttsInitialized = false;
    FusedLocationProviderClient locationProviderClient;
    LocationRequest locationRequest = new LocationRequest();
    LocationCallback mLocationCallBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_recording);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        tts = new TextToSpeech(RecordingActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.ENGLISH);
                    ttsInitialized=true;
                }
            }
        });

        btn_drop_breadcrumb = (Button) findViewById(R.id.btn_drop_breadcrumb);
        btn_save_and_exit = (Button) findViewById(R.id.btn_end_save);

        btn_drop_breadcrumb.setOnClickListener(this);
        btn_save_and_exit.setOnClickListener(this);

//        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//
//        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0.0f, locationListener);
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        mLocationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location loc : locationResult.getLocations()) {
                    location = loc;
                    Log.d("New Location Update",""+location.getLongitude()+":"+location.getLatitude());
                    break;
                }
            }
        };


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            //On clicking the drop button the recording starts and the text of this button is changed to Stop recording. On Clicked stop recording the details of this
            //breadcrumb are saved on file.
            case R.id.btn_drop_breadcrumb:
                if(!isRecording) {
                    try {
                        if(location!=null) {
                            String audioPath = fileLocationAudio + String.valueOf(System.currentTimeMillis());
                            mediaRecorderHelper = new MediaRecorderHelper(audioPath);
                            mediaRecorderHelper.startRecording();

                            String street = getLocationAddress(location.getLatitude(), location.getLongitude());
                            BreadCrumb bread = new BreadCrumb(location.getLatitude(), location.getLongitude(), audioPath, street);
                            breadCrumbs.add(bread);
                            isRecording=true;
                            btn_drop_breadcrumb.setText("Stop Recording");
//                            btn_drop_breadcrumb.setContentDescription("Stop Recording");
                        }
                        else
                        {
                            if(ttsInitialized)
                            {
                                tts.speak("Location Fix not acquired. Please try again in 2 seconds", TextToSpeech.QUEUE_FLUSH, null, null);
                            }
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
                else
                {
                    mediaRecorderHelper.stopRecording();
                    btn_drop_breadcrumb.setText("Drop Breadcrumb");
                    tts.speak("Breadcrumb dropped",TextToSpeech.QUEUE_FLUSH,null,null);
//                    btn_drop_breadcrumb.setContentDescription("Drop Breadcrumb");
                    isRecording=false;
                }
                break;
            case R.id.btn_end_save:
                //can put if condition for a minimum number of breadcrumbs
//                route = new Route(breadCrumbs,"123","");

                //clicking this once starts the recording for the Route label and then clicking it again stop route label recording
                // and saves the route to the file.
                if(!isSaving)
                {
                    String audioPath = fileLocationAudio+String.valueOf(System.currentTimeMillis());
                    mediaRecorderHelper = new MediaRecorderHelper(audioPath);
                    mediaRecorderHelper.startRecording();
                    isSaving=true;
                    saveRouteToFile(String.valueOf(System.currentTimeMillis()),audioPath);

                    btn_save_and_exit.setText("Finish Recording Name and Exit");
//                    btn_save_and_exit.setContentDescription("Finish Recording Name and Exit");
                }
                else
                {
                    mediaRecorderHelper.stopRecording();
                    finish();
                }

                break;
        }
    }
    //get the address from Location to store in Breadcrumb
    public String getLocationAddress(double latitude,double longitude)
    {
        Geocoder geocoder = new Geocoder(RecordingActivity.this, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(
                    latitude, longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                return address.getThoroughfare();
            }
        } catch (IOException e) {
            Log.e("geo coder not working", "Unable connect to Geocoder", e);
        }
        return "err";
    }
    //saving the route to the file.
    private void saveRouteToFile(String route_id,String route_name_audio_path)
    {
        try {
            File file = new File(fileLocationRoutes,route_id);
            FileOutputStream fos =
                    new FileOutputStream(file);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(new Route(breadCrumbs,route_id,route_name_audio_path));
            os.close();

        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    //currently volume up doesnt work, we want a better way to stop audio recordings.
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        Log.d("KeyCode",keyCode+"");
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {
                    Log.d("hello","volume up");
                    if(isRecording) {
                        mediaRecorderHelper.stopRecording();
                        btn_drop_breadcrumb.setText("Drop Breadcrumb");
                        btn_drop_breadcrumb.setContentDescription("Drop Breadcrumb");
                        isRecording = false;
                    }
                    else
                        return super.dispatchKeyEvent(event);

                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    //TODO
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }
    @Override
    protected void onResume() {


//        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0.0f, locationListener);
        if(tts==null) {
            tts = new TextToSpeech(RecordingActivity.this, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR) {
                        tts.setLanguage(Locale.ENGLISH);
                        ttsInitialized=true;
                    }
                }
            });
        }
        locationProviderClient.requestLocationUpdates(locationRequest,mLocationCallBack,null);
        super.onResume();

    }



    @Override
    public void onStop() {
        if (tts != null) {
            tts.stop();
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.shutdown();
        }
        super.onDestroy();
    }


    @Override
    protected void onPause() {
        super.onPause();
//        mLocationManager.removeUpdates(locationListener);
        locationProviderClient.removeLocationUpdates(mLocationCallBack);
    }
}
