package com.moko.mkremotegw.activity;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.moko.mkremotegw.AppConstants;
import com.moko.mkremotegw.base.BaseActivity;
import com.moko.mkremotegw.databinding.ActivityDeviceInfoProBinding;
import com.moko.mkremotegw.entity.MQTTConfig;
import com.moko.mkremotegw.entity.MokoDevice;
import com.moko.mkremotegw.utils.SPUtiles;
import com.moko.support.remotegw.MQTTConstants;
import com.moko.support.remotegw.MQTTSupport;
import com.moko.support.remotegw.entity.MasterSystemInfo;
import com.moko.support.remotegw.entity.MsgDeviceInfo;
import com.moko.support.remotegw.entity.MsgReadResult;
import com.moko.support.remotegw.entity.SlaveDeviceInfo;
import com.moko.support.remotegw.event.DeviceOnlineEvent;
import com.moko.support.remotegw.event.MQTTMessageArrivedEvent;
import com.moko.support.remotegw.handler.MQTTMessageAssembler;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Type;

public class DeviceInfoProActivity extends BaseActivity<ActivityDeviceInfoProBinding> {

    private MokoDevice mMokoDevice;
    private MQTTConfig appMqttConfig;

    public Handler mHandler;

    @Override
    protected void onCreate() {
        String mqttConfigAppStr = SPUtiles.getStringValue(this, AppConstants.SP_KEY_MQTT_CONFIG_APP, "");
        appMqttConfig = new Gson().fromJson(mqttConfigAppStr, MQTTConfig.class);
        mMokoDevice = (MokoDevice) getIntent().getSerializableExtra(AppConstants.EXTRA_KEY_DEVICE);

        mHandler = new Handler(Looper.getMainLooper());
        showLoadingProgressDialog();
        mHandler.postDelayed(() -> {
            dismissLoadingProgressDialog();
            finish();
        }, 30 * 1000);
        getMasterDeviceInfo();
    }

    @Override
    protected ActivityDeviceInfoProBinding getViewBinding() {
        return ActivityDeviceInfoProBinding.inflate(getLayoutInflater());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMQTTMessageArrivedEvent(MQTTMessageArrivedEvent event) {
        // 更新所有设备的网络状态
        final String topic = event.getTopic();
        final String message = event.getMessage();
        if (TextUtils.isEmpty(message))
            return;
        int msg_id;
        try {
            JsonObject object = new Gson().fromJson(message, JsonObject.class);
            JsonElement element = object.get("msg_id");
            msg_id = element.getAsInt();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        if (msg_id == MQTTConstants.READ_MSG_ID_MASTER_DEVICE_INFO) {
            Type type = new TypeToken<MsgReadResult<MasterSystemInfo>>() {
            }.getType();
            MsgReadResult<MasterSystemInfo> result = new Gson().fromJson(message, type);
            if (!mMokoDevice.deviceId.equals(result.device_info.device_id)) {
                return;
            }
            mBind.tvProductModel.setText(result.data.product_model);
            mBind.tvManufacturer.setText(result.data.company_name);
            mBind.tvDeviceHardwareVersion.setText(result.data.hardware_version);
            mBind.tvDeviceMasterSoftwareVersion.setText(result.data.software_version);
            mBind.tvDeviceMasterFirmwareVersion.setText(result.data.firmware_version);
            mBind.tvDeviceMasterMac.setText(result.data.mac.toUpperCase());
            getSlaveDeviceInfo();
        }
        if (msg_id == MQTTConstants.READ_MSG_ID_SLAVE_DEVICE_INFO) {
            Type type = new TypeToken<MsgReadResult<SlaveDeviceInfo>>() {
            }.getType();
            MsgReadResult<SlaveDeviceInfo> result = new Gson().fromJson(message, type);
            if (!mMokoDevice.deviceId.equals(result.device_info.device_id)) {
                return;
            }
            dismissLoadingProgressDialog();
            mHandler.removeMessages(0);
            mBind.tvDeviceSlaveSoftwareVersion.setText(result.data.software_version);
            mBind.tvDeviceSlaveFirmwareVersion.setText(result.data.firmware_version);
            mBind.tvDeviceSlaveMac.setText(result.data.slave_mac.toUpperCase());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceOnlineEvent(DeviceOnlineEvent event) {
        String deviceId = event.getDeviceId();
        if (!mMokoDevice.deviceId.equals(deviceId)) {
            return;
        }
        boolean online = event.isOnline();
        if (!online) {
            finish();
        }
    }

    public void back(View view) {
        finish();
    }


    private void getMasterDeviceInfo() {
        String appTopic;
        if (TextUtils.isEmpty(appMqttConfig.topicPublish)) {
            appTopic = mMokoDevice.topicSubscribe;
        } else {
            appTopic = appMqttConfig.topicPublish;
        }
        MsgDeviceInfo deviceInfo = new MsgDeviceInfo();
        deviceInfo.device_id = mMokoDevice.deviceId;
        deviceInfo.mac = mMokoDevice.mac;
        String message = MQTTMessageAssembler.assembleReadMasterDeviceInfo(deviceInfo);
        try {
            MQTTSupport.getInstance().publish(appTopic, message, MQTTConstants.READ_MSG_ID_MASTER_DEVICE_INFO, appMqttConfig.qos);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void getSlaveDeviceInfo() {
        String appTopic;
        if (TextUtils.isEmpty(appMqttConfig.topicPublish)) {
            appTopic = mMokoDevice.topicSubscribe;
        } else {
            appTopic = appMqttConfig.topicPublish;
        }
        MsgDeviceInfo deviceInfo = new MsgDeviceInfo();
        deviceInfo.device_id = mMokoDevice.deviceId;
        deviceInfo.mac = mMokoDevice.mac;
        String message = MQTTMessageAssembler.assembleReadSlaveDeviceInfo(deviceInfo);
        try {
            MQTTSupport.getInstance().publish(appTopic, message, MQTTConstants.READ_MSG_ID_SLAVE_DEVICE_INFO, appMqttConfig.qos);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
