package com.pathfinder.Adapters;

import android.app.Application;
import android.app.UiAutomation;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pathfinder.AllBreadcrumbsActivity;
import com.pathfinder.MainActivity;
import com.pathfinder.Models.Route;
import com.pathfinder.R;

import java.io.Serializable;
import java.util.List;

/**
 * Created by vrajdelhivala on 4/2/18.
 */
//Adapter that helps to list all routes present in file system.

public class ListRouteAdapter extends RecyclerView.Adapter<ListRouteAdapter.ViewHolder>  {
    private List<Route> allRoutes;
    MediaPlayer mediaPlayer;
    Context context;


    public static class ViewHolder extends RecyclerView.ViewHolder{


        public TextView tv_route_id;
        public LinearLayout ll_item_route;
        public ViewHolder(View v) {

            super(v);
            tv_route_id = (TextView)v.findViewById(R.id.tv_item_route_id);
            ll_item_route=(LinearLayout)v.findViewById(R.id.ll_item_route);


        }

    }

    public ListRouteAdapter(List<Route> allRoutes, Context context) {
        this.allRoutes = allRoutes;
        this.context =context;
    }

    @Override
    public ListRouteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_route, parent, false);


        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Route route = allRoutes.get(position);

        holder.tv_route_id.setText("Route "+(position+1));
        holder.ll_item_route.setContentDescription("Route "+(position+1));
        //click for route name
        holder.ll_item_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playRouteName(route.getRoute_name_audio_path()+".3gp");
            }
        });
        //long press to open all breadcrumbs in the application
        holder.ll_item_route.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(context, AllBreadcrumbsActivity.class);
                intent.putExtra("ALL_BREADCRUMBS", (Serializable) route.getWholeRoute());
                context.startActivity(intent);
                return false;
            }
        });



    }

    @Override
    public int getItemCount() {
        return allRoutes.size();
    }

    //play audio file that contains the route label
    private void playRouteName(String route_name_path) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(route_name_path);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            Log.e("Media Play", "prepare() failed");
        }
    }
}
