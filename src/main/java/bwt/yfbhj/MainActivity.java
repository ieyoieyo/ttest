package bwt.yfbhj;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.google.zxing.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.net.URLDecoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import bwt.push.MyReceiver;
import bwt.utils.AES;
import bwt.utils.AppInstalledUtils;
import bwt.utils.CheckVersionuUtils;
import bwt.utils.DialogUtils;
import bwt.utils.DownloadUtils;
import bwt.utils.QRCodeCheckUtils;
import bwt.utils.SystemUtil;
import bwt.yfbhj.adapter.RightMenuAdapter;
import bwt.yfbhj.itemVo.RightMenuItemVo;
import cn.jpush.android.api.JPushInterface;
import im.delight.android.webview.AdvancedWebView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.support.v4.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
import static android.support.v4.widget.DrawerLayout.LOCK_MODE_LOCKED_OPEN;
import static android.support.v4.widget.DrawerLayout.LOCK_MODE_UNLOCKED;

public class MainActivity extends DialogActivity implements View.OnClickListener, AdvancedWebView.Listener,AdapterView.OnItemClickListener {
    //private WebView mWebView;
    private AdvancedWebView mWebView;
    private AdvancedWebView mRollingWebView;
    //private WebView mWebViewHidden;
    private long firstTime=0;//记录第一次按返回键的时间
    private RelativeLayout rlayout;
    private OkHttpClient okHttpClient;
    public static String requestUrl = "";
    public static ArrayList<String> requestUrlList = new ArrayList<String>();
    public String apkUrl="";//每家app不同需要，需要各別修改
    public static final String key = "G6MRA3KD5R972KK3";
    private String baseUrl="";
    private String webVersion="";
    private static final String APP_CACAHE_DIRNAME = "/webcache";
    private Context context;
    private AlertDialog apkUpdateDialog = null;
    private AlertDialog alertDialog = null;
    //loading 畫面
    private ImageView startImage;
    private Handler mHandler;
    public final static int RELOAD = 0;
    public final static int CLOSE_LAUNCH = 1;
    public final static int CLOSE_ROLLING = 2;
    public final static int NOTICE_IOS_LINK = 3;
    public final static int SHOW_TIMEOUT_ALERT = 4;
    public final static int RUN_WEBVIEW = 7;
    public final static int SHOW_DEFAULT_DOMAIN_TIMEOUT = 8;
    public final static int SHOW_DEFAULT_WEBVIEW_ERROR = 9;
    public final static int CLOSE_ALERT = 10;
    public final static int LOAD_LOCAL_STORAGE = 11; //更新LocalStorage的值
    public final static int SAVE_LOCAL_STORAGE = 12; //更新SharedPref的值
    public final static int SHOW_INSTALL_QQ = 13;
    public final static int SHOW_INSTALL_WEIXIN = 14;
    public final static int GO_BACK = 15;
    public final static int SHOW_CAN_NOT_DECODE_QRCODE = 16;
    public final static int SHOW_QRCODE_CONTENT = 17;
    public final static int RUN_QRCODE_STEP2 = 18;
    public final static int SHOW_INSTALL_ALIPAY = 19;
    public final static int SHOW_INSTALL_UPPAY = 20;
    public final static int REFRESH_UNLOCK = 21;
    public final static int BACK_TO_APP = 22;
    public final static int BACK_TO_APP_UNLOCK = 23;
    public final static int RELINE = 24;
    public final static int SHOW_INSTALL_TAOBAO = 25;
    public final static long timeoutTime = 1000 * 15;
    public final static long reloadTimeoutTime = 1000 * 5;
    public static final MediaType JSONType = MediaType.parse("application/json; charset=utf-8");
    //ip + serial no
    private TextView ipSerial;
    //alert settings
    private long appStartTime = 0L; //記錄「15秒Timeout倒數」的起始時間
    private String alertIsEnabled = "Y";
    private String routerCheckedIsEnabled = "N";
    private int alertShowTime = 15;
    private String alertContent = "网路连接异常，请点击重新加载";
    private ArrayList<String> urls = new ArrayList<>();
    private int step = 0;
    private JSONArray phaseData = new JSONArray();

    private HashMap<String,Long> startTimeMap = new HashMap<String,Long>();
    private HashMap<String,JSONObject> step1Map = new HashMap<String,JSONObject>();
    private HashMap<String,JSONObject> step2Map = new HashMap<String,JSONObject>();
    private HashMap<String,JSONObject> step3Map = new HashMap<String,JSONObject>();

    private SharedPreferences sharePref;
    private SharedPreferences.Editor editor;
    private String setAllItems;
    String sitePath = "";
    private String phase3ErrorCode = "";

    private ListView rightMenu;
    private RightMenuAdapter rightMenuAdapter;
    private ArrayList<RightMenuItemVo> rightMenuItems = new ArrayList<RightMenuItemVo>();
    private TextView rightMenuVersion;

    private DrawerLayout mDrawerLayout;
    private LinearLayout bottomMenu;
//    private String backUrl = "";
    private String onlinePayFlag = "onlinePay?";
    private String downloadAppUrl = "ppfapp.com/";
//    private String weixinH5Referer = "http://2233007.com";
//    private String pay095Referer = "http://pay.095pay.com/BarCodePay/BarCodeMobile";
//    private String pay3vReferer = "http://payapi.3vpay.net";
    private String[] filterQRcodeUrl = {"uaacc.cn"};
    private String[] offlinePayUrl = {"wechatOffline?", "aliPayOffline?", "qqPayOffline?", "baiduOffline?", "unionpayOffline?", "jdpayOffline?", "cloudpayOffline?"};
    private String downloadQRcodeUrl = "";
    private ArrayList<RefererUrlDO> refererUrlList = new ArrayList<RefererUrlDO>();
    //側滑選單 刷新按下後設為true 開始
    private boolean isReload = false;
    private boolean isRefreshLock = false;
    private boolean isWebviewBackLock = false;
    private boolean isWebviewBack= false;
    //側滑選單 刷新按下後設為true 結束
    private boolean isWebViewFailure = false;
    private boolean showTimeoutDialogFlag = true; // 控制是否跳"重新加載"視窗(AES解密失敗、線路異常 則不跳視窗，Timeout會跳)
    private String webviewRequestDes = "";
    private String segmentUrl;
    private String loginName;
    //計算測線時間用
    private long phase1SpentTime[];
    private int phase1FastLine = 0;
    private long phase2SpentTime[];
    private int phase2FastLine = 0;

    /*
    點擊通知, 是否要導頁。設在「測線」及「WebView load的結果」。
    預設false, 從Launch > 測線 直到WebView load成功後才設true。WebView load fail則設false。
    實際是否要導頁 還綜合了其他的條件判斷。不導頁的狀況有：
    1. 測線時。
    2. 維護頁時。
    3. 第三方頁面時。
    4. WebView load fail時。
    * */
    private boolean redirectPage = false;
    private AlertDialog redirectDialog;

    private static class RefererUrlDO{
        @SerializedName("url")
        String url;
        @SerializedName("referer")
        String referer;
        @SerializedName("paths")
        List<RefererUrlPathDO> paths = new ArrayList<>();
        @SerializedName("chkRedirectUrl")
        boolean chkRedirectUrl = false;
        @SerializedName("setCurrentUrl")
        boolean setCurrentUrl = true;
    }
    private class RefererUrlPathDO{
        @SerializedName("path")
        String path;
        @SerializedName("referer")
        String referer;
    }

    private DisplayMetrics displayMetrics = new DisplayMetrics();
    private float lastTouchDownX, lastTouchDownY;

    //
    private String saveUserGradeJS = "javascript: (function() {\n" +
            "    var localStorage = window.localStorage;\n" +
            "    var userGrade = '';\n" +
            "    var userTags = '';\n" +
            "    if (localStorage.getItem('user.userGrade') !== null) {\n" +
            "        userGrade = localStorage.getItem('user.userGrade');\n" +
            "        if (localStorage.getItem('user.userTags') === null) {\n" +
            "            window.test.saveUserGrade(userGrade, '');\n" +
            "        } else {\n" +
            "            userTags = JSON.parse(localStorage.getItem('user.userTags'));\n" +
            "            window.test.saveUserGrade(userGrade, JSON.stringify(userTags));\n" +
            "        }\n" +
            "    }\n" +
            "})();";

    private String SaveLocalStorageJS = "javascript:(function(){" +
            "var localStorage = window.localStorage;" +
            //只存需要的資料
            "var requestData = {};" +
//            "var keys = '';" +
//            "var datas = '';" +
            "var loginName = '';" +

            "if(localStorage.getItem('user.loginName') !== null){" +
            "loginName=localStorage.getItem('user.loginName');}" +
            "window.test.getLoginName(loginName);" +
/*
            "for(var key in localStorage){" +
            "keys+=key+'%%';" +
            "datas+=localStorage.getItem(key) + '%%';" +
             "}" +
*/

            "for(var key in localStorage){" +
            "if(!key.includes('site.')){" +
            "if(key.indexOf('lotteryPlan')!=0 && !key.includes('lt.lotteryPlanCache')){" +
            "if(key.indexOf('Rebate')!=0 && !key.includes('lt.rebateGameCache')){" +
//            "requestData.setItem(keys, localStorage.getItem(key));" +
            "requestData[key]=localStorage.getItem(key);" +
//            "keys+=key+'%%';" +
//            "datas+=localStorage.getItem(key) + '%%';" +
            "}" + "}" + "}" + "}" +
            "window.test.getRequestData(JSON.stringify(requestData));" +
            "})();" ;
/*
            "if(localStorage.getItem('user.Balance') !== null){" +
            "keys+='user.Balance'+'%%';" +
            "datas+=localStorage.getItem('user.Balance') + '%%';}" +
            "if(localStorage.getItem('user.Id') !== null){" +
            "keys+='user.Id'+'%%';" +
            "datas+=localStorage.getItem('user.Id') + '%%';}" +
            "if(localStorage.getItem('user.ImgPath') !== null){" +
            "keys+='user.ImgPath'+'%%';" +
            "datas+=localStorage.getItem('user.ImgPath') + '%%';}" +
            "if(localStorage.getItem('user.Status') !== null){" +
            "keys+='user.Status'+'%%';" +
            "datas+=localStorage.getItem('user.Status') + '%%';}" +
            "if(localStorage.getItem('user.agentRebates') !== null){" +
            "keys+='user.agentRebates'+'%%';" +
            "datas+=localStorage.getItem('user.agentRebates') + '%%';}" +
            "if(localStorage.getItem('user.bettingStatus') !== null){" +
            "keys+='user.bettingStatus'+'%%';" +
            "datas+=localStorage.getItem('user.bettingStatus') + '%%';}" +
            "if(localStorage.getItem('user.blackStatus') !== null){" +
            "keys+='user.blackStatus'+'%%';" +
            "datas+=localStorage.getItem('user.blackStatus') + '%%';}" +
            "if(localStorage.getItem('user.freezeStatus') !== null){" +
            "keys+='user.freezeStatus'+'%%';" +
            "datas+=localStorage.getItem('user.freezeStatus') + '%%';}" +

            "if(localStorage.getItem('user.loginName') !== null){" +
            "keys+='user.loginName'+'%%';" +
            "datas+=localStorage.getItem('user.loginName') + '%%';}" +

            "if(localStorage.getItem('user.testAccountType') !== null){" +
            "keys+='user.testAccountType'+'%%';" +
            "datas+=localStorage.getItem('user.testAccountType') + '%%';}" +
            "if(localStorage.getItem('user.type') !== null){" +
            "keys+='user.type'+'%%';" +
            "datas+=localStorage.getItem('user.type') + '%%';}" +
            "if(localStorage.getItem('user.userGrade') !== null){" +
            "keys+='user.userGrade'+'%%';" +
            "datas+=localStorage.getItem('user.userGrade') + '%%';}" +
            "if(localStorage.getItem('user.userTitle') !== null){" +
            "keys+='user.userTitle'+'%%';" +
            "datas+=localStorage.getItem('user.userTitle') + '%%';}" +

            "if(localStorage.getItem('user.pwdEncodeType')!== null){"+
            "keys+='user.pwdEncodeType'+'%%';" +
            "datas+=localStorage.getItem('user.pwdEncodeType') + '%%';}" +
*/
//            "window.test.getAll(keys,datas);})()";

    private String clearLocalStorageJS = "javascript:(function(){" +
            "    var localStorage = window.localStorage;" +
            "    localStorage.clear();" +
            "})();";

