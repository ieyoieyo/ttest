package bwt.utils;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.webkit.URLUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import bwt.yfbhj.R;

public class DownloadUtils extends BroadcastReceiver{
    private DownloadManager mDownloadManager;
    private Context context;
    private String fileName = "";
    private Long fileId;

    public DownloadManager getmDownloadManager() {
        return mDownloadManager;
    }

    public void setmDownloadManager(DownloadManager mDownloadManager) {
        this.mDownloadManager = mDownloadManager;
    }

    public DownloadUtils(Context context){
        this.context = context;
        mDownloadManager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    public void startDownload(String url){
        startDownload(url,null);
    }

    public void startDownload(String url, String filePath) {
        //fix oppo can not update
        File downloadDir = new File(context.getExternalFilesDir("Download"),"");
        if (downloadDir.isDirectory())
        {
            String[] children = downloadDir.list();
            for (int i = 0; i < children.length; i++)
            {
                new File(downloadDir, children[i]).delete();
            }
        }

        String descript = context.getResources().getString(R.string.app_name) + context.getResources().getString(R.string.version) + context.getResources().getString(R.string.download_description);
        context.registerReceiver(this,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        Request request = new Request(Uri.parse(url));
        fileName = URLUtil.guessFileName(url,null,null);
        request.setDescription(descript).setTitle(fileName);
        request.setDestinationInExternalFilesDir(context,Environment.DIRECTORY_DOWNLOADS,fileName);
        fileId = mDownloadManager.enqueue(request);
    }

    public void startDownloadImg(String downloadUrlOfImage){
//        String DIR_NAME = "qrCodeImg";
//        fileName = "qrCodeImg.jpg";
        fileName = URLUtil.guessFileName(downloadUrlOfImage, null, null);

//        File direct = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/" + DIR_NAME + "/");
        File direct = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" );

        if (!direct.exists()) {
            direct.mkdir();
            Log.d("DownloadUtils", "dir created for first time");
        }

        Uri downloadUri = Uri.parse(downloadUrlOfImage);
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle(fileName)
                .setMimeType("image/jpeg")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//                .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, File.separator + DIR_NAME + File.separator + fileName);
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, File.separator + fileName);

        fileId = mDownloadManager.enqueue(request);
    }

    public Bitmap convertDataToImage(String completeImageData){
        try{
            String imageDataBytes = completeImageData.substring(completeImageData.indexOf(",")+1);
            Log.d("convertDataToImage", "imageDataBytes:"+imageDataBytes);
            InputStream stream = new ByteArrayInputStream(Base64.decode(imageDataBytes.getBytes(), Base64.DEFAULT));
            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            return bitmap;
        }
        catch (Exception e) {
            return null;
        }
    }

    public void startSaveBitmap(Bitmap mBitmap)  {
//        String DIR_NAME = "qrCodeImg";
        fileName = "qrCodeImg.jpg";
//        String path = Environment.getExternalStorageDirectory() + "/";
//        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/";
        File dir = new File(path);
        if (!dir.exists()) { dir.mkdir(); }

        File file = new File(path+fileName);
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
            //fOut = new FileOutputStream(path+fileName);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fOut);
        } catch (Exception e) {
            Log.e("startSaveBitmap", e.toString());
        } finally {
            try {
                if (fOut != null) {
                    fOut.flush();
                    fOut.close();
                }
            } catch (IOException e) {
                Log.e("startSaveBitmap", e.toString());
            }
            //儲存完qrCode圖，用MediaScannerConnection來讓系統重新掃描圖檔，總是在相簿軟體中
            MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()}, null, null);
            sendNotification(file);
        }
    }

    private void sendNotification(File file) {
        String msg = context.getResources().getString(R.string.tip_save_qrcode_image);
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            //主要就在下面3行
            /* getUriForFile(Context context, String authority, File file):此处的authority需要和manifest里面保持一致。
            photoURI打印结果：content://cn.lovexiaoai.myapp.fileprovider/camera_photos/Pictures/Screenshots/testImg.png 。
            这里的camera_photos:对应filepaths.xml中的name*/
            //Uri photoURI = FileProvider.getUriForFile(context, context.getPackageName()+".fileprovider", file);
            Log.d("sendNotification", context.getPackageName()+".fileprovider");
            /* 这句要记得写：这是申请权限，添加FLAG_GRANT_READ_URI_PERMISSION。
            如果关联了源码，点开FileProvider的getUriForFile()看看，注释就写着需要添加权限。*/
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //intent.setDataAndType(photoURI, "image/*");
            //因為Jenkins自動包版時，無法識別photoURI，故直接在setDataAndType給值
            //但發現會造成自動包版錯誤，故再調整由MBA這邊做修正
            intent.setDataAndType(FileProvider.getUriForFile(context, context.getPackageName()+".fileprovider", file), "image/*");

            PendingIntent contentIntent = PendingIntent.getActivity(context,123, intent,0);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            String channelId = "channelid";
            if(Build.VERSION.SDK_INT > 25){
                NotificationChannel channel = new NotificationChannel(
                        channelId,
                        "channelnews",
                        NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("save image");
                channel.enableLights(true);
                channel.enableVibration(true);
                notificationManager.createNotificationChannel(channel);
            }

            Bitmap licon = BitmapFactory.decodeResource(context.getResources(), android.R.drawable.stat_sys_download_done);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(android.R.drawable.stat_sys_download_done)
                    .setLargeIcon(licon)
                    .setContentTitle(context.getResources().getString(R.string.tip_save_qrcode_image_tilte))
                    .setContentText(msg)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true)
                    .setChannelId(channelId); //for Android 8.0

            notificationManager.notify(0, builder.build());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //install
        context.unregisterReceiver(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //Uri contentUri = FileProvider.getUriForFile(context, context.getResources().getString(R.string.download_tag), queryFileById(fileId));
            //Uri contentUri = FileProvider.getUriForFile(context, "bwt.yfbhj.fileprovider", queryFileById(fileId));
            //避免之後手動包版疏忽，下載PackageName改直接取值
            Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName()+".fileprovider", queryFileById(fileId));
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            i.setDataAndType(contentUri, "application/vnd.android.package-archive");
            context.startActivity(i);
        } else {
            File apkFile = queryFileById(fileId);
            Log.i("acg_test",Uri.fromFile(apkFile).toString());
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }

        ((Activity)context).finish();
    }
    private File queryFileById(Long id){
        File targetApkFile = null;
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(fileId);
        query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
        Cursor cur = mDownloadManager.query(query);
        if(cur != null) {
            if(cur.moveToFirst()) {
                String uriString = cur.getString(cur.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                if(!TextUtils.isEmpty(uriString))
                    targetApkFile = new File(Uri.parse(uriString).getPath());
            }
            cur.close();
        }
        return targetApkFile;
    }
}
