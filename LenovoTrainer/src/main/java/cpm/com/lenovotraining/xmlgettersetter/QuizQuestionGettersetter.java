package cpm.com.lenovotraining.xmlgettersetter;

import java.util.ArrayList;

/**
 * Created by yadavendras on 11-08-2016.
 */
public class QuizQuestionGettersetter {

    ArrayList<String> TOPIC_CD = new ArrayList<>();
    ArrayList<String> QUESTION_CD = new ArrayList<>();
    ArrayList<String> QUESTION = new ArrayList<>();
    ArrayList<String> ANSWER_CD = new ArrayList<>();
    ArrayList<String> ANSWER = new ArrayList<>();
    ArrayList<String> RIGHT_ANSWER = new ArrayList<>();

    String table_quiz_question;

    public ArrayList<String> getTOPIC_CD() {
        return TOPIC_CD;
    }

    public void setTOPIC_CD(String TOPIC_CD) {
        this.TOPIC_CD.add(TOPIC_CD);
    }

    public ArrayList<String> getQUESTION_CD() {
        return QUESTION_CD;
    }

    public void setQUESTION_CD(String QUESTION_CD) {
        this.QUESTION_CD.add(QUESTION_CD);
    }

    public ArrayList<String> getQUESTION() {
        return QUESTION;
    }

    public void setQUESTION(String QUESTION) {
        this.QUESTION.add(QUESTION);
    }

    public ArrayList<String> getANSWER_CD() {
        return ANSWER_CD;
    }

    public void setANSWER_CD(String ANSWER_CD) {
        this.ANSWER_CD.add(ANSWER_CD);
    }

    public ArrayList<String> getANSWER() {
        return ANSWER;
    }

    public void setANSWER(String ANSWER) {
        this.ANSWER.add(ANSWER);
    }

    public ArrayList<String> getRIGHT_ANSWER() {
        return RIGHT_ANSWER;
    }

    public void setRIGHT_ANSWER(String RIGHT_ANSWER) {
        this.RIGHT_ANSWER.add(RIGHT_ANSWER);
    }

    public String getTable_quiz_question() {
        return table_quiz_question;
    }

    public void setTable_quiz_question(String table_quiz_question) {
        this.table_quiz_question = table_quiz_question;
    }
}
