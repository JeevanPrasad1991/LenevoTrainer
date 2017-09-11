package cpm.com.lenovotraining.xmlgettersetter;

import java.util.ArrayList;

/**
 * Created by yadavendras on 23-08-2016.
 */
public class IsdPerformanceGetterSetter {

    String table_isd_performance;

    ArrayList<String> TRAINING_DATE = new ArrayList<>();
    ArrayList<String> TRAINING_TYPE = new ArrayList<>();
    ArrayList<String> TOPIC = new ArrayList<>();
    ArrayList<String> ISD_CD = new ArrayList<>();
    ArrayList<String> ISD = new ArrayList<>();
    ArrayList<String> GROOMING_SCORE = new ArrayList<>();
    ArrayList<String> QUIZ_SCORE = new ArrayList<>();

    public ArrayList<String> getTRAINING_DATE() {
        return TRAINING_DATE;
    }

    public void setTRAINING_DATE(String TRAINING_DATE) {
        this.TRAINING_DATE.add(TRAINING_DATE);
    }

    public ArrayList<String> getTRAINING_TYPE() {
        return TRAINING_TYPE;
    }

    public void setTRAINING_TYPE(String TRAINING_TYPE) {
        this.TRAINING_TYPE.add(TRAINING_TYPE);
    }

    public ArrayList<String> getTOPIC() {
        return TOPIC;
    }

    public void setTOPIC(String TOPIC) {
        this.TOPIC.add(TOPIC);
    }

    public ArrayList<String> getISD_CD() {
        return ISD_CD;
    }

    public void setISD_CD(String ISD_CD) {
        this.ISD_CD.add(ISD_CD);
    }

    public ArrayList<String> getISD() {
        return ISD;
    }

    public void setISD(String ISD) {
        this.ISD.add(ISD);
    }

    public ArrayList<String> getGROOMING_SCORE() {
        return GROOMING_SCORE;
    }

    public void setGROOMING_SCORE(String GROOMING_SCORE) {
        this.GROOMING_SCORE.add(GROOMING_SCORE);
    }

    public ArrayList<String> getQUIZ_SCORE() {
        return QUIZ_SCORE;
    }

    public void setQUIZ_SCORE(String QUIZ_SCORE) {
        this.QUIZ_SCORE.add(QUIZ_SCORE);
    }

    public String getTable_isd_performance() {
        return table_isd_performance;
    }

    public void setTable_isd_performance(String table_isd_performance) {
        this.table_isd_performance = table_isd_performance;
    }
}