    private void clearSessionCookie(){
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().startSync();
        CookieManager.getInstance().removeSessionCookie();
    }
    private void setAllItemJS(){
//        String[] keys = sharePref.getString("localStorageKeys","").split("%%");
//        String[] datas = sharePref.getString("localStorageDatas","").split("%%");
        String loginNameKey = "user.loginName";
        String loginNameValue = sharePref.getString("loginName","");
        setAllItems = "javascript:(function(){var localStorage = window.localStorage;";
        Log.i("keyLoginName","localStorage.setItem('" + loginNameKey + "','" + loginNameValue + "');");
        setAllItems += "localStorage.setItem('" + loginNameKey + "','" + loginNameValue + "');";

        String requestData = sharePref.getString("requestData","");
        try {
            JSONObject jsonObject = new JSONObject(requestData);
            //Logger.json(new Gson().toJson(jsonObject));
            Iterator<?> keys = jsonObject.keys();
            while(keys.hasNext()){
                String key  = keys.next().toString();
                if(key.contains(loginNameKey)){
                    continue;
                }
                Log.i("key","localStorage.setItem('" + key + "','" + jsonObject.getString(key) + "');");
                setAllItems += "localStorage.setItem('" + key +  "','" + jsonObject.getString(key) + "');";
            }
        }catch (Throwable t) {
            Log.e("json parsing P2Error", "Could not parse JSON");
        }

//        for(int i = 0 ; i < keys.length ; i++){
//            if(keys[i].length() > 0 && !keys[i].contains("site.")) {
//                if(keys[i].contains(loginNameKey)){
//                    continue;
//                }
//                Log.e("key","localStorage.setItem('" + keys[i] + "','" + datas[i] + "');");
//                setAllItems += "localStorage.setItem('" + keys[i] + "','" + datas[i] + "');";
//            }
//        }

        setAllItems += "})()";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        segmentUrl = getIntent().getStringExtra(MyReceiver.KEY_SEGMENT_URL);

        displayMetrics = getResources().getDisplayMetrics();
        //Logger.addLogAdapter(new AndroidLogAdapter());
        clearSessionCookie();
//        WebStorage storage = WebStorage.getInstance();
//        storage.deleteAllData();
        sharePref = this.getPreferences(Context.MODE_PRIVATE);
        editor = sharePref.edit();

        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

        String[] requestUrls = getResources().getStringArray(R.array.requestUrls);
        requestUrlList = new ArrayList<>(Arrays.asList(requestUrls));
        requestUrl = requestUrlList.get(0);

        if (requestUrl.lastIndexOf("appSitePath=") < requestUrl.lastIndexOf("&")) {
            sitePath = requestUrl.substring(requestUrl.lastIndexOf("appSitePath="), requestUrl.lastIndexOf("&"));
        } else {
            sitePath = requestUrl.substring(requestUrl.lastIndexOf("appSitePath="));
        }
        sitePath = sitePath.replace("appSitePath=", "");
        //值帶給"接受設備註冊成功的廣播"的 MyReceiver，以便在成功後 setTag(設備註冊未成功前 setTag是無效的！)
        MyReceiver.sitePath = sitePath;

        appStartTime = System.currentTimeMillis();

        setAllItemJS();

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case RELOAD:
                        mWebView.reload();
                        isReload = false;
                        break;
                    case CLOSE_LAUNCH:
                        closeLaunchView();
                        break;
                    case CLOSE_ROLLING:
                        closeRollingView();
                        break;
                    case NOTICE_IOS_LINK:
                        pupTip(getResources().getString(R.string.tip_title),getResources().getString(R.string.tip_content_os_not_match));
                        break;
                    case SHOW_TIMEOUT_ALERT:
                        if (!isMaintain && showTimeoutDialogFlag) {
                            showTimeoutDialog();
                        }
                        break;
                    case RUN_WEBVIEW:
//                        baseUrl = "http://m.winvip888.com:8081"; // DEV環境(DEV01、DEV07)
                        setStartTime(baseUrl.toString(),System.currentTimeMillis());
                        String session = sharePref.getString("session","");
                        String[] temps = session.split(" ");
                        for(String temp:temps){
                            if(temp.contains("JSESSIONID")){
                                session = temp;
                                break;
                            }
                        }
                        Log.i("session",session);
                        if(session.length() > 0){
                            CookieManager.getInstance().setAcceptCookie(true);
                            CookieManager.getInstance().setCookie(baseUrl,session);
                        }
                        //editor.putString("oldUrl",baseUrl);
                        //editor.commit();
                        if (segmentUrl != null) {
                            Log.i("runwebview", "有指定目標網址：" + segmentUrl);
                            mWebView.loadUrl(baseUrl + segmentUrl);
//                            segmentUrl = null;
                        } else {
                            mWebView.loadUrl(baseUrl);
                        }
                        break;
                    case SHOW_DEFAULT_DOMAIN_TIMEOUT:
                        if (!isMaintain && showTimeoutDialogFlag) {
                            showDefaultTimeoutDialog();
                        }
                        break;
                    case SHOW_DEFAULT_WEBVIEW_ERROR:
                        if (!isMaintain && showTimeoutDialogFlag) {
                            showDefaultLoadingTimeoutDialog();
                        }
                        break;
                    case CLOSE_ALERT:
                        closeAlert();
                        break;
                    case LOAD_LOCAL_STORAGE:
                        //Log.i("localStorage",setAllItems);
                        setAllItemJS();
                        mWebView.loadUrl(setAllItems);
                        break;
                    case SAVE_LOCAL_STORAGE:
                        if(mWebView.getUrl() != null && mWebView.getUrl().contains(baseUrl)) {
                            mWebView.loadUrl(SaveLocalStorageJS);
                        }
                        break;
                    case SHOW_INSTALL_QQ:
                        showQQInstallDialog();
                        break;
                    case SHOW_INSTALL_WEIXIN:
                        showWeixinInstallDialog();
                        break;
                    case SHOW_INSTALL_ALIPAY:
                        showAlipayInstallDialog();
                        break;
                    case SHOW_INSTALL_UPPAY:
                        showUPpayInstallDialog();
                        break;
                    case SHOW_INSTALL_TAOBAO:
                        showTaobaoInstallDialog();
                        break;
                    case GO_BACK:
                        mWebView.goBack();
                        break;
                    case SHOW_CAN_NOT_DECODE_QRCODE:
                        showCannotDecodeQRCode();
                        break;
                    case SHOW_QRCODE_CONTENT:
                        String message = (String) msg.obj;
                        showQRcodeContent(message);
                        break;
                    case RUN_QRCODE_STEP2:
                        Picture picture = mWebView.capturePicture();
                        Bitmap bitmap = Bitmap.createBitmap(picture.getWidth(),picture.getHeight(),Bitmap.Config.ARGB_8888);
                        Canvas c = new Canvas(bitmap);
                        picture.draw(c);
                        QRCodeCheckUtils qrCodeCheckUtils = new QRCodeCheckUtils(MainActivity.this, new bwt.yfbhj.Callback() {
                            @Override
                            public void onCheckFinish(String url) {
                                onCheckFinishQrcode(url);
                            }
                            @Override
                            public void onNoQrcodeHandler() {}
                            @Override
                            public void onNotFoundException() {}
                        });
                        Result result = qrCodeCheckUtils.handleQRCodeFormBitmap(bitmap);
                        setCheckQrcodeResult(result);
                        break;
                    case REFRESH_UNLOCK:
                        isRefreshLock = false;
                        break;
                    case BACK_TO_APP:
                        goHomePage(true);
                        break;
                    case BACK_TO_APP_UNLOCK:
                        isWebviewBackLock = false;
                        break;
                    case RELINE:
                        reline();
                        break;
                }
            }
        };
        ipSerial = (TextView)findViewById(R.id.ip);

        initAlert();
        startAlertCount();
        initLoadingView();
        initRolling();//初始化WebView
        initWebView();//初始化WebView
        initWebSettings();//初始化WebSettings
        initWebViewClient();//初始化WebViewClient
        //initWebChromeClient();//初始化WebChromeClient

        //設極光推播的 Tags
        setPushTags();

        post();
    }

    private void initAlert(){
        alertDialog = DialogUtils.getCustomSingleButtonDialog(MainActivity.this, getResources().getString(R.string.tip_title), getResources().getString(R.string.tip_content_default_connect_time_out1),
                getResources().getString(R.string.button_reconnect),
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        apkUpdateDialog.dismiss();
                        reload(true);
                    }
                });

    }

    private void closeAlert(){
        mHandler.sendEmptyMessage(CLOSE_ROLLING);
        if(ipSerial.getVisibility() == View.VISIBLE) {
            alertDialog.dismiss();
            ipSerial.setVisibility(View.GONE);
        }
    }

    private void startAlertCount(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(timeoutTime + 2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(!ipBlock)
                    if(alertIsEnabled.equals("Y") &&  step == 1) {
                        mHandler.sendEmptyMessage(SHOW_TIMEOUT_ALERT);
                    }
            }
        }).start();
    }

    private void startStep2AlertCount(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                long sleepTime = System.currentTimeMillis() - appStartTime;
                sleepTime = alertShowTime - sleepTime;
                if(sleepTime > 0){
                    try {
                        Thread.sleep(sleepTime + 2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (!isMaintain) {
                    if (!ipBlock) {
                        if (alertIsEnabled.equals("Y") && (step == 3 || step == 2)) {
                            mHandler.sendEmptyMessage(SHOW_TIMEOUT_ALERT);
                        } else {
//                            mHandler.sendEmptyMessage(SHOW_DEFAULT_DOMAIN_TIMEOUT);
                        }
                    }
                }
            }
        }).start();
    }

    private Thread mStep3Thread;
    private void startStep3AlertCount(){
        mStep3Thread = new Thread(new Runnable() {
            @Override
            public void run() {
                long sleepTime = 5000;
                if(sleepTime > 0){
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if(isReload) {
                    mHandler.sendEmptyMessage(RELOAD);
                }else {
                    mHandler.sendEmptyMessage(RUN_WEBVIEW);
                }
            }
        });
        mStep3Thread.start();
    }

    private void startReloadAlertCount(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(alertIsEnabled.equals("Y")) {
                    try {
                        Thread.sleep(alertShowTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(!ipBlock)
                        if(alertIsEnabled.equals("Y") && (step == 3 || step == 2)) {
                            mHandler.sendEmptyMessage(SHOW_TIMEOUT_ALERT);
                        }else{
                            //mHandler.sendEmptyMessage(SHOW_DEFAULT_DOMAIN_TIMEOUT);
                        }
                }else{
                    try {
                        Thread.sleep(timeoutTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(!ipBlock)
                        if(alertIsEnabled.equals("Y")) {
                            mHandler.sendEmptyMessage(SHOW_TIMEOUT_ALERT);
                        }
                }
            }
        }).start();
    }

    private void initRolling(){
        mRollingWebView = (AdvancedWebView)findViewById(R.id.rolling);
        mRollingWebView.setWebChromeClient(new WebChromeClient());
        mRollingWebView.setWebViewClient(new WebViewClient());
        mRollingWebView.loadUrl("file:///android_asset/rolling.html");
    }

    private void initLoadingView(){
        startImage = (ImageView) findViewById(R.id.start_image);
        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mHandler.sendEmptyMessage(CLOSE_LAUNCH);
            }
        }).start();
    }

    public void closeLaunchView(){
//        startImage.setBackgroundResource(0);
//        startImage.setBackgroundColor(Color.TRANSPARENT);
        startImage.setVisibility(View.GONE);
    }

    public void closeRollingView(){
        mDrawerLayout.setDrawerLockMode(LOCK_MODE_UNLOCKED);
        mRollingWebView.setVisibility(View.GONE);
    }

    private void pupTip(String title,String message){
        apkUpdateDialog = DialogUtils.getCustomSingleButtonDialog(MainActivity.this, title, message,
                getResources().getString(R.string.button_ok),
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        apkUpdateDialog.dismiss();
                    }
                });
        apkUpdateDialog.show();
    }

    private void initIpSerial(){
        String serial = Build.SERIAL;
        String ip = getIpAddressString();
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String clientTime = formatter.format(appStartTime);

        if(step == 1) {
            String phase1error = "";
            for(int i=0 ; i < phase1Status.size(); i++){
                String indexKey = "line"+(i+1);
                String costKey = "line"+ (i+1) + "TimeCost";
//                if (!phase1Status.containsKey(indexKey)) {
//                    phase1Status.put(indexKey, "to");
//                }
                String errorCode = (phase1Status.containsKey(indexKey)) ? phase1Status.get(indexKey) : "to";
                if(phase1Status.containsKey(indexKey))
                    phase1error += "phase" + step + "_" + (i + 1) + "_" +
                        ((Long.valueOf(phase1Status.get(costKey)) > timeoutTime) ? timeoutTime : phase1Status.get(costKey)) +
                        "_" + errorCode + "\n";

            }
            ipSerial.setText(
                    ip + "\n" +
                            clientTime + "\n" +
                            //phaseDataText +
                            phase1error +
                            sitePath + "_" + getString(R.string.version)+ "\n" +
                            Build.MANUFACTURER + " " + Build.MODEL + "\n" +
                            Build.VERSION_CODES.class.getFields()[Build.VERSION.SDK_INT].getName() + " " + Build.VERSION.RELEASE + "\n" +
                            serial
            );
        }else if(step == 3){
            String line = "";
            for (int i = 0; i < urls.size(); i++) {
                if(baseUrl.contains(urls.get(i))){
                    line = String.valueOf(i+1);
                    break;
                }
            }
            Long phase1CostTime = (phase1Status.get("line" + (phase1FastLine) + "TimeCost") != null
                    ? Long.valueOf(phase1Status.get("line" + (phase1FastLine ) + "TimeCost")) : 0);
            Long phase2CostTime = (phase2Status.get("line" + (phase2FastLine) + "TimeCost") != null
                    ?Long.valueOf(phase2Status.get("line" + (phase2FastLine) + "TimeCost")) : 0);
            String context = "";
            context = ip + "\n" +
                    clientTime + "\n" +
                    //phaseDataText +
                    "phase1" + "_" + phase1FastLine + "_" + phase1CostTime + "\n" +
                    "phase2" + "_" + phase2FastLine + "_" + phase2CostTime + "\n" +
                    "phase" + step + "_" + line + "_" +
                    (timeoutTime - phase1CostTime - phase2CostTime) +
//                    phase3ErrorCode + "_" + webviewRequestDes + "\n" +
                    phase3ErrorCode + "\n" +
                    sitePath + "_" + getString(R.string.version)+ "\n" +
                    Build.MANUFACTURER + " " + Build.MODEL + "\n" +
                    Build.VERSION_CODES.class.getFields()[Build.VERSION.SDK_INT].getName() + " " + Build.VERSION.RELEASE + "\n" +
                    serial;
            ipSerial.setText(context);
            webviewRequestDes = "";
        }else if(step == 2){
            String error = "";
            Long phase1CostTime = Long.valueOf(phase1Status.get("line" + (phase1FastLine) + "TimeCost"));
            error += "phase1" + "_" + phase1FastLine + "_" + phase1CostTime + "\n" ;
            for(int i = 0 ; i < urls.size(); i++){
                String costKey = "line"+ (i+1) + "TimeCost";
                if(!phase2Status.containsKey("line"+(i+1))){
                    phase2Status.put("line"+(i+1),"to");
                }
                if(phase2Status.get(costKey)!=null){
                    error += "phase" + step + "_" + (i+1) + "_" +
                            ((Long.valueOf(phase2Status.get(costKey)) > timeoutTime - phase1CostTime)
                                    ? timeoutTime - phase1CostTime : phase2Status.get(costKey)) +
                            "_" + phase2Status.get("line"+(i+1)) + "\n";
                }
            }
            ipSerial.setText(
                    ip + "\n" +
                            clientTime + "\n" +
                            //phaseDataText +
                            error +
                            sitePath + "_" + getString(R.string.version)+ "\n" +
                            Build.MANUFACTURER + " " + Build.MODEL + "\n" +
                            Build.VERSION_CODES.class.getFields()[Build.VERSION.SDK_INT].getName() + " " + Build.VERSION.RELEASE + "\n" +
                            serial
            );
        }
        Log.i("test",ipSerial.getText().toString());
    }

    private void showTimeoutDialog(){
        String alertMessage;
        if(alertContent == null || alertContent.length() <= 0){
            alertMessage = getResources().getString(R.string.tip_content_default_connect_time_out1);
        }else{
            alertMessage = alertContent;
        }
        initIpSerial();
        postlog();
        ipSerial.setVisibility(View.VISIBLE);
        if(!alertDialog.isShowing()) {
            alertDialog = DialogUtils.getCustomSingleButtonDialog(MainActivity.this, getResources().getString(R.string.tip_title), alertMessage,
                    getResources().getString(R.string.button_reconnect),
                    new Button.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(isWebViewFailure) {
                                isWebViewFailure = false;
                                reline();
                            }else {
                                alertDialog.dismiss();
                                ipSerial.setVisibility(View.GONE);
                                reload(true);
                            }
                        }
                    });
            alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    ipSerial.setVisibility(View.GONE);
                }
            });
            alertDialog.show();
        }
    }

    private void showDefaultTimeoutDialog(){
        initIpSerial();
        postlog();
        ipSerial.setVisibility(View.VISIBLE);
        if(!alertDialog.isShowing() && (step == 1 || step == 2)) {
            alertDialog = DialogUtils.getCustomSingleButtonDialog(MainActivity.this, getResources().getString(R.string.tip_title), getResources().getString(R.string.tip_content_default_connect_time_out1),
                    getResources().getString(R.string.button_reconnect),
                    new Button.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                            ipSerial.setVisibility(View.GONE);
                            reload(true);
                        }
                    });
            alertDialog.show();
        }
    }

    private void showDefaultLoadingTimeoutDialog(){
        initIpSerial();
        postlog();
        ipSerial.setVisibility(View.VISIBLE);
        if(!alertDialog.isShowing() && step == 3) {
            alertDialog = DialogUtils.getCustomTwoButtonDialog(MainActivity.this, getResources().getString(R.string.tip_title), getResources().getString(R.string.tip_content_default_connect_time_out1),
                    getResources().getString(R.string.button_cancel),
                    getResources().getString(R.string.button_reconnect),
                    new Button.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            ipSerial.setVisibility(View.GONE);
                            alertDialog.dismiss();
                        }
                    },
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ipSerial.setVisibility(View.GONE);
                        alertDialog.dismiss();
                        reload(true);
                    }
                });
            alertDialog.show();
        }
    }

    private void showQQInstallDialog(){
        if(!alertDialog.isShowing()) {
            ipSerial.setVisibility(View.VISIBLE);
            alertDialog = DialogUtils.getCustomSingleButtonDialog(MainActivity.this, getResources().getString(R.string.tip_title), getResources().getString(R.string.tip_content_qq_install),
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

    private void showWeixinInstallDialog(){
        if(!alertDialog.isShowing()) {
            ipSerial.setVisibility(View.VISIBLE);
            alertDialog = DialogUtils.getCustomSingleButtonDialog(MainActivity.this, getResources().getString(R.string.tip_title), getResources().getString(R.string.tip_content_weixin_install),
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
    private void showAlipayInstallDialog(){
        if(!alertDialog.isShowing()) {
            ipSerial.setVisibility(View.VISIBLE);
            alertDialog = DialogUtils.getCustomSingleButtonDialog(MainActivity.this, getResources().getString(R.string.tip_title), getResources().getString(R.string.tip_content_alipay_install),
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
    private void showUPpayInstallDialog(){
        if(!alertDialog.isShowing()) {
            ipSerial.setVisibility(View.VISIBLE);
            alertDialog = DialogUtils.getCustomSingleButtonDialog(MainActivity.this, getResources().getString(R.string.tip_title), getResources().getString(R.string.tip_content_uppay_install),
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

    private void showTaobaoInstallDialog(){
        if(!alertDialog.isShowing()) {
            ipSerial.setVisibility(View.VISIBLE);
            alertDialog = DialogUtils.getCustomSingleButtonDialog(MainActivity.this, getResources().getString(R.string.tip_title), getResources().getString(R.string.tip_content_taobao_install),
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

    private void showCannotDecodeQRCode(){
        if(!alertDialog.isShowing()) {
            alertDialog = DialogUtils.getCustomSingleButtonDialog(MainActivity.this, getResources().getString(R.string.tip_title), getResources().getString(R.string.tip_can_not_decode_qrcode),
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

    private void showQRcodeContent(String message){
        if(!alertDialog.isShowing()) {
            String msg = getResources().getString(R.string.tip_show_qrcode_content) + message;
            alertDialog = DialogUtils.getCustomSingleButtonDialog(MainActivity.this, getResources().getString(R.string.tip_title), msg,
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
                    mWebView.goBack();
                }
            });
            alertDialog.show();
        }
    }

    private void initWebView() {
        rlayout=(RelativeLayout)findViewById(R.id.rlayout);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        bottomMenu = (LinearLayout) findViewById(R.id.bottomMenu);
//        findViewById(R.id.back).setOnClickListener(this);
//        findViewById(R.id.next).setOnClickListener(this);
        findViewById(R.id.home).setOnClickListener(this);
        findViewById(R.id.refresh).setOnClickListener(this);
//        findViewById(R.id.clear).setOnClickListener(this);

        mWebView = (AdvancedWebView) findViewById(R.id.web);
        rightMenu = (ListView) findViewById(R.id.rightMenu);
        rightMenuVersion = (TextView) findViewById(R.id.rightMenuVersion);
        rightMenuAdapter = new RightMenuAdapter(this);
//        RightMenuItemVo backVo = new RightMenuItemVo(RightMenuItemVo.TYPE_BACK);
//        RightMenuItemVo nextVo = new RightMenuItemVo(RightMenuItemVo.TYPE_NEXT);
        RightMenuItemVo refreshVo = new RightMenuItemVo(RightMenuItemVo.TYPE_REFRESH);
        RightMenuItemVo clearVo = new RightMenuItemVo(RightMenuItemVo.TYPE_CLEAR);
//        rightMenuItems.add(backVo);
//        rightMenuItems.add(nextVo);
        rightMenuItems.add(refreshVo);
        rightMenuItems.add(clearVo);
        rightMenuAdapter.setItems(rightMenuItems);
        rightMenu.setAdapter(rightMenuAdapter);
        rightMenu.setOnItemClickListener(this);

        rightMenuVersion.setText(String.format(getString(R.string.now_version), getString(R.string.version)));

        mDrawerLayout.setDrawerLockMode(LOCK_MODE_LOCKED_CLOSED);
    }

    private void initWebSettings() {
        mWebView.getSettings().setUserAgentString(mWebView.getSettings().getUserAgentString() + " acgapp");
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
/*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
*/
        settings.setLoadWithOverviewMode(true);
        mWebView.setListener(this,this);
        mWebView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(final String url, String userAgent, final String contentDisposition, final String mimetype, long contentLength) {
                Log.d("DownloadUtils", "url:" + url);
                final boolean isDownloadAPK = url.toLowerCase().trim().endsWith(".apk");
                String contentMsg = isDownloadAPK ? getResources().getString(R.string.tip_content_detect_download) : getResources().getString(R.string.tip_content_browser_download);

                apkUpdateDialog = DialogUtils.getCustomTwoButtonDialog(MainActivity.this,
                        getResources().getString(R.string.tip_title),
                        contentMsg,
                        getResources().getString(R.string.button_cancel),
                        getResources().getString(R.string.button_ok),
                        new Button.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                apkUpdateDialog.dismiss();
                            }
                        }, new Button.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (isDownloadAPK) {
                                    //下載apk
                                    DownloadUtils mDownUtils = new DownloadUtils(MainActivity.this);
                                    mDownUtils.startDownload(url.trim()); //避免app下載連結，前後有空白
                                    Button buttonOk = (Button) apkUpdateDialog.findViewById(R.id.button_ok);
                                    Button buttonCancel = (Button) apkUpdateDialog.findViewById(R.id.button_cancel);
                                    TextView message = (TextView) apkUpdateDialog.findViewById(R.id.message);
                                    buttonOk.setVisibility(View.GONE);
                                    buttonCancel.setVisibility(View.GONE);
                                    message.setText(getResources().getString(R.string.tip_content_downloading));
                                } else {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                    startActivity(intent);
                                    apkUpdateDialog.dismiss();

                                    final Handler handler = new Handler();
                                    Runnable runnable = new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent i = new Intent(MainActivity.this, BrowserDownloadActivity.class);
                                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(i);
                                        }
                                    };
                                    handler.postDelayed(runnable, 3000);
                                }
                            }
                        });

                apkUpdateDialog.setCancelable(false);
                apkUpdateDialog.show();
            }
        });

        //長按解析二維碼
        mWebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                WebView.HitTestResult htr = mWebView.getHitTestResult();
                Log.i("onLongClick","HitTestResult.getType: "+htr.getType());
                Log.i("onLongClick","HitTestResult.getUrl: "+mWebView.getUrl());
                Log.i("onLongClick","HitTestResult.getExtra: "+htr.getExtra());
                //if(!mWebView.getUrl().startsWith(baseUrl) || bottomMenu.getVisibility()==View.VISIBLE){
                if(!mWebView.getUrl().startsWith(baseUrl)){
                    return false; //第三方充值頁面，不做QRcode解析跳轉
                }
                if(htr != null && (htr.getType()==WebView.HitTestResult.IMAGE_TYPE || htr.getType()==WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE)) {
                    for (int i = 0; i < offlinePayUrl.length; i++) {
                        if (mWebView.getUrl().startsWith(baseUrl) && mWebView.getUrl().contains(offlinePayUrl[i])) {
                            if (htr.getExtra() != null && !htr.getExtra().equals("")) {
                                //下載QRcode圖片
                                downloadQRcodeUrl = htr.getExtra().trim();
                                confirmPermission(INDEX_DownloadQrcodeImg);
                                return false;  //線下微信、支付寶、QQ掃碼支付照片長按保存
                            }
                        }
                    }

                    //下級開戶-邀請碼"生成二維碼圖片"
                    if (mWebView.getUrl().contains("layerBox?") || mWebView.getUrl().contains("manageIcode")){
                        if (htr.getExtra().trim().startsWith("data:image/")) {
                            //onLongClickQrcode();
                            base64ImageData = htr.getExtra();
                            confirmPermission(INDEX_SaveQrcodeBitmap);
                            return false;
                        }
                    }
                }
//                if(htr != null && (htr.getType()==WebView.HitTestResult.IMAGE_TYPE || htr.getType()==WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE)){
//                    Log.i("step","step1");
//                    QRCodeCheckUtils qrCodeCheckUtils = new QRCodeCheckUtils(MainActivity.this, new bwt.yfbhj.Callback() {
//                        @Override
//                        public void onCheckFinish(String url) {
//                            onCheckFinishQrcode(url);
//                        }
//                        @Override
//                        public void onNoQrcodeHandler() {}
//                        @Override
//                        public void onNotFoundException() {
//                            mHandler.sendEmptyMessage(RUN_QRCODE_STEP2);
//                        }
//                    });
//                    qrCodeCheckUtils.check(htr.getExtra());
//                }
                if(htr != null && htr.getType() == WebView.HitTestResult.UNKNOWN_TYPE){
//                    Log.i("step","step2");
                    //線上自己域名掃碼支付照片長按保存
                    if(mWebView.getUrl().contains("layerBox?") || mWebView.getUrl().contains("manageIcode")){
                        lastTouchDownX = lastTouchDownX / displayMetrics.density;
                        lastTouchDownY = lastTouchDownY / displayMetrics.density;
                        final String js = "javascript:(function(){" +
                                "var data = document.elementFromPoint("+lastTouchDownX+","+lastTouchDownY+").toDataURL();" +
                                "window.test.getTouchData(data);" +
                                "})();" ;
                        mWebView.loadUrl(js);
                    }
//                    Picture picture = mWebView.capturePicture();
//                    bitmap = Bitmap.createBitmap(picture.getWidth(), picture.getHeight(), Bitmap.Config.ARGB_8888);
//                    Canvas c = new Canvas(bitmap);
//                    picture.draw(c);
                    //onLongClickQrcode();
                }
                return false;
            }
        });

        mWebView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // save the X,Y coordinates
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    lastTouchDownX = event.getX();
                    lastTouchDownY = event.getY();

                    //TODO:此改善的方法，會造成點選APP下載時，會出現第三方功能列的錯誤，故先取消修正，再做討論
                    //避免網路速度過慢，加入每次點擊事件，重新判斷第三方Bar顯示
//                    Handler handler = new Handler();
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            String url = mWebView.getUrl();
//                            Log.d("WebViewTouch", url);
//                            if (!url.contains(baseUrl)) {
//                                showBottomMenu();
//                                try { //加入判斷前往的URL符合我們的Host的域名則隱藏第三方功能列
//                                    String domain = new URI(baseUrl).getHost();
//                                    if(url.contains(domain)){
//                                        hideBottomMenu();
//                                    }
//                                }catch (Exception e){
//                                    e.printStackTrace();
//                                }
//                            } else {
//                                hideBottomMenu();
//                            }
//                        }
//                    }, 100);
                }
                // let the touch event pass on to whoever needs it
                return false;
            }
        });

        mWebView.addJavascriptInterface(new testInterface(this.context),"test");
    }

    private Bitmap bitmap;
    private void onLongClickQrcode() {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                Picture picture = mWebView.capturePicture();
//                Bitmap bitmap = Bitmap.createBitmap(picture.getWidth(), picture.getHeight(), Bitmap.Config.ARGB_8888);
//                Canvas c = new Canvas(bitmap);
//                picture.draw(c);
                QRCodeCheckUtils qrCodeCheckUtils = new QRCodeCheckUtils(MainActivity.this, new bwt.yfbhj.Callback() {
                    @Override
                    public void onCheckFinish(String url) {
                        onCheckFinishQrcode(url);
                    }
                    @Override
                    public void onNoQrcodeHandler() {}
                    @Override
                    public void onNotFoundException() {}
                });
                Result result = qrCodeCheckUtils.handleQRCodeFormBitmap(bitmap);
                setCheckQrcodeResult(result);
            }
        });
    }
    private void onCheckFinishQrcode(String url){
//        Intent qrcodePage = new Intent();
//        qrcodePage.setClass(MainActivity.this, QRCodeActivity.class);
//        qrcodePage.putExtra("url", url);
//        startActivity(qrcodePage);
//        mHandler.sendEmptyMessage(GO_BACK);
        try {
            url = URLDecoder.decode(url,"utf-8");
            Log.i("qrCodeUrl", url);
            for (int i = 0; i < filterQRcodeUrl.length; i++) {
                if(url.contains(filterQRcodeUrl[i])){
                    return;
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(url.contains("://")) {
            Intent qrcodePage = new Intent();
            qrcodePage.setClass(MainActivity.this, QRCodeActivity.class);
            qrcodePage.putExtra("url", url);
            startActivity(qrcodePage);
            mHandler.sendEmptyMessage(GO_BACK);
        }else{
            Message msg;
            msg = mHandler.obtainMessage(SHOW_QRCODE_CONTENT,url);
            mHandler.sendMessage(msg);
        }
    }
    private void setCheckQrcodeResult(Result result){
        //Result result = qrCodeCheckUtils.handleQRCodeFormBitmap(bitmap);
        if (result != null) {
            String qrCodeUrl = "";
            try {
                qrCodeUrl = URLDecoder.decode(result.toString(), "utf-8");
                Log.i("qrCodeUrl", qrCodeUrl);
                for (int i = 0; i < filterQRcodeUrl.length; i++) {
                    if(qrCodeUrl.contains(filterQRcodeUrl[i])){
                        return;
                    }
                }
                if (qrCodeUrl.contains("://")) {
                    Intent qrcodePage = new Intent();
                    qrcodePage.setClass(MainActivity.this, QRCodeActivity.class);
                    qrcodePage.putExtra("url", qrCodeUrl);
                    startActivity(qrcodePage);
                    mHandler.sendEmptyMessage(GO_BACK);
                } else {
                    Message msg;
                    msg = mHandler.obtainMessage(SHOW_QRCODE_CONTENT, qrCodeUrl);
                    mHandler.sendMessage(msg);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    private Long startTime, spentTime; //觀察載入時間
    private void initWebViewClient() {
        mWebView.setWebViewClient(new WebViewClient() {
            String currentPage="";
            //页面开始加载时
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.i("WebViewClientTag", "onPageStarted_currentUrl:" + view.getUrl());
                Log.i("WebViewClientTag", "onPageStarted_Url:" + url);
                startTime = System.currentTimeMillis();
//                Log.i("Timer","onPageStarted startTime="+startTime);
                if (!view.getUrl().trim().toLowerCase().endsWith(".apk")) {
//                    String alias = getPackageName().substring(getPackageName().lastIndexOf(".")+1, getPackageName().length());
//                    if(url.indexOf(baseUrl) < 0 && url.indexOf(downloadAppUrl+alias) < 0) {

                    if (!url.contains(baseUrl)) {
                        showBottomMenu();
                        try { //加入判斷前往的URL符合我們的Host的域名則隱藏第三方功能列
                            String domain = new URI(baseUrl).getHost();
                            Log.i("WebViewClientTag", "baseUrl.getHost():" + domain);
                            if(url.contains(domain)){
                                hideBottomMenu();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    } else {
                        hideBottomMenu();

                        if (segmentUrl != null && !isMaintain) {
                            step = 4;
                        }
                    }
                    segmentUrl = null;
                    redirectPage = true;

                }
                super.onPageStarted(view, url, favicon);
            }

            //页面完成加载时
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.i("WebViewClientTag","onPageFinished_Url:"+url);
                if(startTime != null) {
                    spentTime = System.currentTimeMillis() - startTime;
                    Log.i("Timer","onPageFinished spentTime="+spentTime);
                }
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                    CookieManager cookieManager = CookieManager.getInstance();
                    String test = cookieManager.getCookie(url);
                    if(url.contains(baseUrl)) { //避免session被覆寫，而造成掉線
                        if (test != null && test.length() > 0) {
                            if(test.contains("JSESSIONID")) {
                                editor.putString("session", test);
                                editor.commit();
                                //Log.i("cookies","test"+test);
                            }
                        }
                    }
                }
//                Log.i("test",setAllItems);
//                mWebView.loadUrl(setAllItems);
                super.onPageFinished(view, url);
            }

            //是否在WebView内加载新页面
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Log.i("shouldLoading","shouldOverrideUrl:"+request.toString());
                mWebView.loadUrl(request.toString());
                return true;
            }

            //网络错误时回调的方法
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                //Log.i("Error",p2RequestErrorCount.getErrorCode()+"");
                redirectPage = false;

                webviewRequestDes = error.getDescription().toString();
                if(webviewRequestDes.isEmpty()){
                    webviewRequestDes = "null";
                }
                Log.e("onReceivedError","MMMMMMMMMMM");
                phase3ErrorCode = String.valueOf(error.getErrorCode());
                    //phaseData.put(createPhaseData(3, view.getUrl(), -1));
                step3Map.put(view.getUrl(),createPhaseData(3, view.getUrl(), -1));
                //mHandler.sendEmptyMessage(SHOW_TIMEOUT_ALERT);
                isWebViewFailure = true;
                mHandler.sendEmptyMessage(SHOW_TIMEOUT_ALERT);
                super.onReceivedError(view, request, error);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                //.i("Errorold",description);
                redirectPage = false;

                webviewRequestDes = description;
                if(webviewRequestDes.isEmpty()){
                    webviewRequestDes = "null";
                }
                Log.e("onReceivedError","!!!!!!!!!!!!!!");
                phase3ErrorCode = String.valueOf(errorCode);
                    //phaseData.put(createPhaseData(3, view.getUrl(), -1));
                step3Map.put(view.getUrl(),createPhaseData(3, view.getUrl(), -1));
                isWebViewFailure = true;
                mHandler.sendEmptyMessage(SHOW_TIMEOUT_ALERT);
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
                //Log.i("loadResource","onLoadResourceUrl:"+url.toString());
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                Log.i("shouldInterceptRequest","requestUrl:"+url);
                if (Build.VERSION.SDK_INT != Build.VERSION_CODES.KITKAT) {
                    CookieManager cookieManager = CookieManager.getInstance();
                    String test = cookieManager.getCookie(url);
                    if(url.contains(baseUrl)){ //避免session被覆寫，而造成掉線
                        if (test != null && test.length() > 0) {
                            if(test.contains("JSESSIONID")) {
                                Log.i("session",test);
                                editor.putString("session", test);
                                editor.commit();
//                            Log.i("cookies","test"+test);
                            }
                        }

                        // 如果不是維護頁，並且是"打API"的動作，則取 LocalStorage的"loginName"，來更新 SharedPref裡的值 (防掉線)
                        if(!isMaintain && url.contains("_ajax")){
                            mHandler.sendEmptyMessage(SAVE_LOCAL_STORAGE);
                        }
                    }
                }

                //「登入」及「等級頭銜」頁的API 的 Request時，留兩秒等它 Response並寫LocalStorage，
                // 兩秒後才從LocalStorage裡拿"userGrade"的值出來，去設極光通知的Tags及寫SharedPreference
                // PS:登入頁的"結果"。不是在登入頁的時候，而是「按下登入按鈕」才會打此API("/_ajax/login")。
                // PS:等級頭銜頁的API拿到的 userGrade因有可能與"登入"時拿到的不同，故也要在此時重設極光通知的 Tag一次。
                if (url.endsWith("/_ajax/login") || url.endsWith("/_ajax/getPersonalLevel")) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("shouldInterceptRequest", "2 seconds after login API's request, saving SharedPref from LocalStorage!");
                            mWebView.loadUrl(saveUserGradeJS);
                        }
                    }, 2000);
                }

                if(mRollingWebView.getVisibility() == View.VISIBLE) {
                    if (url.contains("_ajax") || url.contains("callback=getIP")) {
                        step = 4;
                        mHandler.sendEmptyMessage(CLOSE_ALERT);
                        mHandler.sendEmptyMessage(LOAD_LOCAL_STORAGE);
                        //postlog();
                        currentPage = url;
                    }
                }
/*
                if(currentPage.length() > 0 && !url.contains(currentPage)){
                    mHandler.sendEmptyMessage(SAVE_LOCAL_STORAGE);
                }
*/
                return super.shouldInterceptRequest(view,url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i("WebViewClientTag","shouldOverrideUrl_currentUrl:"+view.getUrl());
                Log.i("WebViewClientTag","shouldOverrideUrl_Url:"+url);

                if(mRollingWebView.getVisibility() == View.VISIBLE) {
                    if (url.contains("_ajax") || url.contains("callback=getIP")) {
                        step = 4;
                        mHandler.sendEmptyMessage(CLOSE_ALERT);
                        mHandler.sendEmptyMessage(LOAD_LOCAL_STORAGE);
                        //postlog();
                        currentPage = url;
                    }
                }
/*
                if(currentPage.length() > 0 && !url.contains(currentPage)){
                    mHandler.sendEmptyMessage(SAVE_LOCAL_STORAGE);
                }
*/
                if(url.endsWith(".plist")) {
                    mHandler.sendEmptyMessage(NOTICE_IOS_LINK);
                    return true;
                }
                if(url.startsWith("mqqapi")) {
                    isAppInstalled(view, url, AppInstalledUtils.QQ);
                    return true;
                }
                if(url.startsWith("alipay")){
                    isAppInstalled(view, url, AppInstalledUtils.ALIPAY);
                    return true;
                }
                if(url.contains("scheme=weixin") || url.startsWith("weixin://")) {
                    isAppInstalled(view, url, AppInstalledUtils.WEIXIN);
                    return true;
                }
                if(url.startsWith("upwrp")){
                    isAppInstalled(view, url, AppInstalledUtils.UPPAY);
                    return true;
                }
                if(url.startsWith("taobao://") || url.startsWith("itaobao://")) {
                    isAppInstalled(view, url, AppInstalledUtils.TAOBAO);
                    return true;
                }
                //url scheme 未知來源處理
                if(!url.startsWith("http://") && !url.startsWith("https://")){
                    boolean haveUrlScheme = false;
                    try{
                        haveUrlScheme = true;
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    }catch(Exception e){
                        haveUrlScheme = false;
                    }finally {
                        if(haveUrlScheme){
                           return true;
                        }
                    }
                }
                //金流頁面Referer的問題，改統一由refererUrlList做檢查
                for(RefererUrlDO refererUrlDO : refererUrlList){
                    if (url.contains(refererUrlDO.url)) {
                        Log.d("RefererUrl", "setReferer url:" + url);
                        Map<String, String> extraHeaders = new HashMap<String, String>();
                        extraHeaders.put("Referer", refererUrlDO.referer);

                        //取得目前WebView的URL，設置為要連結網頁的Referer
                        if(refererUrlDO.setCurrentUrl){
                            extraHeaders.put("Referer", view.getUrl());
                        }

                        //確認是否檢查chkRedirectUrl，若有則檢查URL中redirect_url的參數，並更新Referer
                        if(refererUrlDO.chkRedirectUrl){
                            try{
                                String decodeUrl = URLDecoder.decode(url, "UTF-8");
                                Log.d("Referer", "decodeUrl:" + decodeUrl);
                                String parameterRedirect = "redirect_url=";
                                if(decodeUrl.contains(parameterRedirect)){
                                    int indexSub1 = decodeUrl.indexOf(parameterRedirect) + parameterRedirect.length();
                                    int indexSub2 = decodeUrl.indexOf("/", indexSub1 + 8);
                                    if(indexSub1>0 && indexSub2>0){
                                        String redirectUrl = decodeUrl.substring(indexSub1, indexSub2);
                                        Log.d("Referer", "redirect_url:" + redirectUrl);
                                        extraHeaders.put("Referer", redirectUrl);
                                    }
                                }
                            }catch (Exception e){
                                Log.e("Referer", "redirect_url: Failed!");
                                e.printStackTrace();
                            }
                        }

                        //比對是否有其他RefererUrlPath的條件，若有則更新Referer
                        if(refererUrlDO.paths!=null && refererUrlDO.paths.size()>0){
                            for (RefererUrlPathDO refererUrlPath : refererUrlDO.paths) {
                                if(url.contains(refererUrlPath.path)){
                                    extraHeaders.put("Referer", refererUrlPath.referer);
                                }
                            }
                        }

                        Log.d("RefererUrl", "setReferer:" + extraHeaders.get("Referer"));

                        //避免頁面自動導向，造成重複跳轉，先載入空白頁後，再重載URL並加入Header
                        view.loadUrl("about:blank");
                        view.stopLoading();
                        view.loadUrl(url, extraHeaders);
                        return true;
                    }
                }

                return super.shouldOverrideUrlLoading(view,url);
            }
        });
    }

    private void initWebChromeClient() {
        mWebView.setWebChromeClient(new WebChromeClient(){});
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mWebView.onActivityResult(requestCode, resultCode, data);
    }

    private void isAppInstalled(WebView view, String url, String pkgName) {
        //Log.i("url",url);
        if(pkgName.equals(AppInstalledUtils.QQ)) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }catch (ActivityNotFoundException e){
                mHandler.sendEmptyMessage(SHOW_INSTALL_QQ);
            }
        }else if(pkgName.equals(AppInstalledUtils.WEIXIN)) {
            try {
                url = url.replace("intent", "weixin");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                mHandler.sendEmptyMessage(SHOW_INSTALL_WEIXIN);
            }
        }else if(pkgName.equals(AppInstalledUtils.ALIPAY)){
            try{
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }catch(ActivityNotFoundException e){
                mHandler.sendEmptyMessage(SHOW_INSTALL_ALIPAY);
            }
        }else if(pkgName.equals(AppInstalledUtils.UPPAY)){
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }catch (ActivityNotFoundException e){
                mHandler.sendEmptyMessage(SHOW_INSTALL_UPPAY);
            }
        }else if(pkgName.equals(AppInstalledUtils.TAOBAO)){
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }catch (ActivityNotFoundException e){
                mHandler.sendEmptyMessage(SHOW_INSTALL_TAOBAO);
            }
        }
    }

    private void reload(boolean isClearWebViewContent){
        showTimeoutDialogFlag = true;
        hasRequest = false;
        p1RequestErrorCount = 0;
        haveUrl = false;
        p2RequestErrorCount = 0;
        //startReloadAlertCount();
        appStartTime = System.currentTimeMillis();

        if(isClearWebViewContent){
            // 避免phase3在loadWebView時，點選重新加載有機會顯示LOG錯誤畫面
            mWebView.loadUrl("about:blank");
            mWebView.clearHistory();
        }

        if(step == 1){
            startAlertCount();
            isReload = false;
            post();
        }else if(step == 2){
            isReload = false;
            runCheckUrl();
        }else{
            if(!isReload){
                startStep2AlertCount();
            }
            new CheckUrl().checkUrl(baseUrl, command, true);
            //mWebView.reload();
        }
    }

    private void reline(){
        mRollingWebView.setVisibility(View.VISIBLE);
        bottomMenu.setVisibility(View.GONE);
        appStartTime = System.currentTimeMillis();
        step = 1;
        alertIsEnabled = "Y";
        hasRequest = false;
        p1RequestErrorCount = 0;
        step1Map = new HashMap<String,JSONObject>();
        urls = new ArrayList<>();

        ipBlockCount = 0;
        ipBlock = false;

        p2RequestErrorCount = 0;
        step2Map = new HashMap<String,JSONObject>();
        phase2Status = new HashMap<String,String>();

        haveUrl = false;
        step3Map = new HashMap<String,JSONObject>();
        isMaintain = false;
        isReload = false;
        isWebviewBack = false;

//        if(mWebView.getUrl()!=null && mWebView.getUrl().contains(baseUrl)) {
//            mWebView.loadUrl(SaveLocalStorageJS);
//        }

        mWebView.loadUrl("about:blank");
        mWebView.clearHistory();
        mWebView.clearCache(true);
        mWebView.clearFormData();
        clearWebViewCache();

//        clearSessionCookie();
        setAllItemJS();

        showTimeoutDialogFlag = true;
        startAlertCount();
        post();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
//            case R.id.clear:
//                clearWebViewCache();
//                WebStorage storage = WebStorage.getInstance();
//                storage.deleteAllData();
//                editor.clear();
//                CookieManager.getInstance().removeAllCookie();
//                //增加提示成功讯息
//                apkUpdateDialog = DialogUtils.getCustomSingleButtonDialog(MainActivity.this, getResources().getString(R.string.tip_short_title), getResources().getString(R.string.tip_content_clear_sucess),
//                        getResources().getString(R.string.button_ok),
//                        new Button.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                apkUpdateDialog.dismiss();
//                            }
//                        });
//                apkUpdateDialog.setCancelable(false);
//                apkUpdateDialog.show();
//
//                break;
            case R.id.refresh:
                //reload();
                // 只會需要做Phase3的reload
                mWebView.reload();
                break;
//            case R.id.back:
//                WebBackForwardList wbfl = mWebView.copyBackForwardList();
////                if(wbfl.getCurrentIndex() > 1)
////                    mWebView.goBack();
////                goOnlinePay(wbfl);
//                goBaseUrl(wbfl);
//                break;
//            case R.id.next:
//                mWebView.goForward();
//                break;
            case R.id.home:
                if(!isWebviewBackLock) {
                    isWebviewBackLock = true;
                    isWebviewBack = true;
                    new CheckUrl().checkUrl(baseUrl, command, true);
                    //reline();
                    final Handler handler = new Handler();
                    handler.postDelayed(webViewBackTimer, 5000);
                    //goHomePage();
                }
                break;
        }
    }

    private final Runnable webViewBackTimer = new Runnable() {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(BACK_TO_APP_UNLOCK);
        }
    };


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            goBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void post(){
        redirectPage = false;
        mDrawerLayout.setDrawerLockMode(LOCK_MODE_LOCKED_CLOSED);
        step = 1;
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(timeoutTime, TimeUnit.MILLISECONDS).readTimeout(timeoutTime, TimeUnit.MILLISECONDS);
        builder.sslSocketFactory(SSLSocketClient.getSSLSocketFactory());
        builder.hostnameVerifier(SSLSocketClient.getHostnameVerifier());
        okHttpClient = builder.build();
        phase1SpentTime = new long[requestUrlList.size()];
        for(String requestUrl : requestUrlList){
            final String requestUrlItem = requestUrl;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    RequestBody requestBody = RequestBody.create(JSONType,createRequestJSON(System.currentTimeMillis()));
                    Request request = new Request.Builder()
                            .url(requestUrlItem)
                            .post(requestBody)
                            .build();
                    setStartTime(requestUrlItem,System.currentTimeMillis());
                    okHttpClient.newCall(request).enqueue(callback);
                }
            }).start();
        }
