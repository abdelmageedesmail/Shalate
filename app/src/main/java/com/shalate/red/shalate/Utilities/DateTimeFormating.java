package com.shalate.red.shalate.Utilities;

import android.content.Context;

import com.shalate.red.shalate.R;

/**
 * Created by Ahmed on 8/27/2018.
 */

public class DateTimeFormating {
    public static String getFriendlyTime(long dateTime,Context context) {
        StringBuffer sb = new StringBuffer();
        long current = System.currentTimeMillis();
        long diffInSeconds = (current- dateTime) / 1000;

        long sec = (diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds);
        long min = (diffInSeconds = (diffInSeconds / 60)) >= 60 ? diffInSeconds % 60 : diffInSeconds;
        long hrs = (diffInSeconds = (diffInSeconds / 60)) >= 24 ? diffInSeconds % 24 : diffInSeconds;
        long days = (diffInSeconds = (diffInSeconds / 24)) >= 30 ? diffInSeconds % 30 : diffInSeconds;
        long months = (diffInSeconds = (diffInSeconds / 30)) >= 12 ? diffInSeconds % 12 : diffInSeconds;
        long years = (diffInSeconds = (diffInSeconds / 12));

        if (context.getResources().getConfiguration().locale.getLanguage().equalsIgnoreCase("ar")){

            sb.append(context.getText(R.string.friend_time_ago));

        }

        if (years > 0) {
            if (years == 1) {
                sb.append(" "+context.getText(R.string.friend_time_year));
            } else {
                sb.append(" "+years + " "+context.getText(R.string.friend_time_years));
            }
            if (years <= 6 && months > 0) {
                if (months == 1) {
                    sb.append(" "+context.getText(R.string.friend_time_and_month));
                } else {
                    sb.append(" "+context.getText(R.string.friend_time_and)+" " + months + " "+context.getText(R.string.friend_time_months));
                }
            }
        } else if (months > 0) {
            if (months == 1) {
                sb.append(" "+context.getText(R.string.friend_time_month));
            } else {
                sb.append(" "+months + " "+context.getText(R.string.friend_time_months));
            }
            if (months <= 6 && days > 0) {
                if (days == 1) {
                    sb.append(" "+context.getText(R.string.friend_time_and_day));
                } else {
                    sb.append(" "+ context.getText(R.string.friend_time_and)+" " + days + context.getText(R.string.friend_time_days));
                }
            }
        } else if (days > 0) {
            if (days == 1) {
                sb.append(" " + context.getText(R.string.friend_time_aday));
            } else {
                sb.append(days + " "+context.getText(R.string.friend_time_days));
            }
            if (days <= 3 && hrs > 0) {
                if (hrs == 1) {
                    sb.append(" "+ context.getText(R.string.friend_time_and_hour));
                } else {
                    sb.append(" "+context.getText(R.string.friend_time_and)+" " + hrs + " "+context.getText(R.string.friend_time_hours));
                }
            }
        } else if (hrs > 0) {
            if (hrs == 1) {
                sb.append(" "+context.getText(R.string.friend_time_hour));
            } else {
                sb.append(hrs + " "+context.getText(R.string.friend_time_hours));
            }
            if (min > 1) {
                sb.append(" "+context.getText(R.string.friend_time_and)+" " + min + " "+context.getText(R.string.friend_time_minutes));
            }
        } else if (min > 0) {
            if (min == 1) {
                sb.append(" "+context.getText(R.string.friend_time_minute));
            } else {
                sb.append(" "+min + " "+context.getText(R.string.friend_time_minutes));
            }
            if (sec > 1) {
                sb.append(" "+context.getText(R.string.friend_time_and)+" " + sec + " "+context.getText(R.string.friend_time_seconds));
            }
        } else {
            if (sec <= 1) {
                sb.append(" "+context.getText(R.string.friend_time_about_second));
            } else {
                sb.append(" "+context.getText(R.string.friend_time_about)+" " + sec + " "+context.getText(R.string.friend_time_seconds));
            }
        }

        if (!context.getResources().getConfiguration().locale.getLanguage().equalsIgnoreCase("ar")){
             sb.append(" ");
            sb.append(context.getText(R.string.friend_time_ago));

        }

        return sb.toString();
    }

}
