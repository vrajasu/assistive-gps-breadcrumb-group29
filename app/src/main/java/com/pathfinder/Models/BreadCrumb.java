package com.pathfinder.Models;

import java.io.Serializable;

/**
 * Created by vrajdelhivala on 3/26/18.
 */

public class BreadCrumb implements Serializable{

    double latitude=0;
    double longitude=0;
    String audio_recording_path="";
    String streetName = "";

    public BreadCrumb(double lat, double longi, String path,String streetName)
    {
        this.latitude=lat;
        this.longitude=longi;
        this.audio_recording_path = path;
        this.streetName = streetName;
    }

    //getters
    public double getLatitude()
    {
        return this.latitude;
    }
    public double getLongitude()
    {
        return this.longitude;
    }
    public String getAudioPath()
    {
        return this.audio_recording_path;
    }
    public String getStreetName()
    {
        return this.streetName;
    }

    //setters

    public void setLatitude(double l)
    {
        this.latitude = l;
    }
    public void setLongitude(double l)
    {
        this.longitude = l;
    }
    public void setAudioPath(String path)
    {
        this.audio_recording_path = path;
    }
    public void setStreetName(String streetName)
    {
        this.streetName = streetName;
    }



}