/*
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestBody requestBody = RequestBody.create(JSONType,createRequestJSON(System.currentTimeMillis()));
                Request request = new Request.Builder()
                        .url(requestUrl2)
                        .post(requestBody)
                        .build();
                setStartTime(requestUrl2,System.currentTimeMillis());
                okHttpClient.newCall(request).enqueue(callback);
            }
        }).start();
*/
    }

    private synchronized void setStartTime(String url,long time){
        startTimeMap.put(url,time);
    }

    private synchronized Long getStartTime(String url){
        return startTimeMap.get(url);
    }

    private HashMap<String,String> phase1Status = new HashMap<String,String>();
    private void saveStatus(String url, String errorCode, boolean isResponse){
        for (int i=0; i<requestUrlList.size(); i++) {
            if(url.equals(requestUrlList.get(i))) {
                phase1Status.put("line"+(i+1), errorCode);
                phase1Status.put("line" + (i+1) + "TimeCost", String.valueOf(System.currentTimeMillis() - appStartTime));
                if(isResponse && phase1FastLine == 0)
                    phase1FastLine = i + 1;
                break;
            }
        }
    }

    //请求后的回调方法
    private Callback callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            //phaseOneCostTime(call.request().url().toString(), false);
            Log.i(call.request().url().toString(), "Phase 1 onFailure" + call.request().url().toString());
