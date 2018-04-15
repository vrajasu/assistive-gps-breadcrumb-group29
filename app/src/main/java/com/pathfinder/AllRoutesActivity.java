package com.pathfinder;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.pathfinder.Adapters.ListRouteAdapter;
import com.pathfinder.Models.Route;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//contains the Recycler view that lists all the routes saved on the mobile device

public class AllRoutesActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    List<Route> allRoutes = new ArrayList<>();

    TextView tv_no_routes;
    boolean isEditable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_routes);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_all_routes);
        tv_no_routes = (TextView)findViewById(R.id.tv_no_routes);

        if(getIntent().hasExtra("IS_EDIT"))
        {
            isEditable=true;
        }


        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        retrieveAllRoutes();
        if(allRoutes.size()>0)
        {
            tv_no_routes.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);

            mAdapter = new ListRouteAdapter(allRoutes,AllRoutesActivity.this,isEditable);
            mRecyclerView.setAdapter(mAdapter);

        }
        else
        {
            tv_no_routes.setFocusable(true);
        }


    }
    //retrieve all routes saved on device and populate a List of Route Objects
    public void retrieveAllRoutes()
    {
        String fileLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PathFinder/Routes";
        File[] allFiles = new File(fileLocation).listFiles();

        if(allFiles ==null)
        {
            return;
        }
        for(File f: allFiles)
        {
            try {
                FileInputStream fis = new FileInputStream(f.getAbsolutePath());
                ObjectInputStream ois = new ObjectInputStream(fis);

                Route route;

                route = (Route) ois.readObject();
                allRoutes.add(route);

                ois.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onResume() {

        allRoutes.clear();
        retrieveAllRoutes();
        Log.d("All Routes Size",allRoutes.size()+"");
        if (allRoutes.size() > 0) {
            tv_no_routes.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mAdapter = new ListRouteAdapter(allRoutes, AllRoutesActivity.this, isEditable);
            mRecyclerView.setAdapter(mAdapter);

        } else {
            mRecyclerView.setVisibility(View.GONE);
            tv_no_routes.setVisibility(View.VISIBLE);
            tv_no_routes.setFocusable(true);
        }
        super.onResume();

    }
    @Override
    protected void onPause(){
        MediaRecorderHelper.getInstance().pause();
        super.onPause();
    }
}
