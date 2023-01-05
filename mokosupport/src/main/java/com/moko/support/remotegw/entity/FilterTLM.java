package com.moko.support.remotegw.entity;

import com.google.gson.annotations.SerializedName;

public class FilterTLM {
    @SerializedName("switch")
    public int onOff;
    public int version;
}
