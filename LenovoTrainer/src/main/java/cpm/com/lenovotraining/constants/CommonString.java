package cpm.com.lenovotraining.constants;

import android.os.Environment;

/**
 * Created by yadavendras on 17-06-2016.
 */
public class CommonString {
    public static final String KEY_ISD_IMAGE = "isd_image";
    public static final String DATABASE_NAME = "Lenovo_Databases";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_REMEMBER = "remember";
    public static final String KEY_GEO_TAG = "GEOTAG";
    public static final String KEY_LAT = "LATTITUDE";
    public static final String KEY_LONGT = "LONGITUDE";
    public static final String KEY_NEW_EMPLOYEE = "NEW_EMPLOYEE";
    public static final String KEY_NEW_ISD = "NEW_ISD";
    public static final String KEY_TRAINNING_TOPIC = "TRAINNING_TOPIC";
    public static final int CAPTURE_MEDIA = 131;
    public static final String KEY_AUDIT_DATA = "AUDIT_DATA";
    public static final String KEY_N = "N";

    public static final String KEY_IS_QUIZ_DONE = "is_quiz_done";

    public static final String KEY_TRAINING_MODE_CD = "TRAINING_MODE_CD";
    public static final String KEY_TRAINING_MODE = "TRAINING_MODE";
    public static final String KEY_MANAGED = "MANAGED";

    public static final String KEY_TOPIC_CD = "TOPIC_CD";
    public static final String KEY_TOPIC = "TOPIC";

    public static final String KEY_MID = "MID";

    public static final String KEY_ISD_CD = "ISD_CD";

    public static final String KEY_ISD_NAME = "ISD_NAME";

    public static final String KEY_NAME = "NAME";

    public static final String KEY_PHONE_NO = "PHONE_NO";

    public static final String KEY_IS_ISD = "IS_ISD";

    //Url etc
    public static final String URL = "http://lenovo.parinaam.in/LenovoService.asmx";
    public static final String URL_Notice_Board = "http://visa.parinaam.in/notice/notice.html";
    public static final String NAMESPACE = "http://tempuri.org/";

    public static final String METHOD_NAME_UNIVERSAL_DOWNLOAD = "Download_Universal";
    public static final String SOAP_ACTION_UNIVERSAL = "http://tempuri.org/"
            + METHOD_NAME_UNIVERSAL_DOWNLOAD;

    public static final String METHOD_UPLOAD_XML = "DrUploadXml";
    public static final String SOAP_ACTION = "http://tempuri.org/";

    public static final String METHOD_UPLOAD_DR_STORE_COVERAGE = "UPLOAD_COVERAGE_TrainerNew";
    public static final String MEHTOD_UPLOAD_COVERAGE_STATUS = "UploadCoverage_Status_Trainer";

    public static final String METHOD_UPLOAD_REASON_ATTENDANCE = "Attendance_NEW";

    public static final String METHOD_UPLOAD_IMAGE = "GetImageWithFolderName";

    public static final String SOAP_ACTION_UPLOAD_IMAGE = "http://tempuri.org/" + METHOD_UPLOAD_IMAGE;

    public static final String FILE_PATH = Environment.getExternalStorageDirectory() + "/Lenovo_Trainer_Images/";

    public static final String UERNAME_OR_PASSWORD_IS_WRONG = "User id or password not matched with current user";
    public static final String NO_INTERNET_CONNECTION = "No internet connection found";

    public static final String ONBACK_ALERT_MESSAGE = "Unsaved data will be lost - Do you want to continue?";

    public static final String DATA_DELETE_ALERT_MESSAGE = "Saved data will be lost - Do you want to continue?";

    public static final String METHOD_LOGIN = "UserLoginDetail_Trainer";
    public static final String SOAP_ACTION_LOGIN = "http://tempuri.org/"
            + METHOD_LOGIN;

    public static final String KEY_SUCCESS = "Success";
    public static final String KEY_FAILURE = "Failure";
    public static final String KEY_FALSE = "False";
    public static final String KEY_CHANGED = "Changed";

    public static final String KEY_NO_DATA = "NoData";

    public static final String KEY_PATH = "path";
    public static final String KEY_VERSION = "version";
    public static final String KEY_USER_TYPE = "RIGHTNAME";

    public static final String KEY_DATE = "date";

    public static final String KEY_ = "date";

