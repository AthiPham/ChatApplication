package com.thiPNA219166.onlinechatapp.Prevalent;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateTime {
    public static String getDatetimeNow(){
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
        return currentDate.format(calendar.getTime());
    }

}
