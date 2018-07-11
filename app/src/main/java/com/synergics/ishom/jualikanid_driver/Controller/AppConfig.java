package com.synergics.ishom.jualikanid_driver.Controller;

/**
 * Created by asmarasusanto on 1/22/18.
 */

public class AppConfig {
//    public static String url = "http://192.168.1.3/jualikan.id/";
//    public static String url = "http://192.168.43.168/jualikan.id/";
    public static String url = "http://ishom.jagopesan.com/jualikan.id/";
    public static String user_level = "2";

    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "ah_firebase";
}
