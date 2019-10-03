package bwt.utils;

public class SystemUtil {
    //取得手機的版本號
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    //取得手機型號
    public static String getSystemModel() {
        return android.os.Build.MODEL;

    }
    //取得手機廠商
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }

}
