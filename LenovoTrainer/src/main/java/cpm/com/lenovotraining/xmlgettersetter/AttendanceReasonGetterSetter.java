package cpm.com.lenovotraining.xmlgettersetter;

import java.util.ArrayList;

/**
 * Created by yadavendras on 19-09-2016.
 */
public class AttendanceReasonGetterSetter {

    ArrayList<String> REASON_CD = new ArrayList<>();
    ArrayList<String> REASON = new ArrayList<>();
    ArrayList<String> ENTRY_ALLOW = new ArrayList<>();
    ArrayList<String> STATUS = new ArrayList<>();

    String table_reason_attendance;

    public ArrayList<String> getREASON_CD() {
        return REASON_CD;
    }

    public void setREASON_CD(String REASON_CD) {
        this.REASON_CD.add(REASON_CD);
    }

    public ArrayList<String> getREASON() {
        return REASON;
    }

    public void setREASON(String REASON) {
        this.REASON.add(REASON);
    }

    public ArrayList<String> getENTRY_ALLOW() {
        return ENTRY_ALLOW;
    }

    public void setENTRY_ALLOW(String ENTRY_ALLOW) {
        this.ENTRY_ALLOW.add(ENTRY_ALLOW);
    }

    public String getTable_reason_attendance() {
        return table_reason_attendance;
    }

    public void setTable_reason_attendance(String table_reason_attendance) {
        this.table_reason_attendance = table_reason_attendance;
    }

    public ArrayList<String> getSTATUS() {
        return STATUS;
    }

    public void setSTATUS(String STATUS) {
        this.STATUS.add(STATUS);
    }
}
