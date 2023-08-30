package com.moko.mkremotegw.entity;


import java.io.Serializable;

public class MQTTConfig implements Serializable {
    public String host = "194.233.85.217";
    public String port = "1883";
    public boolean cleanSession = false;
    public int connectMode;
    public int qos = 0;
    public int keepAlive = 60;
    public String clientId = "";
    public String username = "mk107";
    public String password = "A6TU*B:L";
    public String caPath = "";
    public String clientKeyPath = "";
    public String clientCertPath = "";
    public String topicSubscribe = "";
    public String topicPublish = "";
    public boolean lwtEnable = true;
    public boolean lwtRetain;
    public int lwtQos = 1;
    public String lwtTopic = "";
    public String lwtPayload = "";
    public String deviceName;
    public String staMac;
}
