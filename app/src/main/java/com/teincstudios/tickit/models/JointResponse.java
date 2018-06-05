package com.teincstudios.tickit.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by kwesi on 1/14/18.
 */

public class JointResponse {
    @SerializedName("time")
    private String time;
    @SerializedName("spots")
    private ArrayList<Joints> spotList;

    public String getTime() {
        return time;
    }

    public ArrayList<Joints> getSpotList() {
        return spotList;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setSpotList(ArrayList<Joints> spotList) {
        this.spotList = spotList;
    }
}
