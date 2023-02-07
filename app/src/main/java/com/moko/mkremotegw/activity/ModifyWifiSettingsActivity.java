package com.moko.mkremotegw.activity;

import android.os.Handler;
import android.os.Looper;
import android.text.InputFilter;
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
import com.moko.mkremotegw.databinding.ActivityModifyWifiSettingsBinding;
import com.moko.mkremotegw.dialog.BottomDialog;
import com.moko.mkremotegw.entity.MQTTConfig;
import com.moko.mkremotegw.entity.MokoDevice;
import com.moko.mkremotegw.utils.SPUtiles;
import com.moko.mkremotegw.utils.ToastUtils;
import com.moko.support.remotegw.MQTTConstants;
import com.moko.support.remotegw.MQTTSupport;
import com.moko.support.remotegw.entity.MsgConfigResult;
import com.moko.support.remotegw.entity.MsgNotify;
import com.moko.support.remotegw.entity.MsgReadResult;
import com.moko.support.remotegw.event.DeviceOnlineEvent;
import com.moko.support.remotegw.event.MQTTMessageArrivedEvent;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ModifyWifiSettingsActivity extends BaseActivity<ActivityModifyWifiSettingsBinding> {
    private final String FILTER_ASCII = "[ -~]*";
    private InputFilter filter;

    private ArrayList<String> mSecurityValues;
    private int mSecuritySelected;
    private ArrayList<String> mEAPTypeValues;
    private int mEAPTypeSelected;

    private MokoDevice mMokoDevice;
    private MQTTConfig appMqttConfig;
    private String mAppTopic;

    public Handler mHandler;

    @Override
    protected void onCreate() {
        mBind.cbVerifyServer.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (mSecuritySelected != 0 && mEAPTypeSelected != 2)
                mBind.clHost.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            mBind.clPort.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            mBind.clCa.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });
        mSecurityValues = new ArrayList<>();
        mSecurityValues.add("Personal");
        mSecurityValues.add("Enterprise");
        mEAPTypeValues = new ArrayList<>();
        mEAPTypeValues.add("PEAP-MSCHAPV2");
        mEAPTypeValues.add("TTLS-MSCHAPV2");
        mEAPTypeValues.add("TLS");
        filter = (source, start, end, dest, dstart, dend) -> {
            if (!(source + "").matches(FILTER_ASCII)) {
                return "";
            }

            return null;
        };
        mBind.etUsername.setFilters(new InputFilter[]{new InputFilter.LengthFilter(32), filter});
        mBind.etPassword.setFilters(new InputFilter[]{new InputFilter.LengthFilter(64), filter});
        mBind.etSsid.setFilters(new InputFilter[]{new InputFilter.LengthFilter(64), filter});
        mBind.etDomainId.setFilters(new InputFilter[]{new InputFilter.LengthFilter(64), filter});
        mBind.etHost.setFilters(new InputFilter[]{new InputFilter.LengthFilter(64), filter});

        mMokoDevice = (MokoDevice) getIntent().getSerializableExtra(AppConstants.EXTRA_KEY_DEVICE);
        String mqttConfigAppStr = SPUtiles.getStringValue(this, AppConstants.SP_KEY_MQTT_CONFIG_APP, "");
        appMqttConfig = new Gson().fromJson(mqttConfigAppStr, MQTTConfig.class);
        mAppTopic = TextUtils.isEmpty(appMqttConfig.topicPublish) ? mMokoDevice.topicSubscribe : appMqttConfig.topicPublish;
        mHandler = new Handler(Looper.getMainLooper());
        mHandler.postDelayed(() -> {
            dismissLoadingProgressDialog();
            finish();
        }, 30 * 1000);
        showLoadingProgressDialog();
        getWifiSettings();
    }

    @Override
    protected ActivityModifyWifiSettingsBinding getViewBinding() {
        return ActivityModifyWifiSettingsBinding.inflate(getLayoutInflater());
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
        if (msg_id == MQTTConstants.READ_MSG_ID_WIFI_SETTINGS) {
            Type type = new TypeToken<MsgReadResult<JsonObject>>() {
            }.getType();
            MsgReadResult<JsonObject> result = new Gson().fromJson(message, type);
            if (!mMokoDevice.mac.equalsIgnoreCase(result.device_info.mac))
                return;
            dismissLoadingProgressDialog();
            mHandler.removeMessages(0);
            mSecuritySelected = result.data.get("security_type").getAsInt();
            mBind.etSsid.setText(result.data.get("ssid").getAsString());
            mBind.etPassword.setText(result.data.get("passwd").getAsString());
            mBind.etDomainId.setText(result.data.get("eap_id").getAsString());
            mBind.tvSecurity.setText(mSecurityValues.get(mSecuritySelected));
            mBind.clEapType.setVisibility(mSecuritySelected != 0 ? View.VISIBLE : View.GONE);

            mEAPTypeSelected = result.data.get("eap_type").getAsInt();
            mBind.tvEpaType.setText(mEAPTypeValues.get(mEAPTypeSelected));
            if (mSecuritySelected != 0) {
                mBind.clUsername.setVisibility(mEAPTypeSelected == 2 ? View.GONE : View.VISIBLE);
                mBind.etUsername.setText(result.data.get("eap_username").getAsString());
                mBind.clPassword.setVisibility(mEAPTypeSelected == 2 ? View.GONE : View.VISIBLE);
                mBind.etPassword.setText(result.data.get("eap_passwd").getAsString());
                mBind.cbVerifyServer.setVisibility(mEAPTypeSelected == 2 ? View.INVISIBLE : View.VISIBLE);
                mBind.cbVerifyServer.setChecked(result.data.get("eap_verify_server").getAsInt() == 1);
                if (mEAPTypeSelected != 2) {
                    mBind.clCa.setVisibility(mBind.cbVerifyServer.isChecked() ? View.VISIBLE : View.GONE);
                    mBind.clHost.setVisibility(mBind.cbVerifyServer.isChecked() ? View.VISIBLE : View.GONE);
                    mBind.clPort.setVisibility(mBind.cbVerifyServer.isChecked() ? View.VISIBLE : View.GONE);
                } else {
                    mBind.clCa.setVisibility(View.VISIBLE);
                    mBind.clHost.setVisibility(View.VISIBLE);
                    mBind.clPort.setVisibility(View.VISIBLE);
                }
            }
            mBind.clDomainId.setVisibility(mEAPTypeSelected == 2 ? View.VISIBLE : View.GONE);
            mBind.clCert.setVisibility(mEAPTypeSelected == 2 ? View.VISIBLE : View.GONE);
            mBind.clKey.setVisibility(mEAPTypeSelected == 2 ? View.VISIBLE : View.GONE);
        }
        if (msg_id == MQTTConstants.CONFIG_MSG_ID_WIFI_SETTINGS) {
            Type type = new TypeToken<MsgConfigResult>() {
            }.getType();
            MsgConfigResult result = new Gson().fromJson(message, type);
            if (!mMokoDevice.mac.equalsIgnoreCase(result.device_info.mac))
                return;
            dismissLoadingProgressDialog();
            mHandler.removeMessages(0);
            if (result.result_code == 0) {
                ToastUtils.showToast(this, "Set up succeed");
                if (mSecuritySelected != 0) {
                    XLog.i("升级Wifi证书");
                    mHandler.postDelayed(() -> {
                        dismissLoadingProgressDialog();
                        finish();
                    }, 50 * 1000);
                    showLoadingProgressDialog();
                    setWifiCertFile();
                }
            } else {
                ToastUtils.showToast(this, "Set up failed");
            }
        }
        if (msg_id == MQTTConstants.NOTIFY_MSG_ID_WIFI_CERT_RESULT) {
            Type type = new TypeToken<MsgNotify<JsonObject>>() {
            }.getType();
            MsgNotify<JsonObject> result = new Gson().fromJson(message, type);
            if (!mMokoDevice.mac.equalsIgnoreCase(result.device_info.mac))
                return;
            dismissLoadingProgressDialog();
            mHandler.removeMessages(0);
            int resultCode = result.data.get("result_code").getAsInt();
            if (resultCode == 1) {
                ToastUtils.showToast(this, R.string.update_success);
            } else {
                ToastUtils.showToast(this, R.string.update_failed);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceOnlineEvent(DeviceOnlineEvent event) {
        super.offline(event, mMokoDevice.mac);
    }

    public void onBack(View view) {
        finish();
    }

    private void setWifiSettings() {
        String ssid = mBind.etSsid.getText().toString();
        String username = mBind.etUsername.getText().toString();
        String password = mBind.etPassword.getText().toString();
        String domainId = mBind.etDomainId.getText().toString();
        int msgId = MQTTConstants.CONFIG_MSG_ID_WIFI_SETTINGS;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("security_type", mSecuritySelected);
        jsonObject.addProperty("ssid", ssid);
        jsonObject.addProperty("passwd", mSecuritySelected == 0 ? password : "");
        jsonObject.addProperty("eap_type", mEAPTypeSelected);
        jsonObject.addProperty("eap_id", mEAPTypeSelected == 2 ? domainId : "");
        jsonObject.addProperty("eap_username", mSecuritySelected != 0 ? username : "");
        jsonObject.addProperty("eap_passwd", mSecuritySelected != 0 ? password : "");
        if (mSecuritySelected != 0 && mEAPTypeSelected != 2) {
            jsonObject.addProperty("eap_verify_server", mBind.cbVerifyServer.isChecked() ? 1 : 0);
        }
        String message = assembleWriteCommonData(msgId, mMokoDevice.mac, jsonObject);
        try {
            MQTTSupport.getInstance().publish(mAppTopic, message, msgId, appMqttConfig.qos);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void setWifiCertFile() {
        String host = mBind.etHost.getText().toString();
        String port = mBind.etPort.getText().toString();
        String caFilePath = mBind.etCaFilePath.getText().toString();
        String certFilePath = mBind.etCertFilePath.getText().toString();
        String keyFilePath = mBind.etKeyFilePath.getText().toString();
        int msgId = MQTTConstants.CONFIG_MSG_ID_WIFI_CERT_FILE;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("host", host);
        jsonObject.addProperty("port", Integer.parseInt(port));
        jsonObject.addProperty("ca_file", caFilePath);
        jsonObject.addProperty("client_cert_file", certFilePath);
        jsonObject.addProperty("client_key_file", keyFilePath);
        String message = assembleWriteCommonData(msgId, mMokoDevice.mac, jsonObject);
        try {
            MQTTSupport.getInstance().publish(mAppTopic, message, msgId, appMqttConfig.qos);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void getWifiSettings() {
        int msgId = MQTTConstants.READ_MSG_ID_WIFI_SETTINGS;
        String message = assembleReadCommon(msgId, mMokoDevice.mac);
        try {
            MQTTSupport.getInstance().publish(mAppTopic, message, msgId, appMqttConfig.qos);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void onSelectSecurity(View view) {
        if (isWindowLocked()) return;
        BottomDialog dialog = new BottomDialog();
        dialog.setDatas(mSecurityValues, mSecuritySelected);
        dialog.setListener(value -> {
            mSecuritySelected = value;
            mBind.tvSecurity.setText(mSecurityValues.get(value));
            mBind.clEapType.setVisibility(mSecuritySelected != 0 ? View.VISIBLE : View.GONE);
            if (mSecuritySelected == 0) {
                mBind.clHost.setVisibility(View.GONE);
                mBind.clPort.setVisibility(View.GONE);
                mBind.clCa.setVisibility(View.GONE);
                mBind.clUsername.setVisibility(View.GONE);
                mBind.clPassword.setVisibility(View.VISIBLE);
                mBind.cbVerifyServer.setVisibility(View.GONE);
                mBind.clDomainId.setVisibility(View.GONE);
                mBind.clCert.setVisibility(View.GONE);
                mBind.clKey.setVisibility(View.GONE);
            } else {
                if (mEAPTypeSelected != 2) {
                    mBind.clHost.setVisibility(mBind.cbVerifyServer.isChecked() ? View.VISIBLE : View.GONE);
                    mBind.clPort.setVisibility(mBind.cbVerifyServer.isChecked() ? View.VISIBLE : View.GONE);
                    mBind.clCa.setVisibility(mBind.cbVerifyServer.isChecked() ? View.VISIBLE : View.GONE);
                } else {
                    mBind.clHost.setVisibility(View.VISIBLE);
                    mBind.clPort.setVisibility(View.VISIBLE);
                    mBind.clCa.setVisibility(View.VISIBLE);
                }
                mBind.clUsername.setVisibility(mEAPTypeSelected == 2 ? View.GONE : View.VISIBLE);
                mBind.clPassword.setVisibility(mEAPTypeSelected == 2 ? View.GONE : View.VISIBLE);
                mBind.cbVerifyServer.setVisibility(mEAPTypeSelected == 2 ? View.INVISIBLE : View.VISIBLE);
                mBind.clDomainId.setVisibility(mEAPTypeSelected == 2 ? View.VISIBLE: View.GONE);
                mBind.clCert.setVisibility(mEAPTypeSelected == 2 ? View.VISIBLE : View.GONE);
                mBind.clKey.setVisibility(mEAPTypeSelected == 2 ? View.VISIBLE : View.GONE);
            }
        });
        dialog.show(getSupportFragmentManager());
    }

    public void onSelectEAPType(View view) {
        if (isWindowLocked()) return;
        BottomDialog dialog = new BottomDialog();
        dialog.setDatas(mEAPTypeValues, mEAPTypeSelected);
        dialog.setListener(value -> {
            mEAPTypeSelected = value;
            mBind.tvEpaType.setText(mEAPTypeValues.get(value));
            mBind.clUsername.setVisibility(mEAPTypeSelected == 2 ? View.GONE : View.VISIBLE);
            mBind.clPassword.setVisibility(mEAPTypeSelected == 2 ? View.GONE : View.VISIBLE);
            mBind.cbVerifyServer.setVisibility(mEAPTypeSelected == 2 ? View.INVISIBLE : View.VISIBLE);
            mBind.clDomainId.setVisibility(mEAPTypeSelected == 2 ? View.VISIBLE : View.GONE);
            if (mEAPTypeSelected != 2) {
                mBind.clHost.setVisibility(mBind.cbVerifyServer.isChecked() ? View.VISIBLE : View.GONE);
                mBind.clPort.setVisibility(mBind.cbVerifyServer.isChecked() ? View.VISIBLE : View.GONE);
                mBind.clCa.setVisibility(mBind.cbVerifyServer.isChecked() ? View.VISIBLE : View.GONE);
            } else {
                mBind.clHost.setVisibility(View.VISIBLE);
                mBind.clPort.setVisibility(View.VISIBLE);
                mBind.clCa.setVisibility(View.VISIBLE);
            }
            mBind.clCert.setVisibility(mEAPTypeSelected == 2 ? View.VISIBLE : View.GONE);
            mBind.clKey.setVisibility(mEAPTypeSelected == 2 ? View.VISIBLE : View.GONE);
        });
        dialog.show(getSupportFragmentManager());
    }

    public void onSave(View view) {
        if (isWindowLocked()) return;
        if (isVerify()) {
            saveParams();
        } else {
            ToastUtils.showToast(this, "Para Error");
        }
    }

    private boolean isVerify() {
        String ssid = mBind.etSsid.getText().toString();
        if (!TextUtils.isEmpty(ssid))
            return false;
        if (mSecuritySelected != 0) {
            if (mEAPTypeSelected != 2 && !mBind.cbVerifyServer.isChecked()) {
                return true;
            }
            String host = mBind.etHost.getText().toString();
            String portStr = mBind.etPort.getText().toString();
            String caFilePath = mBind.etCaFilePath.getText().toString();
            if (TextUtils.isEmpty(host) || TextUtils.isEmpty(portStr) || TextUtils.isEmpty(caFilePath))
                return false;
            int port = Integer.parseInt(portStr);
            if (port < 1 || port > 65535)
                return false;
        }
        return true;
    }

    private void saveParams() {
        if (!MQTTSupport.getInstance().isConnected()) {
            ToastUtils.showToast(this, R.string.network_error);
            return;
        }
        if (!mMokoDevice.isOnline && mBind.cbVerifyServer.isChecked()) {
            ToastUtils.showToast(this, R.string.device_offline);
            return;
        }
        mHandler.postDelayed(() -> {
            dismissLoadingProgressDialog();
            ToastUtils.showToast(this, "Set up failed");
        }, 30 * 1000);
        showLoadingProgressDialog();
        setWifiSettings();
    }

}
