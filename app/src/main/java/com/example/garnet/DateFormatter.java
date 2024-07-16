package com.example.garnet;

public class DateFormatter {
    public static String formatDate(int year, int month, int dayOfMonth){
        //将年月日日期改写为相应的字符串
        String monthStr, dayStr;
        if (month + 1 < 10)monthStr = "0" + (month+1);
        else monthStr = Integer.toString(month+1);
        if (dayOfMonth < 10)dayStr = "0" + (dayOfMonth);
        else dayStr = Integer.toString(dayOfMonth);
        return year + "-" + monthStr + "-" + dayStr;
    }
}
