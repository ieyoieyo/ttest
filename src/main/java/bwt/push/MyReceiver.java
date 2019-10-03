package bwt.push;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Patterns;

import org.json.JSONObject;


import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import bwt.yfbhj.MainActivity;
import bwt.yfbhj.R;
import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = MyReceiver.class.getSimpleName();
    private static final String KEY_TARGET_ACTIVITY = "targetActivity";
    private static final String KEY_IMG = "img";
    private static final String KEY_TITLE = "title";
    private static final String KEY_SUBJECT = "subject";
    public static final String KEY_SEGMENT_URL = "segmentUrl";

    private final String TARGET_ACTIVITY_MAIN = "MainActivity";

    private NotificationManager nm;
    private static final String NOTI_CHANNEL_ID = "id_for_push";
    private static final String NOTI_CHANNEL_NAME = "Normal";

    public static String sitePath = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (null == nm) {
            nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            Log.w(TAG, "Intent's Bundle is NULL!");
            return;
        }
        StringBuilder sb = new StringBuilder("\n");
        for (String key : bundle.keySet()) { //extras is the Bundle containing info
            Object value = bundle.get(key); //get the current object
            sb.append(key).append(": ").append(value).append("\n"); //add the key-value pair to the
        }
        Log.d(TAG, "onReceive - " + intent.getAction() + ", Extras: " + sb.toString());

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            Log.d(TAG, "JPush 用户注册成功");
            Log.i(TAG, "sitePath from MainActivity is : " + sitePath);
            if (sitePath != null && (!"".equals(sitePath))) {
                Set<String> tagSet = new HashSet<>();
                tagSet.add("sitePath_" + sitePath);
                JPushInterface.setTags(context, 1, tagSet);
            }

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "接受到推送下来的自定义消息");

            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            String title = "";
            String subject = "";
            String imgUrl = "";
            String segmentUrl = "";
            try {
                JSONObject extrasJson = new JSONObject(extras);
                title = extrasJson.optString(KEY_TITLE);
                subject = extrasJson.optString(KEY_SUBJECT);
                imgUrl = extrasJson.optString(KEY_IMG);
                segmentUrl = extrasJson.optString(KEY_SEGMENT_URL);
            } catch (Exception e) {
                Log.w(TAG, "Unexpected: extras is not a valid json", e);
                return;
            }
            //如果"img"沒給值，一律秀文字型的通知；如果"img"有值，再檢查是否是合法URL，否的話秀文字型通知，是的話會進行「下載」動作
            if ("".equals(imgUrl)) {
                sendTextNoti(context, title, subject, segmentUrl);
            } else {
                if (Patterns.WEB_URL.matcher(imgUrl).matches()) {
                    downloadImg(context, title, subject, imgUrl, segmentUrl);
                } else{
                    Log.w(TAG, "Image URL is invalid!");
                    sendTextNoti(context, title, subject, segmentUrl);
                }
            }


        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "接受到推送下来的通知");

            receivingNotification(context,bundle);

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "用户点击打开了通知");

            openNotification(context,bundle);

        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            //当前 JPush 服务的连接状态, 不是指 Android 系统的网络连接状态
            boolean connected = bundle.getBoolean(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            Log.d(TAG, "当前 JPush 服务的连接状态 : " + connected);

        } else {
            Log.d(TAG, "Unhandled intent - " + intent.getAction());
        }
    }

    private void downloadImg(final Context context, final String title, final String subject, String imgUrl, final String segmentUrl) {

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(15000, TimeUnit.MILLISECONDS).readTimeout(15000, TimeUnit.MILLISECONDS);
        // .sslSocketFactory()和.hostnameVerifier()這兩行沒加的話，會無法處理https的URL，或在憑證有問題的URL會無法存取
        builder.sslSocketFactory(SSLSocketClient.getSSLSocketFactory());
        builder.hostnameVerifier(SSLSocketClient.getHostnameVerifier());
        OkHttpClient mOkHttpClient = builder.build();

        Request request = new Request.Builder()
                .url(imgUrl)
                .build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.w(TAG, "download image onFailure: " + e.getLocalizedMessage());

                sendTextNoti(context, title, subject, segmentUrl);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() != 200) {
                    Log.w(TAG, "download image error, code = " + response.code() + ", " + response.message());
                    sendTextNoti(context, title, subject, segmentUrl);

                } else {
                    Log.i(TAG, "download image of Notification success!");

                    InputStream inputStream = response.body().byteStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                    sendBigPictureNoti(context, title, subject, bitmap, segmentUrl);
                }
            }
        });
    }

    private void sendTextNoti(Context context, String title, String subject, String segmentUrl) {

        /*Android 8.0以上，"隱式"(implicit)的廣播不支援靜態註冊(寫在AndroidManifest.xml)，靜態註冊的BroadcastReceiver無法接受隱式廣播。
        讓 Intent指定組件(Component)使其成為"顯式"(explicit)，用它發送的廣播就會是 顯式的廣播，8.0以上才可收得到。*/
        Intent notifyIntent = new Intent(context, NotificationClickReceiver.class);
        Bundle bundle1 = new Bundle();
        bundle1.putString(KEY_SEGMENT_URL, segmentUrl);
        notifyIntent.putExtras(bundle1);
        notifyIntent.setAction(NotificationClickReceiver.ACTION_OPEN);
        PendingIntent notifyPendingIntent =
                PendingIntent.getBroadcast(context, new Random().nextInt(), notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NOTI_CHANNEL_ID,
                    NOTI_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            if (nm != null)
                nm.createNotificationChannel(channel);
        }
        Notification notification = new NotificationCompat.Builder(context, NOTI_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(subject)
                .setSmallIcon(R.mipmap.ic_notify)
                .setColor(ContextCompat.getColor(context, R.color.colorNotifyIcon))
//                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .setBigContentTitle(title)
                        .bigText(subject)
                )
                .setContentIntent(notifyPendingIntent)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .build();
        if (nm != null) {
            nm.notify(new Random().nextInt(), notification);
        }
    }

    private void sendBigPictureNoti(final Context context, String title, String subject, final Bitmap bitmap, String segmentUrl) {

        Intent notifyIntent = new Intent(context, NotificationClickReceiver.class);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_SEGMENT_URL, segmentUrl);
        notifyIntent.putExtras(bundle);
        notifyIntent.setAction(NotificationClickReceiver.ACTION_OPEN);
