package cpm.com.lenovotraining.dailyentry;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import cpm.com.lenovotraining.R;
import cpm.com.lenovotraining.constants.CommonString;
import cpm.com.lenovotraining.database.Database;
import cpm.com.lenovotraining.xmlgettersetter.CoverageBean;


/**
 * Created by yadavendras on 19-08-2016.
 */
public class CheckOutStoreActivity extends AppCompatActivity {
    private SharedPreferences preferences = null;
    private String username, visit_date, store_cd, store_intime;
    private Database db;
    ArrayList<CoverageBean> coverageData = new ArrayList<>();
    public static String latitude = "0.0";
    public static String longitude = "0.0";
    private Dialog dialog;
    private ProgressBar pb;
    private TextView percentage, message;
    private Data data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        username = preferences.getString(CommonString.KEY_USERNAME, "");
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        store_cd = preferences.getString(CommonString.KEY_STORE_CD, "");
        db = new Database(this);
        db.open();
        coverageData = db.getCoverageSpecificData(store_cd, visit_date);
        store_intime = coverageData.get(0).getInTime();
        latitude = coverageData.get(0).getLatitude();
        longitude = coverageData.get(0).getLongitude();
        new BackgroundTask(this).execute();

    }

    private class BackgroundTask extends AsyncTask<Void, Data, String> {
        private Context context;

        BackgroundTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            dialog = new Dialog(context);
            dialog.setContentView(R.layout.custom);
            dialog.setTitle("Sending Checkout Data");
            dialog.setCancelable(false);
            dialog.show();
            pb = (ProgressBar) dialog.findViewById(R.id.progressBar1);
            percentage = (TextView) dialog.findViewById(R.id.percentage);
            message = (TextView) dialog.findViewById(R.id.message);

        }

        @SuppressWarnings("deprecation")
        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub

            try {

                //String result = "";
                data = new Data();
                data.value = 20;
                data.name = "Checked out Data Uploading";
                publishProgress(data);


                String onXML = "[STORE_CHECK_OUT_STATUS][USER_ID]"
                        + username
                        + "[/USER_ID]" + "[STORE_ID]"
                        + store_cd
                        + "[/STORE_ID][LATITUDE]"
                        + latitude
                        + "[/LATITUDE][LOGITUDE]"
                        + longitude
                        + "[/LOGITUDE][CHECKOUT_DATE]"
                        + visit_date
                        + "[/CHECKOUT_DATE][CHECK_OUTTIME]"
                        + getCurrentTime()
                        + "[/CHECK_OUTTIME][CHECK_INTIME]"
                        + store_intime
                        + "[/CHECK_INTIME][CREATED_BY]"
                        + username
                        + "[/CREATED_BY][/STORE_CHECK_OUT_STATUS]";


                final String sos_xml = "[DATA]" + onXML
                        + "[/DATA]";

                SoapObject request = new SoapObject(CommonString.NAMESPACE, "Upload_Store_ChecOut_Status");
                request.addProperty("onXML", sos_xml);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(CommonString.URL);
                androidHttpTransport.call(CommonString.SOAP_ACTION + "Upload_Store_ChecOut_Status", envelope);
                Object result = (Object) envelope.getResponse();
                if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                    return "Upload_Store_ChecOut_Status";
                }
                if (result.toString().equalsIgnoreCase(CommonString.KEY_NO_DATA)) {
                    return "Upload_Store_ChecOut_Status";
                }
                // for failure
                if (result.toString().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                    return "Upload_Store_ChecOut_Status";
                }


                data.value = 100;
                data.name = "Checkout Done";
                publishProgress(data);

                if (result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                    db.updateCoverageStoreOutTime(store_cd, visit_date, getCurrentTime(), CommonString.KEY_C);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(CommonString.KEY_STORE_CD, "");
                    editor.putString(CommonString.KEY_MANAGED, "");
                    editor.putString(CommonString.KEY_TRAINING_MODE_CD, "");
                    editor.commit();
                    db.updateStoreStatusOnCheckout(store_cd, visit_date, CommonString.KEY_C);
                } else {
                    if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                        return "Upload_Store_ChecOut_Status";
                    }
                }
                return CommonString.KEY_SUCCESS;

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
        protected void onProgressUpdate(Data... values) {
            // TODO Auto-generated method stub

            pb.setProgress(values[0].value);
            percentage.setText(values[0].value + "%");
            message.setText(values[0].name);

        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            dialog.dismiss();

            if (result.equals(CommonString.KEY_SUCCESS)) {
                showMessage(CommonString.MESSAGE_CHECKOUT);
                finish();

            } else if (!result.equals("")) {
                Toast.makeText(getApplicationContext(), "Network Error Try Again", Toast.LENGTH_SHORT).show();
                finish();

            }

        }

    }

    class Data {
        int value;
        String name;
    }

    public String getCurrentTime() {
        Calendar m_cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String cdate = formatter.format(m_cal.getTime());
        return cdate;
    }

    public void showMessage(String msg) {
        new AlertDialog.Builder(CheckOutStoreActivity.this)
                .setTitle("Alert Dialog")
                .setMessage(msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setIcon(R.drawable.parinaam_logo_ico)
                .show();


    }

}
