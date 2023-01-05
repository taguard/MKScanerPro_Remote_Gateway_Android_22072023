package com.moko.support.remotegw.entity;

import com.google.gson.annotations.SerializedName;

public class NTPServer {
    @SerializedName("switch")
    public int onOff;
    public String host;
}
