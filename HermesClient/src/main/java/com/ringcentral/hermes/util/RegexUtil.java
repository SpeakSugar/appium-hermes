package com.ringcentral.hermes.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {

    public static String getMatchString(String str, String regex, int order) {
        Matcher matcher = Pattern.compile(regex).matcher(str);
        boolean isFind = true;
        for (int i = 1; i <= order; i++) {
            isFind = matcher.find();
        }
        if (isFind) {
            return matcher.group();
        } else {
            return "";
        }
    }

}