    //----- Alert Messages
    public static final String MESSAGE_DELETE = "Do You Want To deletecross This Record";
    public static final String MESSAGE_SAVE = "Do You Want To Save The Data ";
    public static final String MESSAGE_FAILURE = "Server Eroor.Please Access After Some Time";
    public static final String MESSAGE_JCP_FALSE = "Data is not found in ";
    public static final String MESSAGE_INVALID_DATA = "Enter Data";
    public static final String MESSAGE_DUPLICATE_DATA = "Data Already Exist";
    public static final String MESSAGE_DOWNLOAD = "Data Downloaded Successfully";
    public static final String MESSAGE_UPLOAD_DATA = "Data Uploaded Successfully";
    public static final String MESSAGE_UPLOAD_IMAGE = "Images Uploaded Successfully";
    public static final String MESSAGE_FALSE = "Invalid User";
    public static final String MESSAGE_CHANGED = "Invalid UserId Or Password / Password Has Been Changed.";
    public static final String MESSAGE_EXIT = "Do You Want To Exit";
    public static final String MESSAGE_BACK = "Use Back Button";
    public static final String MESSAGE_EXCEPTION = "Network Communication Failure. Check Your Network Connection";
    public static final String MESSAGE_SOCKETEXCEPTION = "Network Communication Failure. Check Your Network Connection";
    public static final String MESSAGE_NO_DATA = "No Data For Upload";
    public static final String MESSAGE_NO_IMAGE = "No Image For Upload";
    public static final String MESSAGE_DATA_FIRST = "Upload Data First";
    public static final String MESSAGE_IMAGE_UPLOAD = "Upload Images";
    public static final String MESSAGE_DATA_ALREADY_UPLOADED = "Data Already Uploaded";
    public static final String MESSAGE_SOTORE_ALREADY_CLOSED = "Store Already Closed";
    public static final String MESSAGE_STORE_ALREADY_CHECKED_OUT = "Store Already Checked out";

    public static final String MESSAGE_FIRST_CHECKOUT = "First checkout from previous store";

    public static final String MESSAGE_CHECKOUT = "Checkedout Successfully";

    public static final String MESSAGE_ANSWER_ALL_QUESTION = "Answer all the questions";
    public static final String MESSAGE_SELECT_TRAINING_TOPIC = "Select training topic";

    public static final String MESSAGE_FIRST_ENTER_QUANTITY = "First enter quantity";

    public static final String MESSAGE_DOWNLOAD_DATA_FIRST = "Download data first";
    public static final String MESSAGE_FILL_ALL_DATA = "Fill all the data ";
    public static final String MESSAGE_SELECT_STORE_TYPE = "Select store type";
    public static final String MESSAGE_CLICK_STORE_IMAGE = "Click store image";
    public static final String MESSAGE_ENTER_VALID_EMAIL = "Enter valid email address";
    public static final String MESSAGE_STORE_ALREADY_ADDED = "This store is already added";

    public static final String MESSAGE_CLICK_IMAGE_FOR_QUANTITY = "Plese click image for a filled quantity";
    public static final String MESSAGE_FILL_ALL_DATA_OR_ZERO = "Please fill 0 if Posm not available";

    public static final String MESSAGE_UNSAVED_DATA_LOST = "Unsaved data will be lost - want to continue";

    //Table Store Data
    public static final String TABLE_STORE_DATA = "STORE_DATA";

    public static final String TABLE_ANSWERED_DATA = "ANSWERED_DATA";
    ////table audit data
    public static final String TABLE_CHECKLIST_INSERTED_DATA = "CHECKLIST_INSERTED_DATA";
    public static final String TABLE_INSERT_OPENINGHEADER_DATA = "TABLE_INSERT_OPENINGHEADER_DATA";

    public static final String TABLE_NEW_ISD = "NEW_ISD";

    public static final String TABLE_ADD_NEW_EMPLOYEE = "ADD_NEW_EMPLOYEE";


    public static final String TABLE_COVERAGE_DATA = "COVERAGE_DATA";

