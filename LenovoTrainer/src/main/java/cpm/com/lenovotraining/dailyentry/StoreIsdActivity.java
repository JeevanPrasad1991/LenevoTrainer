package cpm.com.lenovotraining.dailyentry;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionMenu;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import cpm.com.lenovotraining.R;
import cpm.com.lenovotraining.bean.TableBean;
import cpm.com.lenovotraining.constants.CommonString;
import cpm.com.lenovotraining.database.Database;
import cpm.com.lenovotraining.lenovotrainer.MainActivity;
import cpm.com.lenovotraining.xmlHandler.XMLHandlers;
import cpm.com.lenovotraining.xmlgettersetter.AddNewEmployeeGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.AllIsdNEmployeeGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.EmpCdIsdGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.JCPGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.NavMenuGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.StoreISDGetterSetter;

public class StoreIsdActivity extends AppCompatActivity implements View.OnClickListener {

    private ArrayAdapter<CharSequence> isdAdapter;
    Spinner spinner_isd;
    Database db;
    ArrayList<StoreISDGetterSetter> storeISDGetterSetters;
    private SharedPreferences preferences;
    String store_cd, training_mode_cd, manned;
    Button btn_next;
    Button btn_add_isd, btn_cancel;
    Data data;
    String emp_id, isd_cd, name, phone, email;
    boolean isIsd = false;
    EmpCdIsdGetterSetter empCdIsdGetterSetter;
    RelativeLayout lay_spin, lay_next;
    LinearLayout lay_add_isd;
    TextView tv_isd, tv_emp_cd;
    FloatingActionMenu materialDesignFAM;
    com.github.clans.fab.FloatingActionButton fab_add_new_employee, fab_add_existing;
    RecyclerView rec_isd;
    ArrayList<AllIsdNEmployeeGetterSetter> allIsdNEmployeeList;
    boolean is_new_isd_flag = false;
    CardView cardView_isd;
    ImageView isd_image, isdimage;
    String _pathforcheck = "", _path, str, intime, image1, img1 = "", _pathforcheck1 = "", image2, _path1;
    ArrayList<JCPGetterSetter> jcpGetterSetters = new ArrayList<>();
    RelativeLayout layout_camera;
    String trainning_mode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_isd);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        rec_isd = (RecyclerView) findViewById(R.id.rec_isd_added);
        btn_next = (Button) findViewById(R.id.btn_next);
        btn_add_isd = (Button) findViewById(R.id.btn_add_isd);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        lay_spin = (RelativeLayout) findViewById(R.id.layout_isd_spin);
        lay_next = (RelativeLayout) findViewById(R.id.layout_next);
        lay_add_isd = (LinearLayout) findViewById(R.id.layout_show_isd);
        tv_isd = (TextView) findViewById(R.id.tv_isd);
        tv_emp_cd = (TextView) findViewById(R.id.tv_emp_cd);
        cardView_isd = (CardView) findViewById(R.id.card_view_isd);
        materialDesignFAM = (FloatingActionMenu) findViewById(R.id.material_design_android_floating_action_menu);
        fab_add_new_employee = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_add_new_employee);
        fab_add_existing = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_add_existing);
        isd_image = (ImageView) findViewById(R.id.isd_image);
        layout_camera = (RelativeLayout) findViewById(R.id.layout_camera);
        str = Environment.getExternalStorageDirectory() + "/Lenovo_Trainer_Images/";
        materialDesignFAM.setClosedOnTouchOutside(true);
        training_mode_cd = getIntent().getStringExtra(CommonString.KEY_TRAINING_MODE_CD);
        manned = getIntent().getStringExtra(CommonString.KEY_MANAGED);
        if (manned.equals("0")) {
            //showAddNewEmployee();
            cardView_isd.setVisibility(View.GONE);
            materialDesignFAM.removeMenuButton(fab_add_existing);
        }
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        store_cd = preferences.getString(CommonString.KEY_STORE_CD, null);
        trainning_mode = preferences.getString(CommonString.KEY_TRAINING_MODE, null);
        fab_add_existing.setOnClickListener(this);
        fab_add_new_employee.setOnClickListener(this);
        db = new Database(getApplicationContext());
        db.open();
        allIsdNEmployeeList = db.getAllIsdNEmployeeData(store_cd);
        if (allIsdNEmployeeList.size() > 0) {
            IsdAddedAdapter isdAddedAdapter = new IsdAddedAdapter(this, allIsdNEmployeeList);
            rec_isd.setAdapter(isdAddedAdapter);
            rec_isd.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        }
        storeISDGetterSetters = db.getStoreIsdData(store_cd);
        isdAdapter = new ArrayAdapter<CharSequence>(this, R.layout.spinner_custom_item);
        spinner_isd = (Spinner) findViewById(R.id.spinisd);
        isdAdapter.add("Select");
        for (int i = 0; i < storeISDGetterSetters.size(); i++) {
            isdAdapter.add(storeISDGetterSetters.get(i).getISD_NAME().get(0));
        }
        spinner_isd.setAdapter(isdAdapter);
        spinner_isd.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                materialDesignFAM.close(true);
                if (position != 0) {

                    isd_cd = storeISDGetterSetters.get(position - 1).getISD_CD().get(0);

                } else {
                    isd_cd = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btn_next.setOnClickListener(this);
        btn_add_isd.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        isd_image.setOnClickListener(this);

        if (trainning_mode.equalsIgnoreCase("Remote")) {
            layout_camera.setVisibility(View.GONE);
        } else {
            layout_camera.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch (id) {
            case R.id.btn_add_isd:
                is_new_isd_flag = true;
            case R.id.btn_next:
                materialDesignFAM.close(true);
                if (trainning_mode.equalsIgnoreCase("Remote")) {
                    if (isd_cd.equals("")) {
                        Snackbar.make(lay_spin, "First select an ISD", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                    else {
                        boolean is_isd_exists = false;
                        for (int i = 0; i < allIsdNEmployeeList.size(); i++) {
                            if (allIsdNEmployeeList.get(i).getIsd_cd().equals(isd_cd)) {
                                is_isd_exists = true;
                                break;
                            }

                        }

                        if (is_isd_exists) {

                            if (is_new_isd_flag) {

                                lay_next.setVisibility(View.VISIBLE);
                                lay_spin.setVisibility(View.VISIBLE);
                                lay_add_isd.setVisibility(View.GONE);
                                isd_cd = "";
                                empCdIsdGetterSetter = null;
                                is_new_isd_flag = false;
                            } else {

                                isd_cd = "";
                                spinner_isd.setSelection(0);
                            }

                            Snackbar.make(lay_spin, "ISD already done", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        } else {
                            startAuditOrTopic(null, is_new_isd_flag);
                        }

                    }
                } else {
                    if (isd_cd.equals("")) {
                        Snackbar.make(lay_spin, "First select an ISD", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    } else if (img1.equalsIgnoreCase("")) {
                        Snackbar.make(lay_spin, "Please capture image", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    } else {
                        boolean is_isd_exists = false;
                        for (int i = 0; i < allIsdNEmployeeList.size(); i++) {
                            if (allIsdNEmployeeList.get(i).getIsd_cd().equals(isd_cd)) {
                                is_isd_exists = true;
                                break;
                            }

                        }


                        if (is_isd_exists) {

                            if (is_new_isd_flag) {

                                lay_next.setVisibility(View.VISIBLE);
                                lay_spin.setVisibility(View.VISIBLE);
                                lay_add_isd.setVisibility(View.GONE);
                                isd_cd = "";
                                empCdIsdGetterSetter = null;
                                is_new_isd_flag = false;
                            } else {

                                isd_cd = "";
                                spinner_isd.setSelection(0);
                            }

                            Snackbar.make(lay_spin, "ISD already done", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        } else {
                            startAuditOrTopic(null, is_new_isd_flag);
                        }

                    }

                }
                break;

            case R.id.btn_cancel:

                lay_next.setVisibility(View.VISIBLE);
                lay_spin.setVisibility(View.VISIBLE);
                lay_add_isd.setVisibility(View.GONE);
                isd_cd = "";
                empCdIsdGetterSetter = null;
                is_new_isd_flag = false;

                break;

            case R.id.fab_add_existing:

                materialDesignFAM.close(true);

                final Dialog dialog_emp = new Dialog(StoreIsdActivity.this);
                dialog_emp.setTitle("Get ISD");
                //dialog_emp.setCancelable(false);
                //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog_emp.setContentView(R.layout.enter_empid_dialog_layout);

                final EditText editEmpCd = (EditText) dialog_emp.findViewById(R.id.et_empid);
                Button btngetIsd = (Button) dialog_emp.findViewById(R.id.btn_get_isd);

                btngetIsd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        emp_id = editEmpCd.getText().toString();
                        if (emp_id.equals("")) {
                            Snackbar.make(lay_spin, "First enter employee id ", Snackbar.LENGTH_SHORT).show();

                        } else {
                            dialog_emp.cancel();
                            new GetISDTask().execute();
                        }

                    }
                });

                dialog_emp.show();

                break;

            case R.id.fab_add_new_employee:

                materialDesignFAM.close(true);
                showAddNewEmployee();

                break;
            case R.id.isd_image:
                intime = String.valueOf(current_Time_date());

                _pathforcheck = store_cd + "Store" + "Image" + intime + ".jpg";

                _path = str + _pathforcheck;


                startCameraActivity();
                break;

        }
    }

    class Data {
        int value;
        String name;
    }

    public CharSequence current_Time_date() {
        Date d = new Date();
        CharSequence s = DateFormat.format("yyyyMMddhhmmss", d.getTime());
        return s;

    }

    //ISD Asynctask
    class GetISDTask extends AsyncTask<Void, Void, String> {
        private ProgressDialog dialog = null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(StoreIsdActivity.this);
            dialog.setTitle("Get ISD");
            dialog.setMessage("Submitting Employee Id.....");
            dialog.setCancelable(false);
            dialog.show();

        }

        @Override
        protected String doInBackground(Void... params) {

            try {
                data = new Data();
                // STore ISD data

                XmlPullParserFactory factory = XmlPullParserFactory
                        .newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();

                SoapObject request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                request.addProperty("UserName", emp_id);
                request.addProperty("Type", "SEARCH_ISD");

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                HttpTransportSE androidHttpTransport = new HttpTransportSE(CommonString.URL);

                androidHttpTransport.call(CommonString.SOAP_ACTION_UNIVERSAL, envelope);
                Object result = (Object) envelope.getResponse();

                if (result.toString() != null) {

                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    int eventType = xpp.getEventType();

                    empCdIsdGetterSetter = XMLHandlers.empcdXMLHandler(xpp, eventType);

                    if (empCdIsdGetterSetter.getEmp_cd() != null) {
                        return CommonString.KEY_SUCCESS;

                    } else {
                        return CommonString.KEY_NO_DATA;
                    }

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

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            dialog.cancel();

            if (result.equals(CommonString.KEY_NO_DATA)) {
                Snackbar.make(lay_add_isd, "Inavlid Employee Id", Snackbar.LENGTH_SHORT).show();
            } else {
                lay_spin.setVisibility(View.GONE);
                lay_next.setVisibility(View.GONE);
                lay_add_isd.setVisibility(View.VISIBLE);
                tv_emp_cd.setText("Emp CD - " + empCdIsdGetterSetter.getEmp_cd());
                tv_isd.setText("ISD - " + empCdIsdGetterSetter.getIsd());
                isd_cd = empCdIsdGetterSetter.getIsd_cd();
            }
        }

    }

    public void showMessage(String msg) {
        new AlertDialog.Builder(StoreIsdActivity.this)
                .setTitle("Alert Dialog")
                .setMessage(msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        finish();
                    }
                })

                .setIcon(R.drawable.parinaam_logo_ico)
                .show();
    }

    public class IsdAddedAdapter extends RecyclerView.Adapter<IsdAddedAdapter.MyViewHolder> {

        private LayoutInflater inflator;

        List<AllIsdNEmployeeGetterSetter> data = Collections.emptyList();

        public IsdAddedAdapter(Context context, List<AllIsdNEmployeeGetterSetter> data) {

            inflator = LayoutInflater.from(context);
            this.data = data;

        }

        @Override
        public IsdAddedAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {

            View view = inflator.inflate(R.layout.item_isd_added_layout, parent, false);

            MyViewHolder holder = new MyViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(final IsdAddedAdapter.MyViewHolder viewHolder, final int position) {

            final AllIsdNEmployeeGetterSetter current = data.get(position);

            viewHolder.tv_name.setText(current.getName());
            viewHolder.tv_topic.setText(current.getTopic());
            viewHolder.tv_type.setText(current.getType());

        }

        @Override
        public int getItemCount() {
            return data.size();
        }


        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView tv_name, tv_topic, tv_type;
            //ImageView icon;

            public MyViewHolder(View itemView) {
                super(itemView);
                tv_name = (TextView) itemView.findViewById(R.id.tv_isd);
                tv_topic = (TextView) itemView.findViewById(R.id.tv_topic);
                tv_type = (TextView) itemView.findViewById(R.id.tv_training_type);

            }
        }

    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }

    //---------------------------------------

    public void startAuditOrTopic(AddNewEmployeeGetterSetter addNewEmployeeGetterSetter, boolean is_new_isd) {
        Intent in1;
        if (training_mode_cd.equals("1")) {

            in1 = new Intent(getApplicationContext(), AuditActivity.class);
            in1.putExtra(CommonString.KEY_ISD_CD, isd_cd);
            in1.putExtra(CommonString.KEY_TRAINING_MODE_CD, training_mode_cd);
            in1.putExtra(CommonString.KEY_MANAGED, manned);
            in1.putExtra(CommonString.KEY_ISD_IMAGE, img1);

            if (is_new_isd) {
                is_new_isd_flag = false;
                in1.putExtra(CommonString.KEY_NEW_ISD, empCdIsdGetterSetter);
            }
            if (addNewEmployeeGetterSetter != null)
                in1.putExtra(CommonString.KEY_NEW_EMPLOYEE, addNewEmployeeGetterSetter);
        } else {
            in1 = new Intent(getApplicationContext(), TrainingActivity.class);
            in1.putExtra(CommonString.KEY_ISD_CD, isd_cd);
            in1.putExtra(CommonString.KEY_TRAINING_MODE_CD, training_mode_cd);
            in1.putExtra(CommonString.KEY_MANAGED, manned);
            in1.putExtra(CommonString.KEY_ISD_IMAGE, img1);
            if (is_new_isd) {
                is_new_isd_flag = false;
                in1.putExtra(CommonString.KEY_NEW_ISD, empCdIsdGetterSetter);
            }

            if (addNewEmployeeGetterSetter != null)
                in1.putExtra(CommonString.KEY_NEW_EMPLOYEE, addNewEmployeeGetterSetter);
        }

        startActivity(in1);

        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

        finish();
    }

    public void showAddNewEmployee() {

        final Dialog dialog_new_emp = new Dialog(StoreIsdActivity.this);
        dialog_new_emp.setTitle("Add Employee");
        dialog_new_emp.setContentView(R.layout.add_new_employee_dialog);
        final EditText editName = (EditText) dialog_new_emp.findViewById(R.id.et_name);
        final EditText editPhone = (EditText) dialog_new_emp.findViewById(R.id.et_phone);
        final EditText editEmail = (EditText) dialog_new_emp.findViewById(R.id.et_email_id);
        final CheckBox cb_isd = (CheckBox) dialog_new_emp.findViewById(R.id.cb_isisd);
        RelativeLayout rl_camera_dialog= (RelativeLayout) dialog_new_emp.findViewById(R.id.rl_camera_dialog);
        if (trainning_mode.equalsIgnoreCase("Remote")){
            rl_camera_dialog.setVisibility(View.GONE);
        }

        isdimage = (ImageView) dialog_new_emp.findViewById(R.id.isd_image);
        cb_isd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isIsd = isChecked;
            }
        });
        Button btn_add_emp = (Button) dialog_new_emp.findViewById(R.id.btn_get_isd);
        Button btn_cancel = (Button) dialog_new_emp.findViewById(R.id.btn_cancel);
        if (manned.equals("0")) {
            btn_cancel.setVisibility(View.VISIBLE);
            dialog_new_emp.setCancelable(false);
            cb_isd.setVisibility(View.GONE);
        }
        isdimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intime = String.valueOf(current_Time_date());
                _pathforcheck1 = store_cd + "Store" + "Image" + intime + ".jpg";

                _path1 = str + _pathforcheck1;

                startCameraActivity1();

            }
        });

        btn_add_emp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = editName.getText().toString();
                phone = editPhone.getText().toString();
                email = editEmail.getText().toString();
                if (trainning_mode.equalsIgnoreCase("Remote")){
                    if (name.isEmpty()) {
                        Snackbar.make(lay_spin, "First enter name ", Snackbar.LENGTH_SHORT).show();
                    } else if (phone.isEmpty()) {
                        Snackbar.make(lay_spin, "First enter mobile number ", Snackbar.LENGTH_SHORT).show();
                    } else if (phone.length() < 10) {
                        Snackbar.make(lay_spin, "First enter 10 digit mobile number ", Snackbar.LENGTH_SHORT).show();
                    } else if (email.isEmpty()) {
                        Snackbar.make(lay_spin, "First enter email id ", Snackbar.LENGTH_SHORT).show();
                    } else if (!isValidEmail(email)) {
                        Snackbar.make(lay_spin, "First enter valid email id ", Snackbar.LENGTH_SHORT).show();
                    }
                    else {
                        dialog_new_emp.cancel();
                        isd_cd = "0";
                        AddNewEmployeeGetterSetter addNewEmployee = new AddNewEmployeeGetterSetter(name, email, phone, isIsd);
                        startAuditOrTopic(addNewEmployee, false);
                    }
                }else {
                    if (name.isEmpty()) {
                        Snackbar.make(lay_spin, "First enter name ", Snackbar.LENGTH_SHORT).show();
                    } else if (phone.isEmpty()) {
                        Snackbar.make(lay_spin, "First enter mobile number ", Snackbar.LENGTH_SHORT).show();
                    } else if (phone.length() < 10) {
                        Snackbar.make(lay_spin, "First enter 10 digit mobile number ", Snackbar.LENGTH_SHORT).show();
                    } else if (email.isEmpty()) {
                        Snackbar.make(lay_spin, "First enter email id ", Snackbar.LENGTH_SHORT).show();
                    } else if (!isValidEmail(email)) {
                        Snackbar.make(lay_spin, "First enter valid email id ", Snackbar.LENGTH_SHORT).show();
                    } else if (img1.equalsIgnoreCase("")) {
                        Snackbar.make(lay_spin, "Please capture image", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    } else {
                        dialog_new_emp.cancel();
                        isd_cd = "0";
                        AddNewEmployeeGetterSetter addNewEmployee = new AddNewEmployeeGetterSetter(name, email, phone, isIsd);
                        startAuditOrTopic(addNewEmployee, false);

                    }
                }

            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog_new_emp.cancel();

            }
        });

        dialog_new_emp.show();

    }

    protected void startCameraActivity1() {

        try {
            Log.i("MakeMachine", "startCameraActivity()");
            File file = new File(_path1);
            Uri outputFileUri = Uri.fromFile(file);
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            startActivityForResult(intent, 0);
        } catch (Exception e) {

            e.printStackTrace();
        }
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

            e.printStackTrace();
        }
    }


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
                        isd_image.setBackgroundResource(R.drawable.camera_icon_done);
                        image1 = _pathforcheck;
                        img1 = _pathforcheck;
                        _pathforcheck = "";

                    }
                } else if (_pathforcheck1 != null && !_pathforcheck1.equals("")) {
                    if (new File(str + _pathforcheck1).exists()) {
                        isdimage.setBackgroundResource(R.drawable.camera_icon_done);
                        image2 = _pathforcheck1;
                        img1 = _pathforcheck1;
                        _pathforcheck1 = "";

                    }
                }

                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}