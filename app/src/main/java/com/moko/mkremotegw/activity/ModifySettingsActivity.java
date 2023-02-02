package com.moko.mkremotegw.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;

import com.elvishew.xlog.XLog;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.moko.mkremotegw.AppConstants;
import com.moko.mkremotegw.R;
import com.moko.mkremotegw.base.BaseActivity;
import com.moko.mkremotegw.databinding.ActivityModifySettingsBinding;
import com.moko.mkremotegw.db.DBTools;
import com.moko.mkremotegw.entity.MQTTConfig;
import com.moko.mkremotegw.entity.MokoDevice;
import com.moko.mkremotegw.utils.SPUtiles;
import com.moko.mkremotegw.utils.ToastUtils;
import com.moko.support.remotegw.MQTTConstants;
import com.moko.support.remotegw.MQTTSupport;
import com.moko.support.remotegw.entity.MsgConfigResult;
import com.moko.support.remotegw.event.DeviceOnlineEvent;
import com.moko.support.remotegw.event.MQTTMessageArrivedEvent;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Type;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

public class ModifySettingsActivity extends BaseActivity<ActivityModifySettingsBinding> {
    public static String TAG = ModifySettingsActivity.class.getSimpleName();
    private MokoDevice mMokoDevice;
    private MQTTConfig appMqttConfig;
    private String mAppTopic;

    public Handler mHandler;

    @Override
    protected void onCreate() {
        mMokoDevice = (MokoDevice) getIntent().getSerializableExtra(AppConstants.EXTRA_KEY_DEVICE);
        String mqttConfigAppStr = SPUtiles.getStringValue(this, AppConstants.SP_KEY_MQTT_CONFIG_APP, "");
        appMqttConfig = new Gson().fromJson(mqttConfigAppStr, MQTTConfig.class);
        mAppTopic = TextUtils.isEmpty(appMqttConfig.topicPublish) ? mMokoDevice.topicSubscribe : appMqttConfig.topicPublish;
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    protected ActivityModifySettingsBinding getViewBinding() {
        return ActivityModifySettingsBinding.inflate(getLayoutInflater());
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
        if (msg_id == MQTTConstants.CONFIG_MSG_ID_REBOOT) {
            Type type = new TypeToken<MsgConfigResult>() {
            }.getType();
            MsgConfigResult result = new Gson().fromJson(message, type);
            if (!mMokoDevice.mac.equalsIgnoreCase(result.device_info.mac))
                return;
            dismissLoadingProgressDialog();
            mHandler.removeMessages(0);
            if (result.result_code == 0) {
                ToastUtils.showToast(this, "Set up succeed");
                // 跳转首页，刷新数据
                Intent intent = new Intent(this, RemoteMainActivity.class);
                intent.putExtra(AppConstants.EXTRA_KEY_FROM_ACTIVITY, TAG);
                intent.putExtra(AppConstants.EXTRA_KEY_MAC, mMokoDevice.mac);
                startActivity(intent);
            } else {
                ToastUtils.showToast(this, "Set up failed");
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceOnlineEvent(DeviceOnlineEvent event) {
        super.offline(event, mMokoDevice.mac);
    }

    public void onBack(View view) {
        if (isWindowLocked())
            finish();
    }


    public void onWifiSettings(View view) {
        if (isWindowLocked())
            return;
        if (!MQTTSupport.getInstance().isConnected()) {
            ToastUtils.showToast(this, R.string.network_error);
            return;
        }
        if (!mMokoDevice.isOnline) {
            ToastUtils.showToast(this, R.string.device_offline);
            return;
        }
        Intent i = new Intent(this, ModifyWifiSettingsActivity.class);
        i.putExtra(AppConstants.EXTRA_KEY_DEVICE, mMokoDevice);
        startActivity(i);
    }

    public void onMqttSettings(View view) {
        if (isWindowLocked())
            return;
        if (!MQTTSupport.getInstance().isConnected()) {
            ToastUtils.showToast(this, R.string.network_error);
            return;
        }
        if (!mMokoDevice.isOnline) {
            ToastUtils.showToast(this, R.string.device_offline);
            return;
        }
        Intent i = new Intent(this, ModifyMQTTSettingsActivity.class);
        i.putExtra(AppConstants.EXTRA_KEY_DEVICE, mMokoDevice);
        launcher.launch(i);
    }

    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> onMQTTSettingsResult(result));

    private void onMQTTSettingsResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            String subscribeTopic = result.getData().getStringExtra(AppConstants.EXTRA_KEY_DEVICE_SUBSCRIBE_TOPIC);
            String publishTopic = result.getData().getStringExtra(AppConstants.EXTRA_KEY_DEVICE_PUBLISH_TOPIC);
            MQTTConfig mqttConfig = new Gson().fromJson(mMokoDevice.mqttInfo, MQTTConfig.class);
            mqttConfig.topicPublish = publishTopic;
            mqttConfig.topicSubscribe = subscribeTopic;
            mMokoDevice.topicPublish = publishTopic;
            mMokoDevice.topicSubscribe = subscribeTopic;
            mMokoDevice.mqttInfo = new Gson().toJson(mqttConfig, MQTTConfig.class);
            DBTools.getInstance(this).updateDevice(mMokoDevice);
        }
    }

    public void onNetworkSettings(View view) {
        if (isWindowLocked()) return;
        if (!MQTTSupport.getInstance().isConnected()) {
            ToastUtils.showToast(this, R.string.network_error);
            return;
        }
        if (!mMokoDevice.isOnline) {
            ToastUtils.showToast(this, R.string.device_offline);
            return;
        }
        Intent i = new Intent(this, ModifyNetworkSettingsActivity.class);
        i.putExtra(AppConstants.EXTRA_KEY_DEVICE, mMokoDevice);
        startActivity(i);
    }

    public void onConnect(View view) {
        if (isWindowLocked()) return;
        if (!MQTTSupport.getInstance().isConnected()) {
            ToastUtils.showToast(this, R.string.network_error);
            return;
        }
        if (!mMokoDevice.isOnline) {
            ToastUtils.showToast(this, R.string.device_offline);
            return;
        }
        mHandler.postDelayed(() -> {
            dismissLoadingProgressDialog();
            ToastUtils.showToast(this, "Set up failed");
        }, 30 * 1000);
        showLoadingProgressDialog();
        rebootDevice();
    }

    private void rebootDevice() {
        XLog.i("重启设备");
        int msgId = MQTTConstants.CONFIG_MSG_ID_REBOOT;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("reset", 0);
        String message = assembleWriteCommonData(msgId, mMokoDevice.mac, jsonObject);
        try {
            MQTTSupport.getInstance().publish(mAppTopic, message, msgId, appMqttConfig.qos);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
