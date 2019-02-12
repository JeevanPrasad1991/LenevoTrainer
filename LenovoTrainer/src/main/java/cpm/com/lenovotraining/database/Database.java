package cpm.com.lenovotraining.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cpm.com.lenovotraining.bean.TableBean;
import cpm.com.lenovotraining.constants.CommonString;
import cpm.com.lenovotraining.geocode.GeotaggingBeans;
import cpm.com.lenovotraining.xmlgettersetter.AddNewEmployeeGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.AllIsdNEmployeeGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.AuditChecklistAnswerGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.AuditChecklistGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.CoverageBean;
import cpm.com.lenovotraining.xmlgettersetter.EmpCdIsdGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.IsdPerformanceGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.JCPGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.NonWorkingReasonGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.PosmGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.QuizAnwserGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.QuizQuestionGettersetter;
import cpm.com.lenovotraining.xmlgettersetter.SaleTeamGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.StoreDataGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.StoreISDGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.TrainingTopicGetterSetter;

/**
 * Created by yadavendras on 21-06-2016.
 */

@SuppressLint("LongLogTag")
public class Database extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Lenovo_Databases_10";
    public static final int DATABASE_VERSION = 6;
    private SQLiteDatabase db;

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void open() {
        try {
            db = this.getWritableDatabase();
        } catch (Exception e) {
        }
    }

    public void close() {
        db.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TableBean.getTable_jcp());
        db.execSQL(TableBean.getTable_trainig_topic());
        db.execSQL(TableBean.getTable_store_isd());
        db.execSQL(TableBean.getTable_quiz_question());
        db.execSQL(TableBean.getTable_non_working());
        db.execSQL(TableBean.getTable_isd_performance());
        db.execSQL(TableBean.getTable_audit_checklist());

        ///for audit checklist answer
        db.execSQL(TableBean.getTable_auditchecklist_answer());
        db.execSQL(TableBean.getTable_sale_team());
        db.execSQL(CommonString.CREATE_TABLE_COVERAGE_DATA);
        db.execSQL(CommonString.CREATE_TABLE_ANSWERED_DATA);
        db.execSQL(CommonString.CREATE_TABLE_CHECKLIST_INSERTED_DATA);
        db.execSQL(CommonString.CREATE_TABLE_ADD_NEW_EMPLOYEE_DATA);
        db.execSQL(CommonString.CREATE_TABLE_SALETEAM_TRAINEE_DATA);
        db.execSQL(CommonString.CREATE_TABLE_NEW_ISD_DATA);
        db.execSQL(CommonString.CREATE_TABLE_STORE_GEOTAGGING);
        db.execSQL(CommonString.CREATE_TABLE_CHECKLIST_HEADER_DATA);
        db.execSQL(CommonString.CREATE_TABLE_TRAINING_TOPC_DATA);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public void deleteAllTables() {
        // DELETING TABLES
        db.delete(CommonString.TABLE_COVERAGE_DATA, null, null);
        db.delete(CommonString.TABLE_ANSWERED_DATA, null, null);
        db.delete(CommonString.TABLE_CHECKLIST_INSERTED_DATA, null, null);
        db.delete(CommonString.TABLE_ADD_NEW_EMPLOYEE, null, null);
        db.delete(CommonString.TABLE_NEW_ISD, null, null);
        db.delete(CommonString.TABLE_STORE_GEOTAGGING, null, null);
        db.delete(CommonString.TABLE_INSERT_OPENINGHEADER_DATA, null, null);
        db.delete(CommonString.TABLE_TRAINING_TOPIC_DATA, null, null);

    }


    public void deletePreviousUploadedData(String visit_date) {
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * from " + CommonString.TABLE_COVERAGE_DATA + " VISIT_DATE < '" + visit_date + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                int icount = dbcursor.getCount();
                dbcursor.close();
                if (icount > 0) {
                    db.delete(CommonString.TABLE_COVERAGE_DATA, null, null);
                    db.delete(CommonString.TABLE_ANSWERED_DATA, null, null);
                    db.delete(CommonString.TABLE_CHECKLIST_INSERTED_DATA, null, null);
                    db.delete(CommonString.TABLE_ADD_NEW_EMPLOYEE, null, null);
                    db.delete(CommonString.TABLE_NEW_ISD, null, null);
                    db.delete(CommonString.TABLE_STORE_GEOTAGGING, null, null);
                    db.delete(CommonString.TABLE_SALETEAM_TRAINEE_DATA, null, null);
                    db.delete(CommonString.TABLE_INSERT_OPENINGHEADER_DATA, null, null);
                    db.delete(CommonString.TABLE_TRAINING_TOPIC_DATA, null, null);


                }
                dbcursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //JCP data\

    public void insertJCPData(JCPGetterSetter data) {
        db.delete("JOURNEY_PLAN_TRAINER", null, null);
        ContentValues values = new ContentValues();
        try {
            for (int i = 0; i < data.getSTORE_CD().size(); i++) {
                values.put("STORE_CD", Integer.parseInt(data.getSTORE_CD().get(i)));
                values.put("EMP_CD", data.getEMP_CD().get(i));
                values.put("VISIT_DATE", data.getVISIT_DATE().get(i));
                values.put("KEYACCOUNT", data.getKEYACCOUNT().get(i));
                values.put("STORENAME", data.getSTORENAME().get(i));
                values.put("STORETYPE", data.getSTORETYPE().get(i));
                values.put("CITY", data.getCITY().get(i));
                values.put("TMODE_CD", data.getTMODE_CD().get(i));
                values.put("TRAINING_MODE", data.getTRAINING_MODE().get(i));
                values.put("UPLOAD_STATUS", data.getUPLOAD_STATUS().get(i));
                values.put("CHECKOUT_STATUS", data.getCHECKOUT_STATUS().get(i));
                values.put("MANAGED", data.getMANAGED().get(i));

                // values.put("MANAGED", "0");
                values.put("LATTITUDE", data.getLAT().get(i));
                values.put("LONGITUDE", data.getLONG().get(i));
                values.put("GEOTAG", data.getGEOTAG().get(i));
                db.insert("JOURNEY_PLAN_TRAINER", null, values);
            }
        } catch (Exception ex) {
            Log.d("DB Excep in JCP Insert", ex.toString());
        }
    }

    //Training Topic data
    public void insertTrainingtopicData(TrainingTopicGetterSetter data) {
        db.delete("TRAINING_TOPIC", null, null);
        ContentValues values = new ContentValues();
        try {
            for (int i = 0; i < data.getTOPIC_CD().size(); i++) {
                values.put("TOPIC_CD", Integer.parseInt(data.getTOPIC_CD().get(i)));
                values.put("TOPIC", data.getTOPIC().get(i));
                db.insert("TRAINING_TOPIC", null, values);
            }

        } catch (Exception ex) {
            Log.d("DB Exc Training Insert", ex.toString());
        }
    }
    //Training Topic data

    public void insertStoreISDData(StoreISDGetterSetter data) {
        db.delete("STORE_ISD", null, null);
        ContentValues values = new ContentValues();
        try {
            for (int i = 0; i < data.getSTORE_CD().size(); i++) {
                values.put("STORE_CD", Integer.parseInt(data.getSTORE_CD().get(i)));
                values.put("ISD_CD", data.getISD_CD().get(i));
                values.put("ISD_NAME", data.getISD_NAME().get(i));
                db.insert("STORE_ISD", null, values);
            }

        } catch (Exception ex) {
            Log.d("DB Exc ISD Insert", ex.toString());
        }
    }

    public void deleteISDDataOnNoData() {
        db.delete("STORE_ISD", null, null);
    }
    //Quiz Question data

    public void insertQuizQuestionData(QuizQuestionGettersetter data) {

        db.delete("QUIZ_QUESTION", null, null);
        ContentValues values = new ContentValues();

        try {
            for (int i = 0; i < data.getQUESTION_CD().size(); i++) {

                values.put("TOPIC_CD", Integer.parseInt(data.getTOPIC_CD().get(i)));
                values.put("QUESTION_CD", data.getQUESTION_CD().get(i));
                values.put("QUESTION", data.getQUESTION().get(i));
                values.put("ANSWER_CD", data.getANSWER_CD().get(i));
                values.put("ANSWER", data.getANSWER().get(i));
                values.put("RIGHT_ANSWER", data.getRIGHT_ANSWER().get(i));

                db.insert("QUIZ_QUESTION", null, values);
            }

        } catch (Exception ex) {
            Log.d("DB Exc Quiz Insert", ex.toString());
        }

    }

    //Audit Checklist data

    public long insertAuditCheckListData(AuditChecklistGetterSetter data) {
        db.delete("AUDIT_CHECKLIST", null, null);
        ContentValues values = new ContentValues();
        long l = 0;
        try {
            for (int i = 0; i < data.getCHECKLIST_CD().size(); i++) {
                values.put("CHECKLIST_CD", Integer.parseInt(data.getCHECKLIST_CD().get(i)));
                values.put("CHECKLIST", data.getCHECKLIST().get(i));
                ///////changes by jeevan
                values.put("CHECKLIST_TYPE", data.getCHECKLIST_TYPE().get(i));
                values.put("CHECKLIST_CATEGORY_CD", Integer.parseInt(data.getCHECKLIST_CATEGORY_CD().get(i)));
                values.put("CHECKLIST_CATEGORY", data.getCHECKLIST_CATEGORY().get(i));

                l = db.insert("AUDIT_CHECKLIST", null, values);
            }

        } catch (Exception ex) {
            Log.d("DB Exc Audit Insert", ex.toString());
        }
        return l;
    }

    //Non Working data

    public void insertNonWorkingReasonData(NonWorkingReasonGetterSetter data) {

        db.delete("NON_WORKING_REASON", null, null);
        ContentValues values = new ContentValues();

        try {

            for (int i = 0; i < data.getReason_cd().size(); i++) {

                values.put("REASON_CD", Integer.parseInt(data.getReason_cd().get(i)));
                values.put("REASON", data.getReason().get(i));
                values.put("ENTRY_ALLOW", data.getEntry_allow().get(i));

                db.insert("NON_WORKING_REASON", null, values);

            }

        } catch (Exception ex) {
            Log.d("Exception Non Working",
                    ex.toString());
        }

    }

    //Isd Performance data

    public void insertIsdPerformanceData(IsdPerformanceGetterSetter data) {

        db.delete("ISD_PERFORMANCE", null, null);
        ContentValues values = new ContentValues();

        try {

            for (int i = 0; i < data.getISD_CD().size(); i++) {

                values.put("ISD_CD", Integer.parseInt(data.getISD_CD().get(i)));
                values.put("ISD", data.getISD().get(i));
                values.put("TRAINING_DATE", data.getTRAINING_DATE().get(i));
                values.put("TRAINING_TYPE", data.getTRAINING_TYPE().get(i));
                values.put("TOPIC", data.getTOPIC().get(i));
                values.put("GROOMING_SCORE", data.getGROOMING_SCORE().get(i));
                values.put("QUIZ_SCORE", data.getQUIZ_SCORE().get(i));

                db.insert("ISD_PERFORMANCE", null, values);

            }

        } catch (Exception ex) {
            Log.d("Exc ISD Performance",
                    ex.toString());
        }

    }

    public void deletePerformanceDataOnNoData() {
        db.delete("ISD_PERFORMANCE", null, null);
    }

    // get Non Working data
    public ArrayList<NonWorkingReasonGetterSetter> getNonWorkingData() {
        Log.d("FetcNonWorking->Start<-",
                "-");
        ArrayList<NonWorkingReasonGetterSetter> list = new ArrayList<NonWorkingReasonGetterSetter>();
        Cursor dbcursor = null;

        try {

            dbcursor = db.rawQuery("SELECT * FROM NON_WORKING_REASON", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    NonWorkingReasonGetterSetter sb = new NonWorkingReasonGetterSetter();
                    sb.setReason_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow("REASON_CD")));
                    sb.setReason(dbcursor.getString(dbcursor.getColumnIndexOrThrow("REASON")));
                    sb.setEntry_allow(dbcursor.getString(dbcursor.getColumnIndexOrThrow("ENTRY_ALLOW")));

                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exc Non working!!",
                    e.toString());
            return list;
        }

        Log.d("Fet non working->Stop<-",
                "-");
        return list;
    }


