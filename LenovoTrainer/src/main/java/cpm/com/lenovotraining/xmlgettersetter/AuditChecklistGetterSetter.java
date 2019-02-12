package cpm.com.lenovotraining.xmlgettersetter;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by yadavendras on 11-08-2016.
 */
public class AuditChecklistGetterSetter implements Serializable{

    ArrayList<String> CHECKLIST_CD = new ArrayList<>();
    ArrayList<String> CHECKLIST = new ArrayList<>();
    ArrayList<String> CHECKLIST_TYPE = new ArrayList<>();
    ArrayList<String> CHECKLIST_CATEGORY_CD = new ArrayList<>();
    ArrayList<String> CHECKLIST_CATEGORY = new ArrayList<>();
    String table_audit_checklist;
    int availability;
    String isd_cd, store_cd,key_id;
    public ArrayList<String> getCHECKLIST_TYPE() {
        return CHECKLIST_TYPE;
    }

    public void setCHECKLIST_TYPE(String CHECKLIST_TYPE) {
        this.CHECKLIST_TYPE.add(CHECKLIST_TYPE);
    }

    public ArrayList<String> getCHECKLIST_CATEGORY_CD() {
        return CHECKLIST_CATEGORY_CD;
    }

    public void setCHECKLIST_CATEGORY_CD(String CHECKLIST_CATEGORY_CD) {
        this.CHECKLIST_CATEGORY_CD.add(CHECKLIST_CATEGORY_CD);
    }

    public ArrayList<String> getCHECKLIST_CATEGORY() {
        return CHECKLIST_CATEGORY;
    }

    public void setCHECKLIST_CATEGORY(String CHECKLIST_CATEGORY) {
        this.CHECKLIST_CATEGORY.add(CHECKLIST_CATEGORY);
    }
    public ArrayList<String> getCHECKLIST() {
        return CHECKLIST;
    }

    public void setCHECKLIST(String CHECKLIST) {
        this.CHECKLIST.add(CHECKLIST);
    }

    public String getTable_audit_checklist() {
        return table_audit_checklist;
    }

    public void setTable_audit_checklist(String table_audit_checklist) {
        this.table_audit_checklist = table_audit_checklist;
    }

    public ArrayList<String> getCHECKLIST_CD() {
        return CHECKLIST_CD;
    }

    public void setCHECKLIST_CD(String CHECKLIST_CD) {
        this.CHECKLIST_CD.add(CHECKLIST_CD);
    }

    public int getAvailability() {
        return availability;
    }

    public void setAvailability(int availability) {
        this.availability = availability;
    }

    public String getIsd_cd() {
        return isd_cd;
    }

    public void setIsd_cd(String isd_cd) {
        this.isd_cd = isd_cd;
    }

    public String getStore_cd() {
        return store_cd;
    }

    public void setStore_cd(String store_cd) {
        this.store_cd = store_cd;
    }

    public String getKey_id() {
        return key_id;
    }

    public void setKey_id(String key_id) {
        this.key_id = key_id;
    }
}