//        notifyIntent.putExtra(KEY_SEGMENT_URL, segmentUrl);
//        notifyIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
//        notifyIntent.setFlags(FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
        PendingIntent notifyPendingIntent =
                PendingIntent.getBroadcast(context, new Random().nextInt(), notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        PendingIntent notifyPendingIntent =
//                PendingIntent.getActivity(context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NOTI_CHANNEL_ID,
                    NOTI_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            if (nm != null)
                nm.createNotificationChannel(channel);
        }
        Notification notification = new NotificationCompat.Builder(context, NOTI_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(subject)
                .setSmallIcon(R.mipmap.ic_notify)
                .setColor(ContextCompat.getColor(context, R.color.colorNotifyIcon))
//                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setStyle(new NotificationCompat.BigPictureStyle()
//                        .setBigContentTitle("BigContentTitle")
//                        .setSummaryText("SummaryText")
//                        .bigLargeIcon(BitmapFactory.decodeResource(context.getResources(), android.R.drawable.stat_notify_missed_call))
                        .bigPicture(bitmap))
                .setContentIntent(notifyPendingIntent)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .build();
        if (nm != null) {
            nm.notify(new Random().nextInt(), notification);
        }
    }



    private void receivingNotification(Context context, Bundle bundle){
        String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
        Log.d(TAG, " title : " + title);
        String message = bundle.getString(JPushInterface.EXTRA_ALERT);
        Log.d(TAG, "message : " + message);
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        Log.d(TAG, "extras : " + extras);
    }

    private void openNotification(Context context, Bundle bundle){
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        String targetActivity = "";
        try {
            JSONObject extrasJson = new JSONObject(extras);
            targetActivity = extrasJson.optString(KEY_TARGET_ACTIVITY);
        } catch (Exception e) {
            Log.w(TAG, "Unexpected: extras is not a valid json", e);
            return;
        }
        if (TARGET_ACTIVITY_MAIN.equals(targetActivity)) {
            Intent mIntent = new Intent(context, MainActivity.class);
            mIntent.putExtras(bundle);
            mIntent.setFlags(FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mIntent);
        }
//        else if (TYPE_ANOTHER.equals(targetActivity)){
//            Intent mIntent = new Intent(context, AnotherActivity.class);
//            mIntent.putExtras(bundle);
//            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(mIntent);
//        }
    }

    private static class SSLSocketClient {

        //获取这个SSLSocketFactory
        private static SSLSocketFactory getSSLSocketFactory() {
            try {
                SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, getTrustManager(), new SecureRandom());
                return sslContext.getSocketFactory();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        //获取TrustManager
        private static TrustManager[] getTrustManager() {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[]{};
                        }
                    }
            };
            return trustAllCerts;
        }

        //获取HostnameVerifier
        private static HostnameVerifier getHostnameVerifier() {
            return new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            };
        }
    }


}
