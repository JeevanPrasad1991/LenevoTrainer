package cpm.com.lenovotraining.lenovotrainer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cpm.com.lenovotraining.R;
import cpm.com.lenovotraining.autoupdate.AutoupdateActivity;
import cpm.com.lenovotraining.bean.TableBean;
import cpm.com.lenovotraining.constants.CommonString;
import cpm.com.lenovotraining.xmlHandler.XMLHandlers;
import cpm.com.lenovotraining.xmlgettersetter.AttendanceReasonGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.FailureGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.LoginGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.QuestionGetterSetter;


public class LoginActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 500; // 5 sec
    private static int FATEST_INTERVAL = 100; // 1 sec
    private static int DISPLACEMENT = 5; // 10 meters

    Location mLastLocation;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    // LogCat tag
    private static final String TAG = LoginActivity.class.getSimpleName();

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    private LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

    TextView tv_version;
    String app_ver;
    String lat = "0.0";
    String lon = "0.0";

    private SharedPreferences preferences = null;
    private SharedPreferences.Editor editor = null;
    private String p_username, p_password, user_id, password;

    private boolean isChecked;

    private LocationManager locmanager = null;

    private Intent intent = null;

    private int versionCode;
    int eventType;
    LoginGetterSetter lgs = null;

    private QuestionGetterSetter questionGetterSetter;

    AttendanceReasonGetterSetter attendanceReasonGetterSetter;

    String right_answer, rigth_answer_cd = "", qns_cd, ans_cd;

    ProgressBar progressBar;

    private Dialog dialog;
    private ProgressBar pb;
    private TextView percentage, message;
    //private Data data;
    private GoogleApiClient googleApiClient;
    private static final int REQUEST_LOCATION = 1;
    AttendanceReasonGetterSetter attendanceReason = new AttendanceReasonGetterSetter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        tv_version = (TextView) findViewById(R.id.tv_version_code);
        //populateAutoComplete();
        mPasswordView = (EditText) findViewById(R.id.password);
       /* mEmailView.setText("testtrainer");
        mPasswordView.setText("cpm123");*/
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        p_username = preferences.getString(CommonString.KEY_USERNAME, null);
        p_password = preferences.getString(CommonString.KEY_PASSWORD, null);
        isChecked = preferences.getBoolean(CommonString.KEY_REMEMBER, false);

        try {
            app_ver = String.valueOf(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);

            tv_version.setText("Version " + app_ver);
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        // Check if enabled and if not send user to the GSP settings
        // Better solution would be to display a dialog and suggesting to
        // go to the settings

        checkgpsEnableDevice();

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


        // Create a Folder for Images
        File file = new File(Environment.getExternalStorageDirectory(),
                "Lenovo_Trainer_Images");
        if (!file.isDirectory()) {
            file.mkdir();
        }

        //Login
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mEmailView.getWindowToken(), 0);
                if (checkgpsEnableDevice()) {
                    attemptLogin();
                } else {
                    Toast.makeText(LoginActivity.this, "Please Enable Gps", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        user_id = mEmailView.getText().toString();
        password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(user_id)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } /*else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }*/

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {

            p_username = preferences.getString(CommonString.KEY_USERNAME,
                    null);
            p_password = preferences.getString(CommonString.KEY_PASSWORD,
                    null);

            boolean previous_user_flag = false;
            if (p_username != null && p_password != null) {

                if (user_id.equals(p_username) && password.equals(p_password)) {
                    previous_user_flag = true;
                }
            } else {
                previous_user_flag = true;
            }

            if (previous_user_flag) {
                // Show a progress spinner, and kick off a background task to
                // perform the user login attempt.

                Handler h = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {

                        if (msg.what != 1) { // code if not connected
                            mProgressView.setVisibility(View.INVISIBLE);
                            Snackbar.make(mEmailView, CommonString.NO_INTERNET_CONNECTION, Snackbar.LENGTH_SHORT).show();

                        } else { // code if connected
                            // showProgress(true);
                            mProgressView.setVisibility(View.INVISIBLE);
                            new AuthenticateTask().execute();
                        }
                    }
                };


                mProgressView.setVisibility(View.VISIBLE);
                isNetworkAvailable(h, 5000);
/*
                if(isOnline()){

                    showProgress(true);
                    new AuthenticateTask().execute();
                }
                else{

                    Snackbar.make(mEmailView, CommonString.NO_INTERNET_CONNECTION,Snackbar.LENGTH_SHORT).show();
                }*/


            } else {
                Snackbar.make(mEmailView, CommonString.UERNAME_OR_PASSWORD_IS_WRONG, Snackbar.LENGTH_SHORT).show();
            }


        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 0;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

         /*   mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });*/
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            //  mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
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


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    public boolean CheckNetAvailability() {

        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .getState() == NetworkInfo.State.CONNECTED
                || connectivityManager.getNetworkInfo(
                ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            // we are connected to a network
            connected = true;
        }
        return connected;
    }

    private void showToast(String message) {
        Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
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
    private class AuthenticateTask extends AsyncTask<Void, Void, String> {
        private ProgressDialog dialog = null;
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog = new ProgressDialog(LoginActivity.this);
            dialog.setTitle("Login");
            dialog.setMessage("Authenticating....");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub

            try {
                String resultHttp = "";
                versionCode = getPackageManager().getPackageInfo(
                        getPackageName(), 0).versionCode;

                if (lat == null || lat.equals("")) {
                    lat = "0.0";
                }

                if (lon == null || lon.equals("")) {
                    lon = "0.0";
                }

                String userauth_xml = "[DATA]" + "[USER_DATA][USER_ID]"
                        + user_id + "[/USER_ID]" + "[Password]" + password
                        + "[/Password]" + "[IN_TIME]" + getCurrentTime()
                        + "[/IN_TIME]" + "[LATITUDE]" + lat
                        + "[/LATITUDE]" + "[LONGITUDE]" + lon
                        + "[/LONGITUDE]" + "[APP_VERSION]" + app_ver
                        + "[/APP_VERSION]" + "[ATT_MODE]OnLine[/ATT_MODE]"
                        + "[/USER_DATA][/DATA]";

                SoapObject request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_LOGIN);

                request.addProperty("onXML", userauth_xml);

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                        SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                HttpTransportSE androidHttpTransport = new HttpTransportSE(
                        CommonString.URL);

                androidHttpTransport.call(CommonString.SOAP_ACTION_LOGIN,
                        envelope);

                Object result = (Object) envelope.getResponse();

                if (result.toString().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                    return CommonString.KEY_FAILURE;

                } else if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                    return CommonString.KEY_FALSE;

                } else if (result.toString().equalsIgnoreCase(CommonString.KEY_CHANGED)) {
                    return CommonString.KEY_CHANGED;
                } else {

                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(true);
                    XmlPullParser xpp = factory.newPullParser();
                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    FailureGetterSetter failureGetterSetter = XMLHandlers
                            .failureXMLHandler(xpp, eventType);

                    if (failureGetterSetter.getStatus().equalsIgnoreCase(
                            CommonString.KEY_FAILURE)) {


                        return CommonString.KEY_FAILURE;

                    } else {

                        try {
                            // For String source

                            xpp.setInput(new StringReader(result.toString()));
                            xpp.next();
                            eventType = xpp.getEventType();
                            lgs = XMLHandlers.loginXMLHandler(xpp, eventType);

                        } catch (XmlPullParserException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        // PUT IN PREFERENCES
                        editor.putString(CommonString.KEY_USERNAME, user_id);
                        editor.putString(CommonString.KEY_PASSWORD, password);
                        editor.putString(CommonString.KEY_VERSION,
                                lgs.getVERSION());

                        editor.putString(CommonString.KEY_PATH, lgs.getPATH());

                        editor.putString(CommonString.KEY_DATE, lgs.getDATE());
                        //temp hardcoded
                        //editor.putString(CommonString.KEY_DATE, "09/17/2016");

                        editor.putString(CommonString.KEY_USER_TYPE, lgs.getRIGHTNAME());

                        editor.commit();



                    }

                    request = new SoapObject(CommonString.NAMESPACE,
                            CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                    request.addProperty("UserName", user_id);
                    request.addProperty("Type", "TODAY_QUESTION");

                    envelope = new SoapSerializationEnvelope(
                            SoapEnvelope.VER11);
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(request);

                    androidHttpTransport = new HttpTransportSE(
                            CommonString.URL);

                    androidHttpTransport.call(CommonString.SOAP_ACTION_UNIVERSAL,
                            envelope);
                    result = (Object) envelope.getResponse();

                    if (result.toString() != null) {

                        xpp.setInput(new StringReader(result.toString()));
                        xpp.next();
                        eventType = xpp.getEventType();

                        questionGetterSetter = XMLHandlers.QuestionXMLHandler(xpp, eventType);

                        if (questionGetterSetter.getQuestion_cd().size() > 0) {
                            resultHttp = CommonString.KEY_SUCCESS;
                            String qnsTable = questionGetterSetter.getTable_question_today();
                            TableBean.setTable_todays_quiz(qnsTable);

                        }

                    }

                    //Attendance download
                    request = new SoapObject(CommonString.NAMESPACE,
                            CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                    request.addProperty("UserName", user_id);
                    request.addProperty("Type", "NON_WORKING_REASON_ATTENDANCE");

                    envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(request);

                    androidHttpTransport = new HttpTransportSE(CommonString.URL);

                    androidHttpTransport.call(CommonString.SOAP_ACTION_UNIVERSAL,
                            envelope);
                    result = (Object) envelope.getResponse();

                    if (result.toString() != null) {

                        xpp.setInput(new StringReader(result.toString()));
                        xpp.next();
                        eventType = xpp.getEventType();

                        attendanceReasonGetterSetter = XMLHandlers.AttendanceXMLHandler(xpp, eventType);

                        if (attendanceReasonGetterSetter.getREASON_CD().size() > 0) {
                            resultHttp = CommonString.KEY_SUCCESS;
                            String attTable = attendanceReasonGetterSetter.getTable_reason_attendance();
                            TableBean.setTable_reason_attendance(attTable);

                        } else {
                            return CommonString.KEY_SUCCESS;
                        }

                    }

                    return resultHttp;
                }



            } catch (MalformedURLException e) {

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        showMessage(CommonString.MESSAGE_EXCEPTION);
                    }
                });


            } catch (IOException e) {

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        showMessage(CommonString.MESSAGE_SOCKETEXCEPTION);
                    }
                });

            } catch (Exception e) {

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        showMessage(CommonString.MESSAGE_EXCEPTION);
                    }
                });
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            dialog.dismiss();
            if (result.equals(CommonString.KEY_SUCCESS)) {
                if (preferences.getString(CommonString.KEY_VERSION, "").equals(Integer.toString(versionCode))) {
                    String visit_date = preferences.getString(CommonString.KEY_DATE, "");
                    if (questionGetterSetter.getAnswer_cd().size() > 0 && questionGetterSetter.getStatus().get(0).equals("N")
                            && !preferences.getBoolean(CommonString.KEY_IS_QUIZ_DONE + visit_date, false)) {

                        for (int i = 0; i < questionGetterSetter.getRight_answer().size(); i++) {

                            if (questionGetterSetter.getRight_answer().get(i).equals("1")) {
                                right_answer = questionGetterSetter.getAnswer().get(i);
                                rigth_answer_cd = questionGetterSetter.getAnswer_cd().get(i);
                                break;
                            }

                        }

                        final AnswerData answerData = new AnswerData();

                        final Dialog dialog_quiz = new Dialog(LoginActivity.this);
                        dialog_quiz.setTitle("Todays Question");
                        dialog_quiz.setCancelable(false);
                        dialog_quiz.setContentView(R.layout.todays_question_layout);

                        ((TextView) dialog_quiz.findViewById(R.id.tv_qns)).setText(questionGetterSetter.getQuestion().get(0));
                        Button btnsubmit = (Button) dialog_quiz.findViewById(R.id.btnsubmit);
                        RadioGroup radioGroup = (RadioGroup) dialog_quiz.findViewById(R.id.radiogrp);

                        for (int i = 0; i < questionGetterSetter.getAnswer_cd().size(); i++) {
                            RadioButton rdbtn = new RadioButton(LoginActivity.this);
                            rdbtn.setId(i);
                            rdbtn.setText(questionGetterSetter.getAnswer().get(i));
                            //ll.addView(rdbtn);
                            radioGroup.addView(rdbtn);
                        }


                        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {

                                answerData.setAnswer_id(questionGetterSetter.getAnswer_cd().get(checkedId));
                                answerData.setRight_answer(questionGetterSetter.getRight_answer().get(checkedId));
                            }
                        });
                        btnsubmit.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                if (answerData.getAnswer_id() == null || answerData.getAnswer_id().equals("")) {
                                    Snackbar.make(mEmailView, "First select an answer", Snackbar.LENGTH_SHORT).show();
                                } else {
                                    dialog_quiz.cancel();
                                    dialog.cancel();

                                    String ansisright = "";

                                    if (answerData.getRight_answer().equals("1")) {
                                        ansisright = "Your Answer Is Right!";
                                    } else {
                                        ansisright = "Your Answer is Wrong! Right Answer Is :- " + right_answer;
                                    }

                                    final Dialog ans_dialog = new Dialog(LoginActivity.this);
                                    ans_dialog.setTitle("Answer");
                                    ans_dialog.setCancelable(false);
                                    //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    ans_dialog.setContentView(R.layout.show_answer_layout);
                                    ((TextView) ans_dialog.findViewById(R.id.tv_ans)).setText(ansisright);
                                    Button btnok = (Button) ans_dialog.findViewById(R.id.btnsubmit);
                                    btnok.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            answerData.setQuestion_id(questionGetterSetter.getQuestion_cd().get(0));
                                            answerData.setUsername(user_id);
                                            answerData.setVisit_date(lgs.getDATE());

                                            if (CheckNetAvailability()) {
                                                ans_dialog.cancel();

                                                new AnswerTodayTask().execute(answerData);

                                            } else {
                                                showToast("No internet connection");
                                            }

                                        }
                                    });

                                    ans_dialog.show();

                                }

                            }
                        });

                        dialog_quiz.show();
                    } else {

                        if (attendanceReasonGetterSetter.getREASON_CD().size() > 0) {

                            boolean attendance_done_flag = false, entry_allow_flag = true;
                            String reason = "";
                            for (int i = 0; i < attendanceReasonGetterSetter.getREASON_CD().size(); i++) {

                                if (attendanceReasonGetterSetter.getSTATUS().get(i).equals("1")) {
                                    attendance_done_flag = true;
                                    if (attendanceReasonGetterSetter.getENTRY_ALLOW().get(i).equals("0")) {
                                        entry_allow_flag = false;
                                        reason = attendanceReasonGetterSetter.getREASON().get(i);
                                        break;
                                    }
                                }
                            }

                            if (attendance_done_flag) {

                                if (entry_allow_flag) {

                                    intent = new Intent(getBaseContext(), MainActivity.class);
                                    startActivity(intent);

                                    overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

                                    finish();

                                } else {

                                    showAttendanceMessage(reason);
                                }

                            } else {

                                showAttendanceDialog();
                            }

                        } else {

                            intent = new Intent(getBaseContext(),
                                    MainActivity.class);
                            startActivity(intent);

                            overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

                            finish();
                        }

                    }

                } else {
                    intent = new Intent(getBaseContext(),
                            AutoupdateActivity.class);

                    intent.putExtra(CommonString.KEY_PATH,
                            preferences.getString(CommonString.KEY_PATH, ""));
                    startActivity(intent);

                    overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

                    finish();
                }

            } else if (result.equals(CommonString.KEY_FAILURE)) {

                showMessage(CommonString.METHOD_LOGIN + "Failure");
            }
            if (result.equals(CommonString.KEY_CHANGED)) {

                showMessage(CommonString.MESSAGE_CHANGED);
            } else if (result.equals(CommonString.KEY_FALSE)) {

                showMessage(CommonString.MESSAGE_FALSE);
            }

            dialog.dismiss();
        }

    }

    public String getCurrentTime() {

        Calendar m_cal = Calendar.getInstance();

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String intime = formatter.format(m_cal.getTime());
        return intime;

    }

    public void showMessage(String msg) {

        new AlertDialog.Builder(LoginActivity.this)
                .setTitle("Login Dialog")
                .setMessage(msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        //showProgress(false);
                    }
                })

                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();


    }

    public void showAttendanceMessage(String reason) {

        String msg = "Entry not allowed for - " + reason;

        new AlertDialog.Builder(LoginActivity.this)
                .setTitle("Entry Dialog")
                .setMessage(msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        //showProgress(false);
                        finish();
                    }
                })

                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();


    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPlayServices();

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
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(), "This device is not supported.", Toast.LENGTH_LONG).show();
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

    //Answer Asynctask

    class AnswerTodayTask extends AsyncTask<AnswerData, Void, String> {

        private ProgressDialog dialog = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(LoginActivity.this);
            dialog.setTitle("Todays Question");
            dialog.setMessage("Submitting Answer..");
            dialog.setCancelable(false);
            dialog.show();

        }

        @Override
        protected String doInBackground(AnswerData... params) {

            try {

                AnswerData answerData = params[0];

                String resultHttp = "";
                versionCode = getPackageManager().getPackageInfo(
                        getPackageName(), 0).versionCode;

                qns_cd = answerData.getQuestion_id();
                ans_cd = answerData.getAnswer_id();

                String userauth_xml = "[DATA]" + "[TODAY_ANSWER][USER_ID]"
                        + answerData.getUsername() + "[/USER_ID]" + "[QUESTION_ID]" + answerData.getQuestion_id()
                        + "[/QUESTION_ID]" + "[ANSWER_ID]" + answerData.getAnswer_id()
                        + "[/ANSWER_ID]" + "[VISIT_DATE]" + answerData.getVisit_date()
                        + "[/VISIT_DATE]"
                        + "[/TODAY_ANSWER][/DATA]";

                SoapObject request = new SoapObject(CommonString.NAMESPACE,
                        CommonString.METHOD_UPLOAD_XML);

                request.addProperty("XMLDATA", userauth_xml);
                request.addProperty("KEYS", "TODAYS_ANSWER");
                request.addProperty("USERNAME", answerData.getUsername());

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                        SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                HttpTransportSE androidHttpTransport = new HttpTransportSE(
                        CommonString.URL);

                androidHttpTransport.call(CommonString.SOAP_ACTION + CommonString.METHOD_UPLOAD_XML,
                        envelope);

                Object result = (Object) envelope.getResponse();

                if (!result.toString()
                        .equalsIgnoreCase(CommonString.KEY_SUCCESS)) {

                    showMessage("Fail to Submit Quiz Answer, try again");

                } else {
                    String visit_date = preferences.getString(CommonString.KEY_DATE, null);
                    editor.putBoolean(CommonString.KEY_IS_QUIZ_DONE + visit_date, true);
                    editor.commit();
                    return CommonString.KEY_SUCCESS;
                }

                return "";

            } catch (MalformedURLException e) {

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        showMessage(CommonString.MESSAGE_EXCEPTION);
                    }
                });


            } catch (IOException e) {

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        showMessage(CommonString.MESSAGE_SOCKETEXCEPTION);
                    }
                });

            } catch (Exception e) {

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        showMessage(CommonString.MESSAGE_EXCEPTION);
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (!result.equals(CommonString.KEY_SUCCESS)) {

                String visit_date = preferences.getString(CommonString.KEY_DATE, null);
                editor.putString(CommonString.KEY_QUESTION_CD + visit_date, qns_cd);
                editor.putString(CommonString.KEY_ANSWER_CD + visit_date, ans_cd);
                editor.commit();

            }

            if (attendanceReasonGetterSetter.getREASON_CD().size() > 0) {

                boolean attendance_done_flag = false, entry_allow_flag = true;
                String reason = "";
                for (int i = 0; i < attendanceReasonGetterSetter.getREASON_CD().size(); i++) {

                    if (attendanceReasonGetterSetter.getSTATUS().get(i).equals("1")) {
                        attendance_done_flag = true;
                        if (attendanceReasonGetterSetter.getENTRY_ALLOW().get(i).equals("0")) {
                            entry_allow_flag = false;
                            reason = attendanceReasonGetterSetter.getREASON().get(i);
                            break;
                        }
                    }
                }

                if (attendance_done_flag) {

                    if (entry_allow_flag) {

                        intent = new Intent(getBaseContext(),
                                MainActivity.class);
                        startActivity(intent);

                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

                        finish();

                    } else {

                        showAttendanceMessage(reason);
                    }

                } else {

                    showAttendanceDialog();
                }

            } else {

                intent = new Intent(getBaseContext(),
                        MainActivity.class);
                startActivity(intent);

                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

                finish();
            }
        }

    }

    class AnswerData {

        public String question_id, answer_id, username, visit_date, right_answer;

        public String getQuestion_id() {
            return question_id;
        }

        public void setQuestion_id(String question_id) {
            this.question_id = question_id;
        }

        public String getAnswer_id() {
            return answer_id;
        }

        public void setAnswer_id(String answer_id) {
            this.answer_id = answer_id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getVisit_date() {
            return visit_date;
        }

        public void setVisit_date(String visit_date) {
            this.visit_date = visit_date;
        }

        public String getRight_answer() {
            return right_answer;
        }

        public void setRight_answer(String right_answer) {
            this.right_answer = right_answer;
        }
    }


    //Submit Attendance Asynctask

    class AttendanceTask extends AsyncTask<AttendanceReasonGetterSetter, Void, String> {

        private ProgressDialog dialog = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(LoginActivity.this);
            dialog.setTitle("Todays Attendance");
            dialog.setMessage("Submitting Attendance..");
            dialog.setCancelable(false);
            dialog.show();

        }

        @Override
        protected String doInBackground(AttendanceReasonGetterSetter... params) {

            try {

                String resultHttp = "";
                versionCode = getPackageManager().getPackageInfo(
                        getPackageName(), 0).versionCode;

                String reason_cd = attendanceReason.getREASON_CD().get(0);
                String visit_date = preferences.getString(CommonString.KEY_DATE, null);
                String username = preferences.getString(CommonString.KEY_USERNAME, null);
                String app_version = preferences.getString(CommonString.KEY_VERSION, null);


                String userauth_xml = "[DATA]" + "[USER_DATA][USER_ID]"
                        + username + "[/USER_ID]" + "[REASON_CD]" + reason_cd
                        + "[/REASON_CD]" + "[VISIT_DATE]" + visit_date
                        + "[/VISIT_DATE]" + "[APP_VERSION]" + app_version
                        + "[/APP_VERSION]"
                        + "[/USER_DATA][/DATA]";


                SoapObject request = new SoapObject(
                        CommonString.NAMESPACE,
                        CommonString.METHOD_UPLOAD_REASON_ATTENDANCE);
                request.addProperty("onXML", userauth_xml);

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                        SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                HttpTransportSE androidHttpTransport = new HttpTransportSE(
                        CommonString.URL);

                androidHttpTransport.call(
                        CommonString.SOAP_ACTION + CommonString.METHOD_UPLOAD_REASON_ATTENDANCE,
                        envelope);

                Object result = (Object) envelope.getResponse();

                XmlPullParserFactory factory = XmlPullParserFactory
                        .newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput(new StringReader(result.toString()));
                xpp.next();
                eventType = xpp.getEventType();
                FailureGetterSetter failureGetterSetter = XMLHandlers
                        .failureXMLHandler(xpp, eventType);

                //------------------


                if (!failureGetterSetter.getStatus()
                        .equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                    //showMessage("Fail to Submit Attendance, try again");
                    return CommonString.KEY_FAILURE;
                } else {
                    return CommonString.KEY_SUCCESS;
                }

                //return "";

            } catch (MalformedURLException e) {

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        showMessage(CommonString.MESSAGE_EXCEPTION);
                    }
                });


            } catch (IOException e) {

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        showMessage(CommonString.MESSAGE_SOCKETEXCEPTION);
                    }
                });

            } catch (Exception e) {

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        showMessage(CommonString.MESSAGE_EXCEPTION);
                    }
                });
            }

            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result.equals(CommonString.KEY_SUCCESS)) {

                if (attendanceReason.getENTRY_ALLOW().get(0).equals("0")) {
                    showAttendanceMessage(attendanceReason.getREASON().get(0));
                } else {
                    intent = new Intent(getBaseContext(),
                            MainActivity.class);

                    startActivity(intent);

                    overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

                    finish();
                }

            } else if (result.equals(CommonString.KEY_FAILURE)) {

                showMessage("Fail to Submit Attendance, try again");
                //finish();
            } else {
                showMessage("Problem occurred while uploading attendance");
            }
        }

    }

    public class CustomSpinnerAdapter extends BaseAdapter {
        Context context;
        // int flags[];
        AttendanceReasonGetterSetter reason;
        LayoutInflater inflter;

        public CustomSpinnerAdapter(Context applicationContext, AttendanceReasonGetterSetter reason) {
            this.context = applicationContext;
            //this.flags = flags;
            this.reason = reason;
            inflter = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {
            return reason.getREASON_CD().size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            view = inflter.inflate(R.layout.custom_spinner_item, null);
            //ImageView icon = (ImageView) view.findViewById(R.id.imageView);
            TextView names = (TextView) view.findViewById(R.id.tv_ans);
            if (position == 0) {
                names.setText("Select Reason");
            } else {
                names.setText(reason.getREASON().get(position - 1));
            }

            return view;
        }
    }

    public void showAttendanceDialog() {

        CustomSpinnerAdapter customAdapter;

        final Dialog dialog_attendance = new Dialog(LoginActivity.this);
        dialog_attendance.setTitle("Todays Attendance");
        dialog_attendance.setCancelable(false);
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_attendance.setContentView(R.layout.attendance_dialog_layout);
        Spinner spinner = (Spinner) dialog_attendance.findViewById(R.id.spin_reason);
        Button btnsubmit = (Button) dialog_attendance.findViewById(R.id.btnsubmit);
        customAdapter = new CustomSpinnerAdapter(getApplicationContext(), attendanceReasonGetterSetter);
        spinner.setAdapter(customAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    attendanceReason = new AttendanceReasonGetterSetter();
                    attendanceReason.setREASON(attendanceReasonGetterSetter.getREASON().get(position - 1));
                    attendanceReason.setREASON_CD(attendanceReasonGetterSetter.getREASON_CD().get(position - 1));
                    attendanceReason.setENTRY_ALLOW(attendanceReasonGetterSetter.getENTRY_ALLOW().get(position - 1));
                    attendanceReason.setSTATUS(attendanceReasonGetterSetter.getSTATUS().get(position - 1));
                } else {
                    if (attendanceReason.getREASON_CD().size() > 0)
                        attendanceReason = new AttendanceReasonGetterSetter();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnsubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (attendanceReason.getREASON_CD().size() > 0) {

                    dialog_attendance.cancel();
                    new AttendanceTask().execute(attendanceReason);

                } else {
                    Snackbar.make(mEmailView, "First select a reason", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        dialog_attendance.show();

    }

    private boolean checkgpsEnableDevice() {
        boolean flag = true;
        if (!hasGPSDevice(LoginActivity.this)) {
            Toast.makeText(LoginActivity.this, "Gps not Supported", Toast.LENGTH_SHORT).show();
        }

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(LoginActivity.this)) {
            //  Toast.makeText(LoginActivity.this, "Gps not enabled", Toast.LENGTH_SHORT).show();
            enableLoc();
            flag = false;

        } else if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(LoginActivity.this)) {
            //  Toast.makeText(LoginActivity.this, "Gps already enabled", Toast.LENGTH_SHORT).show();
            flag = true;
        }
        return flag;
    }


    private boolean hasGPSDevice(Context context) {
        final LocationManager mgr = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
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
                                status.startResolutionForResult(LoginActivity.this, REQUEST_LOCATION);
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
                        finish();
                    }

                    default: {
                        break;
                    }
                }
                break;
        }

    }


}
