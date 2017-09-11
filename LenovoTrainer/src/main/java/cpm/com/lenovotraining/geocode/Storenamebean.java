package cpm.com.lenovotraining.geocode;

/**
 * Created by ashishc on 10-06-2016.
 */
public class Storenamebean {
    public String storename ;
    public String storeid;
    public String latitude;
    public String longitude;
    public String status;
    public String address;

    public String getStoreid() {
        return storeid;
    }

    public void setStoreid(String storeid) {
        this.storeid = storeid;
    }

    public String getStorename() {
        return storename;
    }

    public void setStorename(String storename) {
        this.storename = storename;
    }
    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getStoreAddress() {
        return address;
    }

    public void setStoreAddress(String address) {
        this.address = address;
    }

}