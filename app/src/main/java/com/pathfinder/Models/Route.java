package com.pathfinder.Models;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vrajdelhivala on 4/2/18.
 */

public class Route implements Serializable {

    List<BreadCrumb> wholeRoute = new ArrayList<>();
    String route_id = "";
    String route_name_audio_path="";

    String fileLocation= Environment.getExternalStorageDirectory()+ File.separator +"PathFinder/Routes";

    public Route(List<BreadCrumb> route,String id,String route_name_audio_path)
    {
        this.wholeRoute=route;
        this.route_id=id;
        this.route_name_audio_path = route_name_audio_path;

    }

    public List<BreadCrumb> getWholeRoute(){return this.wholeRoute;}
    public String getRoute_id(){return this.route_id;}
    public String getRoute_name_audio_path(){return this.route_name_audio_path;}

    public void getWholeRoute(List<BreadCrumb> list){this.wholeRoute=list;}
    public void getRoute_id(String id){this.route_id=id;}
    public void getRoute_name_audio_path(String path){this.route_name_audio_path=path;}




}
