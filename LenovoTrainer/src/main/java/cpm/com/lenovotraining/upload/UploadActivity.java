package cpm.com.lenovotraining.upload;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import cpm.com.lenovotraining.R;
import cpm.com.lenovotraining.constants.CommonString;
import cpm.com.lenovotraining.database.Database;
import cpm.com.lenovotraining.geocode.GeotaggingBeans;
import cpm.com.lenovotraining.xmlHandler.FailureXMLHandler;
import cpm.com.lenovotraining.xmlgettersetter.AddNewEmployeeGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.AuditChecklistGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.CoverageBean;
import cpm.com.lenovotraining.xmlgettersetter.FailureGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.PosmGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.QuizAnwserGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.StoreDataGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.TrainingTopicGetterSetter;

public class UploadActivity extends AppCompatActivity {

    private Dialog dialog;
    private ProgressBar pb;
    private TextView percentage, message;
    Data data;
    private FailureGetterSetter failureGetterSetter = null;
    private String visit_date, username, app_ver;
    private SharedPreferences preferences;
    Database database;
    String errormsg = "";
    String Path;
    boolean up_success_flag = true;
    boolean isDialogShowing = false;
    private ArrayList<CoverageBean> coverageBeanlist = new ArrayList<>();
    String datacheck = "";
    String[] words;
    String validity;
    int mid;
    ArrayList<AuditChecklistGetterSetter> auditData;
    ArrayList<QuizAnwserGetterSetter> quizData;
    ArrayList<AddNewEmployeeGetterSetter> newEmpData;
    ArrayList<AddNewEmployeeGetterSetter> newEmpMannagedZeroData = new ArrayList<>();
    ArrayList<GeotaggingBeans> geodata = new ArrayList<GeotaggingBeans>();
    ArrayList<TrainingTopicGetterSetter> traineCDataList = new ArrayList<>();
    ArrayList<TrainingTopicGetterSetter> trainingTopicList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        app_ver = preferences.getString(CommonString.KEY_VERSION, "");
        database = new Database(this);
        database.open();
        Path = CommonString.FILE_PATH;
        new UploadDataTask(this).execute();
    }


    class Data {
        int value;
        String name;
    }


    public String UploadImage(String path, String folder_name) throws Exception {
        errormsg = "";
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(Path + path, o);
        // The new size we want to scale to
        final int REQUIRED_SIZE = 1639;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        Bitmap bitmap = BitmapFactory.decodeFile(Path + path, o2);

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        String ba1 = Base64.encodeBytes(ba);

        SoapObject request = new SoapObject(CommonString.NAMESPACE,
                CommonString.METHOD_UPLOAD_IMAGE);

        String[] split = path.split("/");
        String path1 = split[split.length - 1];

        request.addProperty("img", ba1);
        request.addProperty("name", path1);
        request.addProperty("FolderName", folder_name);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(CommonString.URL);
        androidHttpTransport.call(CommonString.SOAP_ACTION_UPLOAD_IMAGE, envelope);
        Object result = (Object) envelope.getResponse();

        if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {

            if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                return CommonString.KEY_FALSE;
            }

            SAXParserFactory saxPF = SAXParserFactory.newInstance();
            SAXParser saxP = saxPF.newSAXParser();
            XMLReader xmlR = saxP.getXMLReader();

            // for failure
            FailureXMLHandler failureXMLHandler = new FailureXMLHandler();
            xmlR.setContentHandler(failureXMLHandler);

            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(result.toString()));
            xmlR.parse(is);

            failureGetterSetter = failureXMLHandler.getFailureGetterSetter();

            if (failureGetterSetter.getStatus().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                errormsg = failureGetterSetter.getErrorMsg();
                return CommonString.KEY_FAILURE;
            }
        } else {
            new File(Path + path).delete();

        }

        return result.toString();
    }


    public void showMessage(String msg) {

        if (!isDialogShowing) {

            isDialogShowing = true;

            new AlertDialog.Builder(UploadActivity.this)
                    .setTitle("Alert Dialog")
                    .setMessage(msg)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            //isDialogShowing = false;
                            finish();
                        }
                    })

                    .setIcon(R.drawable.parinaam_logo_ico)
                    .show();

        }
    }

    private class UploadDataTask extends AsyncTask<Void, Data, String> {
        private Context context;

        UploadDataTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            dialog = new Dialog(context);
            dialog.setContentView(R.layout.custom_dialog);
            dialog.setTitle("Uploading Data");
            dialog.setCancelable(false);
            dialog.show();
            pb = (ProgressBar) dialog.findViewById(R.id.progressBar1);
            percentage = (TextView) dialog.findViewById(R.id.percentage);
            message = (TextView) dialog.findViewById(R.id.message);
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub

            try {

                data = new Data();
                data.value = 10;
                data.name = "Uploading Data";
                publishProgress(data);
                database.open();
                coverageBeanlist = database.getCoverageData(visit_date);
                for (int i = 0; i < coverageBeanlist.size(); i++) {
                    if (!coverageBeanlist.get(i).getStatus().equalsIgnoreCase(CommonString.KEY_U)) {
                        String onXML = "[DATA][USER_DATA][STORE_CD]"
                                + coverageBeanlist.get(i).getStoreId()
                                + "[/STORE_CD]" + "[VISIT_DATE]"
                                + coverageBeanlist.get(i).getVisitDate()
                                + "[/VISIT_DATE][LATITUDE]"
                                + coverageBeanlist.get(i).getLatitude()
                                + "[/LATITUDE][APP_VERSION]"
                                + app_ver
                                + "[/APP_VERSION][LONGITUDE]"
                                + coverageBeanlist.get(i).getLongitude()
                                + "[/LONGITUDE][IN_TIME]"
                                + coverageBeanlist.get(i).getInTime()
                                + "[/IN_TIME][OUT_TIME]"
                                + coverageBeanlist.get(i).getOutTime()
                                + "[/OUT_TIME][UPLOAD_STATUS]"
                                + "N"
                                + "[/UPLOAD_STATUS][USER_ID]" + username
                                + "[/USER_ID][TMODE_CD]" + coverageBeanlist.get(i).getTraining_mode_cd() +
                                "[/TMODE_CD][MANAGE]" + coverageBeanlist.get(i).getManaged() +
                                "[/MANAGE][IMAGE_URL]" + coverageBeanlist.get(i).getImage()
                                + "[/IMAGE_URL][REASON_ID]"
                                + coverageBeanlist.get(i).getReasonid()
                                + "[/REASON_ID][REASON_REMARK]"
                                + coverageBeanlist.get(i).getRemark()
                                + "[/REASON_REMARK][/USER_DATA][/DATA]";


                        SoapObject request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_DR_STORE_COVERAGE);
                        request.addProperty("onXML", onXML);
                        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                        envelope.dotNet = true;
                        envelope.setOutputSoapObject(request);
                        HttpTransportSE androidHttpTransport = new HttpTransportSE(CommonString.URL);
                        androidHttpTransport.call(CommonString.SOAP_ACTION + CommonString.METHOD_UPLOAD_DR_STORE_COVERAGE, envelope);
                        Object result = (Object) envelope.getResponse();
                        datacheck = result.toString();
                        datacheck = datacheck.replace("\"", "");
                        words = datacheck.split("\\;");
                        validity = (words[0]);
                        if (validity.equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                            database.updateCoverageStatus(coverageBeanlist.get(i).getMID(), CommonString.KEY_P);
                            database.updateStoreStatusOnLeave(coverageBeanlist.get(i).getStoreId(), coverageBeanlist.get(i).getVisitDate(), CommonString.KEY_P);
                        } else {
                            if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                                return CommonString.METHOD_UPLOAD_DR_STORE_COVERAGE;
                            }

                            if (result.toString().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                                return CommonString.METHOD_UPLOAD_DR_STORE_COVERAGE;
                            }

                        }
                        mid = Integer.parseInt((words[1]));
                        data.value = 20;
                        data.name = "Uploading Coverage Data";
                        publishProgress(data);
                        String final_xml = "";


                        //		uploading New Employee data
                        final_xml = "";
                        onXML = "";
                        database.open();
                        newEmpData = database.getNewEmployeeInsertedData(coverageBeanlist.get(i).getStoreId());
                        if (newEmpData.size() > 0) {
                            for (int j = 0; j < newEmpData.size(); j++) {
                                String isIsd;
                                if (newEmpData.get(j).isIsd()) {
                                    isIsd = "1";
                                } else {
                                    isIsd = "0";
                                }
                                onXML = "[NEW_EMPLOYEE_DATA][ISD_CD]"
                                        + "0"
                                        + "[/ISD_CD]"
                                        + "[MID]"
                                        + mid
                                        + "[/MID]"
                                        + "[CREATED_BY]"
                                        + username
                                        + "[/CREATED_BY]"
                                        + "[NAME]"
                                        + newEmpData.get(j).getName()
                                        + "[/NAME]"
                                        + "[EMAIL]"
                                        + newEmpData.get(j).getEmail()
                                        + "[/EMAIL]"
                                        + "[PHONE_NO]"
                                        + newEmpData.get(j).getPhone()
                                        + "[/PHONE_NO]"
                                        + "[KEY_ID]"
                                        + newEmpData.get(j).getKey_id()
                                        + "[/KEY_ID]"
                                        + "[IS_ISD]"
                                        + isIsd
                                        + "[/IS_ISD]"
                                        + "[/NEW_EMPLOYEE_DATA]";

                                final_xml = final_xml + onXML;

                            }

                            final String employee_xml = "[DATA]" + final_xml + "[/DATA]";
                            request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_XML);
                            request.addProperty("XMLDATA", employee_xml);
                            request.addProperty("KEYS", "NEW_EMPLOYEE_DATA");
                            request.addProperty("USERNAME", username);
                            request.addProperty("MID", mid);
                            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                            envelope.dotNet = true;
                            envelope.setOutputSoapObject(request);
                            androidHttpTransport = new HttpTransportSE(CommonString.URL);
                            androidHttpTransport.call(CommonString.SOAP_ACTION + CommonString.METHOD_UPLOAD_XML, envelope);
                            result = (Object) envelope.getResponse();
                            if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                return CommonString.METHOD_UPLOAD_XML;
                            }
                            if (result.toString().equalsIgnoreCase(CommonString.KEY_NO_DATA)) {
                                return CommonString.METHOD_UPLOAD_XML;
                            }

                            if (result.toString().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                                return CommonString.METHOD_UPLOAD_XML;
                            }

                            data.value = 30;
                            data.name = "NEW EMPLOYEE DATA";
                            publishProgress(data);
                        }


                        //		uploading Audit data for New Employee
                        final_xml = "";
                        onXML = "";
                        database.open();
                        auditData = database.getAuditInsertedNewEmpData(coverageBeanlist.get(i).getStoreId());
                        if (auditData.size() > 0) {
                            for (int j = 0; j < auditData.size(); j++) {
                                onXML = "[AUDIT_DATA_NEW_EMPLOYEE][ISD_CD]"
                                        + auditData.get(j).getIsd_cd()
                                        + "[/ISD_CD]"
                                        + "[MID]"
                                        + mid
                                        + "[/MID]"
                                        + "[CREATED_BY]"
                                        + username
                                        + "[/CREATED_BY]"
                                        + "[KEY_ID]"
                                        + auditData.get(j).getKey_id()
                                        + "[/KEY_ID]"
                                        + "[CHECKLIST_CD]"
                                        + auditData.get(j).getCHECKLIST_CD().get(0)
                                        + "[/CHECKLIST_CD]"
                                        + "[AVAILABILITY]"
                                        + auditData.get(j).getAvailability()
                                        + "[/AVAILABILITY]"
                                        + "[/AUDIT_DATA_NEW_EMPLOYEE]";

                                final_xml = final_xml + onXML;

                            }

                            final String audit_xml = "[DATA]" + final_xml + "[/DATA]";

                            request = new SoapObject(
                                    CommonString.NAMESPACE,
                                    CommonString.METHOD_UPLOAD_XML);
                            request.addProperty("XMLDATA", audit_xml);
                            request.addProperty("KEYS", "AUDIT_DATA_NEW_EMPLOYEE");
                            request.addProperty("USERNAME", username);
                            request.addProperty("MID", mid);

                            envelope = new SoapSerializationEnvelope(
                                    SoapEnvelope.VER11);
                            envelope.dotNet = true;
                            envelope.setOutputSoapObject(request);

                            androidHttpTransport = new HttpTransportSE(
                                    CommonString.URL);

                            androidHttpTransport.call(
                                    CommonString.SOAP_ACTION + CommonString.METHOD_UPLOAD_XML,
                                    envelope);
                            result = (Object) envelope.getResponse();


                            if (!result.toString().equalsIgnoreCase(
                                    CommonString.KEY_SUCCESS)) {
                                return CommonString.METHOD_UPLOAD_XML;
                            }

                            if (result.toString().equalsIgnoreCase(
                                    CommonString.KEY_NO_DATA)) {
                                return CommonString.METHOD_UPLOAD_XML;
                            }

                            if (result.toString().equalsIgnoreCase(
                                    CommonString.KEY_FAILURE)) {
                                return CommonString.METHOD_UPLOAD_XML;
                            }
                            data.value = 40;
                            data.name = "AUDIT DATA NEW EMPLOYEE";
                            publishProgress(data);

                        }

                        //		uploading TRAINING_TOPIC_DATA for new employee
                        final_xml = "";
                        onXML = "";
                        database.open();
                        ArrayList<TrainingTopicGetterSetter> trainingTopicList = database.getTrainningTopicfORNEWEMPLOYEEData(coverageBeanlist.get(i).getStoreId());
                        if (trainingTopicList.size() > 0) {

                            for (int j = 0; j < trainingTopicList.size(); j++) {

                                onXML = "[TRAINING_TOPIC_NEW_EMPLOYEE_DATA][ISD_CD]"
                                        + trainingTopicList.get(j).getIsd_cd()
                                        + "[/ISD_CD]"
                                        + "[MID]"
                                        + mid
                                        + "[/MID]"
                                        + "[CREATED_BY]"
                                        + username
                                        + "[/CREATED_BY]"
                                        + "[KEY_ID]"
                                        + trainingTopicList.get(j).getKey_id()
                                        + "[/KEY_ID]"
                                        + "[TOPIC_CD]"
                                        + trainingTopicList.get(j).getTOPIC_CD().get(0)
                                        + "[/TOPIC_CD]"
                                        + "[VISIT_DATE]"
                                        + coverageBeanlist.get(i).getVisitDate()
                                        + "[/VISIT_DATE]"
                                        + "[/TRAINING_TOPIC_NEW_EMPLOYEE_DATA]";

                                final_xml = final_xml + onXML;

                            }

                            final String audit_xml = "[DATA]" + final_xml + "[/DATA]";

                            request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_XML);
                            request.addProperty("XMLDATA", audit_xml);
                            request.addProperty("KEYS", "TRAINING_TOPIC_NEW_EMPLOYEE_DATA");
                            request.addProperty("USERNAME", username);
                            request.addProperty("MID", mid);
                            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                            envelope.dotNet = true;
                            envelope.setOutputSoapObject(request);
                            androidHttpTransport = new HttpTransportSE(CommonString.URL);
                            androidHttpTransport.call(CommonString.SOAP_ACTION + CommonString.METHOD_UPLOAD_XML,
                                    envelope);
                            result = (Object) envelope.getResponse();
                            if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                return CommonString.METHOD_UPLOAD_XML;
                            }
                            if (result.toString().equalsIgnoreCase(CommonString.KEY_NO_DATA)) {
                                return CommonString.METHOD_UPLOAD_XML;
                            }
                            if (result.toString().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                                return CommonString.METHOD_UPLOAD_XML;
                            }

                            data.value = 50;
                            data.name = "TRAINING T N EMPLOYEE DATA";
                            publishProgress(data);

                        }


                        //uploading Quiz data for new Employee
                        final_xml = "";
                        onXML = "";
                        database.open();
                        quizData = database.getQuizNewEmployeeData(coverageBeanlist.get(i).getStoreId());
                        if (quizData.size() > 0) {
                            for (int j = 0; j < quizData.size(); j++) {
                                if (quizData.get(j).getIsd_cd() != null) {
                                    onXML = "[QUIZ_DATA_NEW_EMPLOYEE]"
                                            + "[ISD_CD]"
                                            + quizData.get(j).getIsd_cd()
                                            + "[/ISD_CD]"
                                            + "[MID]"
                                            + mid
                                            + "[/MID]"
                                            + "[CREATED_BY]"
                                            + username
                                            + "[/CREATED_BY]"
                                            + "[TOPIC_CD]"
                                            + quizData.get(j).getTopic_cd()
                                            + "[/TOPIC_CD]"
                                            + "[QUESTION_CD]"
                                            + quizData.get(j).getQuestion_cd()
                                            + "[/QUESTION_CD]"
                                            + "[ANSWER_CD]"
                                            + quizData.get(j).getAnswer_cd()
                                            + "[/ANSWER_CD]"
                                            + "[TRAINING_MODE_CD]"
                                            + quizData.get(j).getTraining_mode_cd()
                                            + "[/TRAINING_MODE_CD]"
                                            + "[KEY_ID]"
                                            + quizData.get(j).getKey_id()
                                            + "[/KEY_ID]"
                                            + "[isd_image]"
                                            + quizData.get(j).getKey_isd_image()
                                            + "[/isd_image]"
                                            + "[/QUIZ_DATA_NEW_EMPLOYEE]";

                                    final_xml = final_xml + onXML;
                                }

                            }

                            final String sos_xml = "[DATA]" + final_xml + "[/DATA]";

                            request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_XML);
                            request.addProperty("XMLDATA", sos_xml);
                            request.addProperty("KEYS", "QUIZ_DATA_NEW_EMPLOYEE_NEW");
                            request.addProperty("USERNAME", username);
                            request.addProperty("MID", mid);
                            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                            envelope.dotNet = true;
                            envelope.setOutputSoapObject(request);
                            androidHttpTransport = new HttpTransportSE(CommonString.URL);
                            androidHttpTransport.call(CommonString.SOAP_ACTION + CommonString.METHOD_UPLOAD_XML, envelope);
                            result = (Object) envelope.getResponse();
                            if (!result.toString().equalsIgnoreCase(
                                    CommonString.KEY_SUCCESS)) {
                                return CommonString.METHOD_UPLOAD_XML;
                            }

                            if (result.toString().equalsIgnoreCase(
                                    CommonString.KEY_NO_DATA)) {
                                return CommonString.METHOD_UPLOAD_XML;
                            }

                            if (result.toString().equalsIgnoreCase(
                                    CommonString.KEY_FAILURE)) {
                                return CommonString.METHOD_UPLOAD_XML;
                            }
                            data.value = 60;
                            data.name = "QUIZ DATA NEW EMPLOYEE NEW";
                            publishProgress(data);

                        }


                        //uploading Quiz data
                        final_xml = "";
                        onXML = "";
                        database.open();
                        quizData = database.getQuizData(coverageBeanlist.get(i).getStoreId());
                        if (quizData.size() > 0) {

                            for (int j = 0; j < quizData.size(); j++) {

                                if (quizData.get(j).getIsd_cd() != null) {
                                    onXML = "[QUIZ_DATA]"
                                            + "[ISD_CD]"
                                            + quizData.get(j).getIsd_cd()
                                            + "[/ISD_CD]"
                                            + "[MID]"
                                            + mid
                                            + "[/MID]"
                                            + "[CREATED_BY]"
                                            + username
                                            + "[/CREATED_BY]"
                                            + "[TOPIC_CD]"
                                            + quizData.get(j).getTopic_cd()
                                            + "[/TOPIC_CD]"
                                            + "[QUESTION_CD]"
                                            + quizData.get(j).getQuestion_cd()
                                            + "[/QUESTION_CD]"
                                            + "[ANSWER_CD]"
                                            + quizData.get(j).getAnswer_cd()
                                            + "[/ANSWER_CD]"
                                            + "[TRAINING_MODE_CD]"
                                            + quizData.get(j).getTraining_mode_cd()
                                            + "[/TRAINING_MODE_CD]"
                                            + "[isd_image]"
                                            + quizData.get(j).getKey_isd_image()
                                            + "[/isd_image]"
                                            + "[/QUIZ_DATA]";

                                    final_xml = final_xml + onXML;
                                }

                            }

                            final String sos_xml = "[DATA]" + final_xml + "[/DATA]";
                            request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_XML);
                            request.addProperty("XMLDATA", sos_xml);
                            request.addProperty("KEYS", "QUIZ_DATA_NEW");
                            request.addProperty("USERNAME", username);
                            request.addProperty("MID", mid);
                            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                            envelope.dotNet = true;
                            envelope.setOutputSoapObject(request);
                            androidHttpTransport = new HttpTransportSE(CommonString.URL);
                            androidHttpTransport.call(CommonString.SOAP_ACTION + CommonString.METHOD_UPLOAD_XML, envelope);
                            result = (Object) envelope.getResponse();
                            if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                return CommonString.METHOD_UPLOAD_XML;
                            }

                            if (result.toString().equalsIgnoreCase(CommonString.KEY_NO_DATA)) {
                                return CommonString.METHOD_UPLOAD_XML;
                            }

                            if (result.toString().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                                return CommonString.METHOD_UPLOAD_XML;
                            }
                            data.value = 70;
                            data.name = "QUIZ DATA NEW";
                            publishProgress(data);
                        }


                        //		uploading ISD SKILLLLL DATA
                        final_xml = "";
                        onXML = "";
                        database.open();
                        auditData = database.getAuditInsertedData(coverageBeanlist.get(i).getStoreId());

                        if (auditData.size() > 0) {

                            for (int j = 0; j < auditData.size(); j++) {


                                onXML = "[AUDIT_DATA][ISD_CD]"
                                        + auditData.get(j).getIsd_cd()
                                        + "[/ISD_CD]"
                                        + "[MID]"
                                        + mid
                                        + "[/MID]"
                                        + "[CREATED_BY]"
                                        + username
                                        + "[/CREATED_BY]"
                                        + "[CHECKLIST_CD]"
                                        + auditData.get(j).getCHECKLIST_CD().get(0)
                                        + "[/CHECKLIST_CD]"
                                        + "[AVAILABILITY]"
                                        + auditData.get(j).getAvailability()
                                        + "[/AVAILABILITY]"
                                        + "[/AUDIT_DATA]";

                                final_xml = final_xml + onXML;

                            }
                            final String audit_xml = "[DATA]" + final_xml + "[/DATA]";
                            request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_XML);
                            request.addProperty("XMLDATA", audit_xml);
                            request.addProperty("KEYS", "AUDIT_DATA");
                            request.addProperty("USERNAME", username);
                            request.addProperty("MID", mid);
                            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                            envelope.dotNet = true;
                            envelope.setOutputSoapObject(request);
                            androidHttpTransport = new HttpTransportSE(CommonString.URL);
                            androidHttpTransport.call(CommonString.SOAP_ACTION + CommonString.METHOD_UPLOAD_XML, envelope);
                            result = (Object) envelope.getResponse();
                            if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                return CommonString.METHOD_UPLOAD_XML;
                            }
                            if (result.toString().equalsIgnoreCase(CommonString.KEY_NO_DATA)) {
                                return CommonString.METHOD_UPLOAD_XML;
                            }

                            if (result.toString().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                                return CommonString.METHOD_UPLOAD_XML;
                            }

                            data.value = 75;
                            data.name = "AUDIT DATA";
                            publishProgress(data);
                        }


                        //		uploading New Employee data
                        final_xml = "";
                        onXML = "";
                        database.open();
                        newEmpMannagedZeroData = database.getNewEmployeeForManagedZeroInsertedData(coverageBeanlist.get(i).getStoreId());
                        if (newEmpMannagedZeroData.size() > 0) {
                            for (int j = 0; j < newEmpMannagedZeroData.size(); j++) {
                                String isIsd;
                                if (newEmpMannagedZeroData.get(j).isIsd()) {
                                    isIsd = "1";
                                } else {
                                    isIsd = "0";
                                }
                                onXML = "[NEW_EMPLOYEE_MANAGED_ZERO_DATA][ISD_CD]"
                                        + "0"
                                        + "[/ISD_CD]"
                                        + "[MID]"
                                        + mid
                                        + "[/MID]"
                                        + "[CREATED_BY]"
                                        + username
                                        + "[/CREATED_BY]"
                                        + "[NAME]"
                                        + newEmpMannagedZeroData.get(j).getName()
                                        + "[/NAME]"
                                        + "[EMAIL]"
                                        + newEmpMannagedZeroData.get(j).getEmail()
                                        + "[/EMAIL]"
                                        + "[PHONE_NO]"
                                        + newEmpMannagedZeroData.get(j).getPhone()
                                        + "[/PHONE_NO]"
                                        + "[IMAGE]"
                                        + newEmpMannagedZeroData.get(j).getImage()
                                        + "[/IMAGE]"
                                        + "[MANAGED]"
                                        + newEmpMannagedZeroData.get(j).getManneged()
                                        + "[/MANAGED]"
                                        + "[IS_ISD]"
                                        + isIsd
                                        + "[/IS_ISD]"
                                        + "[/NEW_EMPLOYEE_MANAGED_ZERO_DATA]";

                                final_xml = final_xml + onXML;

                            }

                            final String employee_xml = "[DATA]" + final_xml + "[/DATA]";
                            request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_XML);
                            request.addProperty("XMLDATA", employee_xml);
                            request.addProperty("KEYS", "NEW_EMPLOYEE_MANAGED_ZERO_DATA");
                            request.addProperty("USERNAME", username);
                            request.addProperty("MID", mid);
                            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                            envelope.dotNet = true;
                            envelope.setOutputSoapObject(request);
                            androidHttpTransport = new HttpTransportSE(CommonString.URL);
                            androidHttpTransport.call(CommonString.SOAP_ACTION + CommonString.METHOD_UPLOAD_XML, envelope);
                            result = (Object) envelope.getResponse();
                            if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                return CommonString.METHOD_UPLOAD_XML;
                            }
                            if (result.toString().equalsIgnoreCase(CommonString.KEY_NO_DATA)) {
                                return CommonString.METHOD_UPLOAD_XML;
                            }
                            if (result.toString().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                                return CommonString.METHOD_UPLOAD_XML;
                            }

                            data.value = 80;
                            data.name = "NEW EMPLOYEE MANAGED ZERO DATA";
                            publishProgress(data);
                        }


                        //		uploading TRAINING_TOPIC_DATA
                        final_xml = "";
                        onXML = "";
                        database.open();
                        trainingTopicList = database.getTrainningTopicData(coverageBeanlist.get(i).getStoreId());
                        if (trainingTopicList.size() > 0) {

                            for (int j = 0; j < trainingTopicList.size(); j++) {

                                onXML = "[TRAINING_TOPIC_DATA][ISD_CD]"
                                        + trainingTopicList.get(j).getIsd_cd()
                                        + "[/ISD_CD]"
                                        + "[MID]"
                                        + mid
                                        + "[/MID]"
                                        + "[CREATED_BY]"
                                        + username
                                        + "[/CREATED_BY]"
                                        + "[KEY_ID]"
                                        + trainingTopicList.get(j).getKey_id()
                                        + "[/KEY_ID]"
                                        + "[TOPIC_CD]"
                                        + trainingTopicList.get(j).getTOPIC_CD().get(0)
                                        + "[/TOPIC_CD]"
                                        + "[VISIT_DATE]"
                                        + coverageBeanlist.get(i).getVisitDate()
                                        + "[/VISIT_DATE]"
                                        + "[/TRAINING_TOPIC_DATA]";

                                final_xml = final_xml + onXML;

                            }

                            final String audit_xml = "[DATA]" + final_xml + "[/DATA]";

                            request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_XML);
                            request.addProperty("XMLDATA", audit_xml);
                            request.addProperty("KEYS", "TRAINING_TOPIC_DATA");
                            request.addProperty("USERNAME", username);
                            request.addProperty("MID", mid);
                            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                            envelope.dotNet = true;
                            envelope.setOutputSoapObject(request);
                            androidHttpTransport = new HttpTransportSE(CommonString.URL);
                            androidHttpTransport.call(CommonString.SOAP_ACTION + CommonString.METHOD_UPLOAD_XML,
                                    envelope);
                            result = (Object) envelope.getResponse();
                            if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                return CommonString.METHOD_UPLOAD_XML;
                            }
                            if (result.toString().equalsIgnoreCase(CommonString.KEY_NO_DATA)) {
                                return CommonString.METHOD_UPLOAD_XML;
                            }
                            if (result.toString().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                                return CommonString.METHOD_UPLOAD_XML;
                            }

                            data.value = 85;
                            data.name = "TRAINING TOPIC DATA";
                            publishProgress(data);

                        }


                        database.open();
                        geodata = database.getGeotaggingData(coverageBeanlist.get(i).getStoreId());
                        if (geodata.size() > 0) {
                            final_xml = "";
                            onXML = "";
                            for (int i1 = 0; i1 < geodata.size(); i1++) {
                                onXML = "[DATA][USER_DATA][STORE_ID]"
                                        + Integer.parseInt(geodata.get(i1).getStoreId())
                                        + "[/STORE_ID]"
                                        + "[USERNAME]"
                                        + username
                                        + "[/USERNAME]"
                                        + "[Image1]"
                                        + geodata.get(i1).getUrl1()
                                        + "[/Image1][Latitude]"
                                        + Double.toString(geodata.get(i1).getLatitude())
                                        + "[/Latitude][Longitude]"
                                        + Double.toString(geodata.get(i1).getLongitude())
                                        + "[/Longitude][/USER_DATA][/DATA]";
                                final_xml = onXML;
                                request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_XML);
                                request.addProperty("XMLDATA", final_xml);
                                request.addProperty("KEYS", "GeoXML");
                                request.addProperty("USERNAME", username);
                                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                                envelope.dotNet = true;
                                envelope.setOutputSoapObject(request);
                                androidHttpTransport = new HttpTransportSE(CommonString.URL);
                                androidHttpTransport.call(CommonString.NAMESPACE + CommonString.METHOD_UPLOAD_XML, envelope);
                                result = (Object) envelope.getResponse();
                                if (result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                }
                            }
                            data.value = 89;
                            data.name = "GeoXML DATA";
                            publishProgress(data);
                        }


                        ///uploading SALES_TEAM_TRAINEE_DATA
                        final_xml = "";
                        onXML = "";
                        database.open();
                        traineCDataList = database.getSALETEAMTRaineeInsertedDATA(coverageBeanlist.get(i).getVisitDate());
                        if (traineCDataList.size() > 0) {
                            boolean status = false;
                            for (int j = 0; j < traineCDataList.size(); j++) {
                                if (traineCDataList.get(j).getStaus().equalsIgnoreCase("N")) {
                                    status = true;
                                    onXML = "[SALES_TEAM_TRAINEE_DATA][SALES_TEAM_TRAINEE_CD]"
                                            + traineCDataList.get(j).getTrainee_cd()
                                            + "[/SALES_TEAM_TRAINEE_CD]"
                                            + "[CREATED_BY]"
                                            + username
                                            + "[/CREATED_BY]"
                                            + "[TOPIC_CD]"
                                            + traineCDataList.get(j).getTOPIC_CD().get(0)
                                            + "[/TOPIC_CD]"
                                            + "[VISIT_DATE]"
                                            + coverageBeanlist.get(i).getVisitDate()
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
                                request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_XML);
                                request.addProperty("XMLDATA", audit_xml);
                                request.addProperty("KEYS", "SALES_TEAM_TRAINEE_DATA");
                                request.addProperty("USERNAME", username);
                                request.addProperty("MID", "0");
                                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                                envelope.dotNet = true;
                                envelope.setOutputSoapObject(request);
                                androidHttpTransport = new HttpTransportSE(CommonString.URL);
                                androidHttpTransport.call(CommonString.SOAP_ACTION + CommonString.METHOD_UPLOAD_XML, envelope);
                                result = (Object) envelope.getResponse();
                                if (result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                    database.updateSaleTeamTraineeStatus(coverageBeanlist.get(i).getVisitDate(), CommonString.KEY_U);
                                }
                                if (result.toString().equalsIgnoreCase(CommonString.KEY_NO_DATA)) {
                                    return CommonString.METHOD_UPLOAD_XML;
                                }
                                if (result.toString().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                                    return CommonString.METHOD_UPLOAD_XML;
                                }
                                data.value = 90;
                                data.name = "SALES_TEAM_TRAINEE_DATA";
                                publishProgress(data);
                            }
                        }


                        if (geodata.size() > 0) {
                            for (int k = 0; k < geodata.size(); k++) {
                                if (geodata.get(k).getUrl1() != null && !geodata.get(k).getUrl1().equals("")) {
                                    if (new File(CommonString.FILE_PATH + geodata.get(k).getUrl1()).exists()) {
                                        result = UploadImage(geodata.get(k).getUrl1(), "GeoTagImages");
                                        if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                            return "GeoTagImages";
                                        }
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                message.setText("GeoTagImages Uploaded");
                                            }
                                        });
                                    }


                                }
                            }
                            data.value = 92;
                            data.name = "GeoTagImages";
                            publishProgress(data);
                        }


                        //Uploading store Images
                        if (coverageBeanlist.get(i).getImage() != null && !coverageBeanlist.get(i).getImage().equals("")) {
                            if (new File(CommonString.FILE_PATH + coverageBeanlist.get(i).getImage()).exists()) {
                                result = UploadImage(coverageBeanlist.get(i).getImage(), "StoreImages");
                                if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                    return "StoreImages";
                                }
                                runOnUiThread(new Runnable() {

                                    public void run() {
                                        message.setText("Store Image Uploaded");
                                    }
                                });
                                data.value = 94;
                                data.name = "StoreImages";
                                publishProgress(data);
                            }
                        }

                        //Quiz images upload
                        database.open();
                        quizData = database.getQuizData(coverageBeanlist.get(i).getStoreId());
                        if (quizData.size() > 0)
                            for (int j = 0; j < quizData.size(); j++) {
                                if (quizData.get(j).getKey_isd_image() != null && !quizData.get(j).getKey_isd_image().equals("")) {
                                    if (new File(CommonString.FILE_PATH + quizData.get(j).getKey_isd_image()).exists()) {
                                        result = UploadImage(quizData.get(j).getKey_isd_image(), "StoreImages");
                                        if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                            return "Quiz Image Uploaded";
                                        }
                                        if (result.toString().trim().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                                            return "Error" + "," + errormsg;
                                        }
                                        runOnUiThread(new Runnable() {

                                            public void run() {
                                                message.setText("Quiz Image Uploaded");
                                            }
                                        });
                                        data.value = 95;
                                        data.name = "Quiz Image";
                                        publishProgress(data);
                                    }
                                }
                            }


                        //Uploading mananged Zero New employeeee Images

                        if (newEmpMannagedZeroData.size() > 0) {
                            for (int k = 0; k < newEmpMannagedZeroData.size(); k++) {
                                if (newEmpMannagedZeroData.get(k).getImage() != null && !newEmpMannagedZeroData.get(k).getImage().equals("")) {
                                    if (new File(CommonString.FILE_PATH + newEmpMannagedZeroData.get(k).getImage()).exists()) {
                                        result = UploadImage(newEmpMannagedZeroData.get(k).getImage(), "StoreImages");
                                        if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                            return "StoreImages";
                                        }
                                        runOnUiThread(new Runnable() {

                                            public void run() {
                                                message.setText("Store Image Uploaded");
                                            }
                                        });
                                        data.value = 96;
                                        data.name = "StoreImages";
                                        publishProgress(data);
                                    }
                                }

                            }

                        }


                        //New employee Quiz data
                        database.open();
                        quizData = database.getQuizNewEmployeeData(coverageBeanlist.get(i).getStoreId());
                        if (quizData.size() > 0)
                            for (int j = 0; j < quizData.size(); j++) {
                                if (quizData.get(j).getKey_isd_image() != null && !quizData.get(j).getKey_isd_image().equals("")) {
                                    if (new File(CommonString.FILE_PATH + quizData.get(j).getKey_isd_image()).exists()) {
                                        result = UploadImage(quizData.get(j).getKey_isd_image(), "StoreImages");
                                        if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                            return "Quiz Image Uploaded";
                                        }
                                        if (result.toString().trim().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                                            return "Error" + "," + errormsg;
                                        }
                                        runOnUiThread(new Runnable() {

                                            public void run() {
                                                message.setText("Quiz Image Uploaded");
                                            }
                                        });
                                        data.value = 98;
                                        data.name = "Quiz Image";
                                        publishProgress(data);
                                    }
                                }
                            }
                    }

                    data.value = 100;
                    data.name = "COVERAGE_STATUS";
                    publishProgress(data);

                    // SET COVERAGE STATUS

                    String final_xml = "";
                    String onXML = "";
                    onXML = "[COVERAGE_STATUS][STORE_ID]"
                            + coverageBeanlist.get(i).getStoreId()
                            + "[/STORE_ID]"
                            + "[VISIT_DATE]"
                            + coverageBeanlist.get(i).getVisitDate()
                            + "[/VISIT_DATE]"
                            + "[USER_ID]"
                            + coverageBeanlist.get(i).getUserId()
                            + "[/USER_ID]"
                            + "[STATUS]"
                            + CommonString.KEY_U
                            + "[/STATUS]"
                            + "[/COVERAGE_STATUS]";

                    final_xml = final_xml + onXML;

                    final String sos_xml = "[DATA]" + final_xml
                            + "[/DATA]";

                    SoapObject request = new SoapObject(CommonString.NAMESPACE, CommonString.MEHTOD_UPLOAD_COVERAGE_STATUS);
                    request.addProperty("onXML", sos_xml);
                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(request);
                    HttpTransportSE androidHttpTransport = new HttpTransportSE(CommonString.URL);
                    androidHttpTransport.call(CommonString.SOAP_ACTION + CommonString.MEHTOD_UPLOAD_COVERAGE_STATUS, envelope);
                    Object result = (Object) envelope.getResponse();
                    if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                        return CommonString.MEHTOD_UPLOAD_COVERAGE_STATUS;
                    }
                    if (result.toString().equalsIgnoreCase(CommonString.KEY_NO_DATA)) {
                        return CommonString.MEHTOD_UPLOAD_COVERAGE_STATUS;
                    }
                    if (result.toString().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                        return CommonString.MEHTOD_UPLOAD_COVERAGE_STATUS;
                    }
                    database.open();
                    database.updateCoverageStatus(coverageBeanlist.get(i).getMID(), CommonString.KEY_U);
                    database.updateStoreStatusOnLeave(coverageBeanlist.get(i).getStoreId(),
                            coverageBeanlist.get(i).getVisitDate(), CommonString.KEY_U);
                }

            } catch (MalformedURLException e) {
                dialog.dismiss();
                up_success_flag = false;
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        showMessage(CommonString.MESSAGE_EXCEPTION);
                    }
                });


            } catch (IOException e) {
                dialog.dismiss();
                up_success_flag = false;
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub

                        showMessage(CommonString.MESSAGE_SOCKETEXCEPTION);

                    }
                });

            } catch (Exception e) {
                dialog.dismiss();
                up_success_flag = false;
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        showMessage(CommonString.MESSAGE_EXCEPTION);
                    }
                });
            }
            if (up_success_flag) {
                return CommonString.KEY_SUCCESS;
            } else {
                return CommonString.KEY_FAILURE;
            }
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
                database.open();
                database.deleteAllTables();
                showMessage(CommonString.MESSAGE_UPLOAD_DATA);
            } else {
                showMessage("Error in Upload :- " + " " + result);

            }
        }
    }

}

