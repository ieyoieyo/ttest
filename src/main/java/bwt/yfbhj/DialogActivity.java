package bwt.yfbhj;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * 自定义Activity
 * Created by eano on 2017/3/27.
 */
public class DialogActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("数据加载中，请稍等...");
        progressDialog.setCanceledOnTouchOutside(false);
    }
    public void showWaittingDialog() {
        if (progressDialog != null) {
            progressDialog.show();
        }
    }
    public boolean dismissWaittingDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            return true;
        }
        return false;
    }
    public void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