//            saveStatus(call.request().url().toString(), e.getMessage(), false);
            saveStatus(call.request().url().toString(), "", false);
            //phaseData.put(createPhaseData(1,call.request().url().toString(),-1));
            showTimeoutDialogFlag = true;
//            TGMessagePost("response onFailure", e.getMessage(), "", call.request().url().toString());
            step1Map.put(call.request().url().toString(),createPhaseData(1, call.request().url().toString(), -1));

            if(e.toString().contains("SocketTimeoutException")) { //原始TimeOut事件排除，由startAlertCount計算
                Log.d(call.request().url().toString(), "Phase 1 onFailure: SocketTimeoutException!");
            } else {
//                setResult(e.getMessage(), false, call.request().url().toString());
//                setResult(new StringBuffer(e.getMessage()), false, call.request().url().toString());
                setResult(new StringBuffer(""), false, call.request().url().toString());
            }
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            //phaseOneCostTime(call.request().url().toString(), true);
            saveStatus(call.request().url().toString(), String.valueOf(response.code()), true);
            Long useTime = System.currentTimeMillis() - getStartTime(call.request().url().toString());
            //phaseData.put(createPhaseData(1,call.request().url().toString(),useTime));
            step1Map.put(call.request().url().toString(),createPhaseData(1, call.request().url().toString(), useTime));
            final StringBuffer sb = new StringBuffer(response.body().string());
