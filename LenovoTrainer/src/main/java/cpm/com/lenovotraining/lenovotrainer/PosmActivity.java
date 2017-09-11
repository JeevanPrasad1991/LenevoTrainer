package cpm.com.lenovotraining.lenovotrainer;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import cpm.com.lenovotraining.R;
import cpm.com.lenovotraining.constants.CommonString;
import cpm.com.lenovotraining.fragment.PosmItemFragment;
import cpm.com.lenovotraining.xmlgettersetter.PosmGetterSetter;

public class PosmActivity extends AppCompatActivity implements PosmItemFragment.OnListFragmentInteractionListener,PosmItemFragment.OnListScrollDetectedListener{

    FloatingActionButton fab;

    String key_str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posm);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String key_id = getIntent().getStringExtra(CommonString.KEY_ID);

       // key_str = getIntent().getStringExtra(CommonString.KEY_ID);

        fab = (FloatingActionButton) findViewById(R.id.fab);
       /* fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });*/

        FragmentManager fragmentManager = getSupportFragmentManager();

        PosmItemFragment cartfrag = new PosmItemFragment();

        Bundle bundle=new Bundle();
        bundle.putString(CommonString.KEY_COMMON_ID, key_id);

        //set Fragmentclass Arguments
        cartfrag.setArguments(bundle);

        fragmentManager.beginTransaction()
                .replace(R.id.frame_layout, cartfrag)
                .commit();
    }

    @Override
    public void onListFragmentInteraction(PosmGetterSetter item) {

    }

    @Override
    public void OnListScrollDetectedListener(boolean isscrolled) {
        if(isscrolled){
            fab.hide();
        }
        else{
            fab.show();
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

    }
}
