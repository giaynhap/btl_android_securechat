package com.kma.securechatapp.utils.common;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class StringHelper {


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static LocalDateTime long2LocalDateTime(Long time){

        try {
            LocalDateTime date = null;

                date = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());

            return date;
        }catch (Exception e){
            return null;
        }
    }
    public static Date long2Date(Long time){

        try {
            Date date = new Date(time);
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
            DateFormat dateFormat = new SimpleDateFormat("HH:mm");
            return dateFormat.format(datetime);
        }
    }
    public static String getTimeText(Date datetime){

        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(datetime);

    }
    public static String getTimeText(Long datetime){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return getTimeText(long2LocalDateTime(datetime));
        }else{
            return getTimeText(long2Date(datetime));
        }
    }


    public static String getLongTimeText(Long time){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return getLongTimeText(long2LocalDateTime(time  ));
        }else{
            return getLongTimeText(long2Date(time  ));
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getLongTimeText(LocalDateTime time){
        try {
            if (time != null) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm");
                    return time.format(formatter);

            }
            return "";
        }
        catch (Exception e){
            return "";
        }
    }
    public static String getLongTextChat(long date){
        long current = Calendar.getInstance().getTime().getTime();
        if (checkSameDay(current,date)){
            return "Today";
        }
        return getLongTimeText(date);
    }
    public static String getLongTimeText(Date time){
        try {
            if (time == null) {
                return "";
            }
            DateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");
            return dateFormat.format(time);
        }catch (Exception e){
            return "";
        }
    }
    public static boolean checkSameDay(long date, long date2){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return checkSameDay(long2LocalDateTime(date),long2LocalDateTime(date2));
        }else{
            return checkSameDay(long2Date(date),long2Date(date2));
        }
    }
    public static boolean checkSameDay(Date date,Date date2){
        return  (date.getMonth() == date2.getMonth()) && (date.getDate() == date2.getDate() && date.getYear() == date2.getYear());
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean checkSameDay(LocalDateTime date, LocalDateTime date2){
        return  (date.getMonth() == date2.getMonth()) && (date.getDayOfMonth()== date2.getDayOfMonth() && date.getYear() == date2.getYear());
    }


}
