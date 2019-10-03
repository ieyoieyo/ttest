package bwt.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.List;


public class AppInstalledUtils {
    public static final String QQ = "com.tencent.mobileqq";
    public static final String WEIXIN = "com.tencent.mm";
    public static final String ALIPAY = "alipay.trade.wap.pay";
    public static final String UPPAY = "com.unionpay";
    public static final String TAOBAO = "com.taobao.taobao";

    public static boolean isAppInstalled(Context context, String pkgName) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals(pkgName)) {
                    return true;
                }
            }
        }
        return false;
    }
}
