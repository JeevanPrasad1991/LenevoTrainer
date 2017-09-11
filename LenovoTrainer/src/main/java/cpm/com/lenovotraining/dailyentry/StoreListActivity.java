package cpm.com.lenovotraining.dailyentry;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cpm.com.lenovotraining.R;
import cpm.com.lenovotraining.constants.CommonString;
import cpm.com.lenovotraining.database.Database;
import cpm.com.lenovotraining.download.CompleteDownloadActivity;
import cpm.com.lenovotraining.lenovotrainer.LoginActivity;
import cpm.com.lenovotraining.upload.CheckoutNUpload;
import cpm.com.lenovotraining.xmlgettersetter.CoverageBean;
import cpm.com.lenovotraining.xmlgettersetter.JCPGetterSetter;

public class StoreListActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    Database db;
    ArrayList<JCPGetterSetter> storedataList = new ArrayList<>();
    ArrayList<CoverageBean> coverageList = new ArrayList<>();

    MyItemRecyclerViewAdapter myItemRecyclerViewAdapter;

    RecyclerView rec_store_data;

    LinearLayout linearLayout;

    private SharedPreferences preferences = null;
    private String user_name, user_type, visit_date, store_cd;

    private SharedPreferences.Editor editor = null;

    FloatingActionButton fab;

    Dialog dialog;

    String coverage_status = "";

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 500; // 5 sec
    private static int FATEST_INTERVAL = 100; // 1 sec
    private static int DISPLACEMENT = 5; // 10 meters

    Location mLastLocation;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    // LogCat tag
    private static final String TAG = PostTrainigActivity.class.getSimpleName();

    GoogleApiClient mGoogleApiClient;
    Double lat, lon;
    private LocationRequest mLocationRequest;
    private LocationManager locmanager = null;

    String training_mode_cd, manned, trainning_mode = "";
    //private Data data;
    private GoogleApiClient googleApiClient;
    private static final int REQUEST_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rec_store_data = (RecyclerView) findViewById(R.id.rec_store_data);
        linearLayout = (LinearLayout) findViewById(R.id.no_data_lay);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        user_name = preferences.getString(CommonString.KEY_USERNAME, null);
        user_type = preferences.getString(CommonString.KEY_USER_TYPE, null);
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        // checkgpsEnableDevice();
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Download data

                Handler h = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what != 1) { // code if not connected
                            Snackbar.make(rec_store_data, CommonString.NO_INTERNET_CONNECTION, Snackbar.LENGTH_SHORT).show();
                        } else { // code if connected
                            // showProgress(true);
                            if (db.isCoverageDataFilled(visit_date)) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(StoreListActivity.this);
                                builder.setTitle("Parinaam");
                                builder.setMessage("Please Upload Previous Data First")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                                Intent startUpload = new Intent(StoreListActivity.this, CheckoutNUpload.class);
                                                startActivity(startUpload);
                                                finish();

                                            }
                                        });

                                AlertDialog alert = builder.create();
                                alert.show();

                            } else {

                                Intent startDownload = new Intent(getApplicationContext(), CompleteDownloadActivity.class);
                                startActivity(startDownload);
                            }
                        }
                    }
                };

                isNetworkAvailable(h, 5000);
            }
        });

        locmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // First we need to check availability of play services
        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();
            createLocationRequest();
        }

        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
        store_cd = preferences.getString(CommonString.KEY_STORE_CD, "");
        db = new Database(getApplicationContext());
        db.open();
        storedataList = db.getStoreData(visit_date);
        coverageList = db.getCoverageData(visit_date);
        if (storedataList.size() > 0) {
            rec_store_data.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            myItemRecyclerViewAdapter = new MyItemRecyclerViewAdapter(storedataList);
            rec_store_data.setAdapter(myItemRecyclerViewAdapter);
            rec_store_data.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.GONE);
            fab.setVisibility(View.GONE);
        }

    }


    public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {
        private final List<JCPGetterSetter> mValues;

        public MyItemRecyclerViewAdapter(List<JCPGetterSetter> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_item_layout, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.mItem = mValues.get(position);
            holder.tv_store_name.setText(mValues.get(position).getSTORENAME().get(0));
            holder.tv_city.setText(mValues.get(position).getCITY().get(0));
            holder.tv_store_type.setText(mValues.get(position).getSTORETYPE().get(0));
            final String upload_status = storedataList.get(position).getUPLOAD_STATUS().get(0);
            final String store_cds = storedataList.get(position).getSTORE_CD().get(0);
            final String checkout_status = storedataList.get(position).getCHECKOUT_STATUS().get(0);
            String coverage_status = "";

            for (int k = 0; k < coverageList.size(); k++) {
                if (coverageList.get(k).getStoreId().equals(store_cds)) {
                    coverage_status = coverageList.get(k).getStatus();
                    break;
                }

            }

            if (upload_status.equals(CommonString.KEY_U)) {
                holder.img.setBackgroundResource(R.drawable.tick_u);
                holder.img.setVisibility(View.VISIBLE);
                holder.btn_checkout.setVisibility(View.GONE);
            } else if (!coverage_status.equals("") && coverage_status.equals(CommonString.KEY_VALID)) {
                holder.btn_checkout.setVisibility(View.VISIBLE);
                holder.img.setVisibility(View.INVISIBLE);
            } else if ((checkout_status.equals(CommonString.KEY_C))) {
                holder.img.setBackgroundResource(R.drawable.tick_c);
                holder.img.setVisibility(View.VISIBLE);
                holder.btn_checkout.setVisibility(View.GONE);

            } else if (!coverage_status.equals("") && coverage_status.equals(CommonString.STORE_STATUS_LEAVE)) {
                holder.img.setBackgroundResource(R.drawable.leave_tick);
                holder.img.setVisibility(View.VISIBLE);
                holder.btn_checkout.setVisibility(View.GONE);
            } else {
                holder.btn_checkout.setVisibility(View.GONE);
                if (!store_cd.equals("") && store_cd.equals(store_cds)) {
                    holder.img.setVisibility(View.VISIBLE);
                    holder.img.setBackgroundResource(R.drawable.checkin_ico);
                } else {
                    holder.img.setVisibility(View.INVISIBLE);
                }
            }

            holder.parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String store_cdss = storedataList.get(position).getSTORE_CD().get(0);
                    final String upload_status = storedataList.get(position).getUPLOAD_STATUS().get(0);
                    final String checkoutstatus = storedataList.get(position).getCHECKOUT_STATUS().get(0);
                    final double storelat = Double.valueOf(storedataList.get(position).getLAT().get(0));
                    final double storelongt = Double.valueOf(storedataList.get(position).getLONG().get(0));
                    final String storegeoT = storedataList.get(position).getGEOTAG().get(0);

                    training_mode_cd = storedataList.get(position).getTMODE_CD().get(0);
                    manned = storedataList.get(position).getMANAGED().get(0);
                    trainning_mode = storedataList.get(position).getTRAINING_MODE().get(0);
                    String coverage_status = "";

                    for (int k = 0; k < coverageList.size(); k++) {
                        if (coverageList.get(k).getStoreId().equals(store_cds)) {
                            coverage_status = coverageList.get(k).getStatus();
                            break;
                        }
                    }


                    if (upload_status.equalsIgnoreCase(CommonString.KEY_U)) {
                        Snackbar.make(rec_store_data, CommonString.MESSAGE_DATA_ALREADY_UPLOADED, Snackbar.LENGTH_SHORT).setAction("Action", null).show();

                    } else if (!coverage_status.equals("") && coverage_status.equals(CommonString.STORE_STATUS_LEAVE)) {
                        Snackbar.make(rec_store_data, CommonString.MESSAGE_SOTORE_ALREADY_CLOSED, Snackbar.LENGTH_SHORT).setAction("Action", null).show();

                    } else if (!checkoutstatus.equals("") && checkoutstatus.equals(CommonString.KEY_C)) {
                        Snackbar.make(rec_store_data, CommonString.MESSAGE_STORE_ALREADY_CHECKED_OUT, Snackbar.LENGTH_SHORT).setAction("Action", null).show();

                    } else if (!store_cd.equals("") && !store_cdss.equals(store_cd)) {
                        Snackbar.make(rec_store_data, CommonString.MESSAGE_FIRST_CHECKOUT, Snackbar.LENGTH_SHORT).setAction("Action", null).show();

                    } else {
                        dialog = new Dialog(StoreListActivity.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.dialog_layout);
                        RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.radiogrpvisit);
                        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                // find which radio button is selected
                                if (checkedId == R.id.yes) {
                                    dialog.cancel();
                                    editor = preferences.edit();
                                    editor.putString(CommonString.KEY_STORE_CD, store_cdss);
                                    editor.putString(CommonString.KEY_TRAINING_MODE, trainning_mode);
                                    editor.putString(CommonString.KEY_TRAINING_MODE_CD, training_mode_cd);
                                    editor.putString(CommonString.KEY_MANAGED, manned);
                                    editor.commit();
                                    getMid(store_cdss);
                                    if (checkgpsEnableDevice()) {
                                        if (training_mode_cd.equals("1")) {
                                            if (storelat != 0 && storelongt != 0 && lat != 0.0 && lon != 0.0) {
                                                int distance = distFrom(storelat, storelongt, lat, lon);
                                                if (distance <= 100) {
                                                    Intent in = new Intent(getApplicationContext(), RouteTrainingActivity.class);
                                                   /* in.putExtra(CommonString.KEY_TRAINING_MODE_CD, training_mode_cd);
                                                    in.putExtra(CommonString.KEY_MANAGED, manned);*/
                                                    startActivity(in);
                                                    overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                                                } else {
                                                    Snackbar.make(rec_store_data, "Store distance is not less 100 meter ,So can't able to come in store", Snackbar.LENGTH_LONG).show();
                                                }
                                            } else {
                                                Intent in = new Intent(getApplicationContext(), RouteTrainingActivity.class);
                                               // in.putExtra(CommonString.KEY_TRAINING_MODE_CD, training_mode_cd);
                                               // in.putExtra(CommonString.KEY_MANAGED, manned);
                                                startActivity(in);
                                                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                                            }
                                        } else {
                                            Intent in = new Intent(getApplicationContext(), RouteTrainingActivity.class);
                                           // in.putExtra(CommonString.KEY_TRAINING_MODE_CD, training_mode_cd);
                                           // in.putExtra(CommonString.KEY_MANAGED, manned);
                                            startActivity(in);
                                            overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                                        }

                                    }

                                } else if (checkedId == R.id.no) {
                                    dialog.cancel();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(StoreListActivity.this);
                                    builder.setMessage(CommonString.DATA_DELETE_ALERT_MESSAGE)
                                            .setCancelable(false)
                                            .setPositiveButton("Yes",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            UpdateData(store_cdss);
                                                            SharedPreferences.Editor editor = preferences.edit();
                                                            editor.putString(CommonString.KEY_STORE_CD, "");
                                                            editor.commit();
                                                            Intent in = new Intent(getApplicationContext(), NonWorkingReasonActivity.class);
                                                            in.putExtra(CommonString.KEY_STORE_CD, store_cdss);
                                                            in.putExtra(CommonString.KEY_TRAINING_MODE_CD, training_mode_cd);
                                                            in.putExtra(CommonString.KEY_MANAGED, manned);
                                                            startActivity(in);

                                                        }
                                                    })
                                            .setNegativeButton("No",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog,
                                                                            int id) {


                                                            dialog.cancel();
                                                        }
                                                    });
                                    AlertDialog alert = builder.create();

                                    alert.show();
                                }

                            }
                        });

                        dialog.show();
                    }

                }
            });

            holder.btn_checkout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            StoreListActivity.this);
                    builder.setMessage("Are you sure you want to Checkout")
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog, int id) {

                                            Handler h = new Handler() {
                                                @Override
                                                public void handleMessage(Message msg) {

                                                    if (msg.what != 1) { // code if not connected
                                                        Snackbar.make(rec_store_data, CommonString.NO_INTERNET_CONNECTION, Snackbar.LENGTH_SHORT).show();
                                                    } else { // code if connected

                                                        editor = preferences.edit();
                                                        editor.putString(CommonString.KEY_STORE_CD, storedataList.get(position).getSTORE_CD().get(0));
                                                        editor.commit();
                                                        Intent i = new Intent(StoreListActivity.this, CheckOutStoreActivity.class);
                                                        startActivity(i);
                                                    }
                                                }
                                            };

                                            isNetworkAvailable(h, 5000);

                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });


            if (mValues.get(position).getTRAINING_MODE().get(0).equalsIgnoreCase("Remote")) {
                holder.img_tick.setVisibility(View.VISIBLE);
            } else {
                holder.img_tick.setVisibility(View.GONE);
            }

        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final LinearLayout parentLayout;
            public final TextView tv_store_name;
            public final TextView tv_city;
            public final TextView tv_store_type;
            public final ImageView img;
            public final ImageView img_tick;
            public final Button btn_checkout;
            public JCPGetterSetter mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;

                tv_store_name = (TextView) view.findViewById(R.id.tv_store_name);
                tv_city = (TextView) view.findViewById(R.id.tv_city);
                tv_store_type = (TextView) view.findViewById(R.id.tv_store_type);
                img = (ImageView) view.findViewById(R.id.img);
                img_tick = (ImageView) view.findViewById(R.id.img_tick);
                btn_checkout = (Button) view.findViewById(R.id.btn_checkout);
                parentLayout = (LinearLayout) view.findViewById(R.id.parent_layout);
            }

        }

    }

    public static void isNetworkAvailable(final Handler handler, final int timeout) {
        // ask fo message '0' (not connected) or '1' (connected) on 'handler'
        // the answer must be send before before within the 'timeout' (in milliseconds)
        new Thread() {
            private boolean responded = false;

            @Override
            public void run() {
                // set 'responded' to TRUE if is able to connect with google mobile (responds fast)
                new Thread() {
                    @Override
                    public void run() {
                        HttpGet requestForTest = new HttpGet("http://m.google.com");
                        try {
                            new DefaultHttpClient().execute(requestForTest); // can last...
                            responded = true;
                        } catch (Exception e) {
                        }
                    }
                }.start();

                try {
                    int waited = 0;
                    while (!responded && (waited < timeout)) {
                        sleep(100);
                        if (!responded) {
                            waited += 100;
                        }
                    }
                } catch (InterruptedException e) {
                } // do nothing
                finally {
                    if (!responded) {
                        handler.sendEmptyMessage(0);
                    } else {
                        handler.sendEmptyMessage(1);
                    }
                }
            }
        }.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        }

        return super.onOptionsItemSelected(item);
    }

    public void UpdateData(String storeCd) {

        db.open();
        db.deleteSpecificStoreData(storeCd);


        db.updateStoreStatusOnCheckout(storeCd, storedataList.get(0).getVISIT_DATE().get(0),
                "N");


    }

    public long checkMid() {
        return db.CheckMid(visit_date, store_cd);
    }

    public long getMid(String store_cd) {

        long mid = 0;

        mid = checkMid();

        if (mid == 0) {
            CoverageBean cdata = new CoverageBean();
            cdata.setStoreId(store_cd);
            cdata.setVisitDate(visit_date);
            cdata.setUserId(user_name);
            cdata.setInTime(getCurrentTime());
            cdata.setOutTime(getCurrentTime());
            cdata.setReason("");
            cdata.setReasonid("0");
            if (lat == null || lat.equals("")) {
                lat = 0.0;
            }
            cdata.setLatitude(String.valueOf(lat));
            if (lon == null || lon.equals("")) {
                lon = 0.0;
            }
            cdata.setLongitude(String.valueOf(lat));
            cdata.setStatus("N");
            cdata.setTraining_mode_cd(training_mode_cd);
            cdata.setManaged(manned);
            mid = db.InsertCoverageData(cdata);

        }
        return mid;
    }

    public String getCurrentTime() {
        Calendar m_cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String cdate = formatter.format(m_cal.getTime());
        return cdate;

    }

    @Override
    public void onConnected(Bundle bundle) {

        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (mLastLocation != null) {
                lat = mLastLocation.getLatitude();
                lon = mLastLocation.getLongitude();
            }

        }

        startLocationUpdates();

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
    }

    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }
    @Override
    public void onLocationChanged(Location location) {

    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    /**
     * Method to verify google play services on the device
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Starting the location updates
     */
    protected void startLocationUpdates() {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        }
    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    /**
     * Creating google api client object
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
    }

    /**
     * Creating location request object
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }
    private boolean checkgpsEnableDevice() {
        boolean flag = true;
        googleApiClient = null;
        if (!hasGPSDevice(StoreListActivity.this)) {
            Toast.makeText(StoreListActivity.this, "Gps not Supported", Toast.LENGTH_SHORT).show();
        }
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(StoreListActivity.this)) {
            enableLoc();
            flag = false;
        } else if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(StoreListActivity.this)) {
            flag = true;
        }
        return flag;
    }

    private boolean hasGPSDevice(Context context) {
        final LocationManager mgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (mgr == null)
            return false;
        final List<String> providers = mgr.getAllProviders();
        if (providers == null)
            return false;
        return providers.contains(LocationManager.GPS_PROVIDER);
    }
    private void enableLoc() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {
                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            googleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {
                            // googleApiClient.connect();
                            Log.d("Location error", "Location error " + connectionResult.getErrorCode());
                        }
                    }).build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(StoreListActivity.this, REQUEST_LOCATION);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                    }
                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_CANCELED: {
                        googleApiClient = null;
                        // finish();
                    }

                    default: {
                        break;
                    }
                }
                break;
        }

    }

    public static int distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        int dist = (int) (earthRadius * c);

        return dist;
    }

}
