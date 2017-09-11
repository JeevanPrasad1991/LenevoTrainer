package cpm.com.lenovotraining.xmlgettersetter;

import java.util.ArrayList;

/**
 * Created by yadavendras on 10-08-2016.
 */
public class JCPGetterSetter {

    ArrayList<String> STORE_CD = new ArrayList<>();
    ArrayList<String> EMP_CD = new ArrayList<>();
    ArrayList<String> VISIT_DATE = new ArrayList<>();
    ArrayList<String> KEYACCOUNT = new ArrayList<>();
    ArrayList<String> STORENAME = new ArrayList<>();
    ArrayList<String> CITY = new ArrayList<>();
    ArrayList<String> STORETYPE = new ArrayList<>();
    ArrayList<String> TMODE_CD = new ArrayList<>();
    ArrayList<String> TRAINING_MODE = new ArrayList<>();
    ArrayList<String> UPLOAD_STATUS = new ArrayList<>();
    ArrayList<String> CHECKOUT_STATUS = new ArrayList<>();
    ArrayList<String> MANAGED = new ArrayList<>();

    public ArrayList<String> getLAT() {
        return LAT;
    }

    public void setLAT(String LAT) {
        this.LAT.add(LAT);
    }

    public ArrayList<String> getLONG() {
        return LONG;
    }

    public void setLONG(String LONG) {
        this.LONG.add(LONG);
    }

    public ArrayList<String> getGEOTAG() {
        return GEOTAG;
    }

    public void setGEOTAG(String GEOTAG) {
        this.GEOTAG.add(GEOTAG);
    }

    ArrayList<String> LAT = new ArrayList<>();
    ArrayList<String> LONG = new ArrayList<>();
    ArrayList<String> GEOTAG = new ArrayList<>();
    String table_juorney_plan_trainer;

    public ArrayList<String> getSTORE_CD() {
        return STORE_CD;
    }

    public void setSTORE_CD(String STORE_CD) {
        this.STORE_CD.add(STORE_CD);
    }

    public ArrayList<String> getEMP_CD() {
        return EMP_CD;
    }

    public void setEMP_CD(String EMP_CD) {
        this.EMP_CD.add(EMP_CD);
    }

    public ArrayList<String> getVISIT_DATE() {
        return VISIT_DATE;
    }

    public void setVISIT_DATE(String VISIT_DATE) {
        this.VISIT_DATE.add(VISIT_DATE);
    }

    public ArrayList<String> getKEYACCOUNT() {
        return KEYACCOUNT;
    }

    public void setKEYACCOUNT(String KEYACCOUNT) {
        this.KEYACCOUNT.add(KEYACCOUNT);
    }

    public ArrayList<String> getSTORENAME() {
        return STORENAME;
    }

    public void setSTORENAME(String STORENAME) {
        this.STORENAME.add(STORENAME);
    }

    public ArrayList<String> getCITY() {
        return CITY;
    }

    public void setCITY(String CITY) {
        this.CITY.add(CITY);
    }

    public ArrayList<String> getSTORETYPE() {
        return STORETYPE;
    }

    public void setSTORETYPE(String STORETYPE) {
        this.STORETYPE.add(STORETYPE);
    }

    public ArrayList<String> getTMODE_CD() {
        return TMODE_CD;
    }

    public void setTMODE_CD(String TMODE_CD) {
        this.TMODE_CD.add(TMODE_CD);
    }

    public ArrayList<String> getTRAINING_MODE() {
        return TRAINING_MODE;
    }

    public void setTRAINING_MODE(String TRAINING_MODE) {
        this.TRAINING_MODE.add(TRAINING_MODE);
    }

    public ArrayList<String> getUPLOAD_STATUS() {
        return UPLOAD_STATUS;
    }

    public void setUPLOAD_STATUS(String UPLOAD_STATUS) {
        this.UPLOAD_STATUS.add(UPLOAD_STATUS);
    }

    public ArrayList<String> getCHECKOUT_STATUS() {
        return CHECKOUT_STATUS;
    }

    public void setCHECKOUT_STATUS(String CHECKOUT_STATUS) {
        this.CHECKOUT_STATUS.add(CHECKOUT_STATUS);
    }

    public String getTable_juorney_plan_trainer() {
        return table_juorney_plan_trainer;
    }

    public void setTable_juorney_plan_trainer(String table_juorney_plan_trainer) {
        this.table_juorney_plan_trainer = table_juorney_plan_trainer;
    }


    public ArrayList<String> getMANAGED() {
        return MANAGED;
    }

    public void setMANAGED(String MANAGED) {
        this.MANAGED.add(MANAGED);
    }
}
