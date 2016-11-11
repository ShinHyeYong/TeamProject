package utils.model;

import android.provider.BaseColumns;

/**
 * Created by PSJ on 2016. 11. 10..
 */

public class UserInfo {
    public static class UserEntry implements BaseColumns{
        public static String USER_NAME = null;
        public static String USER_ID = null;
        public static String USER_PWD = null;
        public static boolean IS_LOGIN = false;
    }
}
