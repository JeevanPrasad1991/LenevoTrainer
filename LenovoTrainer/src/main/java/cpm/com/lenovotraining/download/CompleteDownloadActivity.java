package cpm.com.lenovotraining.download;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;

import cpm.com.lenovotraining.R;
import cpm.com.lenovotraining.bean.TableBean;
import cpm.com.lenovotraining.constants.CommonString;
import cpm.com.lenovotraining.database.Database;
import cpm.com.lenovotraining.xmlHandler.XMLHandlers;
import cpm.com.lenovotraining.xmlgettersetter.AuditChecklistAnswerGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.AuditChecklistGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.IsdPerformanceGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.JCPGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.NonWorkingReasonGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.QuizQuestionGettersetter;
import cpm.com.lenovotraining.xmlgettersetter.SaleTeamGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.StoreISDGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.TrainingTopicGetterSetter;

public class CompleteDownloadActivity extends AppCompatActivity {
    String _UserId;
    private SharedPreferences preferences;
    TableBean tb;
    Database db;
    private Dialog dialog;
    private ProgressBar pb;
    private TextView percentage, message;
    private Data data;
    int eventType;
    JCPGetterSetter jcpGetterSetter;
    TrainingTopicGetterSetter trainingTopicGetterSetter;
    StoreISDGetterSetter storeISDGetterSetter;
    QuizQuestionGettersetter quizQuestionGettersetter;
    AuditChecklistGetterSetter auditChecklistGetterSetter;
    NonWorkingReasonGetterSetter nonworkinggettersetter;
    IsdPerformanceGetterSetter isdPerformanceGetterSetter;
    AuditChecklistAnswerGetterSetter auditChecklistAnswerGetterSetter;
    SaleTeamGetterSetter saleTeamGetterSetter;
    boolean download_flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        _UserId = preferences.getString(CommonString.KEY_USERNAME, "");
        tb = new TableBean();
        db = new Database(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        new BackgroundTask(this).execute();

    }


