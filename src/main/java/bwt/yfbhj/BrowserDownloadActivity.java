package bwt.yfbhj;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class BrowserDownloadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser_download);

        TextView dialogTitle = (TextView) findViewById(R.id.title);
        TextView dialogMessage = (TextView) findViewById(R.id.message);
        Button buttonOK = (Button) findViewById(R.id.button_ok);
        Button buttonCancel = (Button) findViewById(R.id.button_cancel);

        dialogTitle.setText(getString(R.string.tip_title));
        dialogMessage.setText(getString(R.string.tip_content_browser_downloading));
        buttonOK.setText(getString(R.string.button_return_app));
        buttonCancel.setText(getString(R.string.button_cancel));

        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(BrowserDownloadActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
