package cpm.com.lenovotraining.xmlgettersetter;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by jeevan
 */
public class TrainingTopicGetterSetter implements Serializable {

    ArrayList<String> TOPIC_CD = new ArrayList<>();
    ArrayList<String> TOPIC = new ArrayList<>();
    String table_training_topic;
    String key_id;

    public String getIsd_cd() {
        return isd_cd;
    }

    public void setIsd_cd(String isd_cd) {
        this.isd_cd = isd_cd;
    }

    String isd_cd;


    public String getStaus() {
        return staus;
    }

    public void setStaus(String staus) {
        this.staus = staus;
    }

    String staus = "N";

    public String getKey_id() {
        return key_id;
    }

    public void setKey_id(String key_id) {
        this.key_id = key_id;
    }

    public String getTrainee_userN() {
        return trainee_userN;
    }

    public void setTrainee_userN(String trainee_userN) {
        this.trainee_userN = trainee_userN;
    }

    String trainee_userN;

    public String getTrainee_cd() {
        return trainee_cd;
    }

    public void setTrainee_cd(String trainee_cd) {
        this.trainee_cd = trainee_cd;
    }

    String trainee_cd;

    public ArrayList<String> getTOPIC_CD() {
        return TOPIC_CD;
    }

    public void setTOPIC_CD(String TOPIC_CD) {
        this.TOPIC_CD.add(TOPIC_CD);
    }

    public ArrayList<String> getTOPIC() {
        return TOPIC;
    }

    public void setTOPIC(String TOPIC) {
        this.TOPIC.add(TOPIC);
    }

    public String getTable_training_topic() {
        return table_training_topic;
    }

    public void setTable_training_topic(String table_training_topic) {
        this.table_training_topic = table_training_topic;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TrainingTopicGetterSetter)) {
            return false;
        }

        TrainingTopicGetterSetter other = (TrainingTopicGetterSetter) obj;
        return this.getTOPIC_CD().get(0).equals(other.getTOPIC_CD().get(0));
    }

    @Override
    public int hashCode() {
        // return super.hashCode();
        return getTOPIC_CD().get(0).hashCode();
    }

    boolean isSelected = false;

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