//            final StringBuffer sb = new StringBuffer(response.body().string());
            if(response.code() == 200) {
//                setResult(response.body().string(), true, call.request().url().toString());
//                final String msg = response.body().string();
                final String url = call.request().url().toString();

                if(checkMaintain(sb, url)){
//                    setResult(sb, true, url);
                    return;
                }

                // AES解密
                String decoded = null;
                if (sb.length() >= 64) {
                    String timeStampMD5 = sb.substring(sb.length() - 32, sb.length());
//                            Log.i("Phase 1 TimeStampMD5", timeStampMD5);
                            try {
                                StringBuffer aes = new StringBuffer(sb.substring(0, sb.length() - 32));
//                                String aes = sb.substring(0, sb.length() - 32);
                                decoded = AES.decryptAES(aes, timeStampMD5, sitePath);
                            } catch (InvalidKeyException e) {
                                e.printStackTrace();
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            } catch (NoSuchPaddingException e) {
                                e.printStackTrace();
                            } catch (InvalidAlgorithmParameterException e) {
                                e.printStackTrace();
                            } catch (IllegalBlockSizeException e) {
                                e.printStackTrace();
                            } catch (BadPaddingException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            } catch (IllegalArgumentException e){
                                e.printStackTrace();
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                }
                if (decoded == null) {
                    // 解密不過，發TG、跳視窗
//                   showTimeoutDialogFlag = false;
                    TGMessagePost("200", "數據解析異常，疑似被劫持", sb.toString(), url);
                    requestErrorHandle();
//                    return;
                } else {
                    setResult(new StringBuffer(decoded), true, url);
                }

            } else {
//                showTimeoutDialogFlag = false;
                TGMessagePost(response.code() + "", "線路異常", sb.toString(), call.request().url().toString());
                requestErrorHandle();
            }
        }
    };

    private boolean hasRequest = false;
    //显示请求返回的结果
    private synchronized void setResult(final StringBuffer msg, final boolean success, final String url) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (success) {
                    if (!hasRequest) {
                            hasRequest = true;
                            requestUrl = url;

//                            if(checkMaintain(msg, requestUrl)){
//                                return;
//                            }

                            Log.e("phase1 success url pass", requestUrl);

                            try {
//                                JSONObject json = new JSONObject(decoded);
                                JSONObject json = new JSONObject(msg.toString());
                                if (!json.isNull("androidVersion")) {
                                    webVersion = json.optString("androidVersion");
                                    apkUrl = json.optString("androidPath").trim(); //避免app下載連結，前後有空白

                                    if(apkUrl.length() > 1){ //檢查apkUrl是否為空，否則就不做版本檢查
                                        if(!apkUrl.toLowerCase().startsWith("http://") && !apkUrl.toLowerCase().startsWith("https://")){
                                            apkUrl = "http://" + apkUrl;
                                        }

                                        //版本判斷
                                        if (CheckVersionuUtils.isNeedUpdate(MainActivity.this, webVersion)) {
                                            apkUpdateDialog = DialogUtils.getCustomSingleButtonDialog(MainActivity.this, getResources().getString(R.string.tip_title), getResources().getString(R.string.tip_content_detect_new_version),
                                                    getResources().getString(R.string.button_ok),
                                                    new Button.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            //下載apk
                                                            DownloadUtils mDownUtils = new DownloadUtils(MainActivity.this);
                                                            mDownUtils.startDownload(apkUrl);
                                                            TextView message = (TextView) apkUpdateDialog.findViewById(R.id.message);
                                                            v.setVisibility(View.GONE);
                                                            message.setText(getResources().getString(R.string.tip_content_downloading));
                                                        }
                                                    });
                                            apkUpdateDialog.setCancelable(false);
                                            apkUpdateDialog.show();
                                        }
                                    }
                                }

                                alertIsEnabled = json.optString("isCallAttention").trim(); //如果不是Y，就不show Dialog
                                routerCheckedIsEnabled = json.optString("appRouteCheck").trim(); //如果不是Y，就不做routerChecked
                                alertShowTime = json.optInt("appTimeOut")>0 ? json.getInt("appTimeOut") * 1000 : 15000;
                                alertContent = json.optString("appAttentionMsg").trim().length()>1 ? json.optString("appAttentionMsg") : alertContent;
                                JSONArray urlArray = json.getJSONArray("urls");

                                if (routerCheckedIsEnabled.equals("Y")) {
                                    step = 2;
                                    command = json.optString("testAppSecurityUrl").trim().length()>1 ? json.optString("testAppSecurityUrl").trim() : command;
                                    startStep2AlertCount();
                                    for (int i = 0; i < urlArray.length(); i++) {
                                        if (i == 0) {
                                            urls = new ArrayList<>();
                                        }
                                        urls.add(urlArray.getString(i).trim().toLowerCase()); //避免從API取得urls前後有空格，及轉小寫避免判斷錯誤
                                        final String finalI = urlArray.getString(i).trim().toLowerCase(); //避免從API取得urls前後有空格，及轉小寫避免判斷錯誤
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                new CheckUrl().checkUrl(finalI, command, false);
                                            }
                                        }).start();
                                    }
                                } else {
                                    if (urlArray.getString(0).trim().toLowerCase().startsWith("http")) {
                                        baseUrl = urlArray.getString(0).trim().toLowerCase(); //避免從API取得urls前後有空格，及轉小寫避免判斷錯誤
                                    } else {
                                        baseUrl = "http://" + urlArray.getString(0).trim().toLowerCase(); //避免從API取得urls前後有空格，及轉小寫避免判斷錯誤
                                    }
                                    runWebView(baseUrl, System.currentTimeMillis());
                                }

//                            if(!json.isNull("weixinH5Referer") && !json.getString("weixinH5Referer").trim().equalsIgnoreCase("")){
//                                weixinH5Referer = json.getString("weixinH5Referer").trim();
//                            }
//                            if(!json.isNull("pay095Referer") && !json.getString("pay095Referer").trim().equalsIgnoreCase("")){
//                                pay095Referer = json.getString("pay095Referer").trim();
//                            }

