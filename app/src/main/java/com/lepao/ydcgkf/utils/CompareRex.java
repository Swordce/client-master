package com.lepao.ydcgkf.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CompareRex {
    public static boolean isCellPhone(String cellphone) {
        Pattern pattern = Pattern.compile("^((13[0-9])|(14[5,7])|(15[^4])|(17[0,1,3,5-8])|(18[0-9]))\\d{8}$");
        Matcher matcher = pattern.matcher(cellphone);
        //    Log.e("s--->",email);
        return matcher.matches();
    }
}
