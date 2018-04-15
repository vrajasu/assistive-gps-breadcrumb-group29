package com.pathfinder;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.pathfinder.Models.BreadCrumb;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

//entry point of the application.
public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    //declaring UI elements.
    Button btn_new_route, btn_edit_route, btn_saved_route, btn_current_street;
    //declaring Location provider to extact Latitude and Longitude.

    private FusedLocationProviderClient locationProviderClient;
    //Declaring Text to speech engine to Welcome user and read out street name.
    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //making sure the screen doesn't power off
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        btn_new_route = (Button) findViewById(R.id.btn_new_route);
        btn_edit_route = (Button) findViewById(R.id.btn_edit_route);
        btn_saved_route = (Button) findViewById(R.id.btn_saved_route);
        btn_current_street = (Button) findViewById(R.id.btn_current_street);

        //initializing the Text to speech engine.
        tts = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                Log.d("TTS", "Hello!1");
                if (status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.ENGLISH);
                    tts.speak("Hello There, Welcome to Path Finder Home!", TextToSpeech.QUEUE_FLUSH, null, null);
                }
            }
        });


        btn_new_route.setOnClickListener(this);
        btn_edit_route.setOnClickListener(this);
        btn_saved_route.setOnClickListener(this);
        btn_current_street.setOnClickListener(this);


        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        //creating app storage directory on first app run

        File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/PathFinder");
        if (!folder.exists()) {
            folder.mkdir();
        }
        File folder2 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/PathFinder/Routes");
        if (!folder2.exists()) {
            folder2.mkdir();
        }
        File folder3 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/PathFinder/Audio");
        if (!folder3.exists()) {
            folder3.mkdir();
        }


    }

    //on click handlers for the button
    @Override
    public void onClick(View view) {

        Intent intent;
        switch (view.getId()) {
            case R.id.btn_new_route:
                intent = new Intent(MainActivity.this, ConfirmStartRecording.class);
                startActivity(intent);
                break;
            case R.id.btn_saved_route:
                intent = new Intent(MainActivity.this, AllRoutesActivity.class);
                startActivity(intent);
            case R.id.btn_current_street:
                getLocation();
                break;
            case R.id.btn_edit_route:
                intent = new Intent(MainActivity.this, AllRoutesActivity.class);
                intent.putExtra("IS_EDIT", true);
                startActivity(intent);
        }
    }

    //Fetches location and passes to getAddress method to retrieve a street name
    public void getLocation() {
        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationProviderClient.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    String address = getLocationAddress(location.getLatitude(), location.getLongitude());
                    tts.speak(address, TextToSpeech.QUEUE_FLUSH, null, null);
                    btn_current_street.setText(address);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //retrieves street name from Location. The street name is then spoken using Text-to-speech engine
    public String getLocationAddress(double latitude,double longitude)
    {
        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
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

    //handling speech engine's lifecycle through activity's lifecycle
    @Override
    protected void onPause() {
        if(tts !=null){
            tts.stop();
        }
        super.onPause();
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
    public void onResume()
    {
        if(tts==null) {
            tts = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    Log.d("TTS", "Hello2!");
                    if (status != TextToSpeech.ERROR) {
                        tts.setLanguage(Locale.ENGLISH);
                        tts.speak("Hello There, Welcome to Path Finder Home!", TextToSpeech.QUEUE_FLUSH, null, null);
                    }
                }
            });
        }
        else
            tts.speak("Hello There, Welcome to Path Finder Home!", TextToSpeech.QUEUE_FLUSH, null, null);

        super.onResume();
    }
}
