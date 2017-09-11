package cpm.com.lenovotraining.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cpm.com.lenovotraining.R;
import cpm.com.lenovotraining.constants.CommonString;
import cpm.com.lenovotraining.database.Database;
import cpm.com.lenovotraining.xmlgettersetter.PosmGetterSetter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class PosmItemFragment extends Fragment {

    Database db;
    ArrayList<PosmGetterSetter> posmlist;

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private OnListScrollDetectedListener mScrollListener;

    String _pathforcheck, _path, str, visit_date;
    static int child_position = -1;

    private SharedPreferences preferences;

    RecyclerView recyclerView;

    String img1 = "", common_id;

    FloatingActionButton fab;

    MyItemRecyclerViewAdapter myItemRecyclerViewAdapter;

    String error_msg ="Please fill all the data";

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PosmItemFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static PosmItemFragment newInstance(int columnCount) {
        PosmItemFragment fragment = new PosmItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        db = new Database(getActivity());
        db.open();

        fab = (FloatingActionButton)getActivity().findViewById(R.id.fab);

        common_id = getArguments().getString(CommonString.KEY_COMMON_ID);

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        visit_date = preferences.getString(CommonString.KEY_DATE, null);

        str = CommonString.FILE_PATH;

        posmlist = db.getPOSMData();

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            myItemRecyclerViewAdapter = new MyItemRecyclerViewAdapter(posmlist, mListener);
            recyclerView.setAdapter(myItemRecyclerViewAdapter);

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                    boolean flag = isLastItemDisplaying(recyclerView);

                    mScrollListener.OnListScrollDetectedListener(flag);

                    myItemRecyclerViewAdapter.notifyDataSetChanged();

                }

            });
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                recyclerView.clearFocus();

                if(validateData(posmlist)) {
                    db.insertPosmData(posmlist,common_id);

                    getActivity().finish();
                }
                else{

                    Snackbar.make(view,error_msg,Snackbar.LENGTH_LONG).show();
                }

            }
        });

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
        mScrollListener = (OnListScrollDetectedListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(PosmGetterSetter item);
    }

    public interface OnListScrollDetectedListener {
        // TODO: Update argument type and name
        void OnListScrollDetectedListener(boolean isscrolled);
    }

    /**
     * Check whether the last item in RecyclerView is being displayed or not
     *
     * @param recyclerView which you would like to check
     * @return true if last position was Visible and false Otherwise
     */
    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1)
                return true;
        }
        return false;
    }

    //--------------------------------------------------

    public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

        private final List<PosmGetterSetter> mValues;
        private final PosmItemFragment.OnListFragmentInteractionListener mListener;

        public MyItemRecyclerViewAdapter(List<PosmGetterSetter> items, PosmItemFragment.OnListFragmentInteractionListener listener) {
            mValues = items;
            mListener = listener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.mItem = mValues.get(position);

            holder.tv_posm.setText(mValues.get(position).getPosm().get(0));

            holder.btn_cam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(!mValues.get(position).getQuantity().equals("")){

                        child_position = position;

                        _pathforcheck = mValues.get(position).getPosm_cd().get(0) + "POSM"
                                + "Image" + visit_date.replace("/", "") + getCurrentTime().replace(":", "") + ".jpg";

                        _path = CommonString.FILE_PATH + _pathforcheck;

                        startCameraActivity();
                    }
                    else{
                        Snackbar.make(recyclerView,CommonString.MESSAGE_FIRST_ENTER_QUANTITY,Snackbar.LENGTH_SHORT).show();
                    }

                }
            });

            holder.et_quantity.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                @Override
                public void onFocusChange(View v, boolean hasFocus) {

                    if (!hasFocus) {
                        final EditText Caption = (EditText) v;
                        String value1 = Caption.getText().toString().trim();
                        value1 = value1.replaceAll("[&^<>{}'$]", "");
                        if (value1.equals("")) {

                            mValues.get(position).setQuantity("");
                            holder.et_quantity.setText(mValues.get(position).getQuantity());

                        } else {

                            String s = value1.replaceFirst("^0+(?!$)", "");
                            mValues.get(position).setQuantity(s);
                            holder.et_quantity.setText(mValues.get(position).getQuantity());
                        }

                    }
                }
            });

           /* holder.et_quantity.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String str = s.toString().replaceFirst("^0+(?!$)", "");
                    mValues.get(position).setQuantity(str);
                    holder.et_quantity.setText(mValues.get(position).getQuantity());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });*/

            //holder.et_quantity.setText(mValues.get(position).getQuantity());

            if (!img1.equalsIgnoreCase("")) {
                if (position == child_position ) {
                    //childText.get(childPosition).setCamera("YES");
                    mValues.get(position).setPosm_img_str(img1);
                    //childText.setImg(img1);
                    img1 = "";

                }
            }

            if (mValues.get(position).getPosm_img_str()!=null && !mValues.get(position).getPosm_img_str().equals("")) {
                holder.btn_cam.setBackgroundResource(R.drawable.camera_icon_done);
            } else {
                holder.btn_cam.setBackgroundResource(R.drawable.camera_icon);
            }

            holder.et_quantity.setText(mValues.get(position).getQuantity());

        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView tv_posm;
            public final EditText et_quantity;
            public final Button btn_cam;
            public PosmGetterSetter mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                tv_posm = (TextView) view.findViewById(R.id.tv_posm);
                et_quantity = (EditText) view.findViewById(R.id.et_quantity);
                btn_cam = (Button) view.findViewById(R.id.btn_cam);
            }

            @Override
            public String toString() {
                return super.toString() + "'" + et_quantity.getText() + "'";
            }
        }


    }


    //Startcamera
    protected void startCameraActivity() {

        try {
            Log.i("MakeMachine", "startCameraActivity()");
            File file = new File(_path);
            Uri outputFileUri = Uri.fromFile(file);

            Intent intent = new Intent(
                    android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

            startActivityForResult(intent, 0);
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i("MakeMachine", "resultCode: " + resultCode);
        switch (resultCode) {
            case 0:
                Log.i("MakeMachine", "User cancelled");
                break;

            case -1:

                if (_pathforcheck != null && !_pathforcheck.equals("")) {
                    if (new File(str + _pathforcheck).exists()) {

                        //cam.setBackgroundResource(R.drawable.camera_list_tick);
                        img1 = _pathforcheck;
                        myItemRecyclerViewAdapter.notifyDataSetChanged();
                        _pathforcheck = "";
                        //Toast.makeText(getApplicationContext(), ""+image1, Toast.LENGTH_LONG).show();

                    }
                }

                break;
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getCurrentTime() {

        Calendar m_cal = Calendar.getInstance();

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String cdate = formatter.format(m_cal.getTime());

       /* String intime = m_cal.get(Calendar.HOUR_OF_DAY) + ":"
                + m_cal.get(Calendar.MINUTE) + ":" + m_cal.get(Calendar.SECOND);*/

        return cdate;

    }

    boolean validateData(List<PosmGetterSetter> listPosm) {
        boolean checkflag = true;

        //checkHeaderArray.clear();

            for (int j = 0; j < listPosm.size(); j++) {

                String quantity = listPosm.get(j).getQuantity();
                String img_str = listPosm.get(j).getPosm_img_str();
                if(quantity.equals("")){

                    checkflag=false;
                    error_msg = CommonString.MESSAGE_FILL_ALL_DATA_OR_ZERO;
                    //flag = false;
                    break;

                }else if (!quantity.equalsIgnoreCase("0") && img_str.equals("")) {

                    checkflag=false;

                    //flag = false;
                    error_msg = CommonString.MESSAGE_CLICK_IMAGE_FOR_QUANTITY;
                    break;

                } else{

                    //flag = true;
                    checkflag=true;
                }
            }

        //expListView.invalidate();

        return checkflag;

    }
}
