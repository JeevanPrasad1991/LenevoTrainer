package cpm.com.lenovotraining.xmlgettersetter;

import java.util.ArrayList;

/**
 * Created by yadavendras on 22-06-2016.
 */
public class TrainingTopicGetterSetter {

    ArrayList<String> TOPIC_CD = new ArrayList<>();
    ArrayList<String> TOPIC = new ArrayList<>();

    String table_training_topic;

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
}
