package bwt.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;

import bwt.yfbhj.Callback;

public class QRCodeCheckUtils {
    private Context context;
    private Callback callback;
    private Handler mHandler;
    public QRCodeCheckUtils(Context context, Callback callback){
        this.context = context;
        this.callback = callback;
    }

    public void check(final String url){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = downloadImage(url);
                Result result;
                if(bitmap == null){
                    if(url.contains("url")) {
                        String newUrl = url.split("url=")[1];
                        callback.onCheckFinish(newUrl);
                    }
                }else{
                    result = handleQRCodeFormBitmap(bitmap);
                    if(result == null){
                        callback.onNoQrcodeHandler();
                    }else{
                        callback.onCheckFinish(result.toString());
                    }
                }
            }
        }).start();
    }

    public Bitmap downloadImage(String source){
        try{
            URL url = new URL(source);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            if(conn.getResponseCode() == 200){
                InputStream inputStream = conn.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                //saveMyBitmap(bitmap,"code");
                return bitmap;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Result handleQRCodeFormBitmap(Bitmap bitmap){
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType,String>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
        hints.put(DecodeHintType.TRY_HARDER,Boolean.TRUE.toString());
        int[] intArray = new int[bitmap.getWidth()*bitmap.getHeight()];
        bitmap.getPixels(intArray,0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());
        RGBLuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(),bitmap.getHeight(),intArray);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader2= new QRCodeReader();
        Result result = null;
        try {
            Log.i("bitmap",bitmap.toString());
            result = reader2.decode(bitmap1,hints);
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
            callback.onNotFoundException();
        }

        return result;
    }

    public void saveMyBitmap(Bitmap mBitmap, String bitName)  {
        File file;
        file= new File( Environment.getExternalStorageDirectory()+"/"+bitName + ".jpg");
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
