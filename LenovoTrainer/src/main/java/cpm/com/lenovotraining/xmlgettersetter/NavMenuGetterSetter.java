package cpm.com.lenovotraining.xmlgettersetter;

/**
 * Created by yadavendras on 11-08-2016.
 */
public class NavMenuGetterSetter {

    String iconName = "";
    int iconImg = -1; // menu icon resource id

    public String getIconName() {
        return iconName;
    }
    public void setIconName(String iconName) {
        this.iconName = iconName;
    }
    public int getIconImg() {
        return iconImg;
    }
    public void setIconImg(int iconImg) {
        this.iconImg = iconImg;
    }
}
