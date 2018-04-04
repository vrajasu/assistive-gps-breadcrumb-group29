package com.pathfinder.Adapters;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pathfinder.AllBreadcrumbsActivity;
import com.pathfinder.Models.BreadCrumb;
import com.pathfinder.Models.Route;
import com.pathfinder.R;

import java.io.Serializable;
import java.util.List;

/**
 * Created by vrajdelhivala on 4/3/18.
 */

public class ListBreadCrumbAdapter extends RecyclerView.Adapter<ListBreadCrumbAdapter.ViewHolder> {

    Context context;
    private List<BreadCrumb> breadCrumbs;
    MediaPlayer mediaPlayer;
    public ListBreadCrumbAdapter(Context context , List<BreadCrumb> breadCrumbs) {
        this.context=context;
        this.breadCrumbs=breadCrumbs;
        Log.d("Size",""+breadCrumbs.size());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{


        public TextView tv_route_id;
        public LinearLayout ll_item_route;
        public ViewHolder(View v) {

            super(v);
            tv_route_id = (TextView)v.findViewById(R.id.tv_item_route_id);
            ll_item_route=(LinearLayout)v.findViewById(R.id.ll_item_route);


        }

    }
    @Override
    public ListBreadCrumbAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_route, parent, false);


        ListBreadCrumbAdapter.ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final BreadCrumb breadCrumb = breadCrumbs.get(position);


        holder.tv_route_id.setText("Bread Crumb "+(position+1)+" Street :"+breadCrumb.getStreetName());
        holder.ll_item_route.setContentDescription("Breadcrumb "+(position+1)+" and Street Name :"+breadCrumb.getStreetName());
        holder.ll_item_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playRouteName(breadCrumb.getAudioPath()+".3gp");

            }
        });
        holder.ll_item_route.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
//                Intent intent = new Intent(context, AllBreadcrumbsActivity.class);
//                intent.putExtra("ALL_BREADCRUMBS", (Serializable) route.getWholeRoute());
//                context.startActivity(intent);
                return false;
            }
        });
    }



    @Override
    public int getItemCount() {
        return breadCrumbs.size();
    }
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