    class Data {
        int value;
        String name;
    }
    //Download Asynctask

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
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom);
            dialog.setCancelable(false);
            dialog.show();
            pb = (ProgressBar) dialog.findViewById(R.id.progressBar1);
            percentage = (TextView) dialog.findViewById(R.id.percentage);
            message = (TextView) dialog.findViewById(R.id.message);

        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub
            String resultHttp = "";
            try {

                data = new Data();
                data.value = 10;
                data.name = "Downloading Data";
                publishProgress(data);

                // JCP data
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();

                SoapObject request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                request.addProperty("UserName", _UserId);
                request.addProperty("Type", "JOURNEY_PLAN_TRAINER");

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(CommonString.URL, 30000);
                androidHttpTransport.call(CommonString.SOAP_ACTION_UNIVERSAL, envelope);
                Object result = (Object) envelope.getResponse();
                if (result.toString() != null) {
                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    jcpGetterSetter = XMLHandlers.JCPXMLHandler(xpp, eventType);

                    if (jcpGetterSetter.getSTORE_CD().size() > 0) {
                        resultHttp = CommonString.KEY_SUCCESS;
                        String jcpTable = jcpGetterSetter.getTable_juorney_plan_trainer();
                        TableBean.setTable_jcp(jcpTable);
                    } else {
                        return "JOURNEY_PLAN_TRAINER";
                    }
                    data.value = 20;
                    data.name = "JCP Data";
                    publishProgress(data);
                }


                // Training Topic

                factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                xpp = factory.newPullParser();

                request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                request.addProperty("UserName", _UserId);
                request.addProperty("Type", "TRAINING_TOPIC");

                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                androidHttpTransport = new HttpTransportSE(CommonString.URL, 30000);

                androidHttpTransport.call(CommonString.SOAP_ACTION_UNIVERSAL, envelope);
                result = (Object) envelope.getResponse();
                if (result.toString() != null) {

                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    trainingTopicGetterSetter = XMLHandlers.TrainingTopicXMLHandler(xpp, eventType);
                    String training_topicTable = trainingTopicGetterSetter.getTable_training_topic();
                    TableBean.setTable_trainig_topic(training_topicTable);
                    if (trainingTopicGetterSetter.getTOPIC_CD().size() > 0) {
                        resultHttp = CommonString.KEY_SUCCESS;
                    } else {
                        return "TRAINING_TOPIC";
                    }
                    data.value = 30;
                    data.name = "Training Topic Data";
                    publishProgress(data);
                }

                // STore ISD data
                factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                xpp = factory.newPullParser();
                request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                request.addProperty("UserName", _UserId);
                request.addProperty("Type", "STORE_ISD");
                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                androidHttpTransport = new HttpTransportSE(CommonString.URL, 30000);
                androidHttpTransport.call(CommonString.SOAP_ACTION_UNIVERSAL, envelope);
                result = (Object) envelope.getResponse();
                if (result.toString() != null) {
                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    storeISDGetterSetter = XMLHandlers.StoreISDXMLHandler(xpp, eventType);
                    if (storeISDGetterSetter.getSTORE_CD().size() > 0) {
                        resultHttp = CommonString.KEY_SUCCESS;
                        data.value = 40;
                        data.name = "Store ISD Data";
                        publishProgress(data);
                    }
                    String store_isd_Table = storeISDGetterSetter.getTable_store_isd();
                    if (store_isd_Table != null) {
                        TableBean.setTable_store_isd(store_isd_Table);
                    }
                }

                // QUIZ_QUESTION

                factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                xpp = factory.newPullParser();
                request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                request.addProperty("UserName", _UserId);
                request.addProperty("Type", "QUIZ_QUESTION");

                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                androidHttpTransport = new HttpTransportSE(CommonString.URL, 30000);

                androidHttpTransport.call(CommonString.SOAP_ACTION_UNIVERSAL, envelope);
                result = (Object) envelope.getResponse();

                if (result.toString() != null) {

                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    quizQuestionGettersetter = XMLHandlers.QuizQuestionXMLHandler(xpp, eventType);
                    if (quizQuestionGettersetter.getQUESTION_CD().size() > 0) {
                        resultHttp = CommonString.KEY_SUCCESS;
                        String quiz_Table = quizQuestionGettersetter.getTable_quiz_question();
                        TableBean.setTable_quiz_question(quiz_Table);

                    } else {
                        return "QUIZ_QUESTION";
                    }
                    data.value = 50;
                    data.name = "Quiz Data";
                    publishProgress(data);

                }

                // AUDIT_CHECKLIST

                factory = XmlPullParserFactory
                        .newInstance();
                factory.setNamespaceAware(true);
                xpp = factory.newPullParser();

                request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                request.addProperty("UserName", _UserId);
                request.addProperty("Type", "AUDIT_CHECKLIST");
                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                androidHttpTransport = new HttpTransportSE(CommonString.URL, 30000);

                androidHttpTransport.call(CommonString.SOAP_ACTION_UNIVERSAL, envelope);
                result = (Object) envelope.getResponse();
                if (result.toString() != null) {

                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();

                    auditChecklistGetterSetter = XMLHandlers.AuditCheckListXMLHandler(xpp, eventType);
                    if (auditChecklistGetterSetter.getCHECKLIST_CD().size() > 0) {
                        resultHttp = CommonString.KEY_SUCCESS;
                    } else {
                        return "AUDIT_CHECKLIST";
                    }
                    String audit_Table = auditChecklistGetterSetter.getTable_audit_checklist();
                    TableBean.setTable_audit_checklist(audit_Table);
                    data.value = 60;
                    data.name = "Audit Checklist Data";
                    publishProgress(data);

                }


                //Non Working Reason data

                request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);

                request.addProperty("UserName", _UserId);
                request.addProperty("Type", "NON_WORKING_REASON_NEW");

                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                androidHttpTransport = new HttpTransportSE(CommonString.URL, 30000);

                androidHttpTransport.call(
                        CommonString.SOAP_ACTION_UNIVERSAL, envelope);
                Object resultnonworking = (Object) envelope.getResponse();

                if (resultnonworking.toString() != null) {

                    xpp.setInput(new StringReader(resultnonworking.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();

                    nonworkinggettersetter = XMLHandlers.nonWorkinReasonXML(xpp, eventType);

                    if (nonworkinggettersetter.getReason_cd().size() > 0) {
                        resultHttp = CommonString.KEY_SUCCESS;
                        String nonworkingtable = nonworkinggettersetter.getNonworking_table();
                        TableBean.setTable_non_working(nonworkingtable);

                    } else {
                        return "NON_WORKING_REASON_NEW";
                    }

                    data.value = 70;
                    data.name = "Non Working Reason";
                    publishProgress(data);

                }


                //ISD Performance data
                request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);

                request.addProperty("UserName", _UserId);
                request.addProperty("Type", "ISD_PERFORMANCE");
                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                androidHttpTransport = new HttpTransportSE(CommonString.URL, 30000);
                androidHttpTransport.call(CommonString.SOAP_ACTION_UNIVERSAL, envelope);
                Object resultperformance = (Object) envelope.getResponse();
                if (resultperformance.toString() != null) {
                    xpp.setInput(new StringReader(resultperformance.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    isdPerformanceGetterSetter = XMLHandlers.isdPerformanceXML(xpp, eventType);
                    if (isdPerformanceGetterSetter.getISD_CD().size() > 0) {
                        resultHttp = CommonString.KEY_SUCCESS;
                        data.value = 80;
                        data.name = "Performance Data";
                        publishProgress(data);
                    }
                    String isd_performance_table = isdPerformanceGetterSetter.getTable_isd_performance();
                    TableBean.setTable_isd_performance(isd_performance_table);
                }

                // AUDIT_CHECKLIST_ANSWER data
                request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                request.addProperty("UserName", _UserId);
                request.addProperty("Type", "AUDIT_CHECKLIST_ANSWER");
                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                androidHttpTransport = new HttpTransportSE(CommonString.URL, 30000);
                androidHttpTransport.call(CommonString.SOAP_ACTION_UNIVERSAL, envelope);
                resultperformance = (Object) envelope.getResponse();
                if (resultperformance.toString() != null) {
                    xpp.setInput(new StringReader(resultperformance.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    auditChecklistAnswerGetterSetter = XMLHandlers.AuditCheckLANSXMLHandler(xpp, eventType);
                    if (auditChecklistAnswerGetterSetter.getAnswer_cd().size() > 0) {
                        resultHttp = CommonString.KEY_SUCCESS;
                        data.value = 90;
                        data.name = "AUDIT CHECKLIST ANSWER Data";
                    }
                    TableBean.setTable_auditchecklist_answer(auditChecklistAnswerGetterSetter.getAuditchecklistANSTable());
                }
                publishProgress(data);

                // SALES_TEAM data
                request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                request.addProperty("UserName", _UserId);
                request.addProperty("Type", "SALES_TEAM");
                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                androidHttpTransport = new HttpTransportSE(CommonString.URL, 30000);
                androidHttpTransport.call(CommonString.SOAP_ACTION_UNIVERSAL, envelope);
                resultperformance = (Object) envelope.getResponse();
                if (resultperformance.toString() != null) {
                    xpp.setInput(new StringReader(resultperformance.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    saleTeamGetterSetter = XMLHandlers.SaleTeamXMLHandler(xpp, eventType);
                    if (saleTeamGetterSetter.getTrainee_cd().size() > 0) {
                        resultHttp = CommonString.KEY_SUCCESS;
                        data.value = 95;
                        data.name = "Sale Team Data";
                    }
                    TableBean.setTable_sale_team(saleTeamGetterSetter.getSaleTEAMTable());
                }
                publishProgress(data);

                db.open();
                db.insertJCPData(jcpGetterSetter);
                db.insertTrainingtopicData(trainingTopicGetterSetter);
                if (storeISDGetterSetter.getSTORE_CD().size() > 0) {
                    db.insertStoreISDData(storeISDGetterSetter);
                } else {
                    db.deleteISDDataOnNoData();
                }

                db.insertQuizQuestionData(quizQuestionGettersetter);
                db.insertAuditCheckListData(auditChecklistGetterSetter);
                db.insertNonWorkingReasonData(nonworkinggettersetter);
                if (isdPerformanceGetterSetter.getISD_CD().size() > 0) {
                    db.insertIsdPerformanceData(isdPerformanceGetterSetter);
                } else {
                    db.deletePerformanceDataOnNoData();
                }
                // AUDIT_CHECKLIST_ANSWER data
                db.insertAuditCheckLisAnswertData(auditChecklistAnswerGetterSetter);
                db.insertAuditCheckLisAnswertData(saleTeamGetterSetter);
                data.value = 100;
                data.name = "Finishing";
                publishProgress(data);

                return resultHttp;

            } catch (MalformedURLException e) {
                download_flag = false;
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        showMessage(CommonString.MESSAGE_EXCEPTION);
                    }
                });

            } catch (IOException e) {
                download_flag = false;
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        showMessage(CommonString.MESSAGE_SOCKETEXCEPTION);

                    }
                });

            } catch (Exception e) {
                download_flag = false;
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
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
            if (download_flag) {
                if (result.equals(CommonString.KEY_SUCCESS)) {
                    showMessage(CommonString.MESSAGE_DOWNLOAD);
                } else {
                    showMessage(CommonString.MESSAGE_JCP_FALSE + " " + result);
                }
            }
        }
    }

    public void showMessage(String msg) {
        new AlertDialog.Builder(CompleteDownloadActivity.this)
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
