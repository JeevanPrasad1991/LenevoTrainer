package cpm.com.lenovotraining.xmlgettersetter;

import java.util.ArrayList;

/**
 * Created by yadavendras on 10-08-2016.
 */
public class StoreISDGetterSetter {

    ArrayList<String> STORE_CD = new ArrayList<>();
    ArrayList<String> ISD_CD = new ArrayList<>();
    ArrayList<String> ISD_NAME = new ArrayList<>();

    String table_store_isd;

    public ArrayList<String> getSTORE_CD() {
        return STORE_CD;
    }

    public void setSTORE_CD(String STORE_CD) {
        this.STORE_CD.add(STORE_CD);
    }

    public ArrayList<String> getISD_CD() {
        return ISD_CD;
    }

    public void setISD_CD(String ISD_CD) {
        this.ISD_CD.add(ISD_CD);
    }

    public ArrayList<String> getISD_NAME() {
        return ISD_NAME;
    }

    public void setISD_NAME(String ISD_NAME) {
        this.ISD_NAME.add(ISD_NAME);
    }

    public String getTable_store_isd() {
        return table_store_isd;
    }

    public void setTable_store_isd(String table_store_isd) {
        this.table_store_isd = table_store_isd;
    }
}
