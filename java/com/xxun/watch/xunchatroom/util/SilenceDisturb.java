package com.xxun.watch.xunchatroom.util;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jixiang on 2017/11/22.
 */

public class SilenceDisturb {

    /**
     * timeid : 20171122143003734
     * silence_call_in_onoff : 0
     * onoff : 1
     * days : 1000111
     * startmin : 22
     * advanceop : 1
     * starthour : 13
     * endmin : 30
     * endhour : 16
     */

    @SerializedName("timeid")
    public String timeid;
    @SerializedName("silence_call_in_onoff")
    public int silenceCallInOnoff;
    @SerializedName("onoff")
    public String onoff;
    @SerializedName("days")
    public String days;
    @SerializedName("startmin")
    public String startmin;
    @SerializedName("advanceop")
    public int advanceop;
    @SerializedName("starthour")
    public String starthour;
    @SerializedName("endmin")
    public String endmin;
    @SerializedName("endhour")
    public String endhour;

    public static SilenceDisturb objectFromData(String str) {

        return new Gson().fromJson(str, SilenceDisturb.class);
    }

    public static SilenceDisturb objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), SilenceDisturb.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<SilenceDisturb> arraySilenceDisturbFromData(String str) {

        Type listType = new TypeToken<ArrayList<SilenceDisturb>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<SilenceDisturb> arraySilenceDisturbFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<SilenceDisturb>>() {
            }.getType();

            return new Gson().fromJson(jsonObject.getString(str), listType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList();


    }
}
