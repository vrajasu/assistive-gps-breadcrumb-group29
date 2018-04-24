package com.pathfinder.Adapters;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pathfinder.AllBreadcrumbsActivity;
import com.pathfinder.MediaRecorderHelper;
import com.pathfinder.Models.BreadCrumb;
import com.pathfinder.Models.Route;
import com.pathfinder.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

/**
 * Created by vrajdelhivala on 4/3/18.
 */

//Adapter that helps to list all breadcrumbs in a route.
public class ListBreadCrumbAdapter extends RecyclerView.Adapter<ListBreadCrumbAdapter.ViewHolder> {

    Context context;
    private List<BreadCrumb> breadCrumbs;
    MediaPlayer mediaPlayer;
    boolean isEditable=false;
    String fileLocationAudio= Environment.getExternalStorageDirectory()+ File.separator +"PathFinder"+ File.separator+"Audio"+File.separator;
    String fileLocationRoutes= Environment.getExternalStorageDirectory()+ File.separator +"PathFinder"+ File.separator+"Routes"+File.separator;
    String route_path;
    String route_name_audio;


    public ListBreadCrumbAdapter(Context context , List<BreadCrumb> breadCrumbs,boolean isEditable,String route_path,String route_name_audio) {
        this.context=context;
        this.breadCrumbs=breadCrumbs;
        this.isEditable=isEditable;
        this.route_path=route_path;
        this.route_name_audio=route_name_audio;
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
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final BreadCrumb breadCrumb = breadCrumbs.get(position);


        holder.tv_route_id.setText("Bread Crumb "+(position+1)+" Street :"+breadCrumb.getStreetName()+" "+breadCrumb.getLatitude()+" "+breadCrumb.getLongitude());
        holder.ll_item_route.setContentDescription("Breadcrumb "+(position+1)+" and Street Name :"+breadCrumb.getStreetName());
        //play the audio saved for this breadcrumb
        holder.ll_item_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playBreadcrumb(breadCrumb.getAudioPath()+".3gp");

            }
        });

        if(isEditable) {
            holder.ll_item_route.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    //for later implementation
//                Intent intent = new Intent(context, AllBreadcrumbsActivity.class);
//                intent.putExtra("ALL_BREADCRUMBS", (Serializable) route.getWholeRoute());
//                context.startActivity(intent);
                    ItemActions itemActions = new ItemActions() {
                        @Override
                        public void remove() {
                            breadCrumbs.remove(position);
                            notifyItemRemoved(position);
                            notifyDataSetChanged();

                        }

                        @Override
                        public void update(BreadCrumb bc) {
                            breadCrumbs.remove(position);
                            breadCrumbs.add(position, bc);

                            notifyDataSetChanged();
                            saveRouteToFile(route_path, route_name_audio);

                        }
                    };
                    showDialog(position, itemActions);
                    return false;
                }
            });
        }
    }
    public interface ItemActions
    {
         void remove();
         void update(BreadCrumb bc);
    }



    @Override
    public int getItemCount() {
        return breadCrumbs.size();
    }
    //function that helps play the audip
    private void playBreadcrumb(String route_name_path) {
        mediaPlayer = MediaRecorderHelper.getInstance();
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(route_name_path);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Media Play", "prepare() failed");
        }
    }
    public void showDialog(final int position, final ItemActions actions)
    {
        final BreadCrumb bc = breadCrumbs.get(position);
        final String audioPath = bc.getAudioPath();

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.breadcrumb_dialog);
        dialog.setTitle("Actions for BreadCrumbs");

        Button btn_delete_breadcrumb = (Button) dialog.findViewById(R.id.btn_delete_breadcrumb);
        final Button btn_replace_breadcrumb = (Button)dialog.findViewById(R.id.btn_replace_breadcrumb);
        Button btn_back = (Button)dialog.findViewById(R.id.btn_back);

        btn_delete_breadcrumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //delete Recroding
                Log.d("BreadCrumbs",""+breadCrumbs.size());
                if(breadCrumbs.size()==1)
                {
                    Toast.makeText(context,"Only one breadcrumb left, Delete the route by using delete route button",Toast.LENGTH_LONG).show();
                }
                else {
                    File file = new File(audioPath + ".3gp");
                    file.delete();
                    actions.remove();
                    saveRouteToFile(route_path, route_name_audio);
                    dialog.dismiss();
                    //remove from RouteFile as Well
                }

            }
        });
        final MediaRecorderHelper[] mediaRecorderHelper = new MediaRecorderHelper[1];
        final boolean[] isRecording = {false};
        btn_replace_breadcrumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //record a new one

                if(!isRecording[0]) {
                    String audioPath = fileLocationAudio + String.valueOf(System.currentTimeMillis());
                    mediaRecorderHelper[0] = new MediaRecorderHelper(audioPath);
                    mediaRecorderHelper[0].startRecording();
                    isRecording[0] =true;
                    //delete the previous one
                    File file = new File(bc.getAudioPath() + ".3gp");
                    file.delete();

                    //update the route file
                    BreadCrumb new_temp = new BreadCrumb(bc.getLatitude(), bc.getLongitude(), audioPath, bc.getStreetName());
                    actions.update(new_temp);
                    btn_replace_breadcrumb.setText("Finish and Save");
                    btn_replace_breadcrumb.setContentDescription("Finish and Save");

                }
                else{
                    mediaRecorderHelper[0].stopRecording();
                    isRecording[0]=false;
                    btn_replace_breadcrumb.setText("Replace audio recording");
                    btn_replace_breadcrumb.setContentDescription("Replace audio recording");
                    dialog.dismiss();
                }
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
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
}
