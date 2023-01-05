package com.moko.support.remotegw;

import com.moko.ble.lib.task.OrderTask;
import com.moko.support.remotegw.entity.ParamsKeyEnum;
import com.moko.support.remotegw.entity.ParamsLongKeyEnum;
import com.moko.support.remotegw.task.ParamsTask;
import com.moko.support.remotegw.task.SetPasswordTask;

import java.io.File;

import androidx.annotation.IntRange;

public class OrderTaskAssembler {
    ///////////////////////////////////////////////////////////////////////////
    // READ
    ///////////////////////////////////////////////////////////////////////////

    public static OrderTask getDeviceMac() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_DEVICE_MAC);
        return task;
    }

    public static OrderTask getDeviceName() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_DEVICE_NAME);
        return task;
    }

    public static OrderTask getProductModel() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_PRODUCT_MODEL);
        return task;
    }

    public static OrderTask getManufacturer() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_MANUFACTURER);
        return task;
    }

    public static OrderTask getHardwareVersion() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_HARDWARE_VERSION);
        return task;
    }

    public static OrderTask getSoftwareVersion() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_SOFTWARE_VERSION);
        return task;
    }

    public static OrderTask getDeviceType() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_DEVICE_TYPE);
        return task;
    }

    public static OrderTask getChannelDomain() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_CHANNEL_DOMAIN);
        return task;
    }

    public static OrderTask getMQTTConnectMode() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_MQTT_CONNECT_MODE);
        return task;
    }

    public static OrderTask getMQTTHost() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_MQTT_HOST);
        return task;
    }

    public static OrderTask getMQTTPort() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_MQTT_PORT);
        return task;
    }

    public static OrderTask getMQTTCleanSession() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_MQTT_CLEAN_SESSION);
        return task;
    }

    public static OrderTask getMQTTKeepAlive() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_MQTT_KEEP_ALIVE);
        return task;
    }

    public static OrderTask getMQTTQos() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_MQTT_QOS);
        return task;
    }

    public static OrderTask getMQTTClientId() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_MQTT_CLIENT_ID);
        return task;
    }

    public static OrderTask getMQTTDeviceId() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_MQTT_DEVICE_ID);
        return task;
    }

    public static OrderTask getMQTTSubscribeTopic() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_MQTT_SUBSCRIBE_TOPIC);
        return task;
    }

    public static OrderTask getMQTTPublishTopic() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsKeyEnum.KEY_MQTT_PUBLISH_TOPIC);
        return task;
    }

    public static OrderTask getMQTTUsername() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsLongKeyEnum.KEY_MQTT_USERNAME);
        return task;
    }

    public static OrderTask getMQTTPassword() {
        ParamsTask task = new ParamsTask();
        task.setData(ParamsLongKeyEnum.KEY_MQTT_PASSWORD);
        return task;
    }


    ///////////////////////////////////////////////////////////////////////////
    // WIRTE
    ///////////////////////////////////////////////////////////////////////////

    public static OrderTask setPassword(String password) {
        SetPasswordTask task = new SetPasswordTask();
        task.setData(password);
        return task;
    }

    public static OrderTask exitConfigMode() {
        ParamsTask task = new ParamsTask();
        task.exitConfigMode();
        return task;
    }

    public static OrderTask setWifiSSID(String SSID) {
        ParamsTask task = new ParamsTask();
        task.setWifiSSID(SSID);
        return task;
    }

    public static OrderTask setWifiPassword(String password) {
        ParamsTask task = new ParamsTask();
        task.setWifiPassword(password);
        return task;
    }

    public static OrderTask setMqttConnectMode(@IntRange(from = 0, to = 3) int mode) {
        ParamsTask task = new ParamsTask();
        task.setMqttConnectMode(mode);
        return task;
    }

    public static OrderTask setMqttHost(String host) {
        ParamsTask task = new ParamsTask();
        task.setMqttHost(host);
        return task;
    }

    public static OrderTask setMqttPort(@IntRange(from = 0, to = 65535) int port) {
        ParamsTask task = new ParamsTask();
        task.setMqttPort(port);
        return task;
    }

    public static OrderTask setMqttCleanSession(@IntRange(from = 0, to = 1) int enable) {
        ParamsTask task = new ParamsTask();
        task.setMqttCleanSession(enable);
        return task;
    }

    public static OrderTask setMqttKeepAlive(@IntRange(from = 10, to = 120) int keepAlive) {
        ParamsTask task = new ParamsTask();
        task.setMqttKeepAlive(keepAlive);
        return task;
    }

    public static OrderTask setMqttQos(@IntRange(from = 0, to = 2) int qos) {
        ParamsTask task = new ParamsTask();
        task.setMqttQos(qos);
        return task;
    }

    public static OrderTask setMqttClientId(String clientId) {
        ParamsTask task = new ParamsTask();
        task.setMqttClientId(clientId);
        return task;
    }

    public static OrderTask setMqttDeivceId(String deviceId) {
        ParamsTask task = new ParamsTask();
        task.setMqttDeviceId(deviceId);
        return task;
    }

    public static OrderTask setMqttSubscribeTopic(String topic) {
        ParamsTask task = new ParamsTask();
        task.setMqttSubscribeTopic(topic);
        return task;
    }

    public static OrderTask setMqttPublishTopic(String topic) {
        ParamsTask task = new ParamsTask();
        task.setMqttPublishTopic(topic);
        return task;
    }

    public static OrderTask setNTPUrl(String url) {
        ParamsTask task = new ParamsTask();
        task.setNTPUrl(url);
        return task;
    }

    public static OrderTask setNTPTimezone(@IntRange(from = -12, to = 12) int timeZone) {
        ParamsTask task = new ParamsTask();
        task.setNTPTimeZone(timeZone);
        return task;
    }

    public static OrderTask setNTPTimezonePro(@IntRange(from = -24, to = 28) int timeZone) {
        ParamsTask task = new ParamsTask();
        task.setNTPTimeZonePro(timeZone);
        return task;
    }

    public static OrderTask setChannelDomain(@IntRange(from = 0, to = 21) int channelDomain) {
        ParamsTask task = new ParamsTask();
        task.setChannelDomain(channelDomain);
        return task;
    }

    public static OrderTask setConnectionTimeout(@IntRange(from = 0, to = 1440) int timeout) {
        ParamsTask task = new ParamsTask();
        task.setConnectionTimeout(timeout);
        return task;
    }

    public static OrderTask setMqttUserName(String username) {
        ParamsTask task = new ParamsTask();
        task.setLongChar(ParamsLongKeyEnum.KEY_MQTT_USERNAME, username);
        return task;
    }

    public static OrderTask setMqttPassword(String password) {
        ParamsTask task = new ParamsTask();
        task.setLongChar(ParamsLongKeyEnum.KEY_MQTT_PASSWORD, password);
        return task;
    }

    public static OrderTask setCA(File file) throws Exception {
        ParamsTask task = new ParamsTask();
        task.setFile(ParamsLongKeyEnum.KEY_MQTT_CA, file);
        return task;
    }

    public static OrderTask setClientCert(File file) throws Exception {
        ParamsTask task = new ParamsTask();
        task.setFile(ParamsLongKeyEnum.KEY_MQTT_CLIENT_CERT, file);
        return task;
    }

    public static OrderTask setClientKey(File file) throws Exception {
        ParamsTask task = new ParamsTask();
        task.setFile(ParamsLongKeyEnum.KEY_MQTT_CLIENT_KEY, file);
        return task;
    }
}
