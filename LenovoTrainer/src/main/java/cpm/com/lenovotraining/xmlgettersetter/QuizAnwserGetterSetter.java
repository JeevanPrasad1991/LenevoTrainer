package cpm.com.lenovotraining.xmlgettersetter;

/**
 * Created by yadavendras on 17-08-2016.
 */
public class QuizAnwserGetterSetter {

    String isd_cd, topic_cd, question_cd, answer_cd, answer, training_mode_cd, key_id;

    int checkedId;
    public String getKey_isd_image() {
        return key_isd_image;
    }

    public void setKey_isd_image(String key_isd_image) {
        this.key_isd_image = key_isd_image;
    }

    String key_isd_image;

    public String getTopic_cd() {
        return topic_cd;
    }

    public void setTopic_cd(String topic_cd) {
        this.topic_cd = topic_cd;
    }

    public String getQuestion_cd() {
        return question_cd;
    }

    public void setQuestion_cd(String question_cd) {
        this.question_cd = question_cd;
    }

    public String getAnswer_cd() {
        return answer_cd;
    }

    public void setAnswer_cd(String answer_cd) {
        this.answer_cd = answer_cd;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getIsd_cd() {
        return isd_cd;
    }

    public void setIsd_cd(String isd_cd) {
        this.isd_cd = isd_cd;
    }


    public int getCheckedId() {
        return checkedId;
    }

    public void setCheckedId(int checkedId) {
        this.checkedId = checkedId;
    }


    public String getTraining_mode_cd() {
        return training_mode_cd;
    }

    public void setTraining_mode_cd(String training_mode_cd) {
        this.training_mode_cd = training_mode_cd;
    }

    public String getKey_id() {
        return key_id;
    }

    public void setKey_id(String key_id) {
        this.key_id = key_id;
    }
}
