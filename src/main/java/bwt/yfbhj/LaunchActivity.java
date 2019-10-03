package bwt.yfbhj;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;

/**启动页面
 * Created by eano on 2017/3/24.
 */
public class LaunchActivity extends DialogActivity {

    public static final int TIME = 2000;//设置延迟时间
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//手机屏幕只有竖屏
        Handler handler=new Handler();
        //当计时结束时，跳转页面
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(LaunchActivity.this,MainActivity.class));
                LaunchActivity.this.finish();
            }
        },TIME);
    }
}
