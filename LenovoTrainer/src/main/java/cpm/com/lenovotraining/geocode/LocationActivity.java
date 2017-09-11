package cpm.com.lenovotraining.geocode;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cpm.com.lenovotraining.R;
import cpm.com.lenovotraining.constants.CommonString;
import cpm.com.lenovotraining.database.Database;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback,
        ConnectionCallbacks, OnConnectionFailedListener, LocationListener {
    protected static final String PHOTO_TAKEN = "photo_taken";
    LocationManager locationManager;
    Geocoder geocoder;
    protected Button _buttonsave;
    File file;
    protected ImageView _image;
    protected boolean _taken;
    Button capture_1;
    public String text;
    public View view;
    GeotaggingBeans data = new GeotaggingBeans();
    private LocationManager locmanager = null;
    protected String diskpath = "";
    protected String _path;
    boolean enabled;
    protected String _pathforcheck = "";
    public static ArrayList<Storenamebean> storedetails = new ArrayList<Storenamebean>();
    //String storename;
    String storeid;
    String status;
    String storelatitude = "0";
    String storelongitude = "0";
    int abc;
    private GoogleMap mMap;
    double lat;
    double longitude;
    private GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    String visit_date, username;
    Marker currLocationMarker;
    LatLng latLng;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static int UPDATE_INTERVAL = 1000; // 10 sec
    private static int FATEST_INTERVAL = 500; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters
    Location mLastLocation;
    MarkerOptions markerOptions;
    private SharedPreferences preferences = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gpslocationscreen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        storeid = preferences.getString(CommonString.KEY_STORE_CD, null);
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        //training_mode_cd = getIntent().getStringExtra(CommonString.KEY_TRAINING_MODE_CD);
        _image = (ImageView) findViewById(R.id.image);
        _buttonsave = (Button) findViewById(R.id.savedetails);
        capture_1 = (Button) findViewById(R.id.StoreFront);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //for crate home button
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapview);
        mapFragment.getMapAsync(this);
        Database data1 = new Database(getApplicationContext());
        data1.open();
        storedetails = new ArrayList<>();
        ImageView img = new ImageView(getApplicationContext());
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        geocoder = new Geocoder(this);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
        }
        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();

            createLocationRequest();
        }

        if (!(storelatitude.equals("0")) && !(storelongitude.equals("0"))) {
            int latiti = (int) (Double.parseDouble(storelatitude) * 1000000);
            int longi = (int) (Double.parseDouble(storelongitude) * 1000000);
        }
        _pathforcheck = storeid + "_front.jpg";
        _buttonsave.setOnClickListener(new OnClickListener() {

			/*@Override
            public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		}; {*/

            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (_pathforcheck != null) {
                    if (!(storelatitude.equals("0")) && !(storelongitude.equals("0"))) {
                        lat = Double.parseDouble(storelatitude);
                        longitude = Double.parseDouble(storelongitude);
                    } else {
                        lat = data.getLatitude();
                        longitude = data.getLongitude();
                    }
                    if (ImageUploadActivity.CheckGeotagImage(_pathforcheck)) {
                        status = "Y";
                        Database data = new Database(getApplicationContext());
                        data.open();
                        data.updateOutTime(status, storeid, visit_date);
                        data.updateLaTLONGAndSTATUS(status, storeid, visit_date, lat, longitude);
                        data.InsertStoregeotagging(storeid, lat, longitude, _pathforcheck, status);
                        data.close();
                        if (isNetworkOnline()) {
                            Intent intent2 = new Intent(LocationActivity.this, UploadGeotaggingActivity.class);
                            //intent2.putExtra(CommonString.KEY_TRAINING_MODE_CD, training_mode_cd);
                            startActivity(intent2);
                            LocationActivity.this.finish();
                        }


                    } else {

                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                        builder.setMessage("Please take Store Front image").setCancelable(false).setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();

                    }

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setMessage("Please take Store Front image").setCancelable(false).setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }


        });
        capture_1.setOnClickListener(new ButtonClickHandler());
        locmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        enabled = locmanager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(LocationActivity.this);
            // Setting Dialog Title
            alertDialog.setTitle("GPS IS DISABLED...");
            // Setting Dialog Message
            alertDialog.setMessage("Click ok to enable GPS.");
            // Setting Positive "Yes" Button
            alertDialog.setPositiveButton("YES",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    });
            // Setting Negative "NO" Button
            alertDialog.setNegativeButton("NO",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to invoke NO event
                            dialog.cancel();
                        }
                    });
            // Showing Alert Message
            alertDialog.show();

        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        if (ImageUploadActivity.CheckGeotagImage(_pathforcheck)) {
            capture_1.setBackgroundResource(R.drawable.camera_icon_done);
        }

    }

    protected void startCameraActivity() {
        Log.i("MakeMachine", "startCameraActivity()");
        file = new File(diskpath);
        Uri outputFileUri = Uri.fromFile(file);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(intent, 0);
    }

    public boolean isNetworkOnline() {
        boolean status = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                status = true;
            } else {
                netInfo = cm.getNetworkInfo(1);
                if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED)
                    status = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return status;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("MakeMachine", "resultCode: " + resultCode);
        switch (resultCode) {
            case 0:
                Log.i("MakeMachine", "User cancelled");
                break;
            case -1:
                onPhotoTaken();
                if (ImageUploadActivity.CheckGeotagImage(_pathforcheck)) {
                    capture_1.setBackgroundResource(R.drawable.camera_icon_done);

                }
                break;
        }
    }
    protected void onPhotoTaken() {
        Log.i("MakeMachine", "onPhotoTaken");
        _taken = true;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i("MakeMachine", "onRestoreInstanceState()");
        if (savedInstanceState.getBoolean(this.PHOTO_TAKEN)) {
            onPhotoTaken();
            if (ImageUploadActivity.CheckGeotagImage(_pathforcheck)) {
                capture_1.setBackgroundResource(R.drawable.camera_icon);
            }
        }
        if (ImageUploadActivity.CheckGeotagImage(_pathforcheck)) {
            capture_1.setBackgroundResource(R.drawable.camera_icon);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(this.PHOTO_TAKEN, _taken);
    }


    public class ButtonClickHandler implements OnClickListener {
        LocationActivity loc = new LocationActivity();

        public void onClick(View view) {
            if (!(storelatitude.equals("0")) && !(storelongitude.equals("0"))) {

                if (view.getId() == R.id.StoreFront) {
                    diskpath = CommonString.FILE_PATH + storeid + "_front.jpg";
                    _path = storeid + "_front.jpg";
                    Log.i("MakeMachine", "ButtonClickHandler.onClick()");
                    abc = 03;
                    startCameraActivity();
                }

            } else if (data.getLatitude() == 0 && data.getLongitude() == 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                builder.setMessage("Wait For Geo Location").setCancelable(false).setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

            } else {
                if (view.getId() == R.id.StoreFront) {
                    diskpath = CommonString.FILE_PATH + storeid + "_front.jpg";
                    _path = storeid + "_front.jpg";
                    Log.i("MakeMachine", "ButtonClickHandler.onClick()");
                    abc = 03;
                    startCameraActivity();

                }

            }

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // TODO Auto-generated method stub
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getMaxZoomLevel();
        mMap.getMinZoomLevel();
        mMap.getUiSettings();
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        mMap.animateCamera(CameraUpdateFactory.zoomOut());
        mMap.animateCamera(CameraUpdateFactory.zoomTo(20));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult arg0) {
        // TODO Auto-generated method stub

    }


    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {

            String alladress = "";
            //place marker at current position
            //mGoogleMap.clear();
            latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
                String cityName = addresses.get(0).getAddressLine(0);
                String stateName = addresses.get(0).getAddressLine(1);
                String countryName = addresses.get(0).getAddressLine(2);
                String postal = addresses.get(0).getPostalCode();
                alladress = cityName + stateName + postal;
            } catch (IOException e) {
                e.printStackTrace();
            }
            markerOptions.title(alladress);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            currLocationMarker = mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

        }

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000); //5 seconds
        mLocationRequest.setFastestInterval(3000); //3 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        //mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        try {
            int latiti;
            int longi;
            if (!(storelatitude.equals("0")) && !(storelongitude.equals("0"))) {
                latiti = (int) (Double.parseDouble(storelatitude) * 1000000);
                longi = (int) (Double.parseDouble(storelongitude) * 1000000);
            } else {
                data.setLatitude((mLastLocation.getLatitude()));
                data.setLongitude((mLastLocation.getLongitude()));
            }
        } catch (Exception e) {
            Log.e("LocateMe", "Could not get Geocoder data", e);
        }
    }


    @Override
    public void onConnectionSuspended(int arg0) {
        // TODO Auto-generated method stub

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        mLastLocation = location;
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Snackbar.make(_buttonsave, "This device is not supported.", Snackbar.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    protected void onStart() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        super.onStart();

    }

    protected void onStop() {

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }


    public void onBackPressed() {
        // TODO Auto-generated method stub

        LocationActivity.this.finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        }
        return super.onOptionsItemSelected(item);
    }


}