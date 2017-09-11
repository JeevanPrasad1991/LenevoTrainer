package cpm.com.lenovotraining.xmlgettersetter;

import java.util.ArrayList;

/**
 * Created by yadavendras on 22-06-2016.
 */
public class PosmGetterSetter {

    ArrayList<String> posm = new ArrayList<>();
    ArrayList<String> posm_cd = new ArrayList<>();

    String quantity, posm_img_str;

    int common_id;

    String posm_master_table;

    public ArrayList<String> getPosm() {
        return posm;
    }

    public void setPosm(String posm) {
        this.posm.add(posm);
    }

    public ArrayList<String> getPosm_cd() {
        return posm_cd;
    }

    public void setPosm_cd(String posm_cd) {
        this.posm_cd.add(posm_cd);
    }

    public String getPosm_master_table() {
        return posm_master_table;
    }

    public void setPosm_master_table(String posm_master_table) {
        this.posm_master_table = posm_master_table;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPosm_img_str() {
        return posm_img_str;
    }

    public void setPosm_img_str(String posm_img_str) {
        this.posm_img_str = posm_img_str;
    }

    public int getCommon_id() {
        return common_id;
    }

    public void setCommon_id(int common_id) {
        this.common_id = common_id;
    }
}
