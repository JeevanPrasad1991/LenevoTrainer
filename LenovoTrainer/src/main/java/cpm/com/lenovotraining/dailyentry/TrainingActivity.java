package cpm.com.lenovotraining.dailyentry;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import cpm.com.lenovotraining.R;
import cpm.com.lenovotraining.constants.CommonString;
import cpm.com.lenovotraining.database.Database;
import cpm.com.lenovotraining.xmlgettersetter.AddNewEmployeeGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.AuditChecklistGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.CoverageBean;
import cpm.com.lenovotraining.xmlgettersetter.EmpCdIsdGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.QuizAnwserGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.QuizQuestionGettersetter;
import cpm.com.lenovotraining.xmlgettersetter.TrainingTopicGetterSetter;

public class TrainingActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    Database db;

    ArrayList<TrainingTopicGetterSetter> trainingTopicGetterSetterArrayList;

    private ArrayAdapter<CharSequence> topicAdapter;
    Spinner spinner_topic;

    String topic_cd = "", isd_cd, training_mode_cd, manned, isd_image;
    String store_cd, visit_date, username;

    private SharedPreferences preferences = null;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 500; // 5 sec
    private static int FATEST_INTERVAL = 100; // 1 sec
    private static int DISPLACEMENT = 5; // 10 meters

    Location mLastLocation;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    // LogCat tag
    private static final String TAG = PostTrainigActivity.class.getSimpleName();

    GoogleApiClient mGoogleApiClient;
    String lat, lon;
    private LocationRequest mLocationRequest;
    private LocationManager locmanager = null;

    AddNewEmployeeGetterSetter addNewEmployeeGetterSetter;

    ArrayList<AuditChecklistGetterSetter> auditChecklistGetterSetters = new ArrayList<>();
    EmpCdIsdGetterSetter empCdIsdGetterSetter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        spinner_topic = (Spinner) findViewById(R.id.spintopic);
        training_mode_cd = getIntent().getStringExtra(CommonString.KEY_TRAINING_MODE_CD);
        manned = getIntent().getStringExtra(CommonString.KEY_MANAGED);
        isd_cd = getIntent().getStringExtra(CommonString.KEY_ISD_CD);
        isd_image = getIntent().getStringExtra(CommonString.KEY_ISD_IMAGE);

        addNewEmployeeGetterSetter = getIntent().getParcelableExtra(CommonString.KEY_NEW_EMPLOYEE);

        auditChecklistGetterSetters = (ArrayList<AuditChecklistGetterSetter>) getIntent().getSerializableExtra(CommonString.KEY_AUDIT_DATA);
        empCdIsdGetterSetter = (EmpCdIsdGetterSetter) getIntent().getSerializableExtra(CommonString.KEY_NEW_ISD);

        db = new Database(getApplicationContext());
        db.open();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        store_cd = preferences.getString(CommonString.KEY_STORE_CD, null);
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        trainingTopicGetterSetterArrayList = db.getTopicData();
        topicAdapter = new ArrayAdapter<CharSequence>(this, R.layout.spinner_custom_item);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (training_mode_cd.equals("2")) {
            fab.setImageResource(R.drawable.save_icon);
        }


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (topic_cd.equals("")) {
                    Snackbar.make(spinner_topic, CommonString.MESSAGE_SELECT_TRAINING_TOPIC, Snackbar.LENGTH_SHORT).show();
                } else {

                    if (training_mode_cd.equals("1")) {
                        Intent in = new Intent(getApplicationContext(), PostTrainigActivity.class);
                        in.putExtra(CommonString.KEY_ISD_CD, isd_cd);
                        in.putExtra(CommonString.KEY_TOPIC_CD, topic_cd);
                        in.putExtra(CommonString.KEY_TRAINING_MODE_CD, training_mode_cd);
                        in.putExtra(CommonString.KEY_MANAGED, manned);
                        in.putExtra(CommonString.KEY_ISD_IMAGE, isd_image);
                        if (isd_cd.equals("0") && addNewEmployeeGetterSetter != null)
                            in.putExtra(CommonString.KEY_NEW_EMPLOYEE, addNewEmployeeGetterSetter);

                        in.putExtra(CommonString.KEY_AUDIT_DATA, auditChecklistGetterSetters);
                        in.putExtra(CommonString.KEY_NEW_ISD, empCdIsdGetterSetter);
                        startActivity(in);
                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

                    } else {
                        //getMid();
                        db.updateCoverageStoreOutTime(store_cd, visit_date, getCurrentTime(), CommonString.KEY_VALID);
                        ArrayList<QuizAnwserGetterSetter> answered_list = new ArrayList<>();
                        QuizAnwserGetterSetter ans = new QuizAnwserGetterSetter();
                        ans.setIsd_cd(isd_cd);
                        ans.setTopic_cd(topic_cd);
                        ans.setTraining_mode_cd(training_mode_cd);
                        ans.setAnswer_cd("0");
                        ans.setAnswer("");
                        ans.setQuestion_cd("0");
                        answered_list.add(ans);
                        long mid = 0;
                        if (isd_cd.equals("0") && addNewEmployeeGetterSetter != null)
                            mid = db.insertNewEmployeeData(addNewEmployeeGetterSetter, store_cd);
                        db.insertAnsweredData(answered_list, store_cd, mid, isd_image);
                        db.insertAuditChecklistData(auditChecklistGetterSetters, store_cd, isd_cd, mid);
                        if (empCdIsdGetterSetter != null)
                            db.insertNewIsdData(empCdIsdGetterSetter, store_cd);
                    }

                    finish();
                }

            }
        });

        topicAdapter.add("Select");

        for (int i = 0; i < trainingTopicGetterSetterArrayList.size(); i++) {
            topicAdapter.add(trainingTopicGetterSetterArrayList.get(i).getTOPIC().get(0));
        }
        spinner_topic.setAdapter(topicAdapter);

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


        spinner_topic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {

                    topic_cd = trainingTopicGetterSetterArrayList.get(position - 1).getTOPIC_CD().get(0);

                } else {
                    topic_cd = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public long checkMid() {
        return db.CheckMid(visit_date, store_cd);
    }

    public long getMid() {

        long mid = 0;

        mid = checkMid();

        if (mid == 0) {
            CoverageBean cdata = new CoverageBean();
            cdata.setStoreId(store_cd);
            cdata.setVisitDate(visit_date);
            cdata.setUserId(username);
            cdata.setInTime(getCurrentTime());
            cdata.setOutTime(getCurrentTime());
            cdata.setReason("");
            cdata.setReasonid("0");
            if (lat == null || lat.equals("")) {
                lat = "0.0";
            }
            cdata.setLatitude(lat);
            if (lon == null || lon.equals("")) {
                lon = "0.0";
            }
            cdata.setLongitude(lon);
            cdata.setStatus(CommonString.KEY_VALID);
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
                lat = String.valueOf(mLastLocation.getLatitude());
                lon = String.valueOf(mLastLocation.getLongitude());
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
    protected void onResume() {
        super.onResume();

        checkPlayServices();

        // Resuming the periodic location updates
       /* if (mGoogleApiClient.isConnected() ) {
            startLocationUpdates();
        }*/
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
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
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

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

}