    public static final String KEY_ID = "KEY_ID";
    public static final String KEY_CITY = "CITY";
    public static final String KEY_EMAIL = "EMAIL";
    public static final String KEY_LOCALITY = "LOCALITY";
    public static final String KEY_MARKET_NAME = "MARKET";
    public static final String KEY_OWNER_CONTACT_NO = "OWNER_CONTACT_NO";
    public static final String KEY_OWNER_NAME = "OWNER_NAME";
    public static final String KEY_SHOP_NO = "SHOP_NO";
    public static final String KEY_STORE_IMAGE = "STORE_IMAGE";
    public static final String KEY_STORE_NAME = "STORE_NAME";
    public static final String KEY_STORE_TYPE = "STORE_TYPE";
    public static final String KEY_STORE_TYPE_CD = "STORE_TYPE_CD";
    public static final String KEY_TELEPHONE_NO_ONE = "TELEPHONE_NO";
    public static final String KEY_TELEPHONE_NO_TWO = "TELEPHONE_NO_TWO";
    public static final String KEY_UPLOAD_STATUS = "UPLOAD_STATUS";
    public static final String KEY_CHECKOUT_STATUS = "CHECKOUT_STATUS";
    public static final String KEY_IN_TIME = "IN_TIME";
    public static final String KEY_OUT_TIME = "OUT_TIME";
    public static final String KEY_VISIT_DATE = "VISIT_DATE";
    public static final String KEY_LATITUDE = "LATITUDE";
    public static final String KEY_LONGITUDE = "LONGITUDE";
    public static final String KEY_COVERAGE_STATUS = "Coverage";
    public static final String KEY_REASON = "REASON";
    public static final String KEY_REASON_ID = "REASON_ID";
    public static final String KEY_USER_ID = "USER_ID";
    public static final String KEY_IMAGE = "IMAGE";
    public static final String KEY_COVERAGE_REMARK = "COVERAGE_REMARK";
    public static final String MID = "MID";
    public static final String KEY_P = "P";
    public static final String KEY_D = "D";
    public static final String KEY_U = "U";
    public static final String KEY_C = "Y";
    public static final String KEY_CHECK_IN = "I";
    public static final String KEY_L = "Leave";
    public static final String KEY_VALID = "Valid";
    public static final String STORE_STATUS_LEAVE = "L";
    public static final String KEY_INVALID = "INVALID";
    public static final String KEY_QUESTION_CD = "QUESTION_CD";
    public static final String KEY_ANSWER_CD = "ANSWER_CD";
    public static final String KEY_ANSWER = "ANSWER";

    public static final String KEY_STORE_CD = "STORE_CD";
    public static final String KEY_CHECKLIST_CATEGORY_CD = "CHECKLIST_CATEGORY_CD";


    public static final String CREATE_TABLE_COVERAGE_DATA = "CREATE TABLE  IF NOT EXISTS "
            + TABLE_COVERAGE_DATA + " ("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + KEY_STORE_CD + " INTEGER,"
            + KEY_USER_ID + " VARCHAR,"
            + KEY_IN_TIME + " VARCHAR,"
            + KEY_OUT_TIME + " VARCHAR,"
            + KEY_VISIT_DATE + " VARCHAR,"
            + KEY_LATITUDE + " VARCHAR,"
            + KEY_LONGITUDE + " VARCHAR,"
            + KEY_COVERAGE_STATUS + " VARCHAR,"
            + KEY_IMAGE + " VARCHAR,"
            + KEY_REASON_ID + " VARCHAR,"
            + KEY_COVERAGE_REMARK + " VARCHAR,"
            + KEY_TRAINING_MODE_CD + " INTEGER,"
            + KEY_MANAGED + " INTEGER,"
            + KEY_REASON + " VARCHAR)";


    public static final String CREATE_TABLE_ANSWERED_DATA = "CREATE TABLE "
            + TABLE_ANSWERED_DATA
            + " ("
            + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + KEY_STORE_CD + " INTEGER,"
            + KEY_TOPIC_CD + " INTEGER,"
            + KEY_QUESTION_CD + " INTEGER,"
            + KEY_ANSWER + " VARCHAR,"
            + KEY_ANSWER_CD + " INTEGER,"
            + KEY_TRAINING_MODE_CD + " INTEGER,"
            + KEY_MID + " INTEGER,"
            + KEY_ISD_IMAGE + " VARCHAR,"
            + KEY_ISD_CD + " INTEGER)";

    public static final String KEY_CHECKLIST_CD = "CHECKLIST_CD";
    public static final String KEY_CHECKLIST = "CHECKLIST";
    public static final String KEY_AVAILABILITY = "AVAILABILITY";


    public static final String CREATE_TABLE_NEW_ISD_DATA = "CREATE TABLE "
            + TABLE_NEW_ISD
            + " ("
            + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + KEY_STORE_CD + " INTEGER,"
            + KEY_ISD_CD + " INTEGER,"
            + KEY_ISD_NAME + " VARCHAR )";


    public static final String CREATE_TABLE_CHECKLIST_HEADER_DATA = "CREATE TABLE "
            + TABLE_INSERT_OPENINGHEADER_DATA
            + " ("
            + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + KEY_STORE_CD + " INTEGER,"
            + KEY_ISD_CD + " INTEGER,"
            + "CHECKLIST_CATEGORY_CD" + " INTEGER,"
            + "CHECKLIST_CATEGORY" + " VARCHAR,"
            + KEY_MID + " INTEGER)";


    public static final String CREATE_TABLE_CHECKLIST_INSERTED_DATA = "CREATE TABLE "
            + TABLE_CHECKLIST_INSERTED_DATA
            + " ("
            + KEY_STORE_CD + " INTEGER,"
            + KEY_ISD_CD + " INTEGER,"
            + KEY_CHECKLIST_CD + " INTEGER,"
            + KEY_CHECKLIST + " VARCHAR,"
            + KEY_MID + " INTEGER,"
            + "Common_Id" + " INTEGER,"
            + "CHECKLIST_CATEGORY_CD" + " INTEGER,"
            + "CHECKLIST_CATEGORY" + " VARCHAR,"
            + KEY_AVAILABILITY + " INTEGER)";



    public static final String CREATE_TABLE_ADD_NEW_EMPLOYEE_DATA = "CREATE TABLE "
            + TABLE_ADD_NEW_EMPLOYEE
            + " ("
            + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + KEY_STORE_CD + " INTEGER,"
            + KEY_NAME + " VARCHAR,"
            + KEY_EMAIL + " VARCHAR,"
            + KEY_IS_ISD + " VARCHAR,"
            + KEY_IMAGE + " VARCHAR,"
            + KEY_MANAGED + " INTEGER,"
            + KEY_PHONE_NO + " INTEGER)";


    //Table POSM DATA
    public static final String TABLE_POSM_DATA = "POSM_DATA";

    public static final String KEY_COMMON_ID = "COMMON_ID";
    public static final String KEY_POSM = "POSM";
    public static final String KEY_POSM_CD = "POSM_CD";
    public static final String KEY_QUANTITY = "QUANTITY";
    public static final String KEY_POSM_IMAGE = "POSM_IMAGE";
    public static final String CALL_HELPDESK = "tel:01149894989";
    public static final String TABLE_STORE_GEOTAGGING = "STORE_GEOTAGGING";
    public static final String CREATE_TABLE_STORE_GEOTAGGING = "CREATE TABLE IF NOT EXISTS "
            + TABLE_STORE_GEOTAGGING
            + " ("
            + "KEY_ID"
            + " INTEGER PRIMARY KEY AUTOINCREMENT ,"

            + "STORE_CD"
            + " INTEGER,"

            + "LATITUDE"
            + " VARCHAR,"

            + "LONGITUDE"
            + " VARCHAR,"
            + "GEO_TAG"
            + " VARCHAR,"
            + "FRONT_IMAGE" + " VARCHAR)";

    public static final String TABLE_SALETEAM_TRAINEE_DATA = "SALETEAM_TRAINEE_DATA";
    public static final String CREATE_TABLE_SALETEAM_TRAINEE_DATA = "CREATE TABLE IF NOT EXISTS "
            + TABLE_SALETEAM_TRAINEE_DATA
            + " ("
            + "KEY_ID"
            + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + "VISIT_DATE"
            + " VARCHAR,"
            + "TOPIC_CD"
            + " INTEGER,"
            + "USER_ID"
            + " VARCHAR,"
            + "TOPIC"
            + " VARCHAR,"
            + "STATUS"
            + " VARCHAR,"
            + "TRAINEE_CD"
            + " INTEGER,"
            + "TRAINEE_NAME" + " VARCHAR)";


    public static final String TABLE_TRAINING_TOPIC_DATA = "TABLE_TRAINING_TOPIC_DATA";
    public static final String CREATE_TABLE_TRAINING_TOPC_DATA = "CREATE TABLE IF NOT EXISTS "
            + TABLE_TRAINING_TOPIC_DATA
            + " ("
            + "VISIT_DATE"
            + " VARCHAR,"
            + "TOPIC_CD"
            + " INTEGER,"
            + "TOPIC"
            + " VARCHAR,"
            + "ISD_CD"
            + " INTEGER,"
            + "STORE_CD"
            + " INTEGER,"
            + "MID" + " INTEGER)";


}