//                            //目前會拿local的資料和API資料作比對，之後等API穩定後，會取消這個步驟
//                            HashMap<String, String> refererMap = new HashMap<String, String>();
//                            JSONObject refererObject = new JSONObject(getResources().getString(R.string.jsonString_referer_obj));
//                            Iterator<String> refererKeys = refererObject.keys();
//                            while (refererKeys.hasNext()) {
//                                String key = refererKeys.next();
////                                Log.d("RefererUrl", "local url:" + key + "  referer:" + refererObject.get(key));
//                                refererMap.put(key, refererObject.getString(key));
//                            }
//                            //取得API的refererUrls做後續檢查
//                            JSONArray apiRefererArray = json.optJSONArray("appRefererUrls");
//                            //apiRefererArray = new JSONArray(getResources().getString(R.string.jsonString_referer_test)); //測試資料
//                            if(apiRefererArray!=null && apiRefererArray.length() > 0){
//                                for (int i = 0; i < apiRefererArray.length(); i++) {
//                                    JSONObject obj = apiRefererArray.optJSONObject(i);
//                                    if(obj!=null){
//                                        String url = apiRefererArray.getJSONObject(i).optString("url").trim();
//                                        String referer = apiRefererArray.getJSONObject(i).optString("referer").trim();
//                                        if(!obj.isNull("url") && !url.equalsIgnoreCase("") && !obj.isNull("referer") && !referer.equalsIgnoreCase("")) {
//                                            refererMap.put(url, referer);
////                                            Log.d("RefererUrl", "api url :" + url + "  referer :" + referer);
//                                        }
//                                    }
//                                }
////                                Log.d("RefererUrl", "apiRefererArray:" + apiRefererArray.toString());
//                            }
//                            //加入refererUrlList做後續檢查
//                            Set set = refererMap.entrySet();
//                            Iterator iterator = set.iterator();
//                            while(iterator.hasNext()) {
//                                Map.Entry mentry = (Map.Entry)iterator.next();
//                                RefererUrlDO mRefererUrlDO = new RefererUrlDO();
//                                mRefererUrlDO.url = mentry.getKey().toString();
//                                mRefererUrlDO.referer = mentry.getValue().toString();
//                                refererUrlList.add(mRefererUrlDO);
//                                Log.d("RefererUrl", "add url:" + mRefererUrlDO.url);
//                                Log.d("RefererUrl", "add referer:"+mRefererUrlDO.referer);
//                            }

                                //refererUrlList處理解析
                                Gson gson = new GsonBuilder().create();
                                RefererUrlDO[] myRefererUrlDOList = gson.fromJson(getResources().getString(R.string.jsonString_referer_url), RefererUrlDO[].class);
                                RefererUrlDO[] apiRefererUrlDOList = gson.fromJson(json.optString("appRefererUrls"), RefererUrlDO[].class);
                                if (json.isNull("appRefererUrls") || json.optString("appRefererUrls").equals("") || apiRefererUrlDOList.length < 1) {
                                    refererUrlList = new ArrayList<RefererUrlDO>(Arrays.asList(myRefererUrlDOList));
                                } else {
                                    refererUrlList = new ArrayList<RefererUrlDO>(Arrays.asList(apiRefererUrlDOList));
                                    //檢查refererUrlList處理前
                                    Log.d("RefererUrl", "apiRefererUrls:" + gson.toJson(refererUrlList));

                                    //refererUrlList拿local的資料和API資料作比對
                                    if (myRefererUrlDOList != null && myRefererUrlDOList.length > 0) {
                                        for (RefererUrlDO myRefererUrlDO : myRefererUrlDOList) {
                                            //比對local資料，是否和api拿到資料不一致，若有則進行下一步比較
                                            if (!refererUrlList.contains(myRefererUrlDO)) {
                                                for (int i = 0; i < refererUrlList.size(); i++) {
                                                    //比對url資料，是否和api拿到資料不一致，若有則進行下一步比較
                                                    if (myRefererUrlDO.url.equalsIgnoreCase(refererUrlList.get(i).url)) {
                                                        //若local的paths資料為空，則結束迴圈
                                                        if (myRefererUrlDO.paths != null && myRefererUrlDO.paths.size() > 0) {
                                                            //若api的paths資料為空，則直接加入local的paths資料
                                                            if (refererUrlList.get(i).paths != null && refererUrlList.get(i).paths.size() > 0) {
                                                                for (RefererUrlPathDO myRefererUrlPathDO : myRefererUrlDO.paths) {
                                                                    //比對paths資料，是否和api拿到資料不一致，若有則進行下一步比較
                                                                    if (!refererUrlList.get(i).paths.contains(myRefererUrlPathDO)) {
                                                                        for (int j = 0; j < refererUrlList.get(i).paths.size(); j++) {
                                                                            //比對path資料，是否和api拿到資料不一致，若有則進行下一步比較
                                                                            if (myRefererUrlPathDO.path.equalsIgnoreCase(refererUrlList.get(i).paths.get(j).path)) {
                                                                                break;
                                                                            }
                                                                            //若跑完迴圈仍找不到對應的path，則新增一筆
                                                                            if (j == refererUrlList.get(i).paths.size() - 1) {
                                                                                refererUrlList.get(i).paths.add(myRefererUrlPathDO);
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            } else {
                                                                refererUrlList.get(i).paths = myRefererUrlDO.paths;
                                                            }
                                                        }
                                                        break;
                                                    }
                                                    //若跑完迴圈仍找不到對應的url，則新增一筆
                                                    if (i == refererUrlList.size() - 1) {
                                                        refererUrlList.add(myRefererUrlDO);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                //檢查refererUrlList處理後
                                Log.d("RefererUrl", "refererUrlList:" + gson.toJson(refererUrlList));


                            } catch (Exception e) {
                                //e.printStackTrace();
//                            TGMessagePost("200", "解密失敗，疑似被挾持", msg, url);
                                requestErrorHandle();
                                hasRequest = false;
                            }
                    }
                } else {
                    //showToast(StartActivity.this, "网络繁忙");
                    requestErrorHandle();
//                    hasRequest = false;
                }

            }
        });
    }

    private int p1RequestErrorCount = 0;
    protected synchronized void requestErrorHandle(){
        p1RequestErrorCount++;
        Log.i("phase1ErrorTimes", p1RequestErrorCount + "");
        // Fail的這條如果是"最後"一條，才會跳視窗
        if (step == 1 && p1RequestErrorCount >= requestUrlList.size()) {
            if(alertIsEnabled.equals("Y")) {
                mHandler.sendEmptyMessage(SHOW_DEFAULT_DOMAIN_TIMEOUT);
            }

        }

//        p1RequestErrorCount++;
//        if(p1RequestErrorCount >= requestUrlList.size()){
//            mHandler.sendEmptyMessage(SHOW_DEFAULT_DOMAIN_TIMEOUT);
//        }
    }

    private String command = "/app/testAppDomainSecurity";
    private void runCheckUrl(){
        startStep2AlertCount();
        for (String url : urls) {
            final String finalI = url;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    new CheckUrl().checkUrl(finalI, command, false);
                }
            }).start();
        }
    }

    //重写旋转时方法，不销毁activity
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)){
                mHandler.sendEmptyMessage(SAVE_LOCAL_STORAGE);
            }
        }
    };


    /*
    * 用於「點擊推播通知 -> 開啟指定頁面」功能。
    * PS: app處於前台或後台時，「從最近開啟的app清單」裡點擊開啟的 不會到此方法，但「點擊app圖示」則會！
    * */
    @Override
    protected void onNewIntent(Intent intent) {
        Log.i("onNewIntent", "receiver: onNewIntent()");

        if (mWebView != null && mWebView.getUrl() != null && mWebView.getUrl().contains(baseUrl) &&
                redirectPage && !isMaintain) {

            segmentUrl = intent.getStringExtra(MyReceiver.KEY_SEGMENT_URL);
            if (segmentUrl != null && !"".equals(segmentUrl)) {
                Log.i("onNewIntent", "receiver: segmentUrl = " + segmentUrl);

                if (redirectDialog != null && redirectDialog.isShowing()) {
                    redirectDialog.dismiss();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(getString(R.string.tip_title))
                        .setMessage("是否直接前往公告讯息, 查看完整内容?")
                        .setPositiveButton("前往", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.i("onNewIntent", "確認前往segmentUrl = " + segmentUrl);
                                mRollingWebView.setVisibility(View.VISIBLE);
                                step = 2;
                                new CheckUrl().checkUrl(baseUrl, command, true);
                            }
                        })
                        .setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                segmentUrl = null;
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(false);
                redirectDialog = builder.create();
                redirectDialog.show();

            } else {
                Log.i("onNewIntent", "receiver: segmentUrl無值或空字串");
            }
        } else {
            Log.i("onNewIntent", "receiver: 不符合「導頁」條件");
        }

        super.onNewIntent(intent);
    }

    @Override
    protected void onResume(){
        super.onResume();
        IntentFilter filter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(receiver, filter);

        // 從 LocalStorage取要設極光Tag的 userTags，存到 SharedPre, 然後註冊極光Tag
        if (mWebView.getUrl() != null && mWebView.getUrl().contains(baseUrl)) {
            mWebView.loadUrl(saveUserGradeJS);
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        unregisterReceiver(receiver);
        //Log.i("test","onPause");
        if(mWebView.getUrl()!=null && mWebView.getUrl().contains(baseUrl)) {
            mWebView.loadUrl(SaveLocalStorageJS);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Log.i("test","onStop");
    }

    @Override
    protected void onDestroy() {
//        if(mWebView.getUrl().contains(baseUrl)) {
//            mWebView.loadUrl(SaveLocalStorageJS);
//        }
        rlayout.removeView(mWebView);
        clearWebViewCache();
        if(mWebView!=null){
            mWebView.clearHistory();
            mWebView.clearCache(true);
            mWebView.clearFormData();
            mWebView.loadUrl("about:blank");
            mWebView.pauseTimers();
            mWebView.destroy();
            mWebView=null;
        }
        //設極光推播的 Tags
        setPushTags();

        super.onDestroy();
        System.exit(0);
    }

    /**
     * 清除WebView缓存
     */
    public void clearWebViewCache(){
        //清理Webview缓存数据库
        try {
            deleteDatabase("webview.db");
            deleteDatabase("webviewCache.db");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //WebView 缓存文件
        File appCacheDir = new File(getFilesDir().getAbsolutePath()+APP_CACAHE_DIRNAME);
        File webviewCacheDir = new File(getCacheDir().getAbsolutePath()+"/webviewCache");
        //删除webview 缓存目录
        if(webviewCacheDir.exists()){
            deleteFile(webviewCacheDir);
        }
        //删除webview 缓存 缓存目录
        if(appCacheDir.exists()){
            deleteFile(appCacheDir);
        }
    }

    /**
     * 递归删除 文件/文件夹
     *
     * @param file
     */
    public void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            }
            file.delete();
        }
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
        //Log.d("AdvancedWebView", "onPageStarted url:"+url);
    }

    @Override
    public void onPageFinished(String url) {
        //Log.d("AdvancedWebView", "onPageFinished url:"+url);
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
        //Log.d("AdvancedWebView", "onPageError url:"+failingUrl);
    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {
        //Log.d("AdvancedWebView", "onDownloadRequested url:"+url);
    }

    @Override
    public void onExternalPageRequest(String url) {
        //Log.d("AdvancedWebView", "onExternalPageRequest url:"+url);
    }

    boolean haveUrl = false;
    protected synchronized void runWebView(String url, long time){
        if(!haveUrl){
            step = 3;
            baseUrl = url;
            haveUrl = true;
 //           mHandler.sendEmptyMessage(CLOSE_ROLLING);
            mHandler.sendEmptyMessage(RUN_WEBVIEW);
        }
    }

    int p2RequestErrorCount = 0;
    protected synchronized void checkUrlErrorHandle(boolean isPhase3CheckUrl){
        p2RequestErrorCount++;
        Log.i("phase2ErrorTimes", p2RequestErrorCount + "");
        if (isPhase3CheckUrl) {
            if (alertIsEnabled.equals("Y") && !isReload && !isWebviewBack) {
                mHandler.sendEmptyMessage(SHOW_TIMEOUT_ALERT);
            }
            if(isWebviewBack){
                isWebviewBack = false;
                mHandler.sendEmptyMessage(RELINE);
            }
            if(isReload) isReload = false;

        } else if (step >= 2 && p2RequestErrorCount >= urls.size()) {
            if (alertIsEnabled.equals("Y")) {
                mHandler.sendEmptyMessage(SHOW_TIMEOUT_ALERT);
            }
            //else
            //mHandler.sendEmptyMessage(SHOW_DEFAULT_DOMAIN_TIMEOUT);
        }
    }

    private String createRequestJSON(long time){
        JSONObject request = new JSONObject();
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS");
        String clientTime = formatter.format(appStartTime);
//        WifiManager wm = (WifiManager)this.getApplicationContext().getSystemService(WIFI_SERVICE);
//        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        String ip = getIpAddressString();
        try {
            request.put("clientTime",clientTime);
            request.put("deviceSerialNo",Build.SERIAL);
            request.put("clientIp",ip);
            request.put("deviceModel",Build.MODEL);
            request.put("sitePath",sitePath);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        Log.i("request",request.toString());
        return request.toString();
    }


    public JSONObject createPhaseData(int phase, String domain,long timeout){
        JSONObject phaseData = new JSONObject();
        try {
            phaseData.put("phase",phase);
            phaseData.put("domain",domain);
            phaseData.put("timeout",timeout);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return phaseData;
    }
    public void postlog(){
//        Log.i("postLog","postLog");
//        WifiManager wm = (WifiManager)this.getApplicationContext().getSystemService(WIFI_SERVICE);
//        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        String ip = getIpAddressString();
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String clientTime = formatter.format(appStartTime);
        String sitePath = requestUrl.substring(requestUrl.lastIndexOf("=")+1);
        phaseData = new JSONArray();
        for(JSONObject item:step1Map.values()){
            phaseData.put(item);
        }
        for(JSONObject item:step2Map.values()){
            phaseData.put(item);
        }
        for(JSONObject item:step3Map.values()){
            phaseData.put(item);
        }
        JSONObject log = new JSONObject();
        try {
            log.put("clientIp",ip);
            log.put("clientTime",clientTime);
            log.put("sitePath",sitePath);
            log.put("deviceModel",Build.MANUFACTURER + " " +Build.MODEL);
            log.put("deviceOsVersion",Build.VERSION_CODES.class.getFields()[Build.VERSION.SDK_INT].getName()+ " " + Build.VERSION.RELEASE);
            log.put("deviceSerialNo",Build.SERIAL);
            log.put("phaseData",phaseData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String domain = requestUrl.substring(0, requestUrl.indexOf("/", requestUrl.indexOf("//")+2));

        Callback logCallback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("logCallback","fail");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i("logCallback","success");
            }
        };
//        Log.i("test",log.toString());
        RequestBody requestBody = RequestBody.create(JSONType,log.toString());
        Request request = new Request.Builder()
                .url(domain + "/app/timeoutRecord")
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(logCallback);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mDrawerLayout.closeDrawers();
        switch (position){
            case RightMenuItemVo.TYPE_CLEAR:
                //點擊時把列表鎖在關閉狀態(避免連點造成不會關閉)
                mDrawerLayout.setDrawerLockMode(LOCK_MODE_LOCKED_CLOSED);
                clearWebViewCache();
                WebStorage storage = WebStorage.getInstance();
                storage.deleteAllData();
                editor.clear();
                CookieManager.getInstance().removeAllCookie();
                //增加提示成功讯息
                if(apkUpdateDialog == null || !apkUpdateDialog.isShowing()) {
                    apkUpdateDialog = DialogUtils.getCustomSingleButtonDialog(MainActivity.this, getResources().getString(R.string.tip_short_title), getResources().getString(R.string.tip_content_clear_sucess),
                            getResources().getString(R.string.button_ok),
                            new Button.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    apkUpdateDialog.dismiss();
                                }
                            });
                    apkUpdateDialog.setCancelable(false);
                    apkUpdateDialog.show();
                }
                sideMenuUnlock();
                break;

            case RightMenuItemVo.TYPE_REFRESH:
                if(!isRefreshLock) {
                    //點擊時把列表鎖在關閉狀態(避免連點造成不會關閉)
                    mDrawerLayout.setDrawerLockMode(LOCK_MODE_LOCKED_CLOSED);
                    Log.e("refresh", "onClick");
                    Handler handler = new Handler();
                    handler.postDelayed(refreshTimer, 5000);
                    isRefreshLock = true;
                    isReload = true;
                    if(isMaintain){
                        try { //加入維護判斷WebView目前的URL符合我們的Host的域名則重跑測線
                            String domain = new URI(baseUrl).getHost();
                            if(mWebView.getUrl().contains(domain)){
                                reline();
                            }else{ //否則就跑單條測線
                                reload(false);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            //若有問題，就用原方法判斷
                            if(mWebView.getUrl().contains(baseUrl)){
                                reline();
                            }else{ //否則就跑單條測線
                                reload(false);
                            }
                        }
                    }else{
                        reload(false);
                    }
                }
                sideMenuUnlock();
                break;
//            case RightMenuItemVo.TYPE_BACK:
//                goBack(false);
//                break;
//            case RightMenuItemVo.TYPE_NEXT:
//                mWebView.goForward();
//                break;
        }
    }

    private void sideMenuUnlock(){
        //0.5秒後解鎖列表關閉狀態
        Handler handlerUnlockList = new Handler();
        handlerUnlockList.postDelayed(new Runnable() {
            @Override
            public void run() {
                mDrawerLayout.setDrawerLockMode(LOCK_MODE_UNLOCKED);
            }
        }, 500);
    }

    private final Runnable refreshTimer = new Runnable() {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(REFRESH_UNLOCK);
        }
    };

    //設極光推播的 Tags。
    private void setPushTags() {
        Set<String> tagSet = new HashSet<>();
        tagSet.add("sitePath_" + sitePath);
        if (!"".equals(sharePref.getString("pushLoginName", ""))) {
            tagSet.add("userName_" + sharePref.getString("pushLoginName", ""));
        }
        if (!"".equals(sharePref.getString("userGrade", ""))) {
            tagSet.add("userGrade_" + sharePref.getString("userGrade", ""));
        }

        if (!"".equals(sharePref.getString("userTags", ""))) {
            try {
                JSONArray jsonArray = new JSONArray(sharePref.getString("userTags", ""));
                for (int i = 0; i < jsonArray.length(); i++) {
                    tagSet.add(jsonArray.getString(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //第二個參數：用户自定义的操作序列号，同操作结果一起返回，用来标识一次操作的唯一性
        JPushInterface.setTags(this, 1, JPushInterface.filterValidTags(tagSet));
    }


    public class testInterface {
        Context context;
        public testInterface(Context context){
            this.context = context;
        }
        @JavascriptInterface
        public void getAll(String _keys,String _datas){
            //Log.i("test","keep data");
            //Log.i("test",_keys);
            //Log.i("test",_datas);
            editor.putString("localStorageKeys",_keys);
            editor.putString("localStorageDatas",_datas);
            editor.commit();
        }
        /* 這裡代進來的參數，是從 LocalStorage取得的loginName。這邊將其寫入 SharedPref。
        *  每次 shouldInterceptRequest()時，只要不是 isMaintain, 每 Load一個資源檔都會執行這個 Method一次
        * */
        @JavascriptInterface
        public void getLoginName(String loginName){
//            Log.i("loginName : ", ", 從LS取得，要寫入SP的 loginName = " + loginName);
            MainActivity.this.loginName = loginName;
            editor.putString("loginName", loginName).commit();
            if (!"".equals(loginName)) {
                editor.putString("pushLoginName", loginName).commit();
            }
        }
        /* 這裡帶進來的參數，是從 LocalStorage取得的 userGrade。這邊將其寫入 SharedPref 並註冊極光 Tag。
         *  「登入」及「等級頭銜」頁的API Request後的"兩秒"之後，以及 onResume()時，會執行此 Method
         * */
        @JavascriptInterface
        public void saveUserGrade(String userGrade, String userTags){
            Set<String> tags = new HashSet<>();
            tags.add("sitePath_" + sitePath);
            tags.add("userGrade_" + userGrade.trim());
            if (!"".equals(sharePref.getString("pushLoginName", ""))) {
                tags.add("userName_" + sharePref.getString("pushLoginName", ""));
            }

            editor.putString("userGrade", userGrade).commit();

            if (!"null".equalsIgnoreCase(userTags) &&
                    !"".equals(userTags) &&
                    !"[]".equals(userTags)) {

                editor.putString("userTags", userTags).commit();

                try {
                    JSONArray jsonArray = new JSONArray(userTags);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        tags.add(jsonArray.getString(i).trim());
                    }
                } catch (Throwable t) {
                    Log.e("JSON parse fail", "userTags parsed failed. Could not parse JSON");
                }
            }

            // 設極光 Tag。第二個參數：用户自定义的操作序列号，同操作结果一起返回，用来标识一次操作的唯一性
            JPushInterface.setTags(MainActivity.this, 1, JPushInterface.filterValidTags(tags));
        }
        @JavascriptInterface
        public void getRequestData(String requestData){
            //Log.i("requestData", requestData);
            editor.putString("requestData", requestData).commit();
        }
        @JavascriptInterface
        public void getTouchData(String data){
            Log.i("onLongClick", data);
            if (data.trim().startsWith("data:image/")) {
                //onLongClickQrcode();
                base64ImageData = data;
                confirmPermission(INDEX_SaveQrcodeBitmap);

            }else{
                base64ImageData = "";
            }
        }
    }

    int ipBlockCount = 0;
    boolean ipBlock = false;
    private synchronized void checkIPBlock(){
        ipBlockCount++;
        if(ipBlockCount >= urls.size()){
            ipBlock = true;
            mHandler.sendEmptyMessage(CLOSE_ROLLING);
            String url = urls.get(0);
            if(!url.startsWith("http")) {
                url = "http://"+url;
            }
            runWebView(url,System.currentTimeMillis());
        }
    }

    private HashMap<String,String> phase2Status = new HashMap<String,String>();
    public class CheckUrl {
        private long startTime;
        private long endTime;
        private long totalTime;
        private String url;
        private boolean isPhase3CheckUrl = false;

        public void checkUrl(String url, String command, boolean isPhase3CheckUrl){
            redirectPage = false;

            if(isPhase3CheckUrl){
//                startStep3AlertCount();
                this.isPhase3CheckUrl = isPhase3CheckUrl;
                haveUrl = false; // 單條測線，要重設haveUrl
            }

            //url = "http://m.maintain.com"; //IT維修測試用URL
            if(url.startsWith("http")) {
                this.url = url;
            }else{
                this.url = "http://" + url;
            }
            //如果判斷不是Url就不處理
            if(!URLUtil.isValidUrl(this.url))
                return;
            startTime = System.currentTimeMillis();
            phase2SpentTime = new long[urls.size()];
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            if(isReload || isWebviewBack) {
                builder.connectTimeout(reloadTimeoutTime, TimeUnit.MILLISECONDS).readTimeout(reloadTimeoutTime, TimeUnit.MILLISECONDS);
            }else{
//                builder.connectTimeout(timeoutTime, TimeUnit.MILLISECONDS);
                builder.connectTimeout(alertShowTime, TimeUnit.MILLISECONDS).readTimeout(alertShowTime, TimeUnit.MILLISECONDS);
            }
            builder.sslSocketFactory(SSLSocketClient.getSSLSocketFactory());
            builder.hostnameVerifier(SSLSocketClient.getHostnameVerifier());
            OkHttpClient checkUrlHttpClient = builder.build();
            RequestBody requestBody = RequestBody.create(JSONType,createRequestJSON(startTime));
            Request request = new Request.Builder()
                    .url(this.url + command)
//                    .url(url)
                    .post(requestBody)
                    .build();
            setStartTime(request.url().toString(),System.currentTimeMillis());
            checkUrlHttpClient.newCall(request).enqueue(checkUrlCallback);
        }
        //檢查線路的回调方法

//        private void phaseTwoCostTime(String url, boolean isPassed){
//            for(int i = 0; i < urls.size(); i++){
//                if(url.contains(urls.get(i)))
//                    phase2SpentTime[i] = System.currentTimeMillis() - startTime;
//                if(isPassed && phase2FastLine == 0)
//                    phase2FastLine = i + 1;
//                Log.e("phase2SpentTime" +i, phase2SpentTime[i] + "");
//            }
//        }

        private Callback checkUrlCallback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //show ALERT
                //phaseTwoCostTime(call.request().url().toString(), false);
                showTimeoutDialogFlag = true;
//                savePhase2Status(call.request().url().toString(), e.getMessage(), false);
                savePhase2Status(call.request().url().toString(), "", false);
//                TGMessagePost("response onFailure", e.getMessage(), "", call.request().url().toString());
                Log.i(call.request().url().toString(), "Phase 2 onFailure: " + call.request().url().toString());
//                    Log.i(call.request().url().toString(), "fail");
                //phaseData.put(createPhaseData(2,call.request().url().toString(),-1));
                step2Map.put(call.request().url().toString(), createPhaseData(2, call.request().url().toString(), -1));

                if(e.toString().contains("SocketTimeoutException")) { //原始TimeOut事件排除，由startStep2AlertCount計算
                    Log.d(call.request().url().toString(), "Phase 2 onFailure: SocketTimeoutException!");
                    /**
                                        *  側滑選單的重新整理
                                        *  TIMEOUT的訊息框(3個)
                                        *  phase3按下重新載入
                                        *  第三方按下back鍵
                                        *  以上會將isPhase3CheckUrl設為true
                                        */
                    if(isPhase3CheckUrl){
                        checkUrlErrorHandle(isPhase3CheckUrl);
                    }
                } else {
                    checkUrlErrorHandle(isPhase3CheckUrl);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //phaseTwoCostTime(call.request().url().toString(), true);
//                String msg = response.body().string();
                //Log.i(call.request().url().toString(),msg);
                savePhase2Status(call.request().url().toString(), String.valueOf(response.code()), true);
                StringBuffer sb = new StringBuffer(response.body().string());
//                String msg = response.body().string();
                if(response.code() == 200){
                    Long useTime = System.currentTimeMillis() - startTimeMap.get(call.request().url().toString());
                    //phaseData.put(createPhaseData(2,call.request().url().toString(),useTime));
                    step2Map.put(call.request().url().toString(),createPhaseData(2, call.request().url().toString(), useTime));
//                    msg = "{code:success}";
                    if(checkMaintain(sb, url)){
                        isReload = false;
                        isRefreshLock = false;
                        isWebviewBackLock = false;
                        isWebviewBack = false;
                        return;
                    }

                    // AES解密
                    String decoded = null;
                    if (sb.length() >= 64) {
                        String timeStampMD5 = sb.substring(sb.length() - 32, sb.length());
//                        if(isWebviewBack)
//                            timeStampMD5 = 888 + timeStampMD5;
//                        Log.i("aaa__timeStampMD5", timeStampMD5);
                        try {
                            StringBuffer aes = new StringBuffer(sb.substring(0, sb.length() - 32));
//                            String aes = msg.substring(0, msg.length() - 32);
                            decoded = AES.decryptAES(aes, timeStampMD5, sitePath);
                        } catch (InvalidKeyException e) {
                            e.printStackTrace();
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        } catch (NoSuchPaddingException e) {
                            e.printStackTrace();
                        } catch (InvalidAlgorithmParameterException e) {
                            e.printStackTrace();
                        } catch (IllegalBlockSizeException e) {
                            e.printStackTrace();
                        } catch (BadPaddingException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    // 解密不過，發TG、跳視窗
                    if (decoded == null) {
//                            requestErrorHandle();
//                            hasRequest = false;
//                            showTimeoutDialogFlag = false;
                        checkUrlErrorHandle(isPhase3CheckUrl);
                        TGMessagePost(response.code() + "", "數據解析異常，疑似被劫持", sb.toString(), call.request().url().toString());
                        isReload = false;
                        isRefreshLock = false;
                        isWebviewBackLock = false;
                        isWebviewBack = false;
                        return;
                    }


                    try {
                        JSONObject json = new JSONObject(decoded);
//                        JSONObject json = new JSONObject(msg);
//                        if (json.get("code").equals("success") || json.get("code").equals("maintain")) {
                        if (json.get("code").equals("success")) {
//                            if(mStep3Thread != null){
//                                mStep3Thread.interrupt();
//                            }
                            endTime = System.currentTimeMillis();
                            totalTime = endTime - startTime;
                            if(isWebviewBack){
                                mHandler.sendEmptyMessage(BACK_TO_APP);
                                //mHandler.sendEmptyMessage(RELINE);
                            } else if(isReload) {
                                //Log.e("url test","reload");
                                mHandler.sendEmptyMessage(RELOAD);
                            }else {
                                runWebView(url, totalTime);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        // 這個 NullPointerException雖然有警告說"跟JSONException一樣"，但實測若沒加這個，就是會閃退在NullPointerException！
                        // 如果被劫持，但Response長度有大於64，就會解密。解密失敗"decoded"是NULL，new JSONObject(decoded) 會閃退
                        e.printStackTrace();
                    }
                }else if(response.code() == 999){
                    if(!isReload) checkIPBlock();
                }else{
                    //phaseData.put(createPhaseData(2,call.request().url().toString(),-1));
//                    showTimeoutDialogFlag = false;
                    TGMessagePost(response.code() + "", "線路異常", sb.toString(), call.request().url().toString());
//                    if(isReload) isReload = false;
//                    if(isWebviewBack) isWebviewBack = false;
//                    else {
//                        savePhase2Status(call.request().url().toString(), String.valueOf(response.code()));
                        step2Map.put(call.request().url().toString(), createPhaseData(2, call.request().url().toString(), -1));
                        checkUrlErrorHandle(isPhase3CheckUrl);
//                    }
                }
                isReload = false;
                isRefreshLock = false;
                isWebviewBackLock = false;
                isWebviewBack = false;
            }
        };
        private void savePhase2Status(String url, String errorCode, boolean isResponse){
            for(int i = 0; i < urls.size(); i++){
                url = url.replace("/app/testAppDomainSecurity", "");
                if(url.contains(urls.get(i))){
                    phase2Status.put("line"+(i+1), errorCode);
                    phase2Status.put("line"+ (i+1) + "TimeCost", String.valueOf(System.currentTimeMillis() - startTime));
                    if(isResponse && phase2FastLine == 0)
                        phase2FastLine = i + 1;
                }
            }
        }
    }


    public static class SSLSocketClient {

        //获取这个SSLSocketFactory
        public static SSLSocketFactory getSSLSocketFactory() {
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
        public static HostnameVerifier getHostnameVerifier() {
            HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            };
            return hostnameVerifier;
        }
    }

    private void goBack(boolean isBackKeyCode){
        WebBackForwardList wbfl = mWebView.copyBackForwardList();
        if(mRollingWebView.getVisibility() == View.VISIBLE){
            //finish();
            exitApp();
        }else if(bottomMenu.getVisibility() == View.VISIBLE){
            if(!isWebviewBackLock) {
                isWebviewBackLock = true;
                isWebviewBack = true;
                //reline();
                new CheckUrl().checkUrl(baseUrl, command, true);
                final Handler handler = new Handler();
                handler.postDelayed(webViewBackTimer, 5000);
            }else{
//                goOnlinePay(wbfl);
//                goBaseUrl(wbfl);
//                goHomePage();
            }
        }else{
//            if(mWebView.getUrl()!=null && mWebView.getUrl().endsWith("login")){
//                goHomePage();
//                return;
//            }
            if(wbfl.getCurrentIndex() > 0){
                if(mWebView.getUrl().contains(baseUrl) && mWebView.getUrl().endsWith("home")){
                    exitApp();
                }else if(mWebView.getUrl().contains(baseUrl) && mWebView.getUrl().endsWith("login")){
                    exitApp();
                }else if(isMaintain){
                    exitApp();
                }else{
                    mWebView.goBack();
                }
            }else if(isBackKeyCode){
                exitApp();
            }
        }
    }
    private void exitApp(){
        if(System.currentTimeMillis()-firstTime>2000){
            showToast(MainActivity.this, getResources().getString(R.string.exit_app_msg));
            firstTime = System.currentTimeMillis();
        }else{
            finish();
        }
    }

//    private void goOnlinePay(WebBackForwardList wbfl){
//        int steps = -1;
//        for (int i=wbfl.getSize()-1; i>=0; i--) {
//            Log.d("goOnlinePay", "wbfl.getUrl:_"+i+"_"+wbfl.getItemAtIndex(i).getUrl());
//            if(backUrl.equalsIgnoreCase(wbfl.getItemAtIndex(i).getUrl())){
//                steps = i - wbfl.getSize() + 1;
//                break;
//            }
//        }
//        mWebView.goBackOrForward(steps);
//        backUrl = "isBack";
//    }

    //規則修改為:導出其他頁面按返回，一律跳回我們域名的頁面
//    private void goBaseUrl(WebBackForwardList wbfl){
//        int steps = -1;
//        for (int i=wbfl.getSize()-1; i>=0; i--) {
//            Log.d("goBaseUrl", "wbfl.getUrl:_"+i+"_"+wbfl.getItemAtIndex(i).getUrl());
//            if(wbfl.getItemAtIndex(i).getUrl().startsWith(baseUrl)){
//                steps = i - wbfl.getSize() + 1;
//                break;
//            }
//        }
//        mWebView.goBackOrForward(steps);
//        backUrl = "isBack";
//    }

    private void goHomePage(boolean isClearHistory){
        mWebView.loadUrl(baseUrl);
        if(isClearHistory){
            mWebView.clearHistory();
        }
//        backUrl = "isBack";
    }

    private void hideBottomMenu(){
        bottomMenu.setVisibility(View.GONE);
//        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }
    private void showBottomMenu(){
        bottomMenu.setVisibility(View.VISIBLE);
//        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    private boolean isMaintain = false;
    private boolean checkMaintain(StringBuffer msg, String maintainUrl){
        Log.d("checkMaintain", "msg: "+msg);
        int fromIndex = maintainUrl.indexOf("/", maintainUrl.indexOf("."));
        if(fromIndex > 0){
            maintainUrl = maintainUrl.substring(0, fromIndex);
        }
        Log.i("checkMaintain", "maintainUrl: " + maintainUrl);

        try {
            JSONObject json = new JSONObject(msg.toString());
            String code = json.getString("code");
            if ("maintain".equalsIgnoreCase(code.trim()) || "ipError".equalsIgnoreCase(code.trim())) {
                if(isReload){
                    mHandler.sendEmptyMessage(RELOAD);
                }else{
                    mHandler.sendEmptyMessage(CLOSE_ROLLING);
                    runWebView(maintainUrl, System.currentTimeMillis());
                }
                isMaintain = true;
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(msg.toString().trim().equals(getResources().getString(R.string.maintain_msg))) {
            if(isReload){
                mHandler.sendEmptyMessage(RELOAD);
            }else{
                mHandler.sendEmptyMessage(CLOSE_ROLLING);
                runWebView(maintainUrl, System.currentTimeMillis());
            }
            isMaintain = true;
            return true;
        }

        isMaintain = false;
        return false;
    }

    //6.0以後版本，針對危險級別的權限，需要另外詢問使用者同意，才能處理
    //例如存取外部儲存空間(相簿圖片)，屬於危險級別的權限，就要另外詢問處理
    private final static int CODE_FOR_WRITE_PERMISSION = 777;
    private final static int INDEX_DownloadQrcodeImg = 771;
    private final static int INDEX_SaveQrcodeBitmap = 772;
    private final static int REQUEST_PRE_SET = 0x104;
    public void confirmPermission(int index){
        if (Build.VERSION.SDK_INT >= 23) {
            int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
                //requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, CODE_FOR_WRITE_PERMISSION);
                requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, index);
                return;
            }
        }
        switch(index){
            case INDEX_DownloadQrcodeImg:
                downloadQrcodeImg();
                break;
            case INDEX_SaveQrcodeBitmap:
                saveQrcodeBitmap();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //if (requestCode == CODE_FOR_WRITE_PERMISSION){
        if (requestCode == INDEX_DownloadQrcodeImg || requestCode == INDEX_SaveQrcodeBitmap){
            if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    &&grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //用户同意使用write
                switch(requestCode){
                    case INDEX_DownloadQrcodeImg:
                        downloadQrcodeImg();
                        break;
                    case INDEX_SaveQrcodeBitmap:
                        saveQrcodeBitmap();
                        break;
                }
            }else{
                //用户不同意，不提示訊息
                //Toast.makeText(getApplicationContext(), "未取得授权！", Toast.LENGTH_SHORT).show();
                //提示進入APP設定權限
                //openAppSetting();
            }
        }
    }
    private void openAppSetting() {
        alertDialog = DialogUtils.getCustomTwoButtonDialog(MainActivity.this,
                getResources().getString(R.string.tip_title),
                getResources().getString(R.string.tip_save_qrcode_permissions),
                getResources().getString(R.string.button_set_permissions),
                getResources().getString(R.string.button_cancel),
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //打开权限设置界面
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        //申请权限返回执行
                        startActivityForResult(intent, REQUEST_PRE_SET);
                        alertDialog.dismiss();
                    }
                },
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private  void downloadQrcodeImg(){
        DownloadUtils mDownUtils = new DownloadUtils(MainActivity.this);
        mDownUtils.startDownloadImg(downloadQRcodeUrl);
        showToast();
    }
    private String base64ImageData = null;
    private  void saveQrcodeBitmap(){
        DownloadUtils mDownUtils = new DownloadUtils(MainActivity.this);
        if(mDownUtils.convertDataToImage(base64ImageData)!=null){
            mDownUtils.startSaveBitmap(mDownUtils.convertDataToImage(base64ImageData));
            showToast();
        }
    }

    private void showToast(){
        String msg = getResources().getString(R.string.tip_save_qrcode_image);
        //Toast.LENGTH_SHORT = 2seconds；Toast.LENGTH_LONG = 3.5seconds；需求為2秒，故先設定LENGTH_SHORT
        Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        //需求:提示字體加大，背景為半透明的黑
        LinearLayout layout = (LinearLayout) toast.getView();
        layout.setBackgroundColor(getResources().getColor(R.color.colorToastBG));
        layout.setPadding(10, 10, 10, 10);
        TextView textView = (TextView) toast.getView().findViewById(android.R.id.message);
        textView.setTextColor(getResources().getColor(R.color.colorToastText));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        //需求:顯示位置置中
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private String getIpAddressString() {
        try {
            for (Enumeration<NetworkInterface> enNetI = NetworkInterface.getNetworkInterfaces(); enNetI.hasMoreElements(); ) {
                NetworkInterface netI = enNetI.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = netI.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "unknown";
    }

    public void TGMessagePost(String responseCode, String errorCode, String response, String url) {
        String chatID;
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
//        builder.connectTimeout(0, TimeUnit.MILLISECONDS).readTimeout(0, TimeUnit.MILLISECONDS);
        builder.sslSocketFactory(SSLSocketClient.getSSLSocketFactory());
        builder.hostnameVerifier(SSLSocketClient.getHostnameVerifier());
        okHttpClient = builder.build();
        //https://api.telegram.org/bot751010255:AAHMC2SnuUT_zMLuxAJupvfiLSm-KTcG-s8/sendMessage?chat_id=-299003390&text=testMessage
        //String tgUrl = "https://api.telegram.org/bot751010255:AAHMC2SnuUT_zMLuxAJupvfiLSm-KTcG-s8/sendMessage";
        String tgUrl = getString(R.string.tg_url);
        if(sitePath.toUpperCase().equals("VIP1") || sitePath.toUpperCase().equals("WINVIP01") //測試站點，發TG(QA)
                || sitePath.toUpperCase().equals("CB01") || sitePath.toUpperCase().equals("AB00") //測試站點，發TG(QA)
                || sitePath.toUpperCase().contains("APPTEAM") //APP測試站點，發TG(QA)
                || BuildConfig.DEBUG //如果是DeBug在開發模式中，發TG(QA)
                || response.toLowerCase().contains("fiddler") || response.toLowerCase().contains("charles") //用Fiddler或Charles攔截改Response，發TG(QA)
                || response.toLowerCase().contains("redirect:/result")) { //通常為跑測線有問題，才會出現的Response，也歸類在發TG(QA)
            chatID = getString(R.string.tg_test_char_id);
        } else {
            chatID = getString(R.string.tg_char_id);
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("chat_id", chatID);
            jsonObject.put("text", TGSendMessageText(responseCode, errorCode, response, url));
        }catch (JSONException e){
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(JSONType, jsonObject.toString());
        Log.e("tgUrl", tgUrl);
        Log.e("requestBody",  jsonObject.toString());

        Request request = new Request.Builder()
                .url(tgUrl)
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(TGCallback);
    }

    private String TGSendMessageText(String responseCode, String errorCode, String response, String url){
        StringBuilder builder = new StringBuilder();
        builder.append("站點 : ").append(sitePath).append("\n").
                append("域名 : ").append(url).append("\n").
                append("使用者IP : ").append(getIpAddressString()).append("\n").
                append("裝置型號 : ").append(SystemUtil.getDeviceBrand() + " " + SystemUtil.getSystemModel()).append("\n").
                append("裝置版本 : ").append(SystemUtil.getSystemVersion()).append("\n").
                append("異常原因 : ").append(errorCode).append("\n").
                append("status : ").append(responseCode).append("\n").
                append("回傳值 : ").append(response);
        return builder.toString();
    }

    private Callback TGCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            //Log.e("response code", response.code() + "");
        }
    };

}
