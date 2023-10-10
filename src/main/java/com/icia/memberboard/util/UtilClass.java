package com.icia.memberboard.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UtilClass {

    public static String dateTimeFormat(LocalDateTime dateTime){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        if (dateTime == null){
            return null;
        }else {
            return dtf.format(dateTime);
        }
    }
}
