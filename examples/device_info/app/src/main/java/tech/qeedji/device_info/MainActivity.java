package tech.qeedji.device_info;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.pm.PackageManager;

import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import tech.qeedji.system.lib.DipSwitch;

public class MainActivity extends AppCompatActivity {
    private final int REQUEST_PERMISSION_PHONE_STATE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showPhoneStatePermission();
        showText();
    }

    private void showText() {
        String model = android.os.Build.MODEL;
        setText(R.id.Model, model);
        String manufacturer = android.os.Build.MANUFACTURER;
        setText(R.id.Manufacturer, manufacturer);
        setText(R.id.PSN, getPsn(getSerial()));
        String version = android.os.Build.VERSION.BASE_OS;
        if ((version != null) && !(version.isEmpty())) {
            setText(R.id.Software_version, version);
        } else {
            deleteTableRow(R.id.Row_software_version);
        }
        String macEth0 = getMACAddress("eth0");
        if ((macEth0 != null) && !(macEth0.isEmpty())) {
            setText(R.id.Eth0_MAC_address, macEth0);
        } else {
            deleteTableRow(R.id.Row_Eth0_MAC_address);
        }
        setText(R.id.Wlan0_MAC_address, getMACAddress("wlan0"));
        setText(R.id.Wpan0_MAC_address, getBluetoothAddress());
        setText(R.id.DIP_Switch_Camera, getDipSwitchCamera());
        setText(R.id.DIP_Switch_Microphone, getDipSwitchMicrophone());
    }

    private void setText(int id, String text){
        TextView textView = (TextView)findViewById(id);
        textView.setText(text);
    }

    private void deleteTableRow(int id){
        TableLayout table = (TableLayout)findViewById(R.id.Table);
        TableRow row = (TableRow)findViewById(id);
        table.removeView(row);
    }

    private String getSerial() {
        String serialNumber = android.os.Build.SERIAL;
        if (serialNumber.equals("unknown")){
            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    serialNumber = android.os.Build.getSerial();
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
        return serialNumber;
    }

    private String getPsn(String serialNumber) {
        if (serialNumber.equals("unknown")) {
            return "00000-00000";
        }
        String sPsnRaw = serialNumber.substring(8,16);
        byte[] bPsnRaw = new byte[4];
        int j=0;
        for (int i=0; i<sPsnRaw.length(); i=i+2) {
            bPsnRaw[j++] = Byte.parseByte(sPsnRaw.substring(i,i+2), 16);
        }
        int iProductType = (bPsnRaw[0]<<4) | ((bPsnRaw[1]>>4) & 0x0F);
        int iProductSubtype = bPsnRaw[1] & 0x0F;
        int iOrdinalNumber = (bPsnRaw[2]<<8) | bPsnRaw[3];
        return String.format("%04d", iProductType) + String.format("%01d", iProductSubtype)
                + "-" + String.format("%05d", iOrdinalNumber);
    }

    public static String getBluetoothAddress() {
        String btAddress = null;
        try {
            BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
            if (bluetooth != null) {
                boolean isEn = bluetooth.isEnabled();
                if (!isEn)
                    bluetooth.enable();
                btAddress = bluetooth.getAddress();
                if (!isEn)
                    bluetooth.disable();
            }
        } catch (Exception ignored) {
            ignored.getMessage();
        }
        return btAddress;
    }

    public static String getMACAddress(String interfaceName) {
        if (interfaceName == null) {
            return null;
        }
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (!intf.getName().equalsIgnoreCase(interfaceName)) {
                    continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac == null) {
                    return null;
                }
                StringBuilder buf = new StringBuilder();
                for (byte aMac : mac) {
                    buf.append(String.format("%02X:", aMac));
                }
                buf.deleteCharAt(buf.length()-1);
                return buf.toString();
            }
        } catch (Exception ignored) {
            ignored.getMessage();
        }
        return null;
    }

    private void showPhoneStatePermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_PHONE_STATE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.READ_PHONE_STATE, REQUEST_PERMISSION_PHONE_STATE);
        }
    }

    private String getDipSwitchCamera() {
        DipSwitch dipswitch = new DipSwitch(getApplicationContext());
        if (dipswitch.camera()) {
            return "on";
        }
        return "off";
    }

    private String getDipSwitchMicrophone() {
        DipSwitch dipswitch = new DipSwitch(getApplicationContext());
        if (dipswitch.microphone()) {
            return "on";
        }
        return "off";
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String permissions[],
            int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_PHONE_STATE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                    showText();
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                    showText();
                }
        }
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(this,
                new String[]{permissionName}, permissionRequestCode);
    }
}
