package cpm.com.lenovotraining.xmlgettersetter;

import java.util.ArrayList;

/**
 * Created by jeevanp on 3/15/2018.
 */

public class SaleTeamGetterSetter {
    ArrayList<String>trainee_cd=new ArrayList<>();
    ArrayList<String>trainee=new ArrayList<>();
    String saleTEAMTable;

    public ArrayList<String> getTrainee_cd() {
        return trainee_cd;
    }

    public void setTrainee_cd(String trainee_cd) {
        this.trainee_cd.add(trainee_cd);
    }

    public ArrayList<String> getTrainee() {
        return trainee;
    }

    public void setTrainee(String trainee) {
        this.trainee.add(trainee);
    }

    public String getSaleTEAMTable() {
        return saleTEAMTable;
    }

    public void setSaleTEAMTable(String saleTEAMTable) {
        this.saleTEAMTable = saleTEAMTable;
    }
    boolean isSelected=false;
    public boolean isSelected() {
        return isSelected;
    }

    /**
     * @param isSelected the isSelected to set
     */
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

}
