package cpm.com.lenovotraining.dailyentry;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

import cpm.com.lenovotraining.R;
import cpm.com.lenovotraining.constants.CommonString;
import cpm.com.lenovotraining.database.Database;
import cpm.com.lenovotraining.xmlgettersetter.AddNewEmployeeGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.AuditChecklistGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.EmpCdIsdGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.JCPGetterSetter;

public class AuditActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    Database db;
    ArrayList<AuditChecklistGetterSetter> auditChecklistGetterSetters = new ArrayList<>();
    MyItemRecyclerViewAdapter myItemRecyclerViewAdapter;
    boolean ischangedflag = false;
    String store_cd, isd_cd, training_mode_cd, manned,isd_image;

    private SharedPreferences preferences=null;

    AddNewEmployeeGetterSetter addNewEmployeeGetterSetter;
    EmpCdIsdGetterSetter empCdIsdGetterSetter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.rec_audit_data);

        isd_cd = getIntent().getStringExtra(CommonString.KEY_ISD_CD);
        isd_image = getIntent().getStringExtra(CommonString.KEY_ISD_IMAGE);

        training_mode_cd = getIntent().getStringExtra(CommonString.KEY_TRAINING_MODE_CD);
        manned = getIntent().getStringExtra(CommonString.KEY_MANAGED);
        addNewEmployeeGetterSetter = getIntent().getParcelableExtra(CommonString.KEY_NEW_EMPLOYEE);

        empCdIsdGetterSetter = (EmpCdIsdGetterSetter)getIntent().getSerializableExtra(CommonString.KEY_NEW_ISD);


        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        store_cd = preferences.getString(CommonString.KEY_STORE_CD, null);

        db = new Database(getApplicationContext());
        db.open();

        auditChecklistGetterSetters = db.getAuditData();

        if(auditChecklistGetterSetters.size()>0){

            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            myItemRecyclerViewAdapter = new MyItemRecyclerViewAdapter(auditChecklistGetterSetters);
            recyclerView.setAdapter(myItemRecyclerViewAdapter);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_audit);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getApplicationContext(), TrainingActivity.class);
                in.putExtra(CommonString.KEY_ISD_CD, isd_cd);
                in.putExtra(CommonString.KEY_TRAINING_MODE_CD, training_mode_cd);
                in.putExtra(CommonString.KEY_MANAGED,manned);
                in.putExtra(CommonString.KEY_ISD_IMAGE,isd_image);
                if(isd_cd.equals("0") && addNewEmployeeGetterSetter!=null)
                in.putExtra(CommonString.KEY_NEW_EMPLOYEE, addNewEmployeeGetterSetter);

                in.putExtra(CommonString.KEY_AUDIT_DATA, auditChecklistGetterSetters);

                in.putExtra(CommonString.KEY_NEW_ISD, empCdIsdGetterSetter);

                startActivity(in);

                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

                finish();
            }
        });
    }

    public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {
        private final List<AuditChecklistGetterSetter> mValues;
        public MyItemRecyclerViewAdapter(List<AuditChecklistGetterSetter> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.audit_item_layout, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.mItem = mValues.get(position);
            holder.tv_check_list.setText(mValues.get(position).getCHECKLIST().get(0));

            holder.tb.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    ischangedflag = true;
                    String val = ((ToggleButton) v).getText().toString();
                    if(val.equalsIgnoreCase("YES")){
                        mValues.get(position).setAvailability(1);
                    }
                    else{
                        mValues.get(position).setAvailability(0);
                    }
                }
            });

            holder.tb.setChecked(mValues.get(position).getAvailability()==1);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView tv_check_list;
            public final ToggleButton tb;
            public AuditChecklistGetterSetter mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;

                tv_check_list = (TextView) view.findViewById(R.id.tv_checklist);
                tb = (ToggleButton) view.findViewById(R.id.toggle_checklist);

            }


        }

    }
    @Override
    public void onBackPressed() {
    }
}
