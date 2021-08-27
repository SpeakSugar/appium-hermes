package com.ringcentral.hermes.util;

public class EnvUtil {

    public static String getAdbPath() {
        String adbEnv = System.getenv("ADB");
        if (adbEnv == null || adbEnv.isEmpty()) {
            adbEnv = "";
        } else if (!adbEnv.endsWith("/")) {
            adbEnv += "/";
        }
        return adbEnv;
    }

}
