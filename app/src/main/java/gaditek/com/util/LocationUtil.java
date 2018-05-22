package gaditek.com.util;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.Log;

import gaditek.com.App;
/*
 * Created by shayan.rais on 11/29/2017.
 */

public class LocationUtil {

    //https://stackoverflow.com/a/48988199/4754141
    //checking on the basis of network only
    public static boolean isDeviceLocationOn() {
        try {
            LocationManager locationManager = (LocationManager) App.getInstance().getSystemService(Context.LOCATION_SERVICE);
            return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ignored) {
            Log.e("---",ignored.toString());
        }
        return false;
    }

    public static void openLocationSettings(Context ctx) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        ctx.startActivity(intent);
    }
}