//get StoreTypeMaster Data


    //get Posm Data

    public ArrayList<PosmGetterSetter> getPOSMData() {

        Log.d("FetchStoreType>Start<--",
                "----");
        ArrayList<PosmGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT DISTINCT POSM_CD, POSM from POSM_MASTER"
                    , null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    PosmGetterSetter df = new PosmGetterSetter();


                    df.setPosm_cd(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("POSM_CD")));
                    df.setPosm(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("POSM")));
                    df.setQuantity("");
                    df.setPosm_img_str("");


                    list.add(df);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Ex fetching Posm!",
                    e.toString());
            return list;
        }

        Log.d("FetcPosm data->Stop<-",
                "-");
        return list;

    }

    //Insert Answered data

    public long insertAnsweredData(ArrayList<QuizAnwserGetterSetter> data, String store_cd, long mid, String isd_image) {

        ContentValues values = new ContentValues();
        long key = 0;
        try {
            for (int i = 0; i < data.size(); i++) {
                values.put(CommonString.KEY_STORE_CD, store_cd);
                values.put(CommonString.KEY_ISD_CD, data.get(i).getIsd_cd());
                values.put(CommonString.KEY_TOPIC_CD, data.get(i).getTopic_cd());
                values.put(CommonString.KEY_QUESTION_CD, data.get(i).getQuestion_cd());
                values.put(CommonString.KEY_ANSWER_CD, data.get(i).getAnswer_cd());
                values.put(CommonString.KEY_ANSWER, data.get(i).getAnswer());
                values.put(CommonString.KEY_TRAINING_MODE_CD, data.get(i).getTraining_mode_cd());
                values.put(CommonString.KEY_MID, mid);
                values.put(CommonString.KEY_ISD_IMAGE, isd_image);
                key = db.insert(CommonString.TABLE_ANSWERED_DATA, null, values);

            }
        } catch (Exception ex) {
            Log.d("DB Excep Answer Insert", ex.toString());
            //return 0;
        }
        return key;
    }

    //Insert New ISD data

    public long insertNewIsdData(EmpCdIsdGetterSetter data, String store_cd) {

        //db.delete(CommonString.TABLE_STORE_DATA, null, null);
        ContentValues values = new ContentValues();
        long key = 0;

        try {

            values.put(CommonString.KEY_STORE_CD, store_cd);
            values.put(CommonString.KEY_ISD_CD, data.getIsd_cd());
            values.put(CommonString.KEY_ISD_NAME, data.getIsd());
            key = db.insert(CommonString.TABLE_NEW_ISD, null, values);

        } catch (Exception ex) {
            Log.d("DB Excep new isd Insert", ex.toString());
            //return 0;
        }
        return key;
    }

    public long insertAuditChecklistWithCategoryData(String store_cd, String isd_cd, long mid,
                                                     HashMap<AuditChecklistGetterSetter, ArrayList<AuditChecklistGetterSetter>> data,
                                                     ArrayList<AuditChecklistGetterSetter> save_listDataHeader) {
        ContentValues values = new ContentValues();
        ContentValues values1 = new ContentValues();
        long l2 = 0;

        try {
            db.beginTransaction();
            for (int i = 0; i < save_listDataHeader.size(); i++) {
                values.put(CommonString.KEY_STORE_CD, store_cd);
                values.put(CommonString.KEY_ISD_CD, isd_cd);
                values.put(CommonString.KEY_MID, mid);
                values.put("CHECKLIST_CATEGORY_CD", save_listDataHeader.get(i).getCHECKLIST_CATEGORY_CD().get(0));
                values.put("CHECKLIST_CATEGORY", save_listDataHeader.get(i).getCHECKLIST_CATEGORY().get(0));

                long l = db.insert(CommonString.TABLE_INSERT_OPENINGHEADER_DATA, null, values);

                for (int j = 0; j < data.get(save_listDataHeader.get(i)).size(); j++) {
                    values1.put("Common_Id", (int) l);
                    values1.put(CommonString.KEY_STORE_CD, store_cd);
                    values1.put(CommonString.KEY_ISD_CD, isd_cd);
                    values1.put(CommonString.KEY_MID, mid);
                    values1.put("CHECKLIST_CATEGORY_CD", save_listDataHeader.get(i).getCHECKLIST_CATEGORY_CD().get(0));
                    values1.put("CHECKLIST_CATEGORY", save_listDataHeader.get(i).getCHECKLIST_CATEGORY().get(0));

                    values1.put(CommonString.KEY_CHECKLIST_CD, Integer.parseInt(data.get(save_listDataHeader.get(i))
                            .get(j).getCHECKLIST_CD().get(0)));
                    values1.put(CommonString.KEY_CHECKLIST, data.get
                            (save_listDataHeader.get(i)).get(j).getCHECKLIST().get(0));
                    values1.put(CommonString.KEY_AVAILABILITY, data.get(save_listDataHeader.get(i)).get(j).getAvailability());


                    l2 = db.insert(CommonString.TABLE_CHECKLIST_INSERTED_DATA, null, values1);
                }
            }
            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception ex) {
            Log.d("Database Exception", " while Insert Posm Master Data " + ex.toString());
        }
        return l2;
    }


    //Insert Audit Checklist data

    public long insertAuditChecklistData(ArrayList<AuditChecklistGetterSetter> data, String store_cd, String isd_cd, long mid) {
        ContentValues values = new ContentValues();
        long key = 0;
        try {
            for (int i = 0; i < data.size(); i++) {
                values.put(CommonString.KEY_STORE_CD, store_cd);
                values.put(CommonString.KEY_ISD_CD, isd_cd);
                values.put(CommonString.KEY_CHECKLIST_CD, data.get(i).getCHECKLIST_CD().get(0));
                values.put(CommonString.KEY_CHECKLIST, data.get(i).getCHECKLIST().get(0));
                values.put(CommonString.KEY_AVAILABILITY, data.get(i).getAvailability());
                values.put(CommonString.KEY_MID, mid);
                key = db.insert(CommonString.TABLE_CHECKLIST_INSERTED_DATA, null, values);
            }
        } catch (Exception ex) {
            Log.d("DB Excep Audit Insert", ex.toString());
            //return 0;
        }
        return key;
    }

    //Insert new Employee data

    public long insertNewEmployeeData(AddNewEmployeeGetterSetter data, String store_cd, String manned) {
        ContentValues values = new ContentValues();
        long key = 0;
        try {
            values.put(CommonString.KEY_STORE_CD, store_cd);
            values.put(CommonString.KEY_NAME, data.getName());
            values.put(CommonString.KEY_EMAIL, data.getEmail());
            values.put(CommonString.KEY_PHONE_NO, data.getPhone());
            values.put(CommonString.KEY_IS_ISD, data.isIsd());
            values.put(CommonString.KEY_MANAGED, manned);
            key = db.insert(CommonString.TABLE_ADD_NEW_EMPLOYEE, null, values);
        } catch (Exception ex) {
            Log.d("DB Excep Employee Insert", ex.toString());
            //return 0;
        }
        return key;
    }

    public long insertNewEmployeeForManned_ZeroData(AddNewEmployeeGetterSetter data, String store_cd, String manned, String image) {
        ContentValues values = new ContentValues();
        long key = 0;
        try {

            values.put(CommonString.KEY_STORE_CD, store_cd);
            values.put(CommonString.KEY_NAME, data.getName());
            values.put(CommonString.KEY_EMAIL, data.getEmail());
            values.put(CommonString.KEY_PHONE_NO, data.getPhone());
            values.put(CommonString.KEY_IS_ISD, data.isIsd());
            values.put(CommonString.KEY_IMAGE, image);
            values.put(CommonString.KEY_MANAGED, manned);
            key = db.insert(CommonString.TABLE_ADD_NEW_EMPLOYEE, null, values);
        } catch (Exception ex) {
            Log.d("DB Excep Employee Insert", ex.toString());
            //return 0;
        }
        return key;
    }

    //Store data
    public ArrayList<JCPGetterSetter> getSpecificStoreData(String store_cd) {
        Log.d("FetchStorelist>Start<--", "----");
        ArrayList<JCPGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT * from JOURNEY_PLAN_TRAINER WHERE STORE_CD = '" + store_cd + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    JCPGetterSetter df = new JCPGetterSetter();


                    df.setSTORE_CD(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("STORE_CD")));

                    df.setEMP_CD(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("EMP_CD")));

                    df.setVISIT_DATE(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("VISIT_DATE")));
                    df.setKEYACCOUNT(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("KEYACCOUNT")));
                    df.setSTORENAME(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("STORENAME")));
                    df.setCITY(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("CITY")));
                    df.setSTORETYPE(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("STORETYPE")));
                    df.setTMODE_CD(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("TMODE_CD")));
                    df.setTRAINING_MODE(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("TRAINING_MODE")));
                    df.setUPLOAD_STATUS(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("UPLOAD_STATUS")));
                    df.setCHECKOUT_STATUS(dbcursor.getString(dbcursor.getColumnIndexOrThrow("CHECKOUT_STATUS")));

                    df.setMANAGED(dbcursor.getString(dbcursor.getColumnIndexOrThrow("MANAGED")));

                    df.setLAT(dbcursor.getString(dbcursor.getColumnIndexOrThrow("LATTITUDE")));
                    df.setLONG(dbcursor.getString(dbcursor.getColumnIndexOrThrow("LONGITUDE")));
                    df.setGEOTAG(dbcursor.getString(dbcursor.getColumnIndexOrThrow("GEOTAG")));

                    list.add(df);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Ex fetching Store!", e.toString());
            return list;
        }

        Log.d("Fetcstore data->Stop<-", "-");
        return list;

    }


    //get Store Data

    public ArrayList<JCPGetterSetter> getStoreData(String visit_date) {

        Log.d("FetchStorelist>Start<--",
                "----");
        ArrayList<JCPGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT * from JOURNEY_PLAN_TRAINER WHERE VISIT_DATE = '" + visit_date + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    JCPGetterSetter df = new JCPGetterSetter();
                    df.setSTORE_CD(dbcursor.getString(dbcursor.getColumnIndexOrThrow("STORE_CD")));
                    df.setEMP_CD(dbcursor.getString(dbcursor.getColumnIndexOrThrow("EMP_CD")));
                    df.setVISIT_DATE(dbcursor.getString(dbcursor.getColumnIndexOrThrow("VISIT_DATE")));
                    df.setKEYACCOUNT(dbcursor.getString(dbcursor.getColumnIndexOrThrow("KEYACCOUNT")));
                    df.setSTORENAME(dbcursor.getString(dbcursor.getColumnIndexOrThrow("STORENAME")));
                    df.setCITY(dbcursor.getString(dbcursor.getColumnIndexOrThrow("CITY")));
                    df.setSTORETYPE(dbcursor.getString(dbcursor.getColumnIndexOrThrow("STORETYPE")));
                    df.setTMODE_CD(dbcursor.getString(dbcursor.getColumnIndexOrThrow("TMODE_CD")));
                    df.setTRAINING_MODE(dbcursor.getString(dbcursor.getColumnIndexOrThrow("TRAINING_MODE")));
                    df.setUPLOAD_STATUS(dbcursor.getString(dbcursor.getColumnIndexOrThrow("UPLOAD_STATUS")));
                    df.setCHECKOUT_STATUS(dbcursor.getString(dbcursor.getColumnIndexOrThrow("CHECKOUT_STATUS")));
                    df.setMANAGED(dbcursor.getString(dbcursor.getColumnIndexOrThrow("MANAGED")));
                    df.setLAT(dbcursor.getString(dbcursor.getColumnIndexOrThrow("LATTITUDE")));
                    df.setLONG(dbcursor.getString(dbcursor.getColumnIndexOrThrow("LONGITUDE")));
                    df.setGEOTAG(dbcursor.getString(dbcursor.getColumnIndexOrThrow("GEOTAG")));
                    list.add(df);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Ex fetching Store!", e.toString());
            return list;
        }

        Log.d("Fetcstore data->Stop<-", "-");
        return list;

    }

    //get Isd Performance Data

    public ArrayList<IsdPerformanceGetterSetter> getIsdPerfromanceData(String visit_date, String store_cd) {

        Log.d("FetchStorelist>Start<--",
                "----");
        ArrayList<IsdPerformanceGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT * from ISD_PERFORMANCE WHERE ISD_CD IN ( " + "SELECT ISD_CD from STORE_ISD where STORE_CD = '" + store_cd + "' )", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    IsdPerformanceGetterSetter df = new IsdPerformanceGetterSetter();


                    df.setTRAINING_DATE(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("TRAINING_DATE")));

                    df.setTOPIC(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("TOPIC")));

                    df.setTRAINING_TYPE(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("TRAINING_TYPE")));
                    df.setISD_CD(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("ISD_CD")));
                    df.setISD(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("ISD")));
                    df.setGROOMING_SCORE(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("GROOMING_SCORE")));
                    df.setQUIZ_SCORE(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("QUIZ_SCORE")));

                    list.add(df);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Ex fetching Store!", e.toString());
            return list;
        }

        Log.d("Fetcstore data->Stop<-", "-");
        return list;

    }

    //Get Store ISD data

    public ArrayList<StoreISDGetterSetter> getStoreIsdData(String store_cd) {

        Log.d("FetchStoreType>Start<--",
                "----");
        ArrayList<StoreISDGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT * from STORE_ISD where STORE_CD = '" + store_cd + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    StoreISDGetterSetter posm = new StoreISDGetterSetter();

                    posm.setSTORE_CD(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("STORE_CD")));
                    posm.setISD_CD(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("ISD_CD")));
                    posm.setISD_NAME(dbcursor.getString(dbcursor.getColumnIndexOrThrow("ISD_NAME")));

                    list.add(posm);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Ex fetching Posm!",
                    e.toString());
            return list;
        }

        Log.d("FetcPosm data->Stop<-",
                "-");
        return list;

    }


    //Get Audit data

    public ArrayList<AuditChecklistGetterSetter> getAuditData(String checklistCategory_cd) {
        Log.d("FetchAudit>Start<--", "----");
        ArrayList<AuditChecklistGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT DISTINCT CHECKLIST_CD,CHECKLIST FROM AUDIT_CHECKLIST WHERE CHECKLIST_CATEGORY_CD ='" + checklistCategory_cd + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    AuditChecklistGetterSetter audit = new AuditChecklistGetterSetter();
                    audit.setCHECKLIST_CD(dbcursor.getString(dbcursor.getColumnIndexOrThrow("CHECKLIST_CD")));
                    audit.setCHECKLIST(dbcursor.getString(dbcursor.getColumnIndexOrThrow("CHECKLIST")));
                    audit.setAvailability(0);
                    list.add(audit);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Ex fetching Audit!",
                    e.toString());
            return list;
        }

        Log.d("FetcAudit data->Stop<-",
                "-");
        return list;

    }

    public ArrayList<AuditChecklistGetterSetter> getAuditChecklistCategoryData() {
        Log.d("FetchAudit>Start<--", "----");
        ArrayList<AuditChecklistGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT DISTINCT CHECKLIST_CATEGORY_CD,CHECKLIST_CATEGORY FROM AUDIT_CHECKLIST", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    AuditChecklistGetterSetter audit = new AuditChecklistGetterSetter();
                    audit.setCHECKLIST_CATEGORY_CD(dbcursor.getString(dbcursor.getColumnIndexOrThrow("CHECKLIST_CATEGORY_CD")));
                    audit.setCHECKLIST_CATEGORY(dbcursor.getString(dbcursor.getColumnIndexOrThrow("CHECKLIST_CATEGORY")));
                    list.add(audit);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Ex fetching Audit!",
                    e.toString());
            return list;
        }

        Log.d("FetcAudit data->Stop<-",
                "-");
        return list;

    }


    //Get Topic data

    public ArrayList<TrainingTopicGetterSetter> getTopicData() {
        Log.d("FetchTopic>Start<--", "----");
        ArrayList<TrainingTopicGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT * from TRAINING_TOPIC ", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    TrainingTopicGetterSetter topic = new TrainingTopicGetterSetter();
                    topic.setTOPIC_CD(dbcursor.getString(dbcursor.getColumnIndexOrThrow("TOPIC_CD")));
                    topic.setTOPIC(dbcursor.getString(dbcursor.getColumnIndexOrThrow("TOPIC")));

                    list.add(topic);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Ex fetching Audit!",
                    e.toString());
            return list;
        }

        Log.d("FetcTopic data->Stop<-",
                "-");
        return list;

    }

    //Get Quiz Question data

    public ArrayList<QuizQuestionGettersetter> getQuizQuestionData() {

        Log.d("FetchQuiz>Start<--",
                "----");
        ArrayList<QuizQuestionGettersetter> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            ///change by jeevan
            //dbcursor = db.rawQuery("SELECT * from QUIZ_QUESTION where TOPIC_CD = '" + topic_cd + "'", null);
            dbcursor = db.rawQuery("SELECT * from QUIZ_QUESTION", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    QuizQuestionGettersetter quiz = new QuizQuestionGettersetter();
                    quiz.setTOPIC_CD(dbcursor.getString(dbcursor.getColumnIndexOrThrow("TOPIC_CD")));
                    quiz.setQUESTION_CD(dbcursor.getString(dbcursor.getColumnIndexOrThrow("QUESTION_CD")));
                    quiz.setQUESTION(dbcursor.getString(dbcursor.getColumnIndexOrThrow("QUESTION")));
                    quiz.setANSWER_CD(dbcursor.getString(dbcursor.getColumnIndexOrThrow("ANSWER_CD")));
                    quiz.setANSWER(dbcursor.getString(dbcursor.getColumnIndexOrThrow("ANSWER")));
                    quiz.setRIGHT_ANSWER(dbcursor.getString(dbcursor.getColumnIndexOrThrow("RIGHT_ANSWER")));
                    list.add(quiz);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Ex fetching Quiz!",
                    e.toString());
            return list;
        }

        Log.d("FetcQuiz data->Stop<-",
                "-");
        return list;

    }

    //Get All distinct Quiz data

    public ArrayList<QuizQuestionGettersetter> getAllQuizData() {

        Log.d("FetchQuizs>Start<--",
                "----");
        ArrayList<QuizQuestionGettersetter> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            //change by jeevan
            //dbcursor = db.rawQuery("SELECT DISTINCT QUESTION_CD, QUESTION from QUIZ_QUESTION where TOPIC_CD = '" + topic_cd + "'", null);
            dbcursor = db.rawQuery("SELECT DISTINCT QUESTION_CD, QUESTION from QUIZ_QUESTION ", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    QuizQuestionGettersetter quiz = new QuizQuestionGettersetter();
                    quiz.setQUESTION_CD(dbcursor.getString(dbcursor.getColumnIndexOrThrow("QUESTION_CD")));
                    quiz.setQUESTION(dbcursor.getString(dbcursor.getColumnIndexOrThrow("QUESTION")));
                    list.add(quiz);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Ex fetching Quizs!",
                    e.toString());
            return list;
        }

        Log.d("FetcQuizs data->Stop<-",
                "-");
        return list;

    }

    //POSM List data

    public void insertPosmData(List<PosmGetterSetter> data, String common_id) {

        //db.delete(CommonString.TABLE_STORE_DATA, null, null);
        ContentValues values = new ContentValues();
        long key;

        try {
            for (int i = 0; i < data.size(); i++) {

                values.put(CommonString.KEY_POSM_CD, Integer.parseInt(data.get(i).getPosm_cd().get(0)));
                values.put(CommonString.KEY_POSM, data.get(i).getPosm().get(0));
                values.put(CommonString.KEY_COMMON_ID, Long.parseLong(common_id));
                values.put(CommonString.KEY_QUANTITY, data.get(i).getQuantity());
                values.put(CommonString.KEY_POSM_IMAGE, data.get(i).getPosm_img_str());

                db.insert(CommonString.TABLE_POSM_DATA, null, values);
            }
        } catch (Exception ex) {
            Log.d("DB Excep in POSM Insert", ex.toString());

        }

    }


    //get Audit stored Data

    public ArrayList<AuditChecklistGetterSetter> getAuditInsertedData(String store_cd) {

        Log.d("FetchAudit>Start<--",
                "----");
        ArrayList<AuditChecklistGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT * from " + CommonString.TABLE_CHECKLIST_INSERTED_DATA + " WHERE " +
                    CommonString.KEY_STORE_CD + " = '" + store_cd + "' AND " + CommonString.KEY_ISD_CD + " != '0' ", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    AuditChecklistGetterSetter posm = new AuditChecklistGetterSetter();
                    posm.setCHECKLIST(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_CHECKLIST)));
                    posm.setCHECKLIST_CD(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_CHECKLIST_CD)));
                    posm.setAvailability(dbcursor.getInt(dbcursor.getColumnIndexOrThrow(CommonString.KEY_AVAILABILITY)));
                    posm.setIsd_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ISD_CD)));
                    posm.setStore_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_STORE_CD)));
                    posm.setCHECKLIST_CATEGORY_CD(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_CHECKLIST_CATEGORY_CD)));
                    list.add(posm);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Ex fetching Posm!",
                    e.toString());
            return list;
        }

        Log.d("FetcAudit data->Stop<-",
                "-");
        return list;

    }

    //get Audit stored Data for New employee

    public ArrayList<AuditChecklistGetterSetter> getAuditInsertedNewEmpData(String store_cd) {

        Log.d("FetchAudit>Start<--",
                "----");
        ArrayList<AuditChecklistGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT * from " + CommonString.TABLE_CHECKLIST_INSERTED_DATA +
                    " WHERE " + CommonString.KEY_STORE_CD + " = '" + store_cd + "' AND " + CommonString.KEY_ISD_CD + " = '0' ", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    AuditChecklistGetterSetter posm = new AuditChecklistGetterSetter();

                    posm.setKey_id(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_MID)));
                    posm.setCHECKLIST(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_CHECKLIST)));
                    posm.setCHECKLIST_CD(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_CHECKLIST_CD)));
                    posm.setAvailability(dbcursor.getInt(dbcursor.getColumnIndexOrThrow(CommonString.KEY_AVAILABILITY)));
                    posm.setIsd_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ISD_CD)));
                    posm.setStore_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_STORE_CD)));

                    list.add(posm);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Ex fetching Posm!",
                    e.toString());
            return list;
        }

        Log.d("FetcAudit data->Stop<-",
                "-");
        return list;

    }


    //get New Added Employee Data

    public ArrayList<AddNewEmployeeGetterSetter> getNewEmployeeInsertedData(String store_cd) {
        Log.d("FetchEmp>Start<--", "----");
        ArrayList<AddNewEmployeeGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT * from " + CommonString.TABLE_ADD_NEW_EMPLOYEE + " WHERE " + CommonString.KEY_STORE_CD + " ='" + store_cd + "' AND MANAGED<>0", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    AddNewEmployeeGetterSetter posm = new AddNewEmployeeGetterSetter();
                    posm.setKey_id(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ID)));
                    posm.setName(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_NAME)));
                    posm.setEmail(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_EMAIL)));
                    posm.setPhone(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_PHONE_NO)));
                    if (dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IS_ISD)).equals("0")) {
                        posm.setIsIsd(false);
                    } else {
                        posm.setIsIsd(true);
                    }
                    list.add(posm);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Ex fetching Posm!",
                    e.toString());
            return list;
        }

        Log.d("FetcAudit data->Stop<-",
                "-");
        return list;

    }

    public ArrayList<AddNewEmployeeGetterSetter> getNewEmployeeForManagedZeroInsertedData(String store_cd) {

        Log.d("FetchEmp>Start<--",
                "----");
        ArrayList<AddNewEmployeeGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT * from " + CommonString.TABLE_ADD_NEW_EMPLOYEE + " WHERE " + CommonString.KEY_STORE_CD + " = '" + store_cd + "' AND MANAGED='0'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    AddNewEmployeeGetterSetter posm = new AddNewEmployeeGetterSetter();
                    posm.setKey_id(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ID)));
                    posm.setName(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_NAME)));
                    posm.setEmail(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_EMAIL)));
                    posm.setPhone(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_PHONE_NO)));
                    posm.setManneged(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_MANAGED)));
                    posm.setImage(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IMAGE)));
                    if (dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IS_ISD)).equals("0")) {
                        posm.setIsIsd(false);
                    } else {
                        posm.setIsIsd(true);
                    }
                    list.add(posm);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Ex fetching Posm!",
                    e.toString());
            return list;
        }

        Log.d("FetcAudit data->Stop<-",
                "-");
        return list;

    }


    //get all ISD and employee for inseted quiz data for store_cd

    public ArrayList<AllIsdNEmployeeGetterSetter> getAllIsdNEmployeeData(String store_cd) {

        Log.d("FetchEmp>Start<--",
                "----");
        ArrayList<AllIsdNEmployeeGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT DISTINCT 'Existing' AS TYPE,  A.ISD_CD AS ISD_CD, I.ISD_NAME AS NAME FROM ANSWERED_DATA A " +
                    "INNER JOIN STORE_ISD I ON  A.ISD_CD = I.ISD_CD " +
                    "WHERE A.ISD_CD <> 0 AND A.STORE_CD = '" + store_cd + "' " +
                    "UNION " +
                    "SELECT DISTINCT 'Existing' AS TYPE,  A.ISD_CD AS ISD_CD, I.ISD_NAME AS NAME FROM ANSWERED_DATA A " +
                    " INNER JOIN NEW_ISD I ON  A.ISD_CD = I.ISD_CD " +
                    " WHERE A.ISD_CD <> 0 AND A.STORE_CD = '" + store_cd + "'" +
                    " UNION " +
                    "SELECT DISTINCT 'New' AS TYPE, A.ISD_CD, I.NAME FROM ANSWERED_DATA A " +
                    "INNER JOIN ADD_NEW_EMPLOYEE I ON  A.MID = I.KEY_ID " +
                    " WHERE A.ISD_CD = 0 AND A.STORE_CD = '" + store_cd + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    AllIsdNEmployeeGetterSetter isdemp = new AllIsdNEmployeeGetterSetter();
                    isdemp.setName(dbcursor.getString(dbcursor.getColumnIndexOrThrow("NAME")));
                    isdemp.setIsd_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ISD_CD)));
                    // isdemp.setTopic(dbcursor.getString(dbcursor.getColumnIndexOrThrow("TOPIC")));
                    isdemp.setType(dbcursor.getString(dbcursor.getColumnIndexOrThrow("TYPE")));
                    list.add(isdemp);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Ex fetching Posm!",
                    e.toString());
            return list;
        }

        Log.d("FetcAudit data->Stop<-",
                "-");
        return list;

    }


    public ArrayList<AllIsdNEmployeeGetterSetter> getAllMannedNewNEmployeeData(String store_cd) {

        Log.d("FetchEmp>Start<--",
                "----");
        ArrayList<AllIsdNEmployeeGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT DISTINCT 'NEW' AS TYPE ,NAME,IS_ISD AS ISD_CD FROM ADD_NEW_EMPLOYEE WHERE STORE_CD ='" + store_cd + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    AllIsdNEmployeeGetterSetter isdemp = new AllIsdNEmployeeGetterSetter();
                    isdemp.setName(dbcursor.getString(dbcursor.getColumnIndexOrThrow("NAME")));
                    isdemp.setIsd_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ISD_CD)));
                    // isdemp.setTopic(dbcursor.getString(dbcursor.getColumnIndexOrThrow("TOPIC")));
                    isdemp.setType(dbcursor.getString(dbcursor.getColumnIndexOrThrow("TYPE")));
                    list.add(isdemp);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Ex fetching Posm!",
                    e.toString());
            return list;
        }

        Log.d("FetcAudit data->Stop<-",
                "-");
        return list;

    }


    //get Quiz stored Data

    public ArrayList<QuizAnwserGetterSetter> getQuizData(String store_cd) {
        Log.d("FetchQuiz>Start<--", "----");
        ArrayList<QuizAnwserGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT * from " + CommonString.TABLE_ANSWERED_DATA + " WHERE " +
                    CommonString.KEY_STORE_CD + " = '" + store_cd + "' AND " + CommonString.KEY_ISD_CD + " != '0' ", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {


                    QuizAnwserGetterSetter quiz = new QuizAnwserGetterSetter();
                    quiz.setIsd_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ISD_CD)));
                    quiz.setTopic_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_TOPIC_CD)));
                    quiz.setQuestion_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_QUESTION_CD)));
                    quiz.setAnswer_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ANSWER_CD)));
                    quiz.setTraining_mode_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_TRAINING_MODE_CD)));
                    quiz.setKey_isd_image(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ISD_IMAGE)));
                    list.add(quiz);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Ex fetching Posm!",
                    e.toString());
            return list;
        }

        Log.d("FetcAudit data->Stop<-",
                "-");
        return list;

    }

    //get Quiz stored Data for new employee

    public ArrayList<QuizAnwserGetterSetter> getQuizNewEmployeeData(String store_cd) {

        Log.d("FetchQuiz>Start<--",
                "----");
        ArrayList<QuizAnwserGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT * from " + CommonString.TABLE_ANSWERED_DATA + " WHERE "
                    + CommonString.KEY_STORE_CD + " = '" + store_cd + "' AND " + CommonString.KEY_ISD_CD + " = '0' ", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    QuizAnwserGetterSetter quiz = new QuizAnwserGetterSetter();
                    quiz.setKey_id(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_MID)));
                    quiz.setIsd_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ISD_CD)));
                    quiz.setTopic_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_TOPIC_CD)));
                    quiz.setQuestion_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_QUESTION_CD)));
                    quiz.setAnswer_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ANSWER_CD)));
                    quiz.setTraining_mode_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_TRAINING_MODE_CD)));
                    quiz.setKey_isd_image(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ISD_IMAGE)));
                    list.add(quiz);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Ex fetching Posm!",
                    e.toString());
            return list;
        }

        Log.d("FetcAudit data->Stop<-",
                "-");
        return list;

    }


    //Update Upload status to U
    public void updateUploadStatus(String key_id, String status) {

        try {
            ContentValues values = new ContentValues();
            values.put(CommonString.KEY_UPLOAD_STATUS, status);

            db.update(CommonString.TABLE_STORE_DATA, values,
                    CommonString.KEY_ID + "=" + key_id, null);
        } catch (Exception e) {

        }
    }


    public int CheckMid(String currdate, String storeid) {
        Cursor dbcursor = null;
        int mid = 0;
        try {
            dbcursor = db.rawQuery("SELECT  * from " + CommonString.TABLE_COVERAGE_DATA + "  WHERE " + CommonString.KEY_VISIT_DATE + " = '"
                    + currdate + "' AND " + CommonString.KEY_STORE_CD + " ='" + storeid + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                mid = dbcursor.getInt(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ID));
                dbcursor.close();
            }

        } catch (Exception e) {
            Log.d("Exception mid",
                    e.toString());
        }
        return mid;
    }

    public long InsertCoverageData(CoverageBean data) {
        ContentValues values = new ContentValues();
        long l = 0;
        try {
            values.put(CommonString.KEY_STORE_CD, data.getStoreId());
            values.put(CommonString.KEY_USER_ID, data.getUserId());
            values.put(CommonString.KEY_IN_TIME, data.getInTime());
            values.put(CommonString.KEY_OUT_TIME, data.getOutTime());
            values.put(CommonString.KEY_VISIT_DATE, data.getVisitDate());
            values.put(CommonString.KEY_LATITUDE, data.getLatitude());
            values.put(CommonString.KEY_LONGITUDE, data.getLongitude());
            values.put(CommonString.KEY_REASON_ID, data.getReasonid());
            values.put(CommonString.KEY_REASON, data.getReason());
            values.put(CommonString.KEY_COVERAGE_STATUS, data.getStatus());
            values.put(CommonString.KEY_IMAGE, data.getImage());
            values.put(CommonString.KEY_COVERAGE_REMARK, data.getRemark());
            values.put(CommonString.KEY_REASON_ID, data.getReasonid());
            values.put(CommonString.KEY_REASON, data.getReason());
            values.put(CommonString.KEY_TRAINING_MODE_CD, data.getTraining_mode_cd());
            values.put(CommonString.KEY_MANAGED, data.getManaged());
            l = db.insert(CommonString.TABLE_COVERAGE_DATA, null, values);


        } catch (Exception ex) {
            Log.d("DB Exception coverage",
                    ex.toString());
        }
        return l;
    }

    //Update coverage data on leave selected
    public void updateStoreStatusOnLeave(String storeid, String visitdate,
                                         String status) {

        try {
            ContentValues values = new ContentValues();
            values.put("UPLOAD_STATUS", status);

            db.update("JOURNEY_PLAN_TRAINER", values,
                    CommonString.KEY_STORE_CD + "='" + storeid + "' AND "
                            + CommonString.KEY_VISIT_DATE + "='" + visitdate
                            + "'", null);
        } catch (Exception e) {

        }
    }

    public void updateCoverageStatus(int mid, String status) {
        try {
            ContentValues values = new ContentValues();
            values.put(CommonString.KEY_COVERAGE_STATUS, status);
            db.update(CommonString.TABLE_COVERAGE_DATA, values, CommonString.KEY_ID + "=" + mid, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CoverageBean getCoverageStoreORStatus(String visit_date, String store_cd) {
        CoverageBean sb = new CoverageBean();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * from " + CommonString.TABLE_COVERAGE_DATA
                    + " where " + CommonString.KEY_VISIT_DATE + "='" + visit_date + "' AND STORE_CD='" + store_cd + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    sb.setStoreId(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_STORE_CD)));
                    sb.setUserId(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_USER_ID)));
                    sb.setInTime(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IN_TIME)));
                    sb.setOutTime(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_OUT_TIME)));
                    sb.setVisitDate(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_VISIT_DATE)));
                    sb.setLatitude(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LATITUDE)));
                    sb.setLongitude(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LONGITUDE)));
                    sb.setStatus(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COVERAGE_STATUS)));
                    sb.setImage(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IMAGE)));
                    sb.setReason(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON)));
                    sb.setReasonid(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON_ID)));
                    sb.setMID(Integer.parseInt(((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ID))))));
                    sb.setTraining_mode_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_TRAINING_MODE_CD)));
                    sb.setRemark(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COVERAGE_REMARK)));
                    sb.setManaged(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_MANAGED)));
                }
                dbcursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb;
    }


    // getCoverageData
    public ArrayList<CoverageBean> getCoverageData(String visitdate) {

        ArrayList<CoverageBean> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT  * from " + CommonString.TABLE_COVERAGE_DATA + " where " + CommonString.KEY_VISIT_DATE + "='" + visitdate + "'", null);
            if (dbcursor != null) {

                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CoverageBean sb = new CoverageBean();
                    sb.setStoreId(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_STORE_CD)));
                    sb.setUserId(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_USER_ID)));
                    sb.setInTime(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IN_TIME)));
                    sb.setOutTime(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_OUT_TIME)));
                    sb.setVisitDate(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_VISIT_DATE)));
                    sb.setLatitude(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LATITUDE)));
                    sb.setLongitude(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LONGITUDE)));
                    sb.setStatus(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COVERAGE_STATUS)));
                    sb.setImage(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IMAGE)));
                    sb.setReason(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON)));
                    sb.setReasonid(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON_ID)));
                    sb.setMID(Integer.parseInt(((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ID))))));
                    sb.setTraining_mode_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_TRAINING_MODE_CD)));
                    if (dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COVERAGE_REMARK)) == null) {
                        sb.setRemark("");
                    } else {
                        sb.setRemark(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COVERAGE_REMARK)));
                    }
                    sb.setManaged(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_MANAGED)));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Excep fetch Coverage",
                    e.toString());

        }

        return list;

    }

    // getCoverageData
    public ArrayList<CoverageBean> getCoverageSpecificData(String store_id, String visit_date) {
        ArrayList<CoverageBean> list = new ArrayList<CoverageBean>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * from " + CommonString.TABLE_COVERAGE_DATA + " where " + CommonString.KEY_STORE_CD + "='" + store_id + "'AND VISIT_DATE ='" + visit_date + "'", null);
            if (dbcursor != null) {

                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CoverageBean sb = new CoverageBean();
                    sb.setStoreId(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_STORE_CD)));
                    sb.setUserId(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_USER_ID)));
                    sb.setInTime(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IN_TIME)));
                    sb.setOutTime(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_OUT_TIME)));

                    sb.setVisitDate(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_VISIT_DATE)));
                    sb.setLatitude(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LATITUDE)));
                    sb.setLongitude(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LONGITUDE)));
                    sb.setStatus(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COVERAGE_STATUS)));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exce fetching Coverage",
                    e.toString());

        }

        return list;

    }

    public void updateCoverageStoreOutTime(String StoreId, String VisitDate, String outtime, String status) {
        try {
            ContentValues values = new ContentValues();
            values.put(CommonString.KEY_OUT_TIME, outtime);
            values.put(CommonString.KEY_COVERAGE_STATUS, status);
            db.update(CommonString.TABLE_COVERAGE_DATA, values, CommonString.KEY_STORE_CD + "='" + StoreId + "' AND " + CommonString.KEY_VISIT_DATE + "='" + VisitDate + "'", null);
        } catch (Exception e) {

        }
    }

    //Update checkout status in JCP
    public void updateStoreStatusOnCheckout(String storeid, String visitdate, String status) {
        try {
            ContentValues values = new ContentValues();
            values.put(CommonString.KEY_CHECKOUT_STATUS, status);
            db.update("JOURNEY_PLAN_TRAINER", values, CommonString.KEY_STORE_CD + "='" + storeid + "' AND " + CommonString.KEY_VISIT_DATE + "='" + visitdate + "'", null);
        } catch (Exception e) {

        }
    }

    public void deleteSpecificStoreData(String store_cd) {
        db.delete(CommonString.TABLE_COVERAGE_DATA, CommonString.KEY_STORE_CD + "='" + store_cd + "'", null);
        db.delete(CommonString.TABLE_ANSWERED_DATA, CommonString.KEY_STORE_CD + "='" + store_cd + "'", null);
        db.delete(CommonString.TABLE_CHECKLIST_INSERTED_DATA, CommonString.KEY_STORE_CD + "='" + store_cd + "'", null);
        db.delete(CommonString.TABLE_ADD_NEW_EMPLOYEE, CommonString.KEY_STORE_CD + "='" + store_cd + "'", null);
        db.delete(CommonString.TABLE_NEW_ISD, CommonString.KEY_STORE_CD + "='" + store_cd + "'", null);
        db.delete(CommonString.TABLE_INSERT_OPENINGHEADER_DATA, CommonString.KEY_STORE_CD + "='" + store_cd + "'", null);
        db.delete(CommonString.TABLE_TRAINING_TOPIC_DATA, CommonString.KEY_STORE_CD + "='" + store_cd + "'", null);

    }

    //check if previous coverage table is filled
    public boolean isCoverageDataFilled(String visit_date) {
        boolean filled = false;
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT * FROM COVERAGE_DATA " + "where " + CommonString.KEY_VISIT_DATE + "<>'" + visit_date + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                int icount = dbcursor.getInt(0);
                dbcursor.close();
                if (icount > 0) {
                    filled = true;
                } else {
                    filled = false;
                }
            }

        } catch (Exception e) {
            Log.d("Exception isempty",
                    e.toString());
            return filled;
        }

        return filled;
    }

    //get JCP Data without visit date


    public ArrayList<JCPGetterSetter> getAllJCPData() {

        Log.d("FetchingStoredata--------------->Start<------------",
                "------------------");
        ArrayList<JCPGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT * from JOURNEY_PLAN_TRAINER ", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    JCPGetterSetter df = new JCPGetterSetter();
                    df.setSTORE_CD(dbcursor.getString(dbcursor.getColumnIndexOrThrow("STORE_CD")));
                    df.setEMP_CD(dbcursor.getString(dbcursor.getColumnIndexOrThrow("EMP_CD")));

                    df.setVISIT_DATE(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("VISIT_DATE")));
                    df.setKEYACCOUNT(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("KEYACCOUNT")));
                    df.setSTORENAME(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("STORENAME")));
                    df.setCITY(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("CITY")));
                    df.setSTORETYPE(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("STORETYPE")));
                    df.setTMODE_CD(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("TMODE_CD")));
                    df.setTRAINING_MODE(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("TRAINING_MODE")));
                    df.setUPLOAD_STATUS(dbcursor.getString(dbcursor.getColumnIndexOrThrow("UPLOAD_STATUS")));
                    df.setCHECKOUT_STATUS(dbcursor.getString(dbcursor.getColumnIndexOrThrow("CHECKOUT_STATUS")));
                    df.setMANAGED(dbcursor.getString(dbcursor.getColumnIndexOrThrow("MANAGED")));
                    df.setLAT(dbcursor.getString(dbcursor.getColumnIndexOrThrow("LATTITUDE")));
                    df.setLONG(dbcursor.getString(dbcursor.getColumnIndexOrThrow("LONGITUDE")));
                    df.setGEOTAG(dbcursor.getString(dbcursor.getColumnIndexOrThrow("GEOTAG")));

                    list.add(df);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching JCP!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return list;
        }

        Log.d("FetchingJCP data---------------------->Stop<-----------",
                "-------------------");
        return list;

    }


    /// get store Status
    public JCPGetterSetter getStoreStatus(String id) {

        Log.d("FetchingStoredata--------------->Start<------------",
                "------------------");

        JCPGetterSetter sb = new JCPGetterSetter();

        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT  * from  JOURNEY_PLAN_TRAINER"
                    + "  WHERE STORE_CD = '"
                    + id + "'", null);

            if (dbcursor != null) {
                int numrows = dbcursor.getCount();

                dbcursor.moveToFirst();
                for (int i = 0; i < numrows; i++) {

                    sb.setSTORE_CD(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_STORE_CD)));

                    sb.setCHECKOUT_STATUS((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("CHECKOUT_STATUS"))));

                    sb.setUPLOAD_STATUS(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("UPLOAD_STATUS")));


                    dbcursor.moveToNext();

                }

                dbcursor.close();

            }

        } catch (Exception e) {
            Log.d("Exception when fetching Records!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
        }

        Log.d("FetchingStoredat---------------------->Stop<-----------",
                "-------------------");
        return sb;

    }

    public void InsertStoregeotagging(String storeid, double lat, double longitude, String path, String status) {
        db.delete(CommonString.TABLE_STORE_GEOTAGGING, CommonString.KEY_STORE_CD + "='" + storeid + "'", null);

        ContentValues values = new ContentValues();
        try {
            values.put(CommonString.KEY_STORE_CD, storeid);
            values.put("LATITUDE", Double.toString(lat));
            values.put("LONGITUDE", Double.toString(longitude));
            values.put("FRONT_IMAGE", path);
            values.put("GEO_TAG", status);
            db.insert(CommonString.TABLE_STORE_GEOTAGGING, null, values);

        } catch (Exception ex) {
            Log.d("Database Exception while Insert Closes Data ",
                    ex.getMessage());
        }

    }

    public void updateOutTime(String status, String StoreId, String VisitDate) {
        try {
            ContentValues values = new ContentValues();
            values.put(CommonString.KEY_GEO_TAG, status);
            db.update(CommonString.TABLE_COVERAGE_DATA, values, CommonString.KEY_STORE_CD + "='" + StoreId + "' AND " + CommonString.KEY_VISIT_DATE + "='" + VisitDate + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateLaTLONGAndSTATUS(String status, String StoreId, String VisitDate, Double lat, Double lont) {
        try {
            ContentValues values = new ContentValues();
            values.put(CommonString.KEY_GEO_TAG, status);
            values.put(CommonString.KEY_LAT, lat);
            values.put(CommonString.KEY_LONGT, lont);
            db.update("JOURNEY_PLAN_TRAINER", values, CommonString.KEY_STORE_CD + "='" + StoreId + "' AND " + CommonString.KEY_VISIT_DATE + "='" + VisitDate + "'", null);
        } catch (Exception e) {
            e.printStackTrace();

        }

    }


    public ArrayList<GeotaggingBeans> getGeotaggingData(String store_cd) {
        Log.d("FetchingStoredata--------------->Start<------------",
                "------------------");

        ArrayList<GeotaggingBeans> geodata = new ArrayList<>();

        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT DISTINCT * FROM STORE_GEOTAGGING WHERE STORE_CD= '" + store_cd + "' ", null);

            if (dbcursor != null) {
                int numrows = dbcursor.getCount();
                dbcursor.moveToFirst();
                for (int i = 1; i <= numrows; ++i) {
                    GeotaggingBeans data = new GeotaggingBeans();
                    data.setStoreId(dbcursor.getString(dbcursor.getColumnIndexOrThrow("STORE_CD")));
                    data.setLatitude(Double.parseDouble(dbcursor.getString(dbcursor.getColumnIndexOrThrow("LATITUDE"))));
                    data.setLongitude(Double.parseDouble(dbcursor.getString(dbcursor.getColumnIndexOrThrow("LONGITUDE"))));
                    data.setUrl1(dbcursor.getString(dbcursor.getColumnIndexOrThrow("FRONT_IMAGE")));
                    data.setGEO_TAG(dbcursor.getString(dbcursor.getColumnIndexOrThrow("GEO_TAG")));
                    geodata.add(data);
                    dbcursor.moveToNext();

                }

                dbcursor.close();

            }

        } catch (Exception e) {
            Log.d("Exception when fetching Records!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
        }

        Log.d("FetchingStoredat---------------------->Stop<-----------",
                "-------------------");
        return geodata;
    }


    //check if table is empty
    public boolean isPOSMDataFilled(String storeId) {
        boolean filled = false;
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT * FROM ANSWERED_DATA WHERE STORE_CD= '" + storeId + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                int icount = dbcursor.getInt(0);
                dbcursor.close();
                if (icount > 0) {
                    filled = true;
                } else {
                    filled = false;
                }
            }
        } catch (Exception e) {
            Log.d("Exception when fetching Records!!!!!!!!!!!!!!!!!!!!!", e.toString());
            return filled;
        }

        return filled;
    }


    //check if table is empty
    public boolean IsManagedZero(String storeId) {
        boolean filled = false;
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT * FROM ADD_NEW_EMPLOYEE WHERE STORE_CD ='" + storeId + "' AND MANAGED ='0'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                int icount = dbcursor.getInt(0);
                dbcursor.close();
                if (icount > 0) {
                    filled = true;
                } else {
                    filled = false;
                }

            }
        } catch (Exception e) {
            Log.d("Exception when fetching Records!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return filled;
        }

        return filled;
    }

    public long insertSALETEAMTRAININGDATA(
            String visit_date, String USER_ID, ArrayList<TrainingTopicGetterSetter> secCompleteMarketDATA) {
        db.delete(CommonString.TABLE_SALETEAM_TRAINEE_DATA, "VISIT_DATE" + "='" + visit_date + "'", null);
        ContentValues values = new ContentValues();
        long l = 0;
        try {
            for (int i = 0; i < secCompleteMarketDATA.size(); i++) {
                values.put("VISIT_DATE", visit_date);
                values.put("USER_ID", USER_ID);
                values.put("TOPIC_CD", secCompleteMarketDATA.get(i).getTOPIC_CD().get(0));
                values.put("TOPIC", secCompleteMarketDATA.get(i).getTOPIC().get(0));
                values.put("TRAINEE_NAME", secCompleteMarketDATA.get(i).getTrainee_userN());
                values.put("TRAINEE_CD", secCompleteMarketDATA.get(i).getTrainee_cd());
                values.put("STATUS", secCompleteMarketDATA.get(i).getStaus());

                l = db.insert(CommonString.TABLE_SALETEAM_TRAINEE_DATA, null, values);

            }

        } catch (Exception ex) {
            Log.d("Database Exception while Insert Facing Competition Data ",
                    ex.toString());
        }

        return l;
    }

    public void remove_saleteam_trainnee(String user_id) {
        db.execSQL("DELETE FROM " + CommonString.TABLE_SALETEAM_TRAINEE_DATA + " WHERE " + CommonString.KEY_ID + " = '" + user_id + "'");
    }

    public boolean checkdateSAlesTRainee(String visit_date) {
        boolean filled = false;
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT * FROM " + CommonString.TABLE_SALETEAM_TRAINEE_DATA + " where " + CommonString.KEY_VISIT_DATE + "<>'" + visit_date + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                int icount = dbcursor.getInt(0);
                dbcursor.close();
                if (icount > 0) {
                    filled = true;
                } else {
                    filled = false;
                }
            }
        } catch (Exception e) {
            Log.d("Exception isempty",
                    e.toString());
            return filled;
        }

        return filled;
    }

    public void removealldata() {
        db.execSQL("DELETE FROM " + CommonString.TABLE_SALETEAM_TRAINEE_DATA, null);

    }


    public ArrayList<TrainingTopicGetterSetter> getSALETEAMTRaineeInsertedDATA(String visit_data) {
        Log.d("Fetching", "Storedata--------------->Start<------------");
        ArrayList<TrainingTopicGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT * FROM " + CommonString.TABLE_SALETEAM_TRAINEE_DATA + " WHERE VISIT_DATE ='" + visit_data + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    TrainingTopicGetterSetter sb = new TrainingTopicGetterSetter();
                    sb.setTrainee_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow("TRAINEE_CD")));
                    sb.setTrainee_userN(dbcursor.getString(dbcursor.getColumnIndexOrThrow("TRAINEE_NAME")));
                    sb.setTOPIC_CD(dbcursor.getString(dbcursor.getColumnIndexOrThrow("TOPIC_CD")));
                    sb.setTOPIC(dbcursor.getString(dbcursor.getColumnIndexOrThrow("TOPIC")));
                    sb.setKey_id(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ID)));
                    sb.setStaus(dbcursor.getString(dbcursor.getColumnIndexOrThrow("STATUS")));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Log.d("Exception ", "when fetching opening stock!!!!!!!!!!!" + e.toString());
            return list;
        }

        Log.d("Fetching ", "opening stock---------------------->Stop<-----------");
        return list;
    }

    public long updateSaleTeamTraineeStatus(String visit_date, String status) {
        long l = 0;
        try {
            ContentValues values = new ContentValues();
            values.put("STATUS", status);
            l = db.update(CommonString.TABLE_SALETEAM_TRAINEE_DATA, values, CommonString.KEY_VISIT_DATE + "='" + visit_date + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return l;
    }

    //Audit Checklist data

    public long insertAuditCheckLisAnswertData(AuditChecklistAnswerGetterSetter data) {
        db.delete("AUDIT_CHECKLIST_ANSWER", null, null);
        ContentValues values = new ContentValues();
        long l = 0;

        try {
            for (int i = 0; i < data.getAnswer_cd().size(); i++) {

                values.put("CHECKLIST_CD", Integer.parseInt(data.getChecklist_cd().get(i)));
                values.put("ANSWER_CD", Integer.parseInt(data.getAnswer_cd().get(i)));
                values.put("ANSWER", data.getAnswer().get(i));

                l = db.insert("AUDIT_CHECKLIST_ANSWER", null, values);
            }

        } catch (Exception ex) {
            Log.d("DB Exc Audit Insert", ex.toString());
        }
        return l;
    }


    public ArrayList<AuditChecklistAnswerGetterSetter> getAuditChecklistAnswerData(String checklist_cd) {

        Log.d("FetchQuizs>Start<--",
                "----");
        ArrayList<AuditChecklistAnswerGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT DISTINCT ANSWER, ANSWER_CD from AUDIT_CHECKLIST_ANSWER where CHECKLIST_CD= '" + checklist_cd + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    AuditChecklistAnswerGetterSetter quiz = new AuditChecklistAnswerGetterSetter();
                    quiz.setAnswer_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow("ANSWER_CD")));
                    quiz.setAnswer(dbcursor.getString(dbcursor.getColumnIndexOrThrow("ANSWER")));
                    list.add(quiz);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Ex fetching Quizs!",
                    e.toString());
            return list;
        }

        Log.d("FetcQuizs data->Stop<-",
                "-");
        return list;

    }

    public long insertAuditCheckLisAnswertData(SaleTeamGetterSetter data) {
        db.delete("SALES_TEAM", null, null);
        ContentValues values = new ContentValues();
        long l = 0;

        try {
            for (int i = 0; i < data.getTrainee_cd().size(); i++) {

                values.put("SALES_TEAM_ID", Integer.parseInt(data.getTrainee_cd().get(i)));
                values.put("SALES_TEAM", data.getTrainee().get(i));

                l = db.insert("SALES_TEAM", null, values);
            }

        } catch (Exception ex) {
            Log.d("DB Exc SALES_TEAM Insert", ex.toString());
        }
        return l;
    }

    public ArrayList<SaleTeamGetterSetter> getSaleTeamAnswerData() {

        Log.d("FetchQuizs>Start<--",
                "----");
        ArrayList<SaleTeamGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT DISTINCT SALES_TEAM, SALES_TEAM_ID from SALES_TEAM", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    SaleTeamGetterSetter quiz = new SaleTeamGetterSetter();
                    quiz.setTrainee_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow("SALES_TEAM_ID")));
                    quiz.setTrainee(dbcursor.getString(dbcursor.getColumnIndexOrThrow("SALES_TEAM")));
                    list.add(quiz);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Ex fetching Quizs!",
                    e.toString());
            return list;
        }

        Log.d("FetcQuizs data->Stop<-",
                "-");
        return list;

    }

    public long insertTrainningTopicMultiData(ArrayList<TrainingTopicGetterSetter> data, String store_cd, String isd_cd, long mid,
                                              String visit_date) {
        ContentValues values = new ContentValues();
        long key = 0;
        try {

            for (int i = 0; i < data.size(); i++) {
                values.put(CommonString.KEY_STORE_CD, store_cd);
                values.put(CommonString.KEY_ISD_CD, isd_cd);
                values.put(CommonString.KEY_MID, mid);
                values.put(CommonString.KEY_VISIT_DATE, visit_date);
                values.put(CommonString.KEY_TOPIC_CD, data.get(i).getTOPIC_CD().get(0));
                values.put(CommonString.KEY_TOPIC, data.get(i).getTOPIC().get(0));

                key = db.insert(CommonString.TABLE_TRAINING_TOPIC_DATA, null, values);
            }
        } catch (Exception ex) {
            Log.d("DB Excep Audit Insert", ex.toString());
            //return 0;
        }
        return key;
    }

    public ArrayList<TrainingTopicGetterSetter> getTrainningTopicData(String store_cd) {
        Log.d("FetchQuizs>Start<--", "----");
        ArrayList<TrainingTopicGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT * from " + CommonString.TABLE_TRAINING_TOPIC_DATA +
                    " where " + CommonString.KEY_STORE_CD + "='" + store_cd + "' AND " + CommonString.KEY_ISD_CD + " != '0' ", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    TrainingTopicGetterSetter quiz = new TrainingTopicGetterSetter();
                    quiz.setTOPIC_CD(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_TOPIC_CD)));
                    quiz.setTOPIC(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_TOPIC)));
                    quiz.setIsd_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ISD_CD)));
                    quiz.setKey_id(dbcursor.getString(dbcursor.getColumnIndexOrThrow("MID")));
                    list.add(quiz);
                    dbcursor.moveToNext();
                }

                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Ex fetching Quizs!",
                    e.toString());
            return list;
        }

        Log.d("FetcQuizs data->Stop<-",
                "-");
        return list;

    }

    public ArrayList<TrainingTopicGetterSetter> getTrainningTopicfORNEWEMPLOYEEData(String store_cd) {
        Log.d("FetchQuizs>Start<--", "----");
        ArrayList<TrainingTopicGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT * from " + CommonString.TABLE_TRAINING_TOPIC_DATA +
                    " where " + CommonString.KEY_STORE_CD + "='" + store_cd + "' AND " + CommonString.KEY_ISD_CD + " = '0' ", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    TrainingTopicGetterSetter quiz = new TrainingTopicGetterSetter();
                    quiz.setTOPIC_CD(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_TOPIC_CD)));
                    quiz.setTOPIC(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_TOPIC)));
                    quiz.setIsd_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ISD_CD)));
                    quiz.setKey_id(dbcursor.getString(dbcursor.getColumnIndexOrThrow("MID")));
                    list.add(quiz);
                    dbcursor.moveToNext();
                }

                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Ex fetching Quizs!",
                    e.toString());
            return list;
        }

        Log.d("FetcQuizs data->Stop<-",
                "-");
        return list;

    }


}
