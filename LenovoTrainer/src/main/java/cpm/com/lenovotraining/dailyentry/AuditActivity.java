package cpm.com.lenovotraining.dailyentry;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cpm.com.lenovotraining.R;
import cpm.com.lenovotraining.constants.CommonString;
import cpm.com.lenovotraining.database.Database;
import cpm.com.lenovotraining.xmlgettersetter.AddNewEmployeeGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.AuditChecklistAnswerGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.AuditChecklistGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.EmpCdIsdGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.JCPGetterSetter;

public class AuditActivity extends AppCompatActivity {
    //RecyclerView recyclerView;
    Database db;
    HashMap<AuditChecklistGetterSetter, ArrayList<AuditChecklistGetterSetter>> listDataChild = new HashMap<>();
    ArrayList<AuditChecklistGetterSetter> auditChecklistGetterSetters = new ArrayList<>();
    ArrayList<AuditChecklistGetterSetter> listDataHeader = new ArrayList<>();
    String store_cd, isd_cd, training_mode_cd, manned, isd_image, visit_date;
    private SharedPreferences preferences = null;
    AddNewEmployeeGetterSetter addNewEmployeeGetterSetter;
    EmpCdIsdGetterSetter empCdIsdGetterSetter;
    ExpandableListView expListView;
    ExpandableListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        isd_cd = getIntent().getStringExtra(CommonString.KEY_ISD_CD);
        isd_image = getIntent().getStringExtra(CommonString.KEY_ISD_IMAGE);
        training_mode_cd = getIntent().getStringExtra(CommonString.KEY_TRAINING_MODE_CD);
        manned = getIntent().getStringExtra(CommonString.KEY_MANAGED);
        addNewEmployeeGetterSetter = getIntent().getParcelableExtra(CommonString.KEY_NEW_EMPLOYEE);
        empCdIsdGetterSetter = (EmpCdIsdGetterSetter) getIntent().getSerializableExtra(CommonString.KEY_NEW_ISD);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        store_cd = preferences.getString(CommonString.KEY_STORE_CD, null);
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        db = new Database(getApplicationContext());
        setTitle("ISD Skill - " + visit_date);
        db.open();

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_audit);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateChecklist(listDataChild, listDataHeader)) {
                    Intent in = new Intent(getApplicationContext(), TrainingActivity.class);
                    in.putExtra(CommonString.KEY_ISD_CD, isd_cd);
                    in.putExtra(CommonString.KEY_TRAINING_MODE_CD, training_mode_cd);
                    in.putExtra(CommonString.KEY_MANAGED, manned);
                    in.putExtra(CommonString.KEY_ISD_IMAGE, isd_image);
                    if (isd_cd.equals("0") && addNewEmployeeGetterSetter != null)
                        in.putExtra(CommonString.KEY_NEW_EMPLOYEE, addNewEmployeeGetterSetter);
                    ////audit checklist category data
                    in.putExtra(CommonString.KEY_AUDIT_DATA, listDataChild);
                    in.putExtra(CommonString.KEY_NEW_ISD, empCdIsdGetterSetter);
                    startActivity(in);
                    overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                    finish();
                } else {
                    Snackbar.make(fab, "Please Select Checklist", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        expListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }

            @Override
            public void onScrollStateChanged(AbsListView arg0, int arg1) {
                expListView.clearFocus();
                expListView.invalidateViews();

                InputMethodManager inputManager = (InputMethodManager) getApplicationContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                if (getCurrentFocus() != null) {
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    getCurrentFocus().clearFocus();
                }
            }
        });

        // Listview Group click listener
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                return false;
            }
        });

        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                InputMethodManager inputManager = (InputMethodManager) getApplicationContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                if (getCurrentFocus() != null) {
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    getCurrentFocus().clearFocus();
                }
            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                InputMethodManager inputManager = (InputMethodManager) getApplicationContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                if (getCurrentFocus() != null) {
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    getCurrentFocus().clearFocus();
                }
            }
        });

        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                return false;
            }
        });
        validateDATA();
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);
//        for (int i = 0; i < listAdapter.getGroupCount(); i++)
//            expListView.expandGroup(i);
    }


    private void validateDATA() {
        db.open();
        listDataChild.clear();
        listDataHeader.clear();
        ArrayList<AuditChecklistGetterSetter> auditChecklistCategoryList;
        auditChecklistCategoryList = db.getAuditChecklistCategoryData();
        if (auditChecklistCategoryList.size() > 0) {
            for (int i = 0; i < auditChecklistCategoryList.size(); i++) {
                listDataHeader.add(auditChecklistCategoryList.get(i));
                auditChecklistGetterSetters = db.getAuditData(auditChecklistCategoryList.get(i).getCHECKLIST_CATEGORY_CD().get(0));
                listDataChild.put(listDataHeader.get(i), auditChecklistGetterSetters);
            }
        }
    }

    @Override
    public void onBackPressed() {
    }

    public class CustomSpinnerAdapter extends BaseAdapter {
        Context context;
        ArrayList<AuditChecklistAnswerGetterSetter> ans;
        LayoutInflater inflter;

        public CustomSpinnerAdapter(Context applicationContext, ArrayList<AuditChecklistAnswerGetterSetter> ans) {
            this.context = applicationContext;
            this.ans = ans;
            inflter = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {
            return ans.size();
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
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflter.inflate(R.layout.custom_spinner_item, null);
            TextView names = (TextView) view.findViewById(R.id.tv_ans);
            names.setText(ans.get(i).getAnswer().get(0));
            return view;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return (super.getDropDownView(position, convertView, parent));
        }
    }

    class ExpandibleViewHolder {
        public TextView tv_check_list;
        public Spinner spinner_ans;
        CustomSpinnerAdapter customAdapter;
        CardView cardView;
    }

    public class ExpandableListAdapter extends BaseExpandableListAdapter {

        private Context _context;
        private List<AuditChecklistGetterSetter> _listDataHeader; // header titles
        // child data in format of header title, child title
        LayoutInflater infalInflater;
        private HashMap<AuditChecklistGetterSetter, ArrayList<AuditChecklistGetterSetter>> _listDataChild;

        public ExpandableListAdapter(Context context, ArrayList<AuditChecklistGetterSetter> listDataHeader,
                                     HashMap<AuditChecklistGetterSetter, ArrayList<AuditChecklistGetterSetter>> listChildData) {
            this._context = context;
            this._listDataHeader = listDataHeader;
            this._listDataChild = listChildData;
            infalInflater = (LayoutInflater) LayoutInflater.from(_context);
        }

        @Override
        public Object getChild(int groupPosition, int childPosititon) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                    .get(childPosititon);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @SuppressLint("NewApi")
        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild,
                                 View convertView, ViewGroup parent) {

            final AuditChecklistGetterSetter childText = (AuditChecklistGetterSetter) getChild(groupPosition, childPosition);
            ExpandibleViewHolder holder = null;
            if (convertView == null) {
                convertView = infalInflater.inflate(R.layout.audit_item_layout, null);
                holder = new ExpandibleViewHolder();
                holder.cardView = (CardView) convertView.findViewById(R.id.card_view);
                holder.tv_check_list = (TextView) convertView.findViewById(R.id.tv_checklist);
                holder.spinner_ans = (Spinner) convertView.findViewById(R.id.toggle_checklist);
                convertView.setTag(holder);
            } else {
                holder = (ExpandibleViewHolder) convertView.getTag();
            }

            holder.tv_check_list.setText(childText.getCHECKLIST().get(0));
            final ArrayList<AuditChecklistAnswerGetterSetter> checklistAnsList;
            checklistAnsList = db.getAuditChecklistAnswerData(childText.getCHECKLIST_CD().get(0));
            String str = "-Select-";
            AuditChecklistAnswerGetterSetter ch = new AuditChecklistAnswerGetterSetter();
            ch.setAnswer_cd("0");
            ch.setAnswer(str);
            checklistAnsList.add(0, ch);
            if (checklistAnsList.size() > 0) {
                holder.customAdapter = new CustomSpinnerAdapter(getApplicationContext(), checklistAnsList);
                holder.spinner_ans.setAdapter(holder.customAdapter);
                holder.spinner_ans.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        // childText.setAvailability(Integer.parseInt(checklistAnsList.get(pos).getAnswer_cd().get(0)));
                        _listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).
                                setAvailability(Integer.parseInt(checklistAnsList.get(pos).getAnswer_cd().get(0)));

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                for (int i = 0; i < checklistAnsList.size(); i++) {
                    int ans_cd = Integer.parseInt(checklistAnsList.get(i).getAnswer_cd().get(0));
                    if (childText.getAvailability() == ans_cd) {
                        holder.spinner_ans.setSelection(i);
                    }
                }
            }
            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return this._listDataHeader.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return this._listDataHeader.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            AuditChecklistGetterSetter headerTitle = (AuditChecklistGetterSetter) getGroup(groupPosition);
            if (convertView == null) {
                convertView = infalInflater.inflate(R.layout.list_group, null);
            }

            TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
            lblListHeader.setTypeface(null, Typeface.BOLD);
            lblListHeader.setText(headerTitle.getCHECKLIST_CATEGORY().get(0));
            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

    }

    boolean validateChecklist(HashMap<AuditChecklistGetterSetter, ArrayList<AuditChecklistGetterSetter>> listDataChild2,
                              ArrayList<AuditChecklistGetterSetter> listDataHeader2) {
        boolean flag = true;
        for (int i = 0; i < listDataHeader2.size(); i++) {
            for (int j = 0; j < listDataChild2.get(listDataHeader.get(i)).size(); j++) {
                int availebility = listDataChild.get(listDataHeader.get(i)).get(j).getAvailability();

                if (availebility == 0) {
                    flag = false;
                    break;
                } else {
                    flag = true;
                }
            }
        }
        return flag;
    }
}
