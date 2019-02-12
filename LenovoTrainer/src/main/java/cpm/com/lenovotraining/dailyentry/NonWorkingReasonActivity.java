package cpm.com.lenovotraining.dailyentry;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import cpm.com.lenovotraining.R;
import cpm.com.lenovotraining.constants.CommonString;
import cpm.com.lenovotraining.database.Database;
import cpm.com.lenovotraining.xmlgettersetter.CoverageBean;
import cpm.com.lenovotraining.xmlgettersetter.JCPGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.NonWorkingReasonGetterSetter;

public class NonWorkingReasonActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    ArrayList<NonWorkingReasonGetterSetter> reasondata = new ArrayList();
    private Spinner reasonspinner;
    private SharedPreferences preferences;
    String _UserId, visit_date, store_id, training_mode_cd, manned;
    protected boolean status = true;
    EditText text;
    AlertDialog alert;
    ImageButton camera;
    RelativeLayout reason_lay, rel_cam;
    Database database;
    String str;
    protected String _pathforcheck = "", _path = "";
    private String image1 = "";
    String reasonname, reasonid, entry_allow, image, entry, reason_reamrk, intime;
    private ArrayAdapter<CharSequence> reason_adapter;
    ArrayList<JCPGetterSetter> jcp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_non_working_reason);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        reasonspinner = (Spinner) findViewById(R.id.spinner2);
        camera = (ImageButton) findViewById(R.id.imgcam);
        text = (EditText) findViewById(R.id.reasontxt);
        reason_lay = (RelativeLayout) findViewById(R.id.layout_reason);
        rel_cam = (RelativeLayout) findViewById(R.id.relimgcam);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        _UserId = preferences.getString(CommonString.KEY_USERNAME, "");
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        store_id = getIntent().getStringExtra(CommonString.KEY_STORE_CD);
        training_mode_cd = getIntent().getStringExtra(CommonString.KEY_TRAINING_MODE_CD);
        manned = getIntent().getStringExtra(CommonString.KEY_MANAGED);
        setTitle("Non Working - " + visit_date);

        database = new Database(this);
        database.open();
        str = CommonString.FILE_PATH;
        reasondata = database.getNonWorkingData();
        intime = getCurrentTime();
        camera.setOnClickListener(this);
        reason_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        reason_adapter.add("-Select Reason-");
        for (int i = 0; i < reasondata.size(); i++) {
            reason_adapter.add(reasondata.get(i).getReason().get(0));
        }
        reasonspinner.setAdapter(reason_adapter);
        reason_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reasonspinner.setOnItemSelectedListener(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validatedata()) {
                    if (imageAllowed()) {
                        if (textAllowed()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    NonWorkingReasonActivity.this);
                            builder.setMessage("Do you want to save the data ")
                                    .setCancelable(false)
                                    .setPositiveButton("OK",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int id) {
                                                    alert.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                                                    if (entry_allow.equals("0")) {
                                                        database.deleteAllTables();
                                                        jcp = database.getStoreData(visit_date);
                                                        for (int i = 0; i < jcp.size(); i++) {
                                                            String stoteid = jcp.get(i).getSTORE_CD().get(0);
                                                            CoverageBean cdata = new CoverageBean();
                                                            cdata.setStoreId(stoteid);
                                                            cdata.setVisitDate(visit_date);
                                                            cdata.setUserId(_UserId);
                                                            cdata.setInTime(intime);
                                                            cdata.setOutTime(getCurrentTime());
                                                            cdata.setReason(reasonname);
                                                            cdata.setReasonid(reasonid);
                                                            cdata.setLatitude("0.0");
                                                            cdata.setLongitude("0.0");
                                                            cdata.setImage(image1);
                                                            cdata.setRemark(text.getText().toString().replaceAll("[&^<>{}'$]", " "));
                                                            cdata.setStatus(CommonString.STORE_STATUS_LEAVE);
                                                            cdata.setTraining_mode_cd(training_mode_cd);
                                                            cdata.setManaged(manned);
                                                            database.InsertCoverageData(cdata);
                                                            database.updateStoreStatusOnLeave(store_id, visit_date, CommonString.STORE_STATUS_LEAVE);
                                                            SharedPreferences.Editor editor = preferences.edit();
                                                            editor.putString(CommonString.KEY_STORE_CD, "");
                                                        }

                                                    } else {
                                                        CoverageBean cdata = new CoverageBean();
                                                        cdata.setStoreId(store_id);
                                                        cdata.setVisitDate(visit_date);
                                                        cdata.setUserId(_UserId);
                                                        cdata.setInTime(intime);
                                                        cdata.setOutTime(getCurrentTime());
                                                        cdata.setReason(reasonname);
                                                        cdata.setReasonid(reasonid);
                                                        cdata.setLatitude("0.0");
                                                        cdata.setLongitude("0.0");
                                                        cdata.setImage(image1);
                                                        cdata.setManaged(manned);
                                                        cdata.setRemark(text.getText().toString().replaceAll("[&^<>{}'$]", " "));
                                                        cdata.setStatus(CommonString.STORE_STATUS_LEAVE);
                                                        cdata.setTraining_mode_cd("0");
                                                        database.InsertCoverageData(cdata);
                                                        database.updateStoreStatusOnLeave(store_id, visit_date, CommonString.STORE_STATUS_LEAVE);
                                                        SharedPreferences.Editor editor = preferences.edit();
                                                        editor.putString(CommonString.KEY_STORE_CD, "");
                                                    }
                                                    finish();
                                                }
                                            })
                                    .setNegativeButton("Cancel",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int id) {
                                                    dialog.cancel();
                                                }
                                            });

                            alert = builder.create();
                            alert.show();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Please enter required remark reason",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Please Capture Image", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please Select a Reason", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    public String getCurrentTime() {
        Calendar m_cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String cdate = formatter.format(m_cal.getTime());
        return cdate;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // NavUtils.navigateUpFromSameTask(this);
            finish();
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imgcam) {
            _pathforcheck = store_id + "NonWorking" + _UserId + ".jpg";
            _path = CommonString.FILE_PATH + _pathforcheck;
            startCameraActivity();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spinner2:
                if (position != 0) {
                    reasonname = reasondata.get(position - 1).getReason().get(0);
                    reasonid = reasondata.get(position - 1).getReason_cd().get(0);
                    entry_allow = reasondata.get(position - 1).getEntry_allow().get(0);
                    if (reasonname.equalsIgnoreCase("Store Closed")) {
                        rel_cam.setVisibility(View.VISIBLE);
                        image = "true";
                    } else {
                        rel_cam.setVisibility(View.GONE);
                        image = "false";
                    }
                    reason_reamrk = "true";
                    if (reason_reamrk.equalsIgnoreCase("true")) {
                        reason_lay.setVisibility(View.VISIBLE);
                    } else {
                        reason_lay.setVisibility(View.GONE);
                    }
                } else {
                    reasonname = "";
                    reasonid = "";
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    protected void startCameraActivity() {

        try {
            Log.i("MakeMachine", "startCameraActivity()");
            File file = new File(_path);
            Uri outputFileUri = Uri.fromFile(file);
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            startActivityForResult(intent, 0);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("MakeMachine", "resultCode: " + resultCode);
        switch (resultCode) {
            case 0:
                Log.i("MakeMachine", "User cancelled");
                break;

            case -1:

                if (_pathforcheck != null && !_pathforcheck.equals("")) {
                    if (new File(str + _pathforcheck).exists()) {
                        camera.setBackground(getResources().getDrawable(R.drawable.camera_icon_done));
                        image1 = _pathforcheck;

                    }
                }

                break;
        }

    }

    public boolean imageAllowed() {
        boolean result = true;

        if (image.equalsIgnoreCase("true")) {

            if (image1.equalsIgnoreCase("")) {

                result = false;

            }
        }

        return result;

    }

    public boolean textAllowed() {
        boolean result = true;
        if (text.getText().toString().trim().equals("")) {

            result = false;

        }

        return result;
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        finish();

        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }

    public boolean validatedata() {
        boolean result = false;
        if (reasonid != null && !reasonid.equalsIgnoreCase("")) {
            result = true;
        }
        return result;

    }

}


