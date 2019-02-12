package cpm.com.lenovotraining.multiselectionspin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatSpinner;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;

import cpm.com.lenovotraining.R;
import cpm.com.lenovotraining.xmlgettersetter.SaleTeamGetterSetter;

/**
 * Created by jeevanp on 2/2/2018.
 */

public class MultiSpinnerSearch extends AppCompatSpinner implements DialogInterface.OnCancelListener {
    private static final String TAG = MultiSpinnerSearch.class.getSimpleName();
    private String defaultText = "";
    private String spinnerTitle = "";
    private SpinnerListener listener;
    private int limit = -1;
    private int selected = 0;
    MultipleSelectionSpinAdapter adapter;
    ArrayList<SaleTeamGetterSetter> salesTeamList =new ArrayList<>();
    public static AlertDialog ad;
    Context context;
    public MultiSpinnerSearch(Context context) {
        super(context);
    }

    public MultiSpinnerSearch(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
        TypedArray a = arg0.obtainStyledAttributes(arg1, R.styleable.MultiSpinnerSearch);
        for (int i = 0; i < a.getIndexCount(); ++i) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.MultiSpinnerSearch_hintText) {
                spinnerTitle = a.getString(attr);
                defaultText = spinnerTitle;
                break;
            }
        }
        Log.i(TAG, "spinnerTitle: " + spinnerTitle);
        a.recycle();
    }
    public MultiSpinnerSearch(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
    }
    @Override
    public void onCancel(DialogInterface dialog) {
        // refresh text on spinner
        StringBuilder spinnerBuffer = new StringBuilder();
        for (int i = 0; i < salesTeamList.size(); i++) {
            if (salesTeamList.get(i).isSelected()) {
                spinnerBuffer.append(salesTeamList.get(i).getTrainee().get(0));
                spinnerBuffer.append(", ");
            }
        }

        String spinnerText = spinnerBuffer.toString();
        if (spinnerText.length() > 2)
            spinnerText = spinnerText.substring(0, spinnerText.length() - 2);
        else
            spinnerText = defaultText;

        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(getContext(), R.layout.spinner_custom_item, new String[]{spinnerText});
        setAdapter(adapterSpinner);
        if (adapter != null)
            adapter.notifyDataSetChanged();

        listener.onItemsSelected(salesTeamList);
    }

    @Override
    public boolean performClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Trainee List");
        final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.alert_dialog_listview_search, null);
        builder.setView(view);
        final ListView listView = (ListView) view.findViewById(R.id.alertSearchListView);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setFastScrollEnabled(false);
        adapter = new MultipleSelectionSpinAdapter(getContext(), salesTeamList);
        listView.setAdapter(adapter);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, " ITEMS : " + salesTeamList.size());
                dialog.cancel();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.i(TAG, " ITEMS : " + salesTeamList.size());
                dialogInterface.dismiss();
            }
        });
        builder.setOnCancelListener(this);
        ad = builder.show();
        return true;
    }

    public void setItems(ArrayList<SaleTeamGetterSetter> items, int position, SpinnerListener listener) {
        this.salesTeamList = items;
        this.listener = (SpinnerListener) listener;
        StringBuilder spinnerBuffer = new StringBuilder();

        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).isSelected()) {
                spinnerBuffer.append(items.get(i).getTrainee().get(0));
                spinnerBuffer.append(", ");
            }
        }
        if (spinnerBuffer.length() > 2)
            defaultText = spinnerBuffer.toString().substring(0, spinnerBuffer.toString().length() - 2);

        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(getContext(), R.layout.spinner_custom_item, new String[]{defaultText});
        setAdapter(adapterSpinner);

        if (position != -1) {
            items.get(position).setSelected(true);
            //listener.onItemsSelected(items);
            onCancel(null);
        }
    }


    //Adapter Class

    public class MultipleSelectionSpinAdapter extends BaseAdapter {

        ArrayList<SaleTeamGetterSetter> arrayList;
        ArrayList<SaleTeamGetterSetter> mOriginalValues; // Original Values
        LayoutInflater inflater;

        public MultipleSelectionSpinAdapter(Context context, ArrayList<SaleTeamGetterSetter> arrayList) {
            this.arrayList = arrayList;
            inflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private class ViewHolder {
            TextView textView;
            CheckBox checkBox;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            MultipleSelectionSpinAdapter.ViewHolder holder;

            if (convertView == null) {
                holder = new MultipleSelectionSpinAdapter.ViewHolder();
                convertView = inflater.inflate(R.layout.item_listview_multiple, parent, false);
                holder.textView = (TextView) convertView.findViewById(R.id.alertTextView);
                holder.checkBox = (CheckBox) convertView.findViewById(R.id.alertCheckbox);
                convertView.setTag(holder);
            } else {
                holder = (MultipleSelectionSpinAdapter.ViewHolder) convertView.getTag();
            }

            //  final int backgroundColor = (position%2 == 0) ? R.color.list_even : R.color.list_odd;
            //  convertView.setBackgroundColor(ContextCompat.getColor(getContext(), backgroundColor));
            final SaleTeamGetterSetter data = arrayList.get(position);

            holder.textView.setText(data.getTrainee().get(0));
            holder.textView.setTypeface(null, Typeface.NORMAL);
            holder.checkBox.setChecked(data.isSelected());

            convertView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (data.isSelected()) { // deselect
                        selected--;
                    } else { // selected
                        selected++;
                    }
                    final MultipleSelectionSpinAdapter.ViewHolder temp = (MultipleSelectionSpinAdapter.ViewHolder) v.getTag();
                    temp.checkBox.setChecked(!temp.checkBox.isChecked());
                    data.setSelected(!data.isSelected());
                    Log.i("", "On Click Selected Item : " + data.getTrainee().get(0) + " : " + data.isSelected());
                    notifyDataSetChanged();
                }
            });
            if (data.isSelected()) {
                holder.textView.setTypeface(null, Typeface.NORMAL);
            }
            holder.checkBox.setTag(holder);
            return convertView;
        }


    }

}