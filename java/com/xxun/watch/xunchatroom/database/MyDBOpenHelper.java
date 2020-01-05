package com.xxun.watch.xunchatroom.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;



import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class MyDBOpenHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "imibaby.db";
    private static final int DB_VERSION = 2;
    //æ•°æ®åº“è¡¨å
    public static String CONFIG_TABLE_NAME = "configs"; //ç”¨æˆ·é…ç½®ä¿¡æ¯
    public static String OFFLINE_MAP_CITY = "offlinemapcity";//ä»¥ä¸‹è½½ç¦»çº¿åœ°å›¾
    public static String NOTICE_HIS_TABLE_NAME = "notice_his";
    public static String WATCH_TABLE_NAME = "watchs";
    public static String USERS_TABLE_NAME = "users";//
    public static String SUGGEST_TABLE_NAME = "suggests";
    public static String LOCATION_TABLE_NAME = "location_his";
    public static String TRACE_TABLE_NAME = "trace";
    public static String DATEPOINT_TABLE_NAME = "datepoint";
//    public static String CHAT_HIS_TABLE_NAME = "chat_his";

    //æ•°æ®åº“fieldå
    public static final String FIELD_ID = "FIELD_ID"; //id
    public static final String FIELD_INFO = "FIELD_INFO"; //é…ç½®ä¿¡æ¯

    public static final String FIELD_FAMILY_ID = "family_id";
    public static final String FIELD_USER_ID = "uid";
    public static final String FIELD_EID_ID = "eid";
    public static final String FIELD_WATCH_ID = "watch_id";
    public static final String FIELD_NICK_NAME = "nickname";
    public static final String FIELD_WATCH_NAME = "watchname";
    public static final String FIELD_RELATION = "relation";
    public static final String FIELD_BIRTHDAY = "birthday";
    public static final String FIELD_SEX = "sex";
    public static final String FIELD_HEIGHT = "height";
    public static final String FIELD_WEIGHT = "weight";
    public static final String FIELD_ORIGNAL_VER ="orignal_ver";
    public static final String FIELD_CUR_VER = "cur_ver";
    public static final String FIELD_BT_MAC = "btmac";

    public static final String FIELD_SERVICE_TIMEOUT = "service_timeout";
    public static final String FIELD_LIGHT ="light";
    public static final String FIELD_VOLUME = "volume";
    public static final String FIELD_SILENT = "silent";
    public static final String FIELD_SILENT_TIMER = "silent_timer";
    public static final String FIELD_HAND = "hand";
    public static final String FIELD_QR = "qrcode";
    public static final String FIELD_POWER = "power";
    public static final String FIELD_POWER_LASTTIME ="power_lasttime";
    public static final String FIELD_LOCATION_LONGITUDE = "longitude";
    public static final String FIELD_LOCATION_LATITUEDE ="latitude";
    public static final String FIELD_DESCRIPTION ="description";
    public static final String FIELD_ACCURACY= "accuracy";
    public static final String FIELD_POI = "poi";
    public static final String FIELD_CITY = "city";
    public static final String FIELD_TYPE = "type";
    public static final String FIELD_LOCATION_TIME ="location_lasttime";
    public static final String FIELD_EXIPIRE_TIME ="expiretime";
    public static final String FIELD_OFFLINE_CITY ="offline_city";
    public static final String FIELD_OFFLINE_CITY_DOWN_FLAG ="city_down_flag";
    public static final String FIELD_OFFLINE_CITY_COMPLETE_CODE ="city_complete_code";
//    public static final String FIELD_CHAT_SRCID = "chat_srcid";          //å‘é€è€…id
//    public static final String FIELD_CHAT_DSTID = "chat_dstid";          //æŽ¥æ”¶è€…id
//	public static final String FIELD_CHAT_AUDIOPATH = "chat_audio_path";      //è¯­éŸ³è·¯å¾„
//	public static final String FIELD_CHAT_DATE = "chat_date";           //è¯­éŸ³å½•åˆ¶æ—¶é—´
//	public static final String FIELD_CHAT_DURATION = "chat_duration";          //è¯­éŸ³æ—¶é•¿ï¼Œå•ä½ä¸º'ç§’'
//	public static final String FIELD_CHAT_PLAYED = "chat_played";      //æ˜¯å¦æ’­æ”¾æ ‡å¿—
//	public static final String FIELD_CHAT_IS_FROM = "chat_is_from";       //æ”¶åˆ°æˆ–è€…å‘å‡ºæ ‡å¿—ï¼Œtrueè¡¨ç¤ºå†…å®¹æ˜¯æŽ¥æ”¶çš„ï¼Œfalseè¡¨ç¤ºå†…å®¹æ˜¯å‘é€çš„

    public static final String FIELD_MAC = "mac";
    public static final String FIELD_SSID = "ssid";
    public static final String FIELD_HEAD_ID = "head_id";
    public static final String FIELD_HEAD_PATH = "head_path";
    public static final String FIELD_LOCAL_PATH = "local_path";
    public static final String FIELD_FILE_NAME = "file_name";
    public static final String FIELD_SIZE = "size";
    public static final String FIELD_TIME = "time";
    public static final String FIELD_ICCID = "iccid";
    public static final String FIELD_SIM_NO = "sim_no";
    public static final String FIELD_CELLPHONE = "cellphone";
    public static final String FIELD_XIAOMID = "xiaomiid";
    public static final String FIELD_IMEI = "imei";
    public static final String FIELD_ADMIN_USER = "admin_user";



    //åŽ†å²æ¶ˆæ¯ç›¸å…³
    public static final String FIELD_NOTICE_TYPE ="notice_type" ; //
    public static final String FIELD_NOTICE_STATUS ="notice_status" ;    	  //
    public static final String FIELD_DATE_TIME ="notice_date_time" ; //æ—¥æœŸæ ‡è¯†

    //åŽ†å²è½¨è¿¹ç›¸å…³
    public static final String FIELD_DATE ="date" ; //æ—¥æœŸ
    public static final String FIELD_RECORD ="record" ;
    public static final String FIELD_DATE_NUM ="num" ;

    //ç”¨æˆ·è¡Œä¸ºç»Ÿè®¡
    public static final String FIELD_SUGGEST = "suggest";//åé¦ˆå»ºè®®

    public static final String FIELD_STATE = "state";//å®‰è£…çŠ¶æ€

    public static final String FIELD_SIM_ACTIVIE_STATE = "active_status";
    public static final String FIELD_SIM_CERTI_STATE = "certi_status";

    //æ–°å¢ždevictypeå­—æ®µä¿å­˜
    public static final String FIELD_DEVICE_TYPE = "deviceType";

    public MyDBOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e("chat db","create db");

        db.execSQL("CREATE TABLE "+NOTICE_HIS_TABLE_NAME+" (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                FIELD_WATCH_ID+" TEXT," +
                FIELD_NOTICE_TYPE+" TEXT," +
                FIELD_DATE_TIME+" TEXT" +
                ");");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion<1){
            db.execSQL("DROP TABLE IF EXISTS "+NOTICE_HIS_TABLE_NAME+";");
            onCreate(db);
        }

    }
}
