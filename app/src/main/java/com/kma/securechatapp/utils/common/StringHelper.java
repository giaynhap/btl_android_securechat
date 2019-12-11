package com.kma.securechatapp.utils.common;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class StringHelper {


    public static LocalDateTime long2LocalDateTime(Long time){

        try {
            LocalDateTime date = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                date = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
            }
            else{

            }
            return date;
        }catch (Exception e){
            return null;
        }
    }
    public static String getTimeText(LocalDateTime datetime){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            return datetime.format(formatter);
        }else{
            return "";
        }
    }
    public static String getTimeText(Long datetime){

        return getTimeText(long2LocalDateTime(datetime));
    }

    public static String getLongTimeText(Long time){
        return getLongTimeText(long2LocalDateTime(time  ));
    }
    public static String getLongTimeText(LocalDateTime time){
        try {
            if (time != null) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm");
                    return time.format(formatter);
                }
            }
            return "";
        }
        catch (Exception e){
            return "";
        }
    }

}
