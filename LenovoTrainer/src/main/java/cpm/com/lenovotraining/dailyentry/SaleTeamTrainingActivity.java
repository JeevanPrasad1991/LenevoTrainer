package cpm.com.lenovotraining.dailyentry;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import cpm.com.lenovotraining.R;
import cpm.com.lenovotraining.constants.CommonString;
import cpm.com.lenovotraining.database.Database;
import cpm.com.lenovotraining.multiselectionspin.MultiSpinnerSearch;
import cpm.com.lenovotraining.multiselectionspin.SpinnerListener;
import cpm.com.lenovotraining.xmlgettersetter.SaleTeamGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.TrainingTopicGetterSetter;

public class SaleTeamTrainingActivity extends AppCompatActivity implements View.OnClickListener {
    FloatingActionButton add_fab, team_training_saveFAB;
    ArrayList<TrainingTopicGetterSetter> trainingTopic = new ArrayList<>();
    ArrayList<TrainingTopicGetterSetter> completedinsertedDATA = new ArrayList<>();
    ArrayList<SaleTeamGetterSetter> saleTeamList = new ArrayList<>();
    ArrayList<SaleTeamGetterSetter> selectedSaleTreaineeList = new ArrayList<>();
    ArrayList<TrainingTopicGetterSetter> insertedTrainneeTempList = new ArrayList<TrainingTopicGetterSetter>();
    private SharedPreferences preferences = null;
    private ArrayAdapter<CharSequence> topicAdapter, traineeAdapter;
    RecyclerView rec_saleteam_trainee_added;
    String visit_date, username, topic_cd = "", topic = "", Error_Message, trainee_cd = "", trainee_name = "";
    private static final String TAG = SaleTeamTrainingActivity.class.getSimpleName();
    Spinner spin_topic_trainee;
    MultiSpinnerSearch spin_traineeNM;
    boolean addflag = false;
    MyAdapter myAdapter;
    TrainneeNameDataAdapter trainneeDataAdapter;
    ProgressDialog loading;
    // EditText trainee_edt;
    Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_team_training);
        UIDATA();
    }

    private void UIDATA() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        add_fab = (FloatingActionButton) findViewById(R.id.add_fab);
        team_training_saveFAB = (FloatingActionButton)
                findViewById(R.id.team_training_saveFAB);
        rec_saleteam_trainee_added = (RecyclerView)
                findViewById(R.id.rec_saleteam_trainee_added);
        spin_topic_trainee = (Spinner)
                findViewById(R.id.spin_topic_trainee);
        //  trainee_edt = (EditText) findViewById(R.id.trainee_edt);
        spin_traineeNM = (MultiSpinnerSearch)
                findViewById(R.id.spin_traineeNM);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        setTitle("Sales Team Training - " + visit_date);
        db = new Database(this);
        db.open();
        trainingTopic = db.getTopicData();
        ///for sale team get data
        saleTeamList = db.getSaleTeamAnswerData();
        topicAdapter = new ArrayAdapter<>(this, R.layout.spinner_custom_item);
        topicAdapter.add("-Select Topic-");
        for (int i = 0; i < trainingTopic.size(); i++) {
            topicAdapter.add(trainingTopic.get(i).getTOPIC().get(0));
        }
        spin_topic_trainee.setAdapter(topicAdapter);
        spin_topic_trainee.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    topic_cd = trainingTopic.get(position - 1).getTOPIC_CD().get(0);
                    topic = trainingTopic.get(position - 1).getTOPIC().get(0);

                } else {
                    topic_cd = "";
                    topic = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if (saleTeamList.size() > 0) {
            traineeAdapter = new ArrayAdapter<CharSequence>(this, R.layout.spinner_custom_item);
            traineeAdapter.add("-Select Trainee-");
            for (int i = 0; i < saleTeamList.size(); i++) {
                traineeAdapter.add(saleTeamList.get(i).getTrainee().get(0));
            }
            spin_traineeNM.setAdapter(traineeAdapter);
            spin_traineeNM.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position != 0) {
                        trainee_cd = saleTeamList.get(position - 1).getTrainee_cd().get(0);
                        trainee_name = saleTeamList.get(position - 1).getTrainee().get(0);
                    } else {
                        trainee_name = "";
                        trainee_cd = "";
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

        }

        ////////// sale team trainee list interface
        TraineeNamInterfaceData();
        add_fab.setOnClickListener(this);
        team_training_saveFAB.setOnClickListener(this);
        checkdata();
        setDataToListView();
    }

    private void checkdata() {
        try {
            db.open();
            if (db.checkdateSAlesTRainee(visit_date)) {
                db.removealldata();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDataToListView() {
        try {
            completedinsertedDATA = db.getSALETEAMTRaineeInsertedDATA(visit_date);

            //remove duplicate value frpm list
            Set<TrainingTopicGetterSetter> set = new HashSet<>();
            set.addAll(completedinsertedDATA);
            ArrayList<TrainingTopicGetterSetter> removeDuplicateJcp = new ArrayList<>();
            removeDuplicateJcp.clear();
            removeDuplicateJcp.addAll(set);
            insertedTrainneeTempList.clear();
            insertedTrainneeTempList.addAll(removeDuplicateJcp);

            if (insertedTrainneeTempList.size() > 0) {
                Collections.reverse(insertedTrainneeTempList);
                myAdapter = new MyAdapter(this, insertedTrainneeTempList);
                rec_saleteam_trainee_added.setAdapter(myAdapter);
                rec_saleteam_trainee_added.setLayoutManager(new LinearLayoutManager(this));
                myAdapter.notifyDataSetChanged();
            }

        } catch (Exception e) {
            Log.d("Exception when fetching", e.toString());
        }
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(CommonString.ONBACK_ALERT_MESSAGE)
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                                finish();
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(CommonString.ONBACK_ALERT_MESSAGE)
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog, int id) {
                                    overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                                    finish();
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
        return super.onOptionsItemSelected(item);
    }

    private boolean validate() {
        boolean status = true;
        if (spin_topic_trainee.getSelectedItem().toString().contains("-Select Topic-")) {
            Error_Message = "Please Select Dropdown Training Topic";
            status = false;
        }
        if (status && spin_topic_trainee.getSelectedItem().toString().contains("-Select Trainee-")) {
            Error_Message = "Please Select Dropdown Trainee Name";
            status = false;
        }
       /* if (status && trainee_edt.getText().toString().equals("")) {
            Error_Message = "Please Enter Trainee Name";
            status = false;
        }*/
        return status;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_fab:
                if (validate()) {
                    if (trainneeDuplicateUser()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Parinaam").setMessage("Do you want to add data");
                        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                addflag = true;
                                if (selectedSaleTreaineeList.size() > 0) {
                                    for (int j = 0; j < selectedSaleTreaineeList.size(); j++) {
                                        TrainingTopicGetterSetter info = new TrainingTopicGetterSetter();
                                        info.setTOPIC(topic);
                                        info.setTOPIC_CD(topic_cd);
                                        info.setTrainee_userN(selectedSaleTreaineeList.get(j).getTrainee().get(0));
                                        info.setTrainee_cd(selectedSaleTreaineeList.get(j).getTrainee_cd().get(0));
                                        completedinsertedDATA.add(info);
                                    }

                                    //remove duplicate value frpm list
                                    Set<TrainingTopicGetterSetter> set = new HashSet<>();
                                    set.addAll(completedinsertedDATA);
                                    ArrayList<TrainingTopicGetterSetter> removeDuplicateJcp = new ArrayList<>();
                                    removeDuplicateJcp.clear();
                                    removeDuplicateJcp.addAll(set);
                                    insertedTrainneeTempList.clear();
                                    insertedTrainneeTempList.addAll(removeDuplicateJcp);

                                    ///adapter
                                    myAdapter = new MyAdapter(SaleTeamTrainingActivity.this, insertedTrainneeTempList);
                                    rec_saleteam_trainee_added.setAdapter(myAdapter);
                                    rec_saleteam_trainee_added.setLayoutManager(new LinearLayoutManager(SaleTeamTrainingActivity.this));
                                    myAdapter.notifyDataSetChanged();
                                    Snackbar.make(add_fab, "Data has been added", Snackbar.LENGTH_SHORT).show();

                                    ///////for traineee name spineerrrrrrr
                                    unckeckedTraineeNamInterfaceData();
                                    TraineeNamInterfaceData();
                                    selectedSaleTreaineeList.clear();
                                    spin_topic_trainee.setSelection(0);
                                }
                            }
                        });
                        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        builder.show();
                    } else {
                        Snackbar.make(add_fab, "This " + topic + " is already added", Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    Snackbar.make(add_fab, Error_Message, Snackbar.LENGTH_LONG).show();
                }
                break;
            case R.id.team_training_saveFAB:
                if (completedinsertedDATA.size() > 0 && addflag) {
                    if (checkNetIsAvailable()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Parinaam").setMessage("Do you want to save and uplaod data");
                        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                try {
                                    new BackgroundTask(SaleTeamTrainingActivity.this, completedinsertedDATA).execute();
                                    dialogInterface.dismiss();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();

                            }
                        });
                        builder.show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Parinaam").setMessage("Do you want to save data");
                        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                try {
                                    db.open();
                                    db.insertSALETEAMTRAININGDATA(visit_date, username, completedinsertedDATA);
                                    Toast.makeText(getApplicationContext(), "Data has been saved.", Toast.LENGTH_LONG).show();
                                    overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                                    SaleTeamTrainingActivity.this.finish();
                                    dialogInterface.dismiss();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();

                            }
                        });
                        builder.show();
                    }

                } else {
                    Snackbar.make(team_training_saveFAB, "Please add first", Snackbar.LENGTH_LONG).show();
                }
                break;
        }
    }


    public boolean checkNetIsAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }


    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        private LayoutInflater inflator;
        Context context;
        ArrayList<TrainingTopicGetterSetter> insertedlist_Data;

        MyAdapter(Context context, ArrayList<TrainingTopicGetterSetter> insertedlist_Data) {
            inflator = LayoutInflater.from(context);
            this.context = context;
            this.insertedlist_Data = insertedlist_Data;

        }

        @Override
        public int getItemCount() {
            return insertedlist_Data.size();

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflator.inflate(R.layout.secondary_adapter_salet_trainee, parent, false);
            MyAdapter.MyViewHolder holder = new MyAdapter.MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyAdapter.MyViewHolder holder, final int position) {
            holder.delete_img.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (insertedlist_Data.get(position).getKey_id() == null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Are you sure you want to Delete")
                                .setCancelable(false)
                                .setPositiveButton("Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int id) {
                                                if (completedinsertedDATA.size() > 0) {
                                                    for (int i = 0; i < completedinsertedDATA.size(); i++) {
                                                        if (completedinsertedDATA.get(i).getTOPIC_CD().get(0).equals(insertedlist_Data.
                                                                get(position).getTOPIC_CD().get(0))) {
                                                            completedinsertedDATA.remove(i--);
                                                        }
                                                    }
                                                }

                                                insertedlist_Data.remove(position);
                                                notifyDataSetChanged();
                                                if (insertedlist_Data.size() > 0) {
                                                    MyAdapter adapter = new MyAdapter(context, insertedlist_Data);
                                                    rec_saleteam_trainee_added.setAdapter(adapter);
                                                    rec_saleteam_trainee_added.setLayoutManager(new LinearLayoutManager(context));
                                                    adapter.notifyDataSetChanged();
                                                }
                                                notifyDataSetChanged();
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
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Are you sure you want to Delete")
                                .setCancelable(false)
                                .setPositiveButton("Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int id) {
                                                if (completedinsertedDATA.size() > 0) {
                                                    for (int i = 0; i < completedinsertedDATA.size(); i++) {
                                                        if (completedinsertedDATA.get(i).getTOPIC_CD().get(0).equals(insertedlist_Data.
                                                                get(position).getTOPIC_CD().get(0))) {
                                                            completedinsertedDATA.remove(i--);
                                                        }
                                                    }
                                                }

                                                String listid = insertedlist_Data.get(position).getKey_id();
                                                db.remove_saleteam_trainnee(listid);
                                                insertedlist_Data.remove(position);
                                                notifyDataSetChanged();
                                                if (insertedlist_Data.size() > 0) {
                                                    MyAdapter adapter = new MyAdapter(context, insertedlist_Data);
                                                    rec_saleteam_trainee_added.setAdapter(adapter);
                                                    rec_saleteam_trainee_added.setLayoutManager(new LinearLayoutManager(context));
                                                    adapter.notifyDataSetChanged();
                                                }
                                                notifyDataSetChanged();
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
            holder.trainnee_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(context);
                    dialog.setTitle("Trainee List");
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_traineelist);
                    Window window = dialog.getWindow();
                    WindowManager.LayoutParams wlp = window.getAttributes();
                    wlp.gravity = Gravity.CENTER;
                    wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
                    window.setAttributes(wlp);
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                    ArrayList<TrainingTopicGetterSetter> trainneetempList = new ArrayList<>();
                    if (completedinsertedDATA.size() > 0) {
                        trainneetempList.clear();
                        for (int i = 0; i < completedinsertedDATA.size(); i++) {
                            if (completedinsertedDATA.get(i).getTOPIC_CD().get(0).equals(insertedlist_Data.get(position).getTOPIC_CD().get(0))) {
                                trainneetempList.add(completedinsertedDATA.get(i));
                            }
                        }
                    }

                    RecyclerView recyclerRSP = (RecyclerView) dialog.findViewById(R.id.dialog_rsplist);
                    trainneeDataAdapter = new TrainneeNameDataAdapter(context, trainneetempList);
                    recyclerRSP.setAdapter(trainneeDataAdapter);
                    recyclerRSP.setLayoutManager(new LinearLayoutManager((Activity) context));
                    ImageView ok = (ImageView) dialog.findViewById(R.id.dialog_ok);
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            });

            if (insertedlist_Data.get(position).getStaus().equalsIgnoreCase(CommonString.KEY_U)) {
                holder.imgStatus.setImageResource(R.drawable.ic_u);
                holder.delete_img.setEnabled(false);
            } else {
                holder.imgStatus.setImageResource(R.drawable.ic_n);
                holder.delete_img.setEnabled(true);
            }

            // holder.trainnee_name.setText(insertedlist_Data.get(position).getTrainee_userN());
            holder.trainee_topic.setText(insertedlist_Data.get(position).getTOPIC().get(0));
            holder.trainee_topic.setId(position);
            holder.trainnee_name.setId(position);
            holder.delete_img.setId(position);
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView trainee_topic;
            ImageView delete_img, trainnee_name, imgStatus;

            public MyViewHolder(View convertView) {
                super(convertView);
                trainee_topic = (TextView) convertView.findViewById(R.id.trainee_topic);
                trainnee_name = (ImageView) convertView.findViewById(R.id.trainnee_name);
                delete_img = (ImageView) convertView.findViewById(R.id.imgDelRow);
                imgStatus = (ImageView) convertView.findViewById(R.id.imgStatus);
            }
        }
    }

    private void unckeckedTraineeNamInterfaceData() {
        if (saleTeamList.size() > 0) {
            for (int i = 0; i < saleTeamList.size(); i++) {
                saleTeamList.get(i).setSelected(false);
            }
        }
    }


    private void TraineeNamInterfaceData() {
        spin_traineeNM.setItems(saleTeamList, -1, new SpinnerListener() {
            @Override
            public void onItemsSelected(ArrayList<SaleTeamGetterSetter> items) {
                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).isSelected()) {
                        Log.i(TAG, i + " : " + items.get(i).getTrainee().get(0) + " : " + items.get(i).isSelected());
                        selectedSaleTreaineeList.add(items.get(i));
                    }
                }
            }
        });
    }

    private class TrainneeNameDataAdapter extends RecyclerView.Adapter<TrainneeNameDataAdapter.RspHolder> {
        Context context;
        ArrayList<TrainingTopicGetterSetter> rspList;
        LayoutInflater inflater;

        TrainneeNameDataAdapter(Context context, ArrayList<TrainingTopicGetterSetter> rspList) {
            inflater = LayoutInflater.from(context);
            this.context = context;
            this.rspList = rspList;
        }

        @Override
        public RspHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.secondary_adapter_trainneelist, parent, false);
            RspHolder holder = new RspHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(RspHolder holder, int position) {
            holder.traineee_txt.setText(rspList.get(position).getTrainee_userN());
            holder.traineee_txt.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            holder.traineee_txt.setTextColor(Color.BLACK);
        }

        @Override
        public int getItemCount() {
            return rspList.size();
        }

        class RspHolder extends RecyclerView.ViewHolder {
            TextView traineee_txt;

            public RspHolder(View itemView) {
                super(itemView);
                traineee_txt = (TextView) itemView.findViewById(R.id.traineee_txt);
            }
        }
    }

    private boolean trainneeDuplicateUser() {
        boolean status = true;
        if (completedinsertedDATA.size() > 0) {
            for (int i = 0; i < completedinsertedDATA.size(); i++) {
                if (completedinsertedDATA.get(i).getTOPIC_CD().get(0).equals(topic_cd)) {
                    status = false;
                    break;
                }
            }
        }
        return status;
    }


    private class BackgroundTask extends AsyncTask<Void, String, String> {
        private Context context;
        ArrayList<TrainingTopicGetterSetter> completedinsertedDATA;

        BackgroundTask(Context context, ArrayList<TrainingTopicGetterSetter> completedinsertedDATA) {
            this.completedinsertedDATA = completedinsertedDATA;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            ;
            // TODO Auto-generated method stub
            super.onPreExecute();
            loading = ProgressDialog.show(SaleTeamTrainingActivity.this, "Uplaoding Sale Data", "Please wait...", false, false);

        }

        @SuppressWarnings("deprecation")
        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub
            try {
                if (completedinsertedDATA.size() > 0) {
                    boolean status = false;
                    String final_xml = "";
                    for (int j = 0; j < completedinsertedDATA.size(); j++) {
                        if (completedinsertedDATA.get(j).getStaus().equalsIgnoreCase("N")) {
                            status = true;
                            String onXML = "[SALES_TEAM_TRAINEE_DATA][SALES_TEAM_TRAINEE_CD]"
                                    + completedinsertedDATA.get(j).getTrainee_cd()
                                    + "[/SALES_TEAM_TRAINEE_CD]"
                                    + "[CREATED_BY]"
                                    + username
                                    + "[/CREATED_BY]"
                                    + "[TOPIC_CD]"
                                    + completedinsertedDATA.get(j).getTOPIC_CD().get(0)
                                    + "[/TOPIC_CD]"
                                    + "[VISIT_DATE]"
                                    + visit_date
                                    + "[/VISIT_DATE]"
                                    + "[MID]"
                                    + "0"
                                    + "[/MID]"
                                    + "[/SALES_TEAM_TRAINEE_DATA]";
                            final_xml = final_xml + onXML;
                        }
                    }

                    if (status) {
                        final String audit_xml = "[DATA]" + final_xml + "[/DATA]";
                        SoapObject request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_XML);
                        request.addProperty("XMLDATA", audit_xml);
                        request.addProperty("KEYS", "SALES_TEAM_TRAINEE_DATA");
                        request.addProperty("USERNAME", username);
                        request.addProperty("MID", "0");
                        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                        envelope.dotNet = true;
                        envelope.setOutputSoapObject(request);
                        HttpTransportSE androidHttpTransport = new HttpTransportSE(CommonString.URL);
                        androidHttpTransport.call(CommonString.SOAP_ACTION + CommonString.METHOD_UPLOAD_XML, envelope);
                        Object result = (Object) envelope.getResponse();
                        if (result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                            db.open();
                            db.insertSALETEAMTRAININGDATA(visit_date, username, completedinsertedDATA);
                            db.updateSaleTeamTraineeStatus(visit_date, CommonString.KEY_U);
                        }
                        if (result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                            return CommonString.KEY_SUCCESS;
                        } else {
                            return CommonString.METHOD_UPLOAD_XML;
                        }
                    }
                }

            } catch (MalformedURLException e) {
                loading.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        message(CommonString.MESSAGE_EXCEPTION);
                    }
                });

            } catch (IOException e) {
                loading.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        message(CommonString.MESSAGE_SOCKETEXCEPTION);
                        // TODO Auto-generated method stub
                    }
                });
            } catch (Exception e) {
                loading.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        message(CommonString.MESSAGE_EXCEPTION);
                    }
                });
            }

            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            loading.dismiss();
            if (result.contains(CommonString.KEY_SUCCESS)) {
                Toast.makeText(getApplicationContext(), "Data has been saved and upload .", Toast.LENGTH_LONG).show();
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                SaleTeamTrainingActivity.this.finish();
            } else if (result.contains(CommonString.METHOD_UPLOAD_XML)) {
                message("Uploading data error ! Please try again");
            } else if (!result.equals("")) {
                Toast.makeText(getApplicationContext(), CommonString.MESSAGE_SOCKETEXCEPTION, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void message(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("Parinaam").setMessage(msg);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

}