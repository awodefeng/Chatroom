package com.xxun.watch.xunchatroom.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import com.xxun.watch.xunchatroom.info.ChatListItemInfo;
import com.xxun.watch.xunchatroom.info.ChatNoticeInfo;
import com.xxun.watch.xunchatroom.util.ChatUtil;

import java.util.ArrayList;


/**
 * Description Of The Class<br>
 *
 * @author huangqilin
 * @version 1.000, 2015-1-22
 */
public class ChatListDB extends BaseDB {

    public static String CHAT_HIS_TABLE_NAME = "chat_his";
    public static final String FIELD_CHAT_SRCID = "chat_srcid";          //å‘é€è€…id
    public static final String FIELD_CHAT_DSTID = "chat_dstid";          //æŽ¥æ”¶è€…id
    public static final String FIELD_CHAT_AUDIOPATH = "chat_audio_path";      //è¯­éŸ³è·¯å¾„
    public static final String FIELD_CHAT_DATE = "chat_date";           //è¯­éŸ³å½•åˆ¶æ—¶é—´
    public static final String FIELD_CHAT_DURATION = "chat_duration";          //è¯­éŸ³æ—¶é•¿ï¼Œå•ä½ä¸º'ç§’'
    public static final String FIELD_CHAT_PLAYED = "chat_played";      //æ˜¯å¦æ’­æ”¾æ ‡å¿—
    public static final String FIELD_CHAT_IS_LOCAL = "chat_is_local";       //æ”¶åˆ°æˆ–è€…å‘å‡ºæ ‡å¿—ï¼Œtrueè¡¨ç¤ºå†…å®¹æ˜¯æŽ¥æ”¶çš„ï¼Œfalseè¡¨ç¤ºå†…å®¹æ˜¯å‘é€çš„
    public static final String FIELD_CHAT_TYPE = "chat_type";
    public static final String FIELD_CHAT_SEND_STATE = "chat_send_state"; //è¯­éŸ³å‘é€çŠ¶æ€
    public static final String FIELD_CHAT_RECORD_STATE = "chat_record_state"; //è¿œç¨‹å½•éŸ³çŠ¶æ€
    public static final String FIELD_CHAT_PHOTO_ID = "chat_photo_id";
    //    public static final String FIELD_CHAT_MESSAGE_CONTENT = "chat_message_content"; //éžè¯­éŸ³æ¶ˆæ¯å†…å®¹
//    public static final String FIELD_CHAT_SERVICE_KEY = "chat_service_key"; //è¯­éŸ³çš„keyå€¼
    private static ChatListDB instance = null;
    String LOG_TAG="chat db";
    public static ChatListDB getInstance(Context context) {
        if (instance == null)
            instance = new ChatListDB(context);
        return instance;
    }

    public ChatListDB(Context mContext) {
        super(mContext);
        // TODO Auto-generated constructor stub
    }

    public void creatTable(ArrayList<String> newfamilyid) {
        SQLiteDatabase db = this.openWritableDb();
        if (newfamilyid != null) {
            for (String familyid : newfamilyid) {
                StringBuilder table = new StringBuilder(CHAT_HIS_TABLE_NAME);
                table.append(familyid);
//                CREATE TABLE IF NOT EXISTS
                try {
                    db.execSQL("CREATE TABLE " + table.toString() + " (" +
                            "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            FIELD_CHAT_SRCID + " TEXT," +
                            FIELD_CHAT_DSTID + " TEXT," +
                            MyDBOpenHelper.FIELD_FAMILY_ID + " TEXT," +
                            MyDBOpenHelper.FIELD_WATCH_ID + " TEXT," +
                            FIELD_CHAT_AUDIOPATH + " TEXT," +
                            FIELD_CHAT_DATE + " TEXT," +
                            FIELD_CHAT_SEND_STATE + " INTEGER," +
                            FIELD_CHAT_RECORD_STATE + " INTEGER," +
                            FIELD_CHAT_DURATION + " INTEGER," +//
                            FIELD_CHAT_IS_LOCAL + " INTEGER," + //
                            FIELD_CHAT_PLAYED + " INTEGER," +
                            FIELD_CHAT_TYPE + " INTEGER" +
                            ");");
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        db.close();
    }

    public void deleteTable(ArrayList<String> oldfamilyid) {
        SQLiteDatabase db = this.openWritableDb();
        if (oldfamilyid != null) {
            for (String familyid : oldfamilyid) {
                StringBuilder table = new StringBuilder(CHAT_HIS_TABLE_NAME);
                table.append(familyid);
                db.execSQL("DROP TABLE " + table.toString());
            }
        }
        db.close();
    }

    /**
     * æ–°å¢žChatMsg
     *
     * @param chat the chat content need to add
     * @return row ID
     */
    public long addChatMsg(String familyid, ChatListItemInfo chat) {
//        Cursor myCursor = null;
        long id = -1;
        SQLiteDatabase db = this.openWritableDb();
        Log.i("LOG_TAG","addChatMsg");
        if (db == null) {
            Log.i("LOG_TAG","db is null,return");
            return -1;
        }

        try {
            StringBuilder builder = new StringBuilder();
            builder.append("SELECT * FROM ");
            builder.append(CHAT_HIS_TABLE_NAME + familyid);
            builder.append(" WHERE ");
            builder.append(FIELD_CHAT_DATE);
            builder.append("='");
            builder.append(chat.getmDate());
            builder.append("'");
            Cursor cursor = db.rawQuery(builder.toString(), null);
            if (cursor != null && cursor.getCount() > 0) {
                int count = cursor.getCount();
                cursor.close();
                db.close();
                Log.i("LOG_TAG","query return");
                return count;
            }

            ContentValues rowData = new ContentValues();
            rowData.clear();

            rowData.put(MyDBOpenHelper.FIELD_WATCH_ID, chat.getListItemEID());
            rowData.put(MyDBOpenHelper.FIELD_FAMILY_ID, chat.getListItemGID());
            rowData.put(FIELD_CHAT_AUDIOPATH, chat.getFilePath());
            rowData.put(FIELD_CHAT_DATE, chat.getmDate());
           // rowData.put(FIELD_CHAT_DSTID, AESUtil.getInstance().encryptDataStr(chat.getmDstId()));
          //  rowData.put(FIELD_CHAT_SRCID, AESUtil.getInstance().encryptDataStr(chat.getmSrcId()));
            rowData.put(FIELD_CHAT_TYPE, chat.getContentType());
            rowData.put(FIELD_CHAT_DURATION, chat.getDuration());
            //rowData.put(FIELD_CHAT_SEND_STATE, chat.getmSended());
           // rowData.put(FIELD_CHAT_RECORD_STATE, chat.getmForceRecordOk());
            if (chat.getListItemType() == 1) {
                rowData.put(FIELD_CHAT_IS_LOCAL, 1);
            } else {
                rowData.put(FIELD_CHAT_IS_LOCAL, 0);
            }
            if (chat.getIsPlayed() == 1) {
                rowData.put(FIELD_CHAT_PLAYED, 1);
            } else {
                rowData.put(FIELD_CHAT_PLAYED, 0);
            }
	      rowData.put(FIELD_CHAT_SEND_STATE, chat.getSendState());
 	      rowData.put(FIELD_CHAT_RECORD_STATE, chat.getPhotoID());
	      rowData.put(FIELD_CHAT_SRCID, String.valueOf(chat.getListItemState()));
		if (chat.getListItemType() == 1) {
	      rowData.put(FIELD_CHAT_DSTID, String.valueOf(chat.getListItemStartMills()));
		}else{
		rowData.put(FIELD_CHAT_DSTID, chat.getContractItemAvatar());
		}
            id = db.insertOrThrow(CHAT_HIS_TABLE_NAME + familyid, null, rowData);
            if (-1 == id) {

            }
        } catch (Exception e) {
            // TODO: handle exception
	    
            StringBuilder table = new StringBuilder(CHAT_HIS_TABLE_NAME);
            table.append(familyid);
            try {
                db.execSQL("CREATE TABLE " + table.toString() + " (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        FIELD_CHAT_SRCID + " TEXT," +
                        FIELD_CHAT_DSTID + " TEXT," +
                        MyDBOpenHelper.FIELD_FAMILY_ID + " TEXT," +
                        MyDBOpenHelper.FIELD_WATCH_ID + " TEXT," +
                        FIELD_CHAT_AUDIOPATH + " TEXT," +
                        FIELD_CHAT_DATE + " TEXT," +
                        FIELD_CHAT_SEND_STATE + " INTEGER," +
                        FIELD_CHAT_RECORD_STATE + " INTEGER," +
                        FIELD_CHAT_DURATION + " INTEGER," +//
                        FIELD_CHAT_IS_LOCAL + " INTEGER," + //
                        FIELD_CHAT_PLAYED + " INTEGER," +
                        FIELD_CHAT_TYPE + " INTEGER" +
                        ");");
                addChatMsg(familyid, chat);
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        db.close();
//        closeCursor(myCursor);
        return id;
    }

    /**
     * åˆ é™¤ChatMsg
     *
     * @param
     * @return
     */
    public long delChatMsg(String familyid, ChatListItemInfo chat) {
//        Cursor myCursor = null;
        long id = -1;
        SQLiteDatabase db = this.openWritableDb();
        if (db == null)
            return -1;

        try {
            StringBuilder szBuff = new StringBuilder();
            //DELETE FROM mytable WHERE Name='Test1';
            szBuff.append("DELETE FROM ");
            szBuff.append(CHAT_HIS_TABLE_NAME + familyid);
            szBuff.append(" WHERE ");
            szBuff.append(FIELD_CHAT_DATE);
            szBuff.append("='");
            szBuff.append(chat.getmDate());
            szBuff.append("'");
            db.execSQL(szBuff.toString());
        } catch (Exception e) {
            // TODO: handle exception
            //LogUtil.e("chat  delChatMsg() Exp:" + e.getMessage());
        }
        db.close();
//        closeCursor(myCursor);
        return id;
    }

    /**
     * æ›´æ–°ChatMsg çŠ¶æ€
     *
     * @param
     * @return
     */
    public void updateChatMsg(String familyid, ChatListItemInfo chat, String findkey) {
//        Cursor myCursor = null;
        int id = -1;
        SQLiteDatabase db = this.openWritableDb();
        Log.i(LOG_TAG,"updateChatMsg="+findkey);
        if (db == null)
            //LogUtil.e("open db error! updateChatMsg()");
            Log.i(LOG_TAG,"open db error! updateChatMsg()");

        try {
            ContentValues rowData = new ContentValues();

            rowData.clear();

            StringBuilder szBuff = new StringBuilder();

//            szBuff.append("SELECT * FROM ");
//            szBuff.append(CHAT_HIS_TABLE_NAME+familyid);
//            szBuff.append(" WHERE ");
            szBuff.append(FIELD_CHAT_DATE);
            szBuff.append("='");
            szBuff.append(findkey);
            szBuff.append("'");

//            myCursor = db.rawQuery(szBuff.toString(), null);

            rowData.put(MyDBOpenHelper.FIELD_WATCH_ID, chat.getListItemEID());
            rowData.put(MyDBOpenHelper.FIELD_FAMILY_ID, chat.getListItemGID());
            rowData.put(FIELD_CHAT_AUDIOPATH, chat.getFilePath());
            rowData.put(FIELD_CHAT_DATE, chat.getmDate());
            //rowData.put(FIELD_CHAT_DSTID, AESUtil.getInstance().encryptDataStr(chat.getmDstId()));
           // rowData.put(FIELD_CHAT_SRCID, AESUtil.getInstance().encryptDataStr(chat.getmSrcId()));
            rowData.put(FIELD_CHAT_DURATION, chat.getDuration());
            rowData.put(FIELD_CHAT_TYPE, chat.getContentType());
            //rowData.put(FIELD_CHAT_SEND_STATE, chat.getmSended());
           // rowData.put(FIELD_CHAT_RECORD_STATE, chat.getmForceRecordOk());
            if (chat.getListItemType() == 1) {
                rowData.put(FIELD_CHAT_IS_LOCAL, 1);
            } else {
                rowData.put(FIELD_CHAT_IS_LOCAL, 0);
            }
            if (chat.getIsPlayed() == 1) {
                rowData.put(FIELD_CHAT_PLAYED, 1);
            } else {
                rowData.put(FIELD_CHAT_PLAYED, 0);
            }
		  rowData.put(FIELD_CHAT_SEND_STATE, chat.getSendState());
		rowData.put(FIELD_CHAT_RECORD_STATE, chat.getPhotoID());
            Log.i(LOG_TAG,"chat update,is played="+chat.getIsPlayed());
		rowData.put(FIELD_CHAT_SRCID, String.valueOf(chat.getListItemState()));
		if (chat.getListItemType() == 1) {
		rowData.put(FIELD_CHAT_DSTID, String.valueOf(chat.getListItemStartMills()));
		}else{
		rowData.put(FIELD_CHAT_DSTID, chat.getContractItemAvatar());
		}
            id = db.update(CHAT_HIS_TABLE_NAME + familyid, rowData, szBuff.toString(), null);
            if (-1 == id) {
                //LogUtil.e("chat update error! updateChatMsg()");
                Log.i(LOG_TAG,"chat update error! updateChatMsg()");
            }

        } catch (Exception e) {
            // TODO: handle exception
            //LogUtil.e("chat  updateChatMsg() Exp:" + e.getMessage());
        }
        db.close();
//        closeCursor(myCursor);
    }

    public ChatListItemInfo readOneChatFromFamily(String familyid, String date) {
        Cursor myCursor = null;
        ChatListItemInfo chat = new ChatListItemInfo();
        if (familyid == null) {
            Log.e("LOG_TAG","familyid is null ! readOneChatFromFamily()");
        }
        SQLiteDatabase db = this.openWritableDb();
        if (db == null)
            Log.e("LOG_TAG","open db error! readOneChatFromFamily()");

        try {
            StringBuilder szBuff = new StringBuilder();

            szBuff.append("SELECT * FROM ");
            szBuff.append(CHAT_HIS_TABLE_NAME + familyid);
            szBuff.append(" WHERE " + FIELD_CHAT_DATE + " ='"+date+"'"); //æŒ‰æ—¶é—´å‡åºæŸ¥è¯¢

            myCursor = db.rawQuery(szBuff.toString(), null);
            if (myCursor != null && myCursor.moveToFirst()) {
                Log.i(LOG_TAG,"readOneChatFromFamily,"+date);
//        			1	  FIELD_CHAT_SRCID+" TEXT," +
//                  2      FIELD_CHAT_DSTID+" TEXT," +
//                  3      FIELD_FAMILY_ID+" TEXT," +
//                  4      FIELD_WATCH_ID+" TEXT," +
//                  5      FIELD_CHAT_AUDIOPATH+" TEXT," +
//                  6      FIELD_CHAT_DATE+" TEXT," +
//                  7      FIELD_CHAT_SEND_STATE+" INTEGER," +
//                  8       FIELD_CHAT_RECORD_STATE+" INTEGER," +
//                  9      FIELD_CHAT_DURATION+" INTEGER," +//
//                  10      FIELD_CHAT_IS_FROM+" INTEGER," + //
//                  11      FIELD_CHAT_PLAYED+" INTEGER" +
//                  12     FIELD_CHAT_TYPE+" INTEGER" +
                do {

                    //chat.setmSrcId(AESUtil.getInstance().decryptDataStr(myCursor.getString(1)));
                    //chat.setmDstId(AESUtil.getInstance().decryptDataStr(myCursor.getString(2)));
                    chat.setListItemGID(myCursor.getString(3));
                    chat.setListItemEID(myCursor.getString(4));
                    chat.setFilePath(myCursor.getString(5));
                    chat.setmDate(myCursor.getString(6));
                    if (myCursor.getInt(7) == 1 || myCursor.getInt(7) == 4) {
                        //chat.setmSended(myCursor.getInt(7));
                    } else {
                        //chat.setmSended(2);
                    }
                    //chat.setmForceRecordOk(myCursor.getInt(8));
                    chat.setDuration(myCursor.getInt(9));
                    if (myCursor.getInt(10) == 1) {
                        chat.setListItemType(1);
                    } else {
                        chat.setListItemType(0);
                    }
                    if (myCursor.getInt(11) == 1) {

                        chat.setIsPlayed(1);
                    } else {
                        chat.setIsPlayed(0);
                    }
                    Log.i(LOG_TAG,"is played="+myCursor.getInt(11));
                    chat.setContentType(myCursor.getInt(12));
		    chat.setPhotoID(myCursor.getInt(8));
		    chat.setSendState(myCursor.getInt(7));
		    chat.setListItemState(Integer.parseInt(myCursor.getString(1)));
		if (chat.getListItemType() == 1) {
		    chat.setListItemStartMills(Long.parseLong(myCursor.getString(2)));
		}else{
			chat.setContractItemAvatar(myCursor.getString(2));
		}
                    // if (chat.getmSended() == 4) {
                    //       dellist.add(chat);
                    //    } else {

                    Log.i("LOG_TAG","add one item");
                    //   }
                } while (myCursor.moveToNext());
            }
        } catch (Exception e) {
            // TODO: handle exception
            // LogUtil.e("chat read readAllChatMsg() Exp:" + e.getMessage());
            StringBuilder table = new StringBuilder(CHAT_HIS_TABLE_NAME);
            table.append(familyid);
//                CREATE TABLE IF NOT EXISTS
            try {
                db.execSQL("CREATE TABLE " + table.toString() + " (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        FIELD_CHAT_SRCID + " TEXT," +
                        FIELD_CHAT_DSTID + " TEXT," +
                        MyDBOpenHelper.FIELD_FAMILY_ID + " TEXT," +
                        MyDBOpenHelper.FIELD_WATCH_ID + " TEXT," +
                        FIELD_CHAT_AUDIOPATH + " TEXT," +
                        FIELD_CHAT_DATE + " TEXT," +
                        FIELD_CHAT_SEND_STATE + " INTEGER," +
                        FIELD_CHAT_RECORD_STATE + " INTEGER," +
                        FIELD_CHAT_DURATION + " INTEGER," +//
                        FIELD_CHAT_IS_LOCAL + " INTEGER," + //
                        FIELD_CHAT_PLAYED + " INTEGER," +
                        FIELD_CHAT_TYPE + " INTEGER" +
                        ");");
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        db.close();
        closeCursor(myCursor);
        return chat;
    }
    /**
     * æ ¹æ®familyidè¯»å–æ‰€æœ‰ChatMsg
     *
     * @param
     * @return
     */
    public void readAllChatFromFamily(String familyid, ArrayList<ChatListItemInfo> chatlist, ArrayList<ChatListItemInfo> dellist) {
        Cursor myCursor = null;
        if (chatlist == null || familyid == null) {
            Log.e("LOG_TAG","chatlist is null ! readAllChatMsg()");
        }

	if(chatlist.size()>0){
		Log.i("LOG_TAG","chatlist is not empty ! readAllChatMsg()");
		chatlist.clear();
	}
        SQLiteDatabase db = this.openWritableDb();
        if (db == null)
            Log.e("LOG_TAG","open db error! readAllChatMsg()");

        try {
            StringBuilder szBuff = new StringBuilder();

            szBuff.append("SELECT * FROM ");
            szBuff.append(CHAT_HIS_TABLE_NAME + familyid);
            szBuff.append(" ORDER BY " + "_id" + " ASC"); //æŒ‰æ—¶é—´å‡åºæŸ¥è¯¢

            myCursor = db.rawQuery(szBuff.toString(), null);
            if (myCursor != null && myCursor.moveToFirst()) {
//        			1	  FIELD_CHAT_SRCID+" TEXT," +
//                  2      FIELD_CHAT_DSTID+" TEXT," +
//                  3      FIELD_FAMILY_ID+" TEXT," +
//                  4      FIELD_WATCH_ID+" TEXT," +
//                  5      FIELD_CHAT_AUDIOPATH+" TEXT," +
//                  6      FIELD_CHAT_DATE+" TEXT," +
//                  7      FIELD_CHAT_SEND_STATE+" INTEGER," +
//                  8       FIELD_CHAT_RECORD_STATE+" INTEGER," +
//                  9      FIELD_CHAT_DURATION+" INTEGER," +//
//                  10      FIELD_CHAT_IS_FROM+" INTEGER," + //
//                  11      FIELD_CHAT_PLAYED+" INTEGER" +
//                  12     FIELD_CHAT_TYPE+" INTEGER" +
                do {
                    ChatListItemInfo chat = new ChatListItemInfo();
                    int id=myCursor.getInt(0);
                    Log.i(LOG_TAG,"readAllChatMsg,id="+id);
                    //chat.setmSrcId(AESUtil.getInstance().decryptDataStr(myCursor.getString(1)));
                    //chat.setmDstId(AESUtil.getInstance().decryptDataStr(myCursor.getString(2)));
                    chat.setListItemGID(myCursor.getString(3));
                    chat.setListItemEID(myCursor.getString(4));
                   // chat.setmWatchId(AESUtil.getInstance().decryptDataStr(myCursor.getString(4)));
                    chat.setFilePath(myCursor.getString(5));
                    chat.setmDate(myCursor.getString(6));
                    if (myCursor.getInt(7) == 1 || myCursor.getInt(7) == 4) {
                        //chat.setmSended(myCursor.getInt(7));
                    } else {
                        //chat.setmSended(2);
                    }
                    //chat.setmForceRecordOk(myCursor.getInt(8));
                    chat.setDuration(myCursor.getInt(9));
                    if (myCursor.getInt(10) == 1) {
                        chat.setListItemType(1);
                    } else {
                        chat.setListItemType(0);
                    }
                    if (myCursor.getInt(11) == 1) {

                        chat.setIsPlayed(1);
                    } else {
                        chat.setIsPlayed(0);
                    }
                    Log.i("LOG_TAG","is played="+myCursor.getInt(11));
                    chat.setContentType(myCursor.getInt(12));
		    chat.setPhotoID(myCursor.getInt(8));
		    chat.setSendState(myCursor.getInt(7));
		    chat.setListItemState(Integer.parseInt(myCursor.getString(1)));
		if (chat.getListItemType() == 1) {
		    chat.setListItemStartMills(Long.parseLong(myCursor.getString(2)));
		}else{
		    chat.setContractItemAvatar(myCursor.getString(2));
		}
                   // if (chat.getmSended() == 4) {
                 //       dellist.add(chat);
                //    } else {
                        chatlist.add(chat);
                        Log.i("LOG_TAG","add one item");
                 //   }
                } while (myCursor.moveToNext());
            }
        } catch (Exception e) {
            // TODO: handle exception
           // LogUtil.e("chat read readAllChatMsg() Exp:" + e.getMessage());
            StringBuilder table = new StringBuilder(CHAT_HIS_TABLE_NAME);
            table.append(familyid);
//                CREATE TABLE IF NOT EXISTS
            try {
                db.execSQL("CREATE TABLE " + table.toString() + " (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        FIELD_CHAT_SRCID + " TEXT," +
                        FIELD_CHAT_DSTID + " TEXT," +
                        MyDBOpenHelper.FIELD_FAMILY_ID + " TEXT," +
                        MyDBOpenHelper.FIELD_WATCH_ID + " TEXT," +
                        FIELD_CHAT_AUDIOPATH + " TEXT," +
                        FIELD_CHAT_DATE + " TEXT," +
                        FIELD_CHAT_SEND_STATE + " INTEGER," +
                        FIELD_CHAT_RECORD_STATE + " INTEGER," +
                        FIELD_CHAT_DURATION + " INTEGER," +//
                        FIELD_CHAT_IS_LOCAL + " INTEGER," + //
                        FIELD_CHAT_PLAYED + " INTEGER," +
                        FIELD_CHAT_TYPE + " INTEGER" +
                        ");");
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        db.close();
        closeCursor(myCursor);
    }

    public int readMissCountFromFamily(String familyid) {
        Cursor myCursor = null;
        int missCount=0;
        if (familyid == null) {
           // Log.i("LOG_TAG","familyid is null ! readMissCountFromFamily()");
        }
        SQLiteDatabase db = this.openWritableDb();
        if (db == null)
            Log.i("LOG_TAG","open db error! readMissCountFromFamily()");

        try {
            StringBuilder szBuff = new StringBuilder();

            szBuff.append("SELECT * FROM ");
            szBuff.append(CHAT_HIS_TABLE_NAME + familyid);
           // szBuff.append(" ORDER BY " + "_id" + " ASC"); //æŒ‰æ—¶é—´å‡åºæŸ¥è¯¢
            szBuff.append(" WHERE " + FIELD_CHAT_PLAYED + " = 0");

            myCursor = db.rawQuery(szBuff.toString(), null);
            if (myCursor != null && myCursor.moveToFirst()) {
//        			1	  FIELD_CHAT_SRCID+" TEXT," +
//                  2      FIELD_CHAT_DSTID+" TEXT," +
//                  3      FIELD_FAMILY_ID+" TEXT," +
//                  4      FIELD_WATCH_ID+" TEXT," +
//                  5      FIELD_CHAT_AUDIOPATH+" TEXT," +
//                  6      FIELD_CHAT_DATE+" TEXT," +
//                  7      FIELD_CHAT_SEND_STATE+" INTEGER," +
//                  8       FIELD_CHAT_RECORD_STATE+" INTEGER," +
//                  9      FIELD_CHAT_DURATION+" INTEGER," +//
//                  10      FIELD_CHAT_IS_FROM+" INTEGER," + //
//                  11      FIELD_CHAT_PLAYED+" INTEGER" +
//                  12     FIELD_CHAT_TYPE+" INTEGER" +
                do {

                    if (myCursor.getInt(10) == 0&&myCursor.getInt(11) == 0) {
                        missCount++;
                    }

                } while (myCursor.moveToNext());
            }
        } catch (Exception e) {
            // TODO: handle exception
            // LogUtil.e("chat read readAllChatMsg() Exp:" + e.getMessage());
            StringBuilder table = new StringBuilder(CHAT_HIS_TABLE_NAME);
            table.append(familyid);
//                CREATE TABLE IF NOT EXISTS
            try {
                db.execSQL("CREATE TABLE " + table.toString() + " (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        FIELD_CHAT_SRCID + " TEXT," +
                        FIELD_CHAT_DSTID + " TEXT," +
                        MyDBOpenHelper.FIELD_FAMILY_ID + " TEXT," +
                        MyDBOpenHelper.FIELD_WATCH_ID + " TEXT," +
                        FIELD_CHAT_AUDIOPATH + " TEXT," +
                        FIELD_CHAT_DATE + " TEXT," +
                        FIELD_CHAT_SEND_STATE + " INTEGER," +
                        FIELD_CHAT_RECORD_STATE + " INTEGER," +
                        FIELD_CHAT_DURATION + " INTEGER," +//
                        FIELD_CHAT_IS_LOCAL + " INTEGER," + //
                        FIELD_CHAT_PLAYED + " INTEGER," +
                        FIELD_CHAT_TYPE + " INTEGER" +
                        ");");
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        db.close();
        closeCursor(myCursor);
        Log.i("LOG_TAG","readMissCountFromFamily,"+familyid+",missCount="+missCount);
        return missCount;
    }

    public boolean isMsgExist(String familyId, String key) {
        if (familyId == null || familyId.length() == 0 || key == null || key.length() == 0) {
            return false;
        }
        SQLiteDatabase db = this.openWritableDb();
        if (db == null) {
            //LogUtil.e("open db error! readAllChatMsg()");
            return false;
        }
        try {
            StringBuilder szBuff = new StringBuilder();
            szBuff.append("SELECT * FROM ");
            szBuff.append(CHAT_HIS_TABLE_NAME + familyId);
            szBuff.append(" WHERE " + FIELD_CHAT_DATE + " ='" + key + "'");
            Cursor cursor = db.rawQuery(szBuff.toString(), null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.close();
                db.close();
                return true;
            } else {
                closeCursor(cursor);
                db.close();
                return false;
            }
        } catch (Exception e) {
          StringBuilder table = new StringBuilder(CHAT_HIS_TABLE_NAME);
            table.append(familyId);
//                CREATE TABLE IF NOT EXISTS
            try {
                db.execSQL("CREATE TABLE " + table.toString() + " (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        FIELD_CHAT_SRCID + " TEXT," +
                        FIELD_CHAT_DSTID + " TEXT," +
                        MyDBOpenHelper.FIELD_FAMILY_ID + " TEXT," +
                        MyDBOpenHelper.FIELD_WATCH_ID + " TEXT," +
                        FIELD_CHAT_AUDIOPATH + " TEXT," +
                        FIELD_CHAT_DATE + " TEXT," +
                        FIELD_CHAT_SEND_STATE + " INTEGER," +
                        FIELD_CHAT_RECORD_STATE + " INTEGER," +
                        FIELD_CHAT_DURATION + " INTEGER," +//
                        FIELD_CHAT_IS_LOCAL + " INTEGER," + //
                        FIELD_CHAT_PLAYED + " INTEGER," +
                        FIELD_CHAT_TYPE + " INTEGER" +
                        ");");
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            db.close();
            return false;
        }
    }

    public int getMsgCount(String familyId) {
        if (familyId == null || familyId.length() == 0) {
            return -1;
        }
        SQLiteDatabase db = this.openWritableDb();
        if (db == null) {
            //LogUtil.e("open db error! readAllChatMsg()");
            return -1;
        }
        try {
            StringBuilder szBuff = new StringBuilder();
            szBuff.append("SELECT * FROM ");
            szBuff.append(CHAT_HIS_TABLE_NAME + familyId);
            Cursor cursor = db.rawQuery(szBuff.toString(), null);
            if (cursor != null && cursor.getCount() > 0) {
                int count = cursor.getCount();
                cursor.close();
                db.close();
                return count;
            } else {
                closeCursor(cursor);
                db.close();
                return 0;
            }
        } catch (Exception e) {
          StringBuilder table = new StringBuilder(CHAT_HIS_TABLE_NAME);
            table.append(familyId);
//                CREATE TABLE IF NOT EXISTS
            try {
                db.execSQL("CREATE TABLE " + table.toString() + " (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        FIELD_CHAT_SRCID + " TEXT," +
                        FIELD_CHAT_DSTID + " TEXT," +
                        MyDBOpenHelper.FIELD_FAMILY_ID + " TEXT," +
                        MyDBOpenHelper.FIELD_WATCH_ID + " TEXT," +
                        FIELD_CHAT_AUDIOPATH + " TEXT," +
                        FIELD_CHAT_DATE + " TEXT," +
                        FIELD_CHAT_SEND_STATE + " INTEGER," +
                        FIELD_CHAT_RECORD_STATE + " INTEGER," +
                        FIELD_CHAT_DURATION + " INTEGER," +//
                        FIELD_CHAT_IS_LOCAL + " INTEGER," + //
                        FIELD_CHAT_PLAYED + " INTEGER," +
                        FIELD_CHAT_TYPE + " INTEGER" +
                        ");");
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return 0;
        }
    }

    public long delAllMsg(String familyid) {
        long id = -1;
        SQLiteDatabase db = this.openWritableDb();
        if (db == null)
            return -1;
        Cursor myCursor=null;
        try {
            StringBuilder szBuff = new StringBuilder();

            szBuff.append("SELECT * FROM ");
            szBuff.append(CHAT_HIS_TABLE_NAME + familyid);
            szBuff.append(" ORDER BY " + "_id" + " ASC"); //æŒ‰æ—¶é—´å‡åºæŸ¥è¯¢

             myCursor = db.rawQuery(szBuff.toString(), null);
            if (myCursor != null && myCursor.moveToFirst()) {
//        			1	  FIELD_CHAT_SRCID+" TEXT," +
//                  2      FIELD_CHAT_DSTID+" TEXT," +
//                  3      FIELD_FAMILY_ID+" TEXT," +
//                  4      FIELD_WATCH_ID+" TEXT," +
//                  5      FIELD_CHAT_AUDIOPATH+" TEXT," +
//                  6      FIELD_CHAT_DATE+" TEXT," +
//                  7      FIELD_CHAT_SEND_STATE+" INTEGER," +
//                  8       FIELD_CHAT_RECORD_STATE+" INTEGER," +
//                  9      FIELD_CHAT_DURATION+" INTEGER," +//
//                  10      FIELD_CHAT_IS_FROM+" INTEGER," + //
//                  11      FIELD_CHAT_PLAYED+" INTEGER" +
//                  12     FIELD_CHAT_TYPE+" INTEGER" +
                do{
                    String filePath= myCursor.getString(5);
                    Log.i("LOG_TAG","delete file,filePath="+filePath);
                    ChatUtil.ChatUtilDeleteFile(filePath);
                }
                while (myCursor.moveToNext());

            }
        } catch (Exception e) {
            // TODO: handle exception
            // LogUtil.e("chat read readAllChatMsg() Exp:" + e.getMessage());
            StringBuilder table = new StringBuilder(CHAT_HIS_TABLE_NAME);
            table.append(familyid);
//                CREATE TABLE IF NOT EXISTS
            try {
                db.execSQL("CREATE TABLE " + table.toString() + " (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        FIELD_CHAT_SRCID + " TEXT," +
                        FIELD_CHAT_DSTID + " TEXT," +
                        MyDBOpenHelper.FIELD_FAMILY_ID + " TEXT," +
                        MyDBOpenHelper.FIELD_WATCH_ID + " TEXT," +
                        FIELD_CHAT_AUDIOPATH + " TEXT," +
                        FIELD_CHAT_DATE + " TEXT," +
                        FIELD_CHAT_SEND_STATE + " INTEGER," +
                        FIELD_CHAT_RECORD_STATE + " INTEGER," +
                        FIELD_CHAT_DURATION + " INTEGER," +//
                        FIELD_CHAT_IS_LOCAL + " INTEGER," + //
                        FIELD_CHAT_PLAYED + " INTEGER," +
                        FIELD_CHAT_TYPE + " INTEGER" +
                        ");");
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
	if(myCursor!=null)
        myCursor.close();

        try {
            StringBuilder szBuff = new StringBuilder();
            szBuff.append("DELETE FROM ");
            szBuff.append(CHAT_HIS_TABLE_NAME + familyid);
            db.execSQL(szBuff.toString());
        } catch (Exception e) {
            //LogUtil.e("chat  delChatMsg() Exp:" + e.getMessage());
        }
        db.close();
        return id;
    }

    public ChatListItemInfo getLatestMessage(String familyId) {
        Log.e(LOG_TAG,"getLatestMessage");
        if (familyId == null || familyId.length() == 0) {
            return null;
        }
        SQLiteDatabase db = this.openWritableDb();
        if (db == null) {
            //LogUtil.e("open db error! readAllChatMsg()");
            Log.e(LOG_TAG,"open db error!");
            return null;
        }
        try {
            StringBuilder szBuff = new StringBuilder();
            szBuff.append("SELECT * FROM ");
            szBuff.append(CHAT_HIS_TABLE_NAME + familyId);
            //szBuff.append(" WHERE " + FIELD_CHAT_SEND_STATE + " <> 4");
           // szBuff.append(" ORDER BY " + FIELD_CHAT_DATE + " DESC");
            Cursor cursor = db.rawQuery(szBuff.toString(), null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                ChatListItemInfo chat = new ChatListItemInfo();
                //chat.setmSrcId(AESUtil.getInstance().decryptDataStr(cursor.getString(1)));
                //chat.setmDstId(AESUtil.getInstance().decryptDataStr(cursor.getString(2)));
                chat.setListItemGID(cursor.getString(3));
                //chat.setmWatchId(AESUtil.getInstance().decryptDataStr(cursor.getString(4)));
                chat.setFilePath(cursor.getString(5));
                chat.setmDate(cursor.getString(6));
                if (cursor.getInt(7) == 1 || cursor.getInt(7) == 4) {
                    //chat.setmSended(cursor.getInt(7));
                } else {
                   // chat.setmSended(2);
                }
               // chat.setmForceRecordOk(cursor.getInt(8));
                chat.setDuration(cursor.getInt(9));
                if (cursor.getInt(10) == 1) {
                    chat.setListItemType(1);
                } else {
                    chat.setListItemType(0);
                }
                if (cursor.getInt(11) == 1) {
                    chat.setIsPlayed(1);
                } else {
                    chat.setIsPlayed(0);
                }
                //chat.setmType(cursor.getInt(12));
		chat.setPhotoID(cursor.getInt(8));
		chat.setSendState(cursor.getInt(7));
		chat.setListItemState(Integer.parseInt(cursor.getString(1)));
		if (chat.getListItemType() == 1) {
		chat.setListItemStartMills(Long.parseLong(cursor.getString(2)));
		}else{
		chat.setContractItemAvatar(cursor.getString(2));
		}
                cursor.close();
                db.close();
                return chat;
            } else {
                closeCursor(cursor);
                db.close();
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            db.close();
            return null;
        }
    }

    public void readUnreadChatFromFamily(String familyid, ArrayList<ChatNoticeInfo> notiflist,String date) {
        Cursor myCursor = null;
        if (notiflist == null || familyid == null) {
            Log.e("LOG_TAG","chatlist is null ! readAllChatMsg()");
        }
        SQLiteDatabase db = this.openWritableDb();
        if (db == null)
            Log.e("LOG_TAG","open db error! readAllChatMsg()");

        try {
            StringBuilder szBuff = new StringBuilder();

            szBuff.append("SELECT * FROM ");
            szBuff.append(CHAT_HIS_TABLE_NAME + familyid);
            szBuff.append(" WHERE " + FIELD_CHAT_PLAYED + " = 0 ");
            szBuff.append(" AND " + FIELD_CHAT_DATE + " <= "+date );
            szBuff.append(" ORDER BY " + "_id" + " ASC"); //æŒ‰æ—¶é—´å‡åºæŸ¥è¯¢

            myCursor = db.rawQuery(szBuff.toString(), null);
            if (myCursor != null && myCursor.moveToFirst()) {
//        			1	  FIELD_CHAT_SRCID+" TEXT," +
//                  2      FIELD_CHAT_DSTID+" TEXT," +
//                  3      FIELD_FAMILY_ID+" TEXT," +
//                  4      FIELD_WATCH_ID+" TEXT," +
//                  5      FIELD_CHAT_AUDIOPATH+" TEXT," +
//                  6      FIELD_CHAT_DATE+" TEXT," +
//                  7      FIELD_CHAT_SEND_STATE+" INTEGER," +
//                  8       FIELD_CHAT_RECORD_STATE+" INTEGER," +
//                  9      FIELD_CHAT_DURATION+" INTEGER," +//
//                  10      FIELD_CHAT_IS_FROM+" INTEGER," + //
//                  11      FIELD_CHAT_PLAYED+" INTEGER" +
//                  12     FIELD_CHAT_TYPE+" INTEGER" +
                do {
                    ChatNoticeInfo chat = new ChatNoticeInfo();
                    //chat.setmSrcId(AESUtil.getInstance().decryptDataStr(myCursor.getString(1)));
                    //chat.setmDstId(AESUtil.getInstance().decryptDataStr(myCursor.getString(2)));
                    chat.setNoticeGID(myCursor.getString(3));
		    chat.setNoticeEID(myCursor.getString(4));
                    // chat.setmWatchId(AESUtil.getInstance().decryptDataStr(myCursor.getString(4)));
                    chat.setFilePath(myCursor.getString(5));
                    chat.setmDate(myCursor.getString(6));
                    if (myCursor.getInt(7) == 1 || myCursor.getInt(7) == 4) {
                        //chat.setmSended(myCursor.getInt(7));
                    } else {
                        //chat.setmSended(2);
                    }
                    //chat.setmForceRecordOk(myCursor.getInt(8));
                    chat.setDuration(myCursor.getInt(9));

                    if (myCursor.getInt(11) == 1) {

                        chat.setIsPlayed(1);
                    } else {
                        chat.setIsPlayed(0);
                    }
                    Log.i("LOG_TAG","is played="+myCursor.getInt(11));
                    chat.setContentType(myCursor.getInt(12));

                    chat.setPhotoID(myCursor.getInt(8));
		    chat.setContractItemAvatar(myCursor.getString(2));
		    //chat.setListItemState(Integer.parseInt(myCursor.getString(1)));
                    // if (chat.getmSended() == 4) {
                    //       dellist.add(chat);
                    //    } else {
		    if(myCursor.getInt(10) == 0){
                    notiflist.add(chat);
			Log.i("LOG_TAG","add one item");
		    }else{
			Log.i("LOG_TAG","is not a peer item");
		    }
                    
                    //   }
                } while (myCursor.moveToNext());
            }
        } catch (Exception e) {
            // TODO: handle exception
            // LogUtil.e("chat read readAllChatMsg() Exp:" + e.getMessage());
            StringBuilder table = new StringBuilder(CHAT_HIS_TABLE_NAME);
            table.append(familyid);
//                CREATE TABLE IF NOT EXISTS
            try {
                db.execSQL("CREATE TABLE " + table.toString() + " (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        FIELD_CHAT_SRCID + " TEXT," +
                        FIELD_CHAT_DSTID + " TEXT," +
                        MyDBOpenHelper.FIELD_FAMILY_ID + " TEXT," +
                        MyDBOpenHelper.FIELD_WATCH_ID + " TEXT," +
                        FIELD_CHAT_AUDIOPATH + " TEXT," +
                        FIELD_CHAT_DATE + " TEXT," +
                        FIELD_CHAT_SEND_STATE + " INTEGER," +
                        FIELD_CHAT_RECORD_STATE + " INTEGER," +
                        FIELD_CHAT_DURATION + " INTEGER," +//
                        FIELD_CHAT_IS_LOCAL + " INTEGER," + //
                        FIELD_CHAT_PLAYED + " INTEGER," +
                        FIELD_CHAT_TYPE + " INTEGER" +
                        ");");
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        db.close();
        closeCursor(myCursor);
    }

    public void deleteChatFromFamily(String familyid,int last_count,ArrayList<String> dateList) {
        Cursor myCursor = null;
        ChatListItemInfo chat = new ChatListItemInfo();
	String date=null;
	int idIndex=0;
        int index=0;
        //String date=null;
        if (familyid == null) {
            Log.i("LOG_TAG","familyid is null ! deleteChatFromFamily()");
	    return;
        }

        if (dateList == null) {
            Log.i("LOG_TAG","dateList is null ! deleteChatFromFamily()");
	    return;
        }

        int max_count=getMsgCount(familyid);

        if(last_count<1){
             last_count=1;
        }
        if(last_count>max_count){
            last_count=max_count;
        }
        Log.i("LOG_TAG","last_count="+last_count);
        SQLiteDatabase db = this.openWritableDb();
        if (db == null)
            Log.e("LOG_TAG","open db error! deleteChatFromFamily()");

        try {
            StringBuilder szBuff = new StringBuilder();

            szBuff.append("SELECT * FROM ");
            szBuff.append(CHAT_HIS_TABLE_NAME + familyid);
            szBuff.append(" ORDER BY " + "_id" + " ASC"); //æŒ‰æ—¶é—´å‡åºæŸ¥è¯¢

            myCursor = db.rawQuery(szBuff.toString(), null);
            if (myCursor != null && myCursor.moveToFirst()) {
//        			1	  FIELD_CHAT_SRCID+" TEXT," +
//                  2      FIELD_CHAT_DSTID+" TEXT," +
//                  3      FIELD_FAMILY_ID+" TEXT," +
//                  4      FIELD_WATCH_ID+" TEXT," +
//                  5      FIELD_CHAT_AUDIOPATH+" TEXT," +
//                  6      FIELD_CHAT_DATE+" TEXT," +
//                  7      FIELD_CHAT_SEND_STATE+" INTEGER," +
//                  8       FIELD_CHAT_RECORD_STATE+" INTEGER," +
//                  9      FIELD_CHAT_DURATION+" INTEGER," +//
//                  10      FIELD_CHAT_IS_FROM+" INTEGER," + //
//                  11      FIELD_CHAT_PLAYED+" INTEGER" +
//                  12     FIELD_CHAT_TYPE+" INTEGER" +
                do{
                    Log.i("LOG_TAG","delete file,index="+index);
                    String filePath= myCursor.getString(5);
                    Log.i("LOG_TAG","delete file,filePath="+filePath);
		    String delDate=myCursor.getString(6);
		    Log.i("LOG_TAG","delete file,delDate="+delDate);
		    idIndex=myCursor.getInt(0);
		    dateList.add(delDate);
                    ChatUtil.ChatUtilDeleteFile(filePath);
                    index++;
                }
                 while (myCursor.moveToNext()&&index<last_count);
                Log.i("LOG_TAG","deleteChatFromFamily,index="+index);
                date=myCursor.getString(6);
                Log.i("LOG_TAG","deleteChatFromFamily,idIndex="+idIndex);
            }
        } catch (Exception e) {
            // TODO: handle exception
            // LogUtil.e("chat read readAllChatMsg() Exp:" + e.getMessage());
            StringBuilder table = new StringBuilder(CHAT_HIS_TABLE_NAME);
            table.append(familyid);
//                CREATE TABLE IF NOT EXISTS
            try {
                db.execSQL("CREATE TABLE " + table.toString() + " (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        FIELD_CHAT_SRCID + " TEXT," +
                        FIELD_CHAT_DSTID + " TEXT," +
                        MyDBOpenHelper.FIELD_FAMILY_ID + " TEXT," +
                        MyDBOpenHelper.FIELD_WATCH_ID + " TEXT," +
                        FIELD_CHAT_AUDIOPATH + " TEXT," +
                        FIELD_CHAT_DATE + " TEXT," +
                        FIELD_CHAT_SEND_STATE + " INTEGER," +
                        FIELD_CHAT_RECORD_STATE + " INTEGER," +
                        FIELD_CHAT_DURATION + " INTEGER," +//
                        FIELD_CHAT_IS_LOCAL + " INTEGER," + //
                        FIELD_CHAT_PLAYED + " INTEGER," +
                        FIELD_CHAT_TYPE + " INTEGER" +
                        ");");
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        myCursor.close();
        try {
            if(date!=null) {
                StringBuilder szBuff = new StringBuilder();

                szBuff.append("DELETE FROM ");
                szBuff.append(CHAT_HIS_TABLE_NAME + familyid);
                szBuff.append(" WHERE " + "_id" + " <= " + idIndex); //æŒ‰æ—¶é—´å‡åºæŸ¥è¯¢
                //szBuff.append(" WHERE " + "_id" + " = "+"0"); //æŒ‰æ—¶é—´å‡åºæŸ¥è¯¢
                Log.i("LOG_TAG", "sql=" + szBuff.toString());
                db.execSQL(szBuff.toString());
            }
            //db.delete(CHAT_HIS_TABLE_NAME+familyid,"_id<?",new String[]{Integer.toString(last_count-1)});

        } catch (Exception e) {
            // TODO: handle exception
            // LogUtil.e("chat read readAllChatMsg() Exp:" + e.getMessage());
            StringBuilder table = new StringBuilder(CHAT_HIS_TABLE_NAME);
            table.append(familyid);
//                CREATE TABLE IF NOT EXISTS
            try {
                db.execSQL("CREATE TABLE " + table.toString() + " (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        FIELD_CHAT_SRCID + " TEXT," +
                        FIELD_CHAT_DSTID + " TEXT," +
                        MyDBOpenHelper.FIELD_FAMILY_ID + " TEXT," +
                        MyDBOpenHelper.FIELD_WATCH_ID + " TEXT," +
                        FIELD_CHAT_AUDIOPATH + " TEXT," +
                        FIELD_CHAT_DATE + " TEXT," +
                        FIELD_CHAT_SEND_STATE + " INTEGER," +
                        FIELD_CHAT_RECORD_STATE + " INTEGER," +
                        FIELD_CHAT_DURATION + " INTEGER," +//
                        FIELD_CHAT_IS_LOCAL + " INTEGER," + //
                        FIELD_CHAT_PLAYED + " INTEGER," +
                        FIELD_CHAT_TYPE + " INTEGER" +
                        ");");
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        db.close();

    }
}

