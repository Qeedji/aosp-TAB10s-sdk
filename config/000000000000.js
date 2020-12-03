/**
 * Function: configuration-by-script for AOSP 
 * Version: 1.10.12
 * File name pattern: the file name must match one of these formats:
 * - common to several devices 
 *    - 000000000000.js
 * 	  - configuration.js 
 * - specific for one device 
 * 	  - <device_WLAN0_MAC_addr>.js (format ABCDEFABCDEF.js)
 * - when using a external Ethernet to USB-C hub 
 *    - <device_ETH0_MAC_addr>.js (format ABCDEFABCDEF.js)
 *    - in this case, the "device_ETH0_MAC_addr" value is getting the hub_ETH0_MAC_addr value
 */

/** --------------------------------------
 * ---------------------------------------
 * ---- BEGIN of the user configuration
 * ---------------------------------------
 * ---------------------------------------
 */

/*------  Allows to handle the timezone -----------------------------------------------------------------*/
//setTimezone("Europe/Paris");
//setDefaultTimezone();

/*------  Allows to handle the activation of `APK installation from USB storage device` feature ---------*/
//enableExternalStorageCopyApk(); /* default mode */
//disableExternalStorageCopyApk();

/*------ Allows to handle the device mode: `native` or `kiosk` ------------------------------------------*/
//setDeviceModeNative(); /* default mode */
//setDeviceModeKiosk();

/*------  Allows to handle Bluetooth ---------*/
//enableBluetooth();
//disableBluetooth();

/*------  Allows to handle Wifi ---------*/
//enableWifi();
//disableWifi();

/*------  Allows to handle Rfid125KHz ---------*/
//enableRfid125KHz();
//disableRfid125KHz();

/*------  Allows Developer Options ---------*/
//enableAllowDeveloperOptions();
//disableAllowDeveloperOptions();

/** --------------------------------------
 * ---------------------------------------
 * ---- END of the user configuration
 * ---------------------------------------
 * ---------------------------------------
 */


/** --------------------------------------
 * ---- Functions section
 * ---------------------------------------
 */

/* Debug functions */
/** duration 4 seconds */
function showToast(toast) {
	Android.showToast(toast);
}
/** duration 7 seconds */
function showLongToast(toast) {
	Android.showLongToast(toast);
}

/* Functions to get AOSP firmware version */
function getDeliverySoftwareVersion() {
	return Android.SystemProperties().getString("persist.sys.delivery-software-version");
}
/* Functions to get uBoot version */
function getUBootVersion() {
	return Android.SystemProperties().getString("ro.uboot.version");
}

/* Functions to set/get timezone value */
function getTimezone() {
	return Android.SystemProperties().getString("persist.sys.timezone");
}
function setTimezone(m) {
	Android.SystemProperties().setString("persist.sys.timezone", m);
}
function setDefaultTimezone() {
	Android.SystemProperties().setString("persist.sys.timezone", "GMT");
}
/* Functions to handle the activation of the feature `APK upgrade from USB storage device` */
function getExternalStorageCopyApk() {
	return Android.Preferences("SharedPreferences", "tech.qeedji.system.app").getBoolean("externalstorage.copy.apk.enabled", false);
}
function enableExternalStorageCopyApk() {
	Android.Preferences("SharedPreferences", "tech.qeedji.system.app").setBoolean("externalstorage.copy.apk.enabled", true);
}
function disableExternalStorageCopyApk() {
	Android.Preferences("SharedPreferences", "tech.qeedji.system.app").setBoolean("externalstorage.copy.apk.enabled", false);
}

/* Functions to handle the device mode `native`, `kiosk` */
function getDeviceMode() {
	return Android.SystemProperties().getString("persist.sys.device_mode");
}
function setDeviceModeKiosk() {
	Android.SystemProperties().setString("persist.sys.device_mode", "kiosk");
}
function setDeviceModeNative() {
	Android.SystemProperties().setString("persist.sys.device_mode", "native");
}

/* Functions to handle Bluetooth */
function getBluetooth() {
	return Android.Preferences("Settings", "Global").getInt("bluetooth_on", 0);
}
function enableBluetooth() {
	Android.Preferences("Settings", "Global").setInt("bluetooth_on", 1);
}
function disableBluetooth() {
	Android.Preferences("Settings", "Global").setInt("bluetooth_on", 0);
}

/* Functions to handle Wifi */
function getWifi() {
	return Android.Preferences("Settings", "Global").getInt("wifi_on", 0);
}
function enableWifi() {
	Android.Preferences("Settings", "Global").setInt("wifi_on", 1);
}
function disableWifi() {
	Android.Preferences("Settings", "Global").setInt("wifi_on", 0);
}

/* Functions to handle Rfid 125KHz */
function getRfid125KHz() {
	return Android.SystemProperties().getBoolean("persist.sys.rfid125khz.enable", false);
}
function enableRfid125KHz() {
	Android.SystemProperties().setBoolean("persist.sys.rfid125khz.enable", true);
}
function disableRfid125KHz() {
	Android.SystemProperties().setBoolean("persist.sys.rfid125khz.enable", false);
}

/* Functions to handle developer options */
function enableAllowDeveloperOptions() {
	Android.Preferences("Settings", "Secure").setBoolean("developer_options_allowed", true);
}
function disableAllowDeveloperOptions() {
	Android.Preferences("Settings", "Secure").setBoolean("developer_options_allowed", false);
}
