package utils.model;

import android.provider.BaseColumns;

/**
 * Created by PSJ on 2016. 11. 10..
 */

//사용자 정보
public class UserInfo {
    public static class UserEntry implements BaseColumns{
        public static String USER_NAME = null; //이름
        public static String USER_ID = null; //아이디
        public static String USER_PWD = null; //비밀번호
        public static boolean IS_LOGIN = false; //로그인 여부
    }
}
