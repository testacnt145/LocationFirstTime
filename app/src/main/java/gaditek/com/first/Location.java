package gaditek.com.first;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import gaditek.com.util.LocationUtil;
import gaditek.com.util.PermissionUtil;

public class Location extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    //this project will ensure to fetch user last latitude and longitude everytime

    // location listener
    private android.location.Location location;
    private GoogleApiClient googleApiClient;

    TextView txtView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        txtView = findViewById(R.id.txt);
        firstMethodForLocation();
    }

    public void btnClick(View view) {
        if (!PermissionUtil.checkPermissionSilent(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(this, "checkPermissionWithoutAlerts", Toast.LENGTH_SHORT).show();
            PermissionUtil.checkPermissionWithoutAlerts(this, Manifest.permission.ACCESS_FINE_LOCATION, PermissionUtil.REQ_PERMISSION_LOCATION);
        } else {
            Toast.makeText(this, "locationSettingsRequest", Toast.LENGTH_SHORT).show();
            locationSettingsRequest(this);
        }
    }

    //______________________________________________________________________________________________
    final static int FIRST_PERMISSION = 0;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionUtil.REQ_PERMISSION_LOCATION:
                if(grantResults[FIRST_PERMISSION] == PackageManager.PERMISSION_GRANTED)
                    locationSettingsRequest(this);
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                //imp : do not call permissionNotGranted() here because, PERMISSION_DO_NOTHING will come in this case
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_LOCATION_SETTINGS) {
            if (resultCode == RESULT_OK) {
                secondMethodInCaseOfFailure();
            }
        }
    }


    public static final int REQ_LOCATION_SETTINGS                   = 10;
    //https://stackoverflow.com/a/33254073/4754141
    private void locationSettingsRequest(final Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();
        final LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        secondMethodInCaseOfFailure();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(Location.this, REQ_LOCATION_SETTINGS);
                        } catch (IntentSender.SendIntentException ignored) {
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    //______________________________________________________________________________________________
    //______________________________    firstMethodForLocation   ___________________________________
    //______________________________________________________________________________________________
    void firstMethodForLocation() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }


    //firstMethodForLocation
    @Override
    public void onStart() {
        super.onStart();
        if (googleApiClient != null)
            googleApiClient.connect();
    }

    //firstMethodForLocation
    @Override
    public void onStop() {
        super.onStop();
        if (googleApiClient != null)
            googleApiClient.disconnect();
    }


    //firstMethodForLocation
    @Override
    public void onConnected(Bundle bundle) {
        if (location == null) {
            location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (location != null) {
                txtView.setText(location.getLatitude() + " : A : " + location.getLongitude());
            } else {
                txtView.setText(location + ": ... FAIL - First Method");
                secondMethodInCaseOfFailure();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //______________________________________________________________________________________________
    //____________________________    secondMethodInCaseOfFailure   ________________________________
    //______________________________________________________________________________________________

    void secondMethodInCaseOfFailure() {
        if (PermissionUtil.checkPermissionSilent(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            if (LocationUtil.isDeviceLocationOn()) {
                //Request location updates
                //https://developer.android.com/guide/topics/location/strategies#Updates
                final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                final LocationListener locationListener = new LocationListener() {
                    public void onLocationChanged(android.location.Location location) {
                        if (location != null) {
                            txtView.setText(location.getLatitude() + " : B : " + location.getLongitude());
                            locationManager.removeUpdates(this);
                        } else {
                            txtView.setText(location + ": .... FAIL - Second Method"); //will never happen
                        }
                    }
                    public void onStatusChanged(String provider, int status, Bundle extras) {}

                    public void onProviderEnabled(String provider) {}

                    public void onProviderDisabled(String provider) {}
                };
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            } else {
                txtView.setText("Location - Turned off");
            }
        } else {
            txtView.setText("Location - Permission not granted");
        }
    }

}