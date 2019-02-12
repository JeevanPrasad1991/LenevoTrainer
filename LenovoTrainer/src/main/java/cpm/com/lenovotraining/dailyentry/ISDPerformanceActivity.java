package cpm.com.lenovotraining.dailyentry;

import android.content.Context;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cpm.com.lenovotraining.R;
import cpm.com.lenovotraining.constants.CommonString;
import cpm.com.lenovotraining.database.Database;

import cpm.com.lenovotraining.xmlgettersetter.IsdPerformanceGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.JCPGetterSetter;

public class ISDPerformanceActivity extends AppCompatActivity {

    Database database;

    String store_cd, visit_date;

    private SharedPreferences preferences = null;

    ArrayList<IsdPerformanceGetterSetter> isdPerformanceGetterSetters;

    RecyclerView performance_data;

    IsdAddedAdapter myItemRecyclerViewAdapter;

    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_isdperformance);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        performance_data = (RecyclerView) findViewById(R.id.rec_performance);
        linearLayout = (LinearLayout) findViewById(R.id.no_data_lay);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        store_cd = preferences.getString(CommonString.KEY_STORE_CD, null);
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        database = new Database(getApplicationContext());
        database.open();
        isdPerformanceGetterSetters = database.getIsdPerfromanceData(visit_date, store_cd);
        if (isdPerformanceGetterSetters.size() > 0) {
            performance_data.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            myItemRecyclerViewAdapter = new IsdAddedAdapter(isdPerformanceGetterSetters);
            performance_data.setAdapter(myItemRecyclerViewAdapter);
            performance_data.setVisibility(View.VISIBLE);
        } else {
            linearLayout.setVisibility(View.VISIBLE);
            performance_data.setVisibility(View.GONE);
        }

    }

    public class IsdAddedAdapter extends RecyclerView.Adapter<IsdAddedAdapter.MyViewHolder> {
        private final List<IsdPerformanceGetterSetter> mValues;
        List<IsdPerformanceGetterSetter> data = Collections.emptyList();

        public IsdAddedAdapter(List<IsdPerformanceGetterSetter> items) {
            mValues = items;

        }

        @Override
        public IsdAddedAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_performance_layout, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final IsdAddedAdapter.MyViewHolder viewHolder, final int position) {
            viewHolder.mItem = mValues.get(position);
            viewHolder.tv_date.setText(mValues.get(position).getTRAINING_DATE().get(0));
            viewHolder.tv_type.setText(mValues.get(position).getTRAINING_TYPE().get(0));
            viewHolder.tv_isd.setText(mValues.get(position).getISD().get(0));
            viewHolder.tv_topic.setText(mValues.get(position).getTOPIC().get(0));
            viewHolder.tv_grooming.setText(mValues.get(position).getGROOMING_SCORE().get(0));
            viewHolder.tv_quiz.setText(mValues.get(position).getQUIZ_SCORE().get(0));
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }


        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tv_date, tv_topic, tv_type, tv_isd, tv_grooming, tv_quiz;
            public IsdPerformanceGetterSetter mItem;

            public MyViewHolder(View itemView) {
                super(itemView);
                tv_isd = (TextView) itemView.findViewById(R.id.tv_isd);
                tv_topic = (TextView) itemView.findViewById(R.id.tv_topic);
                tv_type = (TextView) itemView.findViewById(R.id.tv_type);
                tv_date = (TextView) itemView.findViewById(R.id.tv_date);
                tv_grooming = (TextView) itemView.findViewById(R.id.tv_grooming);
                tv_quiz = (TextView) itemView.findViewById(R.id.tv_score);
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            finish();

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        finish();
    }
}
