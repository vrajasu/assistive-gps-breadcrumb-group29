package com.pathfinder;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Environment;
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

public class RecordingActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_drop_breadcrumb, btn_save_and_exit;
    private FusedLocationProviderClient locationProviderClient;
    List<BreadCrumb> breadCrumbs = new ArrayList();

    //recording related variables
    String fileLocationAudio= Environment.getExternalStorageDirectory()+ File.separator +"PathFinder"+ File.separator+"Audio"+File.separator;
    String fileLocationRoutes= Environment.getExternalStorageDirectory()+ File.separator +"PathFinder"+ File.separator+"Routes"+File.separator;

    boolean isRecording = false;
    boolean isSaving=false;
    MediaRecorderHelper mediaRecorderHelper;

    Route route;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_recording);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        btn_drop_breadcrumb = (Button) findViewById(R.id.btn_drop_breadcrumb);
        btn_save_and_exit = (Button) findViewById(R.id.btn_end_save);

        btn_drop_breadcrumb.setOnClickListener(this);
        btn_save_and_exit.setOnClickListener(this);

        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_drop_breadcrumb:
//                Toast.makeText(RecordingActivity.this, "Confirmation", Toast.LENGTH_LONG).show();
                if(!isRecording) {
                    try {
                        locationProviderClient.getLastLocation().addOnSuccessListener(RecordingActivity.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    Log.d("TAG", location.getLatitude() + " : " + location.getLongitude());

                                }
                                String audioPath = fileLocationAudio+String.valueOf(System.currentTimeMillis());
                                mediaRecorderHelper = new MediaRecorderHelper(audioPath);
                                mediaRecorderHelper.startRecording();

                                String street = getLocationAddress(location.getLatitude(),location.getLongitude());
                                BreadCrumb bread = new BreadCrumb(location.getLatitude(), location.getLongitude(),audioPath,street);
                                breadCrumbs.add(bread);


                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    btn_drop_breadcrumb.setText("Stop Recording");
                    btn_drop_breadcrumb.setContentDescription("Stop Recording");

                }
                else
                {
                    mediaRecorderHelper.stopRecording();
                    btn_drop_breadcrumb.setText("Drop Breadcrumb");
                    btn_drop_breadcrumb.setContentDescription("Drop Breadcrumb");
                }
                isRecording=!isRecording;
                break;
            case R.id.btn_end_save:
                //can put if condition for a minimum number of breadcrumbs
//                route = new Route(breadCrumbs,"123","");
                if(!isSaving)
                {
                    String audioPath = fileLocationAudio+String.valueOf(System.currentTimeMillis());
                    mediaRecorderHelper = new MediaRecorderHelper(audioPath);
                    mediaRecorderHelper.startRecording();
                    isSaving=true;
                    saveRouteToFile(String.valueOf(System.currentTimeMillis()),audioPath);

                    btn_save_and_exit.setText("Finish Recording Name and Exit");
                    btn_save_and_exit.setContentDescription("Finish Recording Name and Exit");
                }
                else
                {
                    mediaRecorderHelper.stopRecording();
                    finish();
                }

                break;
        }
    }
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
    //currently volume up doesnt work
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


}
