package com.moko.support.remotegw.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class BleService implements Serializable {
    //0:Primary Service
    //1:Secondary Service
    public int type;
    public String service_uuid;
    @SerializedName("char")
    public List<BleCharacteristic> characteristic;
}
