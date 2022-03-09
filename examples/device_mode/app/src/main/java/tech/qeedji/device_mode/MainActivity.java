package tech.qeedji.device_mode;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import tech.qeedji.system.lib.DeviceMode;
import tech.qeedji.system.lib.DeviceModeListener;

public class MainActivity extends AppCompatActivity {

    private Activity mActivity;
    private TextView mTextView;
    private DeviceMode mDeviceMode;
    private DeviceModeListener mDeviceModeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivity = this;
        mDeviceMode = new DeviceMode(this);
        mTextView = (TextView) findViewById(R.id.textView);
        mTextView.setText(intToStringDeviceMode(mDeviceMode.getValue()));
        mDeviceModeListener = new DeviceModeListener() {
            @Override
            public void onDeviceModeChanged(int oldDeviceMode, int newDeviceMode) {
                final String text = intToStringDeviceMode(newDeviceMode);
                mTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        mTextView.setText(text);
                    }
                });
            }
        };
        mDeviceMode.registerListener(mDeviceModeListener);
        Button buttonNative = (Button) findViewById(R.id.buttonNative);
        buttonNative.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mDeviceMode.setValue(DeviceMode.NATIVE);
            }
        });
        Button buttonKiosk = (Button) findViewById(R.id.buttonKiosk);
        buttonKiosk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mDeviceMode.setValue(DeviceMode.KIOSK);
            }
        });
        Button buttonEnd = (Button) findViewById(R.id.buttonEnd);
        buttonEnd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mDeviceMode.unregisterListener(mDeviceModeListener);
                mActivity.finish();
            }
        });
    }

    private String intToStringDeviceMode(int value) {
        if (value == DeviceMode.NATIVE) {
            return "Native";
        } else if (value == DeviceMode.KIOSK) {
            return "Kiosk";
        }
        return "Invalid";
    }
}
