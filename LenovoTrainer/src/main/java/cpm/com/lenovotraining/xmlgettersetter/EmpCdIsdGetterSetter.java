package cpm.com.lenovotraining.xmlgettersetter;

import java.io.Serializable;

/**
 * Created by yadavendras on 23-08-2016.
 */
public class EmpCdIsdGetterSetter implements Serializable{

    String emp_cd, isd, isd_cd;

    public String getEmp_cd() {
        return emp_cd;
    }

    public void setEmp_cd(String emp_cd) {
        this.emp_cd = emp_cd;
    }

    public String getIsd() {
        return isd;
    }

    public void setIsd(String isd) {
        this.isd = isd;
    }

    public String getIsd_cd() {
        return isd_cd;
    }

    public void setIsd_cd(String isd_cd) {
        this.isd_cd = isd_cd;
    }
}
