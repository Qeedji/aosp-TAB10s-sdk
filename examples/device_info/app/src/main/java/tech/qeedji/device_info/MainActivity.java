package tech.qeedji.device_info;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
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
        new UpdateOperstate().execute();
    }

    private class UpdateOperstate extends AsyncTask<Boolean, Boolean, Boolean> {
        protected Boolean doInBackground(Boolean... p) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        protected void onPostExecute(Boolean result) {
            updateText();
            if (result == true) {
                new UpdateOperstate().execute();
            }
        }
    }

    private void showText() {
        initText();
        updateText();
    }

    private void initText() {
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
        setText(R.id.Wlan0_MAC_address, getMACAddress("wlan0"));
        setText(R.id.Wpan0_MAC_address, getBluetoothAddress());
    }

    private void updateText() {
        String macEth0 = getMACAddress("eth0");
        String sUpEth0 = null;
        if ((macEth0 != null) && !(macEth0.isEmpty())) {
            addTableRow(R.id.Row_Eth0_MAC_address);
            setText(R.id.Eth0_MAC_address, macEth0);
            sUpEth0 = getUp("eth0");
            setText(R.id.Eth0_Operstate, "\t(" + sUpEth0 + ")");
        } else {
            deleteTableRow(R.id.Row_Eth0_MAC_address);
        }
        String sUpWlan0 = getUp("wlan0");
        setText(R.id.Wlan0_Operstate, "\t(" + sUpWlan0 + ")");
        setText(R.id.DIP_Switch_Camera, getDipSwitchCamera());
        setText(R.id.DIP_Switch_Microphone, getDipSwitchMicrophone());
        UsbDevice usbDevice = getEthernetUsbDevice(getApplicationContext());
        if ( (usbDevice != null)
                && (usbDevice.getProductName() != null) && !usbDevice.getProductName().isEmpty()) {
            addTableRow(R.id.Row_NAPOE_Title);
            String productName = usbDevice.getProductName();
            String[] s = productName.split("~");
            addTableRow(R.id.Row_NAPOE_ProductName);
            setText(R.id.NAPOE_ProductName, s[0]);
            if ((usbDevice.getSerialNumber() != null) && !(usbDevice.getSerialNumber().isEmpty())) {
                addTableRow(R.id.Row_NAPOE_PSN);
                setText(R.id.NAPOE_PSN, usbDevice.getSerialNumber());
            } else {
                deleteTableRow(R.id.Row_NAPOE_PSN);
            }
            if ((usbDevice.getManufacturerName() != null) && !(usbDevice.getManufacturerName().isEmpty())) {
                addTableRow(R.id.Row_NAPOE_Manufacturer);
                setText(R.id.NAPOE_Manufacturer, usbDevice.getManufacturerName());
            } else {
                deleteTableRow(R.id.Row_NAPOE_Manufacturer);
            }
            if ( (s.length > 1) && (s[1] != null) && !(s[1].isEmpty())) {
                addTableRow(R.id.Row_NAPOE_Version);
                setText(R.id.NAPOE_Version, s[1]);
            } else {
                deleteTableRow(R.id.Row_NAPOE_Version);
            }
        } else {
            deleteTableRow(R.id.Row_NAPOE_Title);
            deleteTableRow(R.id.Row_NAPOE_ProductName);
            deleteTableRow(R.id.Row_NAPOE_PSN);
            deleteTableRow(R.id.Row_NAPOE_Manufacturer);
            deleteTableRow(R.id.Row_NAPOE_Version);
        }
    }

    private void setText(int id, String text){
        TextView textView = (TextView)findViewById(id);
        textView.setText(text);
    }

    private void addTableRow(int id){
        TableRow row = (TableRow)findViewById(id);
        row.setVisibility(android.view.View.VISIBLE);
    }

    private void deleteTableRow(int id){
        TableRow row = (TableRow)findViewById(id);
        row.setVisibility(android.view.View.INVISIBLE);
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
        short[] bPsnRaw = new short[4];
        int j=0;
        for (int i=0; i<sPsnRaw.length(); i=i+2) {
            bPsnRaw[j++] = Short.parseShort(sPsnRaw.substring(i,i+2), 16);
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

    public static String getUp(String interfaceName) {
        if (interfaceName == null) {
            return "Unknown";
        }
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (!intf.getName().equalsIgnoreCase(interfaceName)) {
                    continue;
                }
                if (intf.isUp()) {
                    return "Up";
                }
                return "Down";
            }
        } catch (Exception ignored) {
            ignored.getMessage();
        }
        return "Unknown";
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

    private static UsbDevice getEthernetUsbDevice(Context context) {
        UsbDevice ret = null;
        UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> usbDevices = manager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = usbDevices.values().iterator();
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            // Log.d(TAG, "Device " + device);
            String serialNumber = device.getSerialNumber();
            if ( (serialNumber != null) && !serialNumber.isEmpty()) {
                if ( (serialNumber.startsWith("0140"))
                        || (serialNumber.startsWith("0141"))
                        || (serialNumber.startsWith("0142"))
                        || (serialNumber.startsWith("0143"))
                        || (serialNumber.startsWith("0144"))
                        || (serialNumber.startsWith("0145")) ) {
                    ret = device;
                    break;
                }
            }
            String productName = device.getProductName();
            if ( (productName != null) && !productName.isEmpty()) {
                if (productName.toLowerCase().contains("ethernet")) {
                    ret = device;
                    break;
                }
                if (productName.toLowerCase().contains(" lan")) {
                    ret = device;
                    break;
                }
            }
            for(int i=0; i < device.getInterfaceCount(); i++) {
                UsbInterface usbInterface = device.getInterface(i);
                String name = usbInterface.getName();
                if ( (name != null) && !name.isEmpty()) {
                    if ("Network_Interface".equalsIgnoreCase(name)){
                        ret = device;
                        break;
                    }
                }
            }
        }
        return ret;
    }
}
