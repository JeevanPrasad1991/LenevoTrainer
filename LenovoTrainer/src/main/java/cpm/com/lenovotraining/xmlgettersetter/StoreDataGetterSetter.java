package cpm.com.lenovotraining.xmlgettersetter;

import java.io.Serializable;

/**
 * Created by yadavendras on 27-06-2016.
 */
public class StoreDataGetterSetter implements Serializable{

    String store_name="", shop_no="", market_name="", locality, city="", telephone1="", telephone2="", email="", owner_name="", owner_contactno="", store_type_cd="", store_type="";

    String key_id, store_img, visit_date, uploaod_status;

    double lat = 0.0, lon = 0.0;

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public String getShop_no() {
        return shop_no;
    }

    public void setShop_no(String shop_no) {
        this.shop_no = shop_no;
    }

    public String getMarket_name() {
        return market_name;
    }

    public void setMarket_name(String market_name) {
        this.market_name = market_name;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTelephone1() {
        return telephone1;
    }

    public void setTelephone1(String telephone1) {
        this.telephone1 = telephone1;
    }

    public String getTelephone2() {
        return telephone2;
    }

    public void setTelephone2(String telephone2) {
        this.telephone2 = telephone2;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    public String getOwner_contactno() {
        return owner_contactno;
    }

    public void setOwner_contactno(String owner_contactno) {
        this.owner_contactno = owner_contactno;
    }

    public String getStore_type_cd() {
        return store_type_cd;
    }

    public void setStore_type_cd(String store_type_cd) {
        this.store_type_cd = store_type_cd;
    }

    public String getStore_type() {
        return store_type;
    }

    public void setStore_type(String store_type) {
        this.store_type = store_type;
    }

    public String getKey_id() {
        return key_id;
    }

    public void setKey_id(String key_id) {
        this.key_id = key_id;
    }

    public String getStore_img() {
        return store_img;
    }

    public void setStore_img(String store_img) {
        this.store_img = store_img;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getVisit_date() {
        return visit_date;
    }

    public void setVisit_date(String visit_date) {
        this.visit_date = visit_date;
    }

    public String getUploaod_status() {
        return uploaod_status;
    }

    public void setUploaod_status(String uploaod_status) {
        this.uploaod_status = uploaod_status;
    }
}
