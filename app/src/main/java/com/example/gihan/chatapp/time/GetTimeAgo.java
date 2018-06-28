package com.example.gihan.chatapp.time;

import android.app.Application;
import android.content.Context;
import android.provider.Settings;

/**
 * Created by Gihan on 8/16/2017.
 */

public class GetTimeAgo extends Application {


    private final static int SECOND_MILLIS = 1000;
    private final static int MUINITE_MILLIS = SECOND_MILLIS * 60;
    private final static int HOUR_MILLIS = MUINITE_MILLIS * 60;
    private final static int DAY_MILLIS = HOUR_MILLIS * 24;


    public static String getTimeAgo(long time, Context ctx) {
        long now = System.currentTimeMillis();

        if (time < 1000000000000l) {
            time *= 1000;

        }

        if (time > now || time <= 0) {
            return null;
        }

        final long diff = now-time;
        if (diff < MUINITE_MILLIS) {
            return "Just Now";
        } else if (diff < 2 * MUINITE_MILLIS) {
            return "a munite ago";
        } else if (diff < 50 * MUINITE_MILLIS) {
            return diff / MUINITE_MILLIS + "munite ago";
        }else if(diff<90*MUINITE_MILLIS){
            return "an hour ago";
        }else if(diff<20*HOUR_MILLIS){
            return diff/HOUR_MILLIS +"hours ago";
        }else if(diff<48*HOUR_MILLIS){
           return  "Yesterday";
        }else {
           return diff / DAY_MILLIS +"days ago";
        }

    }

}
