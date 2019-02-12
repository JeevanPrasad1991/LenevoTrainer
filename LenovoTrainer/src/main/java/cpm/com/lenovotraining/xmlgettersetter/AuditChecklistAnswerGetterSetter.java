package cpm.com.lenovotraining.xmlgettersetter;

import java.util.ArrayList;

/**
 * Created by jeevanp on 3/14/2018.
 */

public class AuditChecklistAnswerGetterSetter {
    ArrayList<String>answer_cd=new ArrayList<>();
    ArrayList<String>answer=new ArrayList<>();
    ArrayList<String>checklist_cd=new ArrayList<>();
    String auditchecklistANSTable;

    public ArrayList<String> getAnswer_cd() {
        return answer_cd;
    }

    public void setAnswer_cd(String answer_cd) {
        this.answer_cd.add(answer_cd);
    }

    public ArrayList<String> getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer.add(answer);
    }

    public ArrayList<String> getChecklist_cd() {
        return checklist_cd;
    }

    public void setChecklist_cd(String checklist_cd) {
        this.checklist_cd.add(checklist_cd);
    }

    public String getAuditchecklistANSTable() {
        return auditchecklistANSTable;
    }

    public void setAuditchecklistANSTable(String auditchecklistANSTable) {
        this.auditchecklistANSTable = auditchecklistANSTable;
    }
}
