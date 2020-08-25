package tech.qeedji.device_info;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;

import android.os.Bundle;
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
        TextView textView = (TextView)findViewById(R.id.textView);
        textView.setText(null);
        String model = android.os.Build.MODEL;
        String manufacturer = android.os.Build.MANUFACTURER;
        addText(textView, "Model\t\t\t\t\t\t: " + model);
        addText(textView, "Manufacturer\t: " + manufacturer);
        addText(textView, "\nPSN\t\t\t\t\t\t\t: " + getPsn(getSerial()));
        addText(textView, "\nMAC WLan\t\t\t: " + getMACAddress("wlan0"));
        String macEth0 = getMACAddress("eth0");
        if ((macEth0 != null) && !(macEth0.isEmpty())) {
            addText(textView, "MAC Lan\t\t\t\t: " + macEth0);
        }
        addText(textView, "\nDIP Switch Camera\t\t\t\t: " + getDipSwitchCamera());
        addText(textView, "DIP Switch Microphone\t: " + getDipSwitchMicrophone());
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

    private void addText(TextView textView, String text) {
        if (textView.length() == 0) {
            textView.setText(text);
        } else {
            textView.setText(textView.getText() + "\n" + text);
        }
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
