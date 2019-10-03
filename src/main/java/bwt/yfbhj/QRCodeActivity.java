package bwt.yfbhj;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import bwt.utils.AppInstalledUtils;
import bwt.utils.DialogUtils;
import im.delight.android.webview.AdvancedWebView;

/**
 * Created by JAVA04 on 2017/11/2.
 */

public class QRCodeActivity extends Activity implements AdvancedWebView.Listener{
    private AdvancedWebView mWebView;
    private String url;
    private Handler mHandler;
    private AlertDialog alertDialog = null;

    public final static int SHOW_INSTALL_QQ = 13;
    public final static int SHOW_INSTALL_WEIXIN = 14;
    public final static int SHOW_CAN_NOT_TRANS = 15;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrcode_view);
        mWebView = (AdvancedWebView) findViewById(R.id.qrcodeView);
        this.url = getIntent().getStringExtra("url");
//        url = url.replace("%3a",":");
//        url = url.replace("%2f","/");
        try {
            url = URLDecoder.decode(url,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SHOW_INSTALL_QQ:
                        showQQInstallDialog();
                        break;
                    case SHOW_INSTALL_WEIXIN:
                        showWeixinInstallDialog();
                        break;
                    case SHOW_CAN_NOT_TRANS:
                        String message = (String) msg.obj;
                        showCanNotTrans(message);
                        break;
                }
            }
        };

        initAlert();
        initWebView();
        initWebViewClient();
        if(url.startsWith("weixin"))
            isAppInstalled(url, AppInstalledUtils.WEIXIN);
        else if(url.startsWith("mqqapi"))
            isAppInstalled(url, AppInstalledUtils.QQ);
        else
            mWebView.loadUrl(String.valueOf(Uri.parse(url)));
    }

    public void initWebView(){
        WebSettings settings = mWebView.getSettings();
        mWebView.requestFocusFromTouch();//支持获取手势焦点
        settings.setJavaScriptEnabled(true);//支持JS
        settings.setDomStorageEnabled(true);
        //设置适应屏幕
        settings.setUseWideViewPort(true);//将图片调整到合适webView的大小
        settings.setLoadWithOverviewMode(true);//缩放至屏幕的大小
        //支持缩放
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);//设置内置的缩放控件
        settings.setDisplayZoomControls(false); //隐藏原生的缩放控件
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        if (Build.VERSION.SDK_INT >=19)
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        else
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        settings.supportMultipleWindows();//多窗口
        settings.setSupportMultipleWindows(false);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);//支持通过js打开新窗口
        settings.setBlockNetworkImage(false);
        settings.setBlockNetworkLoads(false);
        //设置缓存模式
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setAppCacheEnabled(true);
        //settings.setAppCacheMaxSize(50*1024*1024);
        //String cacheDirPath = getFilesDir().getAbsolutePath()+APP_CACAHE_DIRNAME;
        //settings.setAppCachePath(this.getApplicationContext().getDir("cache", Context.MODE_PRIVATE).getPath());//开启Application Caches缓存目录
        //settings.setAppCachePath(mWebView.getContext().getCacheDir().getAbsolutePath());
        settings.setAllowFileAccess(true);//设置可访问文件
        settings.setAllowFileAccessFromFileURLs(false);
        settings.setAllowUniversalAccessFromFileURLs(false);
        settings.setNeedInitialFocus(true);//当webview调用requestFocus时为webview设置节点
        //支持自动加载图片
        settings.setLoadsImagesAutomatically(true);
        if (Build.VERSION.SDK_INT >= 21) {
            //适配5.0不允许http和https混合使用情况
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT >= 19) {
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT < 19) {
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        settings.setDefaultTextEncodingName("UTF-8");//设置编码格式
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setTextZoom(100);
    }

    private void initWebViewClient() {
        mWebView.setWebViewClient(new WebViewClient() {
            String currentPage="";
            //页面开始加载时
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            //页面完成加载时
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            //是否在WebView内加载新页面
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                mWebView.loadUrl(request.toString());
                return true;
            }

            //网络错误时回调的方法
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view,errorCode,description,failingUrl);
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public void onLoadResource(WebView view,String url){
                super.onLoadResource(view,url);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                return super.shouldInterceptRequest(view,url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.startsWith("mqqapi")) {
                    isAppInstalled(url, AppInstalledUtils.QQ);
                    return true;
                }
                if(url.contains("scheme=weixin") || url.startsWith("weixin")) {
                    isAppInstalled(url, AppInstalledUtils.WEIXIN);
                    return true;
                }
                return super.shouldOverrideUrlLoading(view,url);
            }
        });
    }

    private void isAppInstalled(String url, String pkgName) {
        //Log.i("url",url);
        if(pkgName.equals(AppInstalledUtils.QQ)) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                this.finish();
            }catch (ActivityNotFoundException e){
                mHandler.sendEmptyMessage(SHOW_INSTALL_QQ);
            }
        }else if(pkgName.equals(AppInstalledUtils.WEIXIN)) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label",url);
            clipboard.setPrimaryClip(clip);
            Message msg;
            msg = mHandler.obtainMessage(SHOW_CAN_NOT_TRANS,url);
            mHandler.sendMessage(msg);
/*
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                this.finish();
            } catch (ActivityNotFoundException e) {
                mHandler.sendEmptyMessage(SHOW_INSTALL_WEIXIN);
            }
*/
        }
    }

    private void initAlert(){
        alertDialog = DialogUtils.getCustomSingleButtonDialog(QRCodeActivity.this, getResources().getString(R.string.tip_title), getResources().getString(R.string.tip_content_default_connect_time_out1),
                getResources().getString(R.string.button_reconnect),
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });

    }

    private void showQQInstallDialog(){
        if(!alertDialog.isShowing()) {
            alertDialog = DialogUtils.getCustomSingleButtonDialog(QRCodeActivity.this, getResources().getString(R.string.tip_title), getResources().getString(R.string.tip_content_qq_install),
                    getResources().getString(R.string.button_ok),
                    new Button.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });
            alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    finish();
                }
            });
            alertDialog.show();
        }
    }

    private void showWeixinInstallDialog(){
        if(!alertDialog.isShowing()) {
            alertDialog = DialogUtils.getCustomSingleButtonDialog(QRCodeActivity.this, getResources().getString(R.string.tip_title), getResources().getString(R.string.tip_content_weixin_install),
                    getResources().getString(R.string.button_ok),
                    new Button.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
    }

    private void showCanNotTrans(String msg){
        if(!alertDialog.isShowing()) {
            String message = msg + getResources().getString(R.string.tip_can_not_trans);
            alertDialog = DialogUtils.getCustomSingleButtonDialog(QRCodeActivity.this, getResources().getString(R.string.tip_title), message,
                    getResources().getString(R.string.button_ok),
                    new Button.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });
            alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    finish();
                }
            });
            alertDialog.show();
        }
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {

    }

    @Override
    public void onPageFinished(String url) {

    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {

    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {

    }

    @Override
    public void onExternalPageRequest(String url) {

    }
}
