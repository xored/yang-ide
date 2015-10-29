package com.cisco.yangide.ext.model.editor.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Strings {
    
    private Strings() {
        super();
    }
    private static final SimpleDateFormat DEFAULT_DATE_FORMAT =  new SimpleDateFormat("yyyy-MM-dd");
    public static final String EMPTY_STRING = "";
    
    public static String getAsString(Object obj) {
        if (null != obj && null != obj.toString()) {
            return obj.toString();
        }
        return EMPTY_STRING;
    }
    
    public static String toString(Date obj) {
        if (null != obj) {
            return DEFAULT_DATE_FORMAT.format(obj);
        }
        return EMPTY_STRING;
    }
    
    public static boolean isEmpty(Object obj) {
        return null == obj || null == obj.toString() || obj.toString().isEmpty();
    }
}
