package gaditek.com.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import java.util.ArrayList;
import java.util.List;

import gaditek.com.App;
/*
 * Created by shayan.raees on 5/22/2018.
 */

public class PermissionUtil {

    public final static int REQ_PERMISSION_LOCATION                         = 300;

    public static Boolean checkPermissionSilent(Context ctx, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            return ctx.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        return true;
    }

    public static Boolean checkPermissionWithoutAlerts(Activity ctx, String permission, int REQUEST_CODE) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ctx.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                List<String> permissionsList = new ArrayList<>();
                permissionsList.add(permission);
                if (ctx.shouldShowRequestPermissionRationale(permission))
                    ctx.requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), REQUEST_CODE);
                else
                    openAppSettings(ctx);
                return false;
            }
        } else
            return true;
    }

    private static void openAppSettings(Context ctx) {
        //move to settings page
        //http://stackoverflow.com/questions/32822101/how-to-programmatically-open-the-permission-screen-for-a-specific-app-on-android
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + App.getInstance().getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        ctx.startActivity(i);
    }
}
