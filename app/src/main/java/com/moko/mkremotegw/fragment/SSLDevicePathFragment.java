package com.moko.mkremotegw.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.moko.mkremotegw.R;
import com.moko.mkremotegw.base.BaseActivity;
import com.moko.mkremotegw.databinding.FragmentSslDevicePathBinding;
import com.moko.mkremotegw.dialog.BottomDialog;
import com.moko.mkremotegw.utils.ToastUtils;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;

public class SSLDevicePathFragment extends Fragment {

    private static final String TAG = SSLDevicePathFragment.class.getSimpleName();
    private FragmentSslDevicePathBinding mBind;


    private BaseActivity activity;

    private int mConnectMode = 0;

    private String host;
    private int port;
    private String caPath;
    private String clientKeyPath;
    private String clientCertPath;

    private ArrayList<String> values;
    private int selected;

    public SSLDevicePathFragment() {
    }

    public static SSLDevicePathFragment newInstance() {
        SSLDevicePathFragment fragment = new SSLDevicePathFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: ");
        mBind = FragmentSslDevicePathBinding.inflate(inflater, container, false);
        activity = (BaseActivity) getActivity();
        mBind.clCertificate.setVisibility(mConnectMode > 0 ? View.VISIBLE : View.GONE);
        mBind.cbSsl.setChecked(mConnectMode > 0);
        mBind.cbSsl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    mConnectMode = 0;
                } else {
                    if (selected == 0) {
                        mConnectMode = 1;
                    } else if (selected == 1) {
                        mConnectMode = 2;
                    } else if (selected == 2) {
                        mConnectMode = 3;
                    }
                }
                mBind.clCertificate.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });
        values = new ArrayList<>();
        values.add("CA signed server certificate");
        values.add("CA certificate file");
        values.add("Self signed certificates");
        if (mConnectMode > 0) {
            selected = mConnectMode - 1;
            mBind.etCaPath.setText(caPath);
            mBind.etClientKeyPath.setText(clientKeyPath);
            mBind.etClientCertPath.setText(clientCertPath);
            mBind.tvCertification.setText(values.get(selected));
        }
        if (selected == 0) {
            mBind.llCa.setVisibility(View.GONE);
            mBind.llClientKey.setVisibility(View.GONE);
            mBind.llClientCert.setVisibility(View.GONE);
            mBind.clCertServer.setVisibility(View.GONE);
        } else if (selected == 1) {
            mBind.llCa.setVisibility(View.VISIBLE);
            mBind.llClientKey.setVisibility(View.GONE);
            mBind.llClientCert.setVisibility(View.GONE);
            mBind.clCertServer.setVisibility(View.VISIBLE);
        } else if (selected == 2) {
            mBind.llCa.setVisibility(View.VISIBLE);
            mBind.llClientKey.setVisibility(View.VISIBLE);
            mBind.llClientCert.setVisibility(View.VISIBLE);
            mBind.clCertServer.setVisibility(View.VISIBLE);
        }
        return mBind.getRoot();
    }

    @Override
    public void onResume() {
        Log.i(TAG, "onResume: ");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.i(TAG, "onPause: ");
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: ");
        super.onDestroy();
    }

    public void setConnectMode(int connectMode) {
        this.mConnectMode = connectMode;
        if (mBind == null)
            return;
        mBind.clCertificate.setVisibility(mConnectMode > 0 ? View.VISIBLE : View.GONE);
        mBind.cbSsl.setChecked(mConnectMode > 0);
        mBind.cbSsl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    mConnectMode = 0;
                } else {
                    if (selected == 0) {
                        mConnectMode = 1;
                    } else if (selected == 1) {
                        mConnectMode = 2;
                    } else if (selected == 2) {
                        mConnectMode = 3;
                    }
                }
                mBind.clCertificate.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });
        if (mConnectMode > 0) {
            selected = mConnectMode - 1;
            mBind.etCaPath.setText(caPath);
            mBind.etClientKeyPath.setText(clientKeyPath);
            mBind.etClientCertPath.setText(clientCertPath);
            mBind.tvCertification.setText(values.get(selected));
        }
        if (selected == 0) {
            mBind.llCa.setVisibility(View.GONE);
            mBind.llClientKey.setVisibility(View.GONE);
            mBind.llClientCert.setVisibility(View.GONE);
        } else if (selected == 1) {
            mBind.llCa.setVisibility(View.VISIBLE);
            mBind.llClientKey.setVisibility(View.GONE);
            mBind.llClientCert.setVisibility(View.GONE);
        } else if (selected == 2) {
            mBind.llCa.setVisibility(View.VISIBLE);
            mBind.llClientKey.setVisibility(View.VISIBLE);
            mBind.llClientCert.setVisibility(View.VISIBLE);
        }
    }

    public void setCAPath(String caPath) {
        this.caPath = caPath;
        if (mBind == null)
            return;
        mBind.etCaPath.setText(caPath);
    }

    public void setClientKeyPath(String clientKeyPath) {
        this.clientKeyPath = clientKeyPath;
        if (mBind == null)
            return;
        mBind.etClientKeyPath.setText(clientKeyPath);
    }

    public void setClientCertPath(String clientCertPath) {
        this.clientCertPath = clientCertPath;
        if (mBind == null)
            return;
        mBind.etClientCertPath.setText(clientCertPath);
    }

    public void setHost(String host) {
        this.host = host;
        if (mBind == null)
            return;
        mBind.etMqttHost.setText(clientCertPath);
    }

    public void setPort(int port) {
        this.port = port;
        if (mBind == null)
            return;
        mBind.etMqttPort.setText(String.valueOf(port));
    }

    public void selectCertificate() {
        BottomDialog dialog = new BottomDialog();
        dialog.setDatas(values, selected);
        dialog.setListener(value -> {
            selected = value;
            mBind.tvCertification.setText(values.get(selected));
            if (selected == 0) {
                mConnectMode = 1;
                mBind.llCa.setVisibility(View.GONE);
                mBind.llClientKey.setVisibility(View.GONE);
                mBind.llClientCert.setVisibility(View.GONE);
                mBind.clCertServer.setVisibility(View.GONE);
            } else if (selected == 1) {
                mConnectMode = 2;
                mBind.llCa.setVisibility(View.VISIBLE);
                mBind.llClientKey.setVisibility(View.GONE);
                mBind.llClientCert.setVisibility(View.GONE);
                mBind.clCertServer.setVisibility(View.VISIBLE);
            } else if (selected == 2) {
                mConnectMode = 3;
                mBind.llCa.setVisibility(View.VISIBLE);
                mBind.llClientKey.setVisibility(View.VISIBLE);
                mBind.llClientCert.setVisibility(View.VISIBLE);
                mBind.clCertServer.setVisibility(View.VISIBLE);
            }
        });
        dialog.show(activity.getSupportFragmentManager());
    }


    public boolean isValid() {
        final String host = mBind.etMqttHost.getText().toString();
        final String port = mBind.etMqttPort.getText().toString();
        final String caFile = mBind.etCaPath.getText().toString();
        final String clientKeyFile = mBind.etClientKeyPath.getText().toString();
        final String clientCertFile = mBind.etClientCertPath.getText().toString();
        if (mConnectMode > 1) {
            if (TextUtils.isEmpty(host)) {
                ToastUtils.showToast(activity, "Host error");
                return false;
            }
            if (TextUtils.isEmpty(port)) {
                ToastUtils.showToast(activity, "Port error");
                return false;
            }
            int portInt = Integer.parseInt(port);
            if (portInt > 65535) {
                ToastUtils.showToast(activity, "Port error");
                return false;
            }
        }
        if (mConnectMode == 2) {
            if (TextUtils.isEmpty(caFile)) {
                ToastUtils.showToast(activity, getString(R.string.mqtt_verify_ca));
                return false;
            }
        } else if (mConnectMode == 3) {
            if (TextUtils.isEmpty(caFile)) {
                ToastUtils.showToast(activity, getString(R.string.mqtt_verify_ca));
                return false;
            }
            if (TextUtils.isEmpty(clientKeyFile)) {
                ToastUtils.showToast(activity, getString(R.string.mqtt_verify_client_key));
                return false;
            }
            if (TextUtils.isEmpty(clientCertFile)) {
                ToastUtils.showToast(activity, getString(R.string.mqtt_verify_client_cert));
                return false;
            }
        }
        return true;
    }

    public int getConnectMode() {
        return mConnectMode;
    }

    public String getSSLHost() {
        return mBind.etMqttHost.getText().toString();
    }

    public int getSSLPort() {
        final String port = mBind.etMqttPort.getText().toString();
        return Integer.parseInt(port);
    }

    public String getCAPath() {
        return mBind.etCaPath.getText().toString();
    }

    public String getClientCertPath() {
        return mBind.etClientCertPath.getText().toString();
    }

    public String getClientKeyPath() {
        return mBind.etClientKeyPath.getText().toString();
    }
}
