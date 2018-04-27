package com.pathfinder;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.pathfinder.Adapters.ListBreadCrumbAdapter;
import com.pathfinder.Adapters.ListRouteAdapter;
import com.pathfinder.Models.BreadCrumb;
import com.pathfinder.Models.Route;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

//contains the Recycler view that lists all the breadcrumbs for a particular route

public class AllBreadcrumbsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    TextView tv_no_info;
    List<BreadCrumb> allBreadCrumbs = new ArrayList<>();
    Button btn_delete_route;

    float thresholdDistanceBefore = 10.00f;
    float thresholdDistanceAt = 10.00f;
    long updateTime = 2000;
    long updateDistance = 0;
    LocationManager mLocationManager = null;
    boolean[] breadCrumbFinishedBeforePoint;
    boolean[] breadCrumbFinishedAtPoint;


    MediaPlayer mediaPlayer;
    boolean isEditable=false;
    int delete_count=0;
    TextToSpeech tts;
    String route_path="",route_name_audio="";
    String fileLocationAudio= Environment.getExternalStorageDirectory()+ File.separator +"PathFinder"+ File.separator+"Audio"+File.separator;
    String fileLocationRoutes= Environment.getExternalStorageDirectory()+ File.separator +"PathFinder"+ File.separator+"Routes"+File.separator;

    FusedLocationProviderClient locationProviderClient;
    LocationRequest locationRequest = new LocationRequest();
    LocationCallback mLocationCallBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_routes);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_all_routes);
        tv_no_info = (TextView) findViewById(R.id.tv_no_routes);
        btn_delete_route=(Button)findViewById(R.id.btn_delete_route);

        isEditable=getIntent().getBooleanExtra("IS_EDIT",false);
        allBreadCrumbs = (List<BreadCrumb>) getIntent().getSerializableExtra("ALL_BREADCRUMBS");
        route_path=getIntent().getStringExtra("ROUTE_PATH");
        route_name_audio=getIntent().getStringExtra("ROUTE_AUDIO_PATH");

        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        if(isEditable)
        {
            btn_delete_route.setVisibility(View.VISIBLE);
        }
        btn_delete_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete_count++;
                if(delete_count==1)
                {
                    tts = new TextToSpeech(AllBreadcrumbsActivity.this, new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if (status != TextToSpeech.ERROR) {
                                tts.setLanguage(Locale.ENGLISH);
                                tts.speak("Are you sure you want to delete this route?", TextToSpeech.QUEUE_FLUSH, null, null);
                            }
                        }
                    });
                }
                else
                {
                    Log.d("File:Route Audio",""+route_name_audio+".3gp");
                    File file = new File(route_name_audio+".3gp");
                    file.delete();


                    File file1 = new File(fileLocationRoutes+""+route_path);
                    file1.delete();

                    for(int i=0;i<allBreadCrumbs.size();i++)
                    {
                        Log.d("File:Breadcrumb",""+allBreadCrumbs.get(i).getAudioPath()+".3gp");
                        File file_t = new File(allBreadCrumbs.get(i).getAudioPath()+".3gp");
                        file_t.delete();
                    }
                    finish();
                }
            }
        });
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // initializing the adapter and then passing the Breadcrumb list
//
//        for (int i = 0; i < allBreadCrumbs.size(); i++) {
//            Log.d("Bread Crumb", "" + allBreadCrumbs.get(i).getLatitude() + "," + allBreadCrumbs.get(i).getLongitude());
//            float[] results = new float[1];
//            Location.distanceBetween(allBreadCrumbs.get(i).getLatitude(), allBreadCrumbs.get(i).getLongitude(), allBreadCrumbs.get(0).getLatitude(), allBreadCrumbs.get(0).getLongitude(), results);
//            Log.d("Distances", "" + results[0]);
//        }
        breadCrumbFinishedBeforePoint = new boolean[allBreadCrumbs.size()];
        breadCrumbFinishedAtPoint = new boolean[allBreadCrumbs.size()];

        Arrays.fill(breadCrumbFinishedAtPoint,false);
        Arrays.fill(breadCrumbFinishedBeforePoint,false);


        mAdapter = new ListBreadCrumbAdapter(AllBreadcrumbsActivity.this, allBreadCrumbs,isEditable,route_path,route_name_audio);

        mRecyclerView.setVisibility(View.VISIBLE);
        tv_no_info.setVisibility(View.GONE);

        mRecyclerView.setAdapter(mAdapter);

        if(!isEditable) {

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
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
                        checkForBreadCrumb(loc);
                        Log.d("New Location Update",""+loc.getLongitude()+":"+loc.getLatitude());
                        break;
                    }
                }
            };

        }
    }

    public void checkForBreadCrumb(Location currentLocation)
    {

        for(int i=0;i<allBreadCrumbs.size();i++)
        {
            Log.d("Accessing Path","true");
            if(!breadCrumbFinishedBeforePoint[i])
            {
                BreadCrumb bc = allBreadCrumbs.get(i);
                float result[]=new float[1];
                Location.distanceBetween(currentLocation.getLatitude(),currentLocation.getLongitude(),bc.getLatitude(),bc.getLongitude(),result);
                if(result.length>0)
                {
                    Log.d("Result for Breadcrummb",""+result[0]);
                    if(result[0]<=thresholdDistanceBefore)
                    {
                        if(i==allBreadCrumbs.size()-1)
                            playBreadcrumb(bc.getAudioPath()+".3gp",bc.getStreetName(),true);
                        else
                            playBreadcrumb(bc.getAudioPath()+".3gp",bc.getStreetName(),false);
                        breadCrumbFinishedBeforePoint[i]=true;
                        break;
                    }
                }
            }
            /*
            else
            {
                if(!breadCrumbFinishedAtPoint[i])
                {
                    BreadCrumb bc = allBreadCrumbs.get(i);
                    float result[]=new float[1];
                    Location.distanceBetween(currentLocation.getLatitude(),currentLocation.getLongitude(),bc.getLatitude(),bc.getLongitude(),result);
                    if(result.length>0)
                    {
                        Log.d("Result Breadcrummb 2",""+result[0]);
                        if(result[0]<=thresholdDistanceAt)
                        {
                            if(i==allBreadCrumbs.size()-1)
                                playBreadcrumb(bc.getAudioPath()+".3gp",bc.getStreetName(),true);
                            else
                                playBreadcrumb(bc.getAudioPath()+".3gp",bc.getStreetName(),false);
                            breadCrumbFinishedAtPoint[i]=true;
                            break;
                        }
                    }
                }
            }
            */

        }
    }
    private void playBreadcrumb(String route_name_path,String street_name,boolean isLast) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(route_name_path);
            mediaPlayer.prepare();
            mediaPlayer.start();
            while(mediaPlayer.isPlaying())
            {

            }
            tts.speak("The street for this breadcrumb is "+street_name, TextToSpeech.QUEUE_FLUSH, null, null);
            if(isLast)
                tts.speak("This was the last breadcrumb, you should reach your destination soon",TextToSpeech.QUEUE_FLUSH,null,null);

        } catch (Exception e) {
            Log.e("Media Play", "prepare() failed");
        }
    }

    @Override
    protected void onPause() {
        if(locationProviderClient!=null && mLocationCallBack!=null)
        locationProviderClient.removeLocationUpdates(mLocationCallBack);
        if(tts !=null){
            tts.stop();
        }
        MediaRecorderHelper.getInstance().pause();
        super.onPause();

    }

    @Override
    protected void onResume() {
        if(!isEditable)
             locationProviderClient.requestLocationUpdates(locationRequest,mLocationCallBack,null);
        if(tts==null) {
            tts = new TextToSpeech(AllBreadcrumbsActivity.this, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR) {
                        tts.setLanguage(Locale.ENGLISH);
                    }
                }
            });
        }

        MediaRecorderHelper.getInstance().pause();
        super.onResume();

    }



    @Override
    public void onStop() {
        if (tts != null) {
            tts.stop();
        }
        MediaRecorderHelper.getInstance().pause();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.shutdown();
        }
        MediaRecorderHelper.getInstance().pause();
        super.onDestroy();
    }
}
