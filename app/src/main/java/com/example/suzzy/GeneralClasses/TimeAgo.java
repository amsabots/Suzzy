package com.example.suzzy.GeneralClasses;

import android.content.Context;

public class TimeAgo {
    public static final  int REQUEST_CODE = 1;
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static String getTimeAgo(long time, Context ctx) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if(time > now){
            return "invalid time";
        }else if(time <= 0){
            return "currently unavailable";
        }
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {

            long months = diff / DAY_MILLIS ;
            if(months<30){
                return months+ " days ago";
            }
            else if(months < 30*2){
                return "1 month ago";

            }else if(months < 30*3){
                return "2 months ago";
            }
            else if(months < 30*4){
                return "3 months ago";
            }
            else if(months < 30*5){
                return "4 months ago";
            }
            else if(months < 30*6){
                return "5 months ago";
            }
            else if(months < 30*7){
                return "6 months ago";
            }
            else
                return "Long time ago";
        }
    }
}
