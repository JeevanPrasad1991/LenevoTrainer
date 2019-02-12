package cpm.com.lenovotraining.dailyentry;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import java.util.HashMap;

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

import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;

public class PostTrainigActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    QuizAdapter adapter;
    RecyclerView recyclerView;
    Database db;
    String topic_cd, isd_cd, store_cd, visit_date, username, training_mode_cd, managed, isd_image;
    ArrayList<QuizQuestionGettersetter> quizQuestionGettersetters = new ArrayList<>();
    ArrayList<QuizQuestionGettersetter> quiz = new ArrayList<>();
    ArrayList<QuizAnwserGetterSetter> answered_list = new ArrayList<>();
    private SharedPreferences preferences = null;
    AddNewEmployeeGetterSetter addNewEmployeeGetterSetter;

    HashMap<AuditChecklistGetterSetter, ArrayList<AuditChecklistGetterSetter>> listDataChild = new HashMap<>();
    ArrayList<AuditChecklistGetterSetter> listDataHeader = new ArrayList<>();
    ArrayList<TrainingTopicGetterSetter> selectedTopicList = new ArrayList<>();
    //location
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
    EmpCdIsdGetterSetter empCdIsdGetterSetter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_trainig);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_quiz);
        db = new Database(getApplicationContext());
        db.open();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        store_cd = preferences.getString(CommonString.KEY_STORE_CD, null);
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        setTitle("Post Training Quiz - " + visit_date);
        topic_cd = getIntent().getStringExtra(CommonString.KEY_TOPIC_CD);
        isd_cd = getIntent().getStringExtra(CommonString.KEY_ISD_CD);
        training_mode_cd = getIntent().getStringExtra(CommonString.KEY_TRAINING_MODE_CD);
        managed = getIntent().getStringExtra(CommonString.KEY_MANAGED);
        isd_image = getIntent().getStringExtra(CommonString.KEY_ISD_IMAGE);
        addNewEmployeeGetterSetter = getIntent().getParcelableExtra(CommonString.KEY_NEW_EMPLOYEE);
        ///get audit data using intent
        listDataChild = (HashMap<AuditChecklistGetterSetter, ArrayList<AuditChecklistGetterSetter>>) getIntent()
                .getSerializableExtra(CommonString.KEY_AUDIT_DATA);
        empCdIsdGetterSetter = (EmpCdIsdGetterSetter) getIntent().getSerializableExtra(CommonString.KEY_NEW_ISD);

        selectedTopicList = (ArrayList<TrainingTopicGetterSetter>) getIntent().getSerializableExtra(CommonString.KEY_TRAINNING_TOPIC);

        quiz = db.getAllQuizData();
        quizQuestionGettersetters = db.getQuizQuestionData();
        //Add dummy Answer data
        for (int i = 0; i < quiz.size(); i++) {
            QuizAnwserGetterSetter answer = new QuizAnwserGetterSetter();
            answer.setIsd_cd(isd_cd);
            answer.setTopic_cd(topic_cd);
            answer.setAnswer("");
            answer.setAnswer_cd("");
            answer.setCheckedId(-1);
            answer.setQuestion_cd(quiz.get(i).getQUESTION_CD().get(0));
            answer.setTraining_mode_cd(training_mode_cd);
            answered_list.add(answer);
        }
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                recyclerView.clearFocus();
                if (SCROLL_STATE_TOUCH_SCROLL == newState) {
                    View currentFocus = getCurrentFocus();
                    if (currentFocus != null) {
                        currentFocus.clearFocus();
                    }
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.clearFocus();
                boolean flag_filled = true;
                for (int j = 0; j < answered_list.size(); j++) {
                    if (answered_list.get(j).getAnswer().equals("") || answered_list.get(j).getAnswer_cd().equals("")) {
                        flag_filled = false;
                        break;
                    }
                }
                if (flag_filled) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PostTrainigActivity.this);
                    builder.setTitle("Parinaam").setMessage("Do you want save quiz data");
                    builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {

                                long mid = 0;

                                //aad new emp insert
                                if (isd_cd.equals("0") && addNewEmployeeGetterSetter != null) {
                                    mid = db.insertNewEmployeeData(addNewEmployeeGetterSetter, store_cd,managed);
                                }

                                ////quiz insert
                                if (answered_list.size() > 0) {
                                    db.insertAnsweredData(answered_list, store_cd, mid, isd_image);
                                }

                                if (selectedTopicList.size() > 0) {
                                    db.insertTrainningTopicMultiData(selectedTopicList, store_cd, isd_cd, mid, visit_date);
                                }

                                //isd skill insert
                                for (AuditChecklistGetterSetter key : listDataChild.keySet()) {
                                    listDataHeader.add(key);
                                    System.out.println(key);
                                }

                                db.insertAuditChecklistWithCategoryData(store_cd, isd_cd, mid, listDataChild, listDataHeader);
//                               db.insertAuditChecklistData(auditChecklistGetterSetters, store_cd, isd_cd, mid);

                                if (empCdIsdGetterSetter != null) {
                                    db.insertNewIsdData(empCdIsdGetterSetter, store_cd);
                                }
                                Intent intent = new Intent(PostTrainigActivity.this, RouteTrainingActivity.class);
                                intent.putExtra(CommonString.KEY_TRAINING_MODE_CD, training_mode_cd);
                                startActivity(intent);
                                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                                finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                } else {
                    Snackbar.make(recyclerView, CommonString.MESSAGE_ANSWER_ALL_QUESTION, Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        adapter = new QuizAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        locmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // First we need to check availability of play services
        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();

            createLocationRequest();
        }

        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

    }

    class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.MyViewHolder> {

        private LayoutInflater inflator = LayoutInflater.from(getApplicationContext());

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflator.inflate(R.layout.child_quiz_layout, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.quiz_no.setText(quiz.get(position).getQUESTION().get(0));
            String question_cd = quiz.get(position).getQUESTION_CD().get(0);
            final ArrayList<QuizQuestionGettersetter> answersList = new ArrayList<>();
            answersList.clear();
            holder.radioGroup.removeAllViews();

            for (int i = 0; i < quizQuestionGettersetters.size(); i++) {
                if (question_cd.equals(quizQuestionGettersetters.get(i).getQUESTION_CD().get(0))) {
                    RadioButton rdbtn = new RadioButton(PostTrainigActivity.this);
                    // rdbtn.setId(i);
                    rdbtn.setText(quizQuestionGettersetters.get(i).getANSWER().get(0));
                    QuizQuestionGettersetter ans = new QuizQuestionGettersetter();
                    ans.setANSWER(quizQuestionGettersetters.get(i).getANSWER().get(0));
                    ans.setANSWER_CD(quizQuestionGettersetters.get(i).getANSWER_CD().get(0));
                    answersList.add(ans);
                    holder.radioGroup.addView(rdbtn);
                    rdbtn.setId(i);
                }

            }

            holder.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    int id = holder.radioGroup.getCheckedRadioButtonId();
                    String question_cd = quizQuestionGettersetters.get(checkedId).getQUESTION_CD().get(0);
                    String ans_cd = quizQuestionGettersetters.get(checkedId).getANSWER_CD().get(0);
                    String ans = quizQuestionGettersetters.get(checkedId).getANSWER().get(0);
                    answered_list.get(position).setTopic_cd(topic_cd);
                    answered_list.get(position).setQuestion_cd(question_cd);
                    answered_list.get(position).setAnswer_cd(ans_cd);
                    answered_list.get(position).setAnswer(ans);
                    answered_list.get(position).setCheckedId(checkedId);

                }
            });

            int id = answered_list.get(position).getCheckedId();
            if (id != -1)
                for (int k = 0; k < holder.radioGroup.getChildCount(); k++) {
                    if (holder.radioGroup.getChildAt(k).getId() == id) {
                        ((RadioButton) holder.radioGroup.getChildAt(k)).setChecked(true);
                        ((RadioButton) holder.radioGroup.getChildAt(k)).setId(id);
                        holder.radioGroup.getChildAt(k);
                        recyclerView.clearFocus();
                    } else {
                        ((RadioButton) holder.radioGroup.getChildAt(k)).setChecked(false);
                        ((RadioButton) holder.radioGroup.getChildAt(k)).setId(id);
                        recyclerView.clearFocus();
                    }
                }

            recyclerView.clearFocus();

        }

        @Override
        public int getItemCount() {
            return quiz.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView quiz_no;
            RadioGroup radioGroup;
            //ImageView icon;

            public MyViewHolder(View itemView) {
                super(itemView);
                quiz_no = (TextView) itemView.findViewById(R.id.tv_quiz_number);
                radioGroup = (RadioGroup) itemView.findViewById(R.id.radiogrp);
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
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
