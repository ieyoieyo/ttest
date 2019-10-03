package bwt.yfbhj;

import android.content.Intent;
import android.os.Bundle;

/**
 * Created by eano on 2017/7/4.
 */
public class StartActivity extends DialogActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        Intent intent = new Intent();
        intent.setClass(StartActivity.this, MainActivity.class);
        startActivity(intent);
    }
}

