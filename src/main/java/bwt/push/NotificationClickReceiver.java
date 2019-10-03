package bwt.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import bwt.yfbhj.MainActivity;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static bwt.push.MyReceiver.KEY_SEGMENT_URL;

public class NotificationClickReceiver extends BroadcastReceiver {
    public static final String ACTION_OPEN = "bwt.push.NotificationClickReceiver.ACTION_OPEN";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("ClickReceiver", "JPush : OPEN NOTI!");
//        Log.i("onNetIntent()", "JPush: 前景？" + isAppForeground(context));
        String segmentUrl = "";

        Intent newIntent = new Intent(context, MainActivity.class);
        newIntent
                .addFlags(FLAG_ACTIVITY_NEW_TASK);
//                .addFlags(FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);

        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            segmentUrl = bundle.getString(KEY_SEGMENT_URL);
            if (segmentUrl != null && !"".equals(segmentUrl)) {
                Log.i("ClickReceiver", "JPush : segmentUrl = " + segmentUrl);
                newIntent.putExtra(KEY_SEGMENT_URL, "/" + segmentUrl.trim());
            }
        }
        context.startActivity(newIntent);
    }


//    private boolean isAppForeground(Context context) {
//        boolean isForground = false;
//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
//            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
//                //前台程序
//                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
//                    for (String pkgName : processInfo.pkgList) {
//                        if (pkgName.equals(context.getPackageName())) {
//                            isForground = true;
//                        }
//                    }
//                }
//            }
//        } else {
//            //@deprecated As of {@link android.os.Build.VERSION_CODES#LOLLIPOP}, 从Android5.0开始不能使用getRunningTask函数
//            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
//            ComponentName componentInfo = taskInfo.get(0).topActivity;
//            if (componentInfo.getPackageName().equals(context.getPackageName())) {
//                isForground = true;
//            }
//        }
//
//        return isForground;
//    }


}
