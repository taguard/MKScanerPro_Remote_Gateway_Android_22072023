package com.moko.support.remotegw.entity;


import java.io.Serializable;

public class DeviceResponse implements Serializable{
    public int code;
    public String message;
    public DeviceResult result;
}
