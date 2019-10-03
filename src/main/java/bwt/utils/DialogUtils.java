package bwt.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import bwt.yfbhj.R;

public class DialogUtils {

    static public AlertDialog getSingleButtonDialog(Context context, String title, String content, DialogInterface.OnClickListener listener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(content)
                .setPositiveButton(R.string.button_ok, listener);
        return builder.create();
    }
    static public AlertDialog getTwoButtonDialog(Context context, String title, String content, DialogInterface.OnClickListener okListener,DialogInterface.OnClickListener cancelListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(content)
                .setNegativeButton(R.string.button_cancel,cancelListener)
                .setPositiveButton(R.string.button_ok, okListener);

        return builder.create();
    }
    static public AlertDialog getCustomSingleButtonDialog(Context context,String title, String content, String buttonName , Button.OnClickListener listener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.DialogStyle);
        Activity activity = (Activity)context;
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alertdialog_custom_single_button,null);
        builder.setView(dialogView);
        Button buttonOK = (Button)dialogView.findViewById(R.id.button_ok);
        TextView dialogTitle = (TextView)dialogView.findViewById(R.id.title);
        TextView dialogMessage = (TextView)dialogView.findViewById(R.id.message);
        buttonOK.setText(buttonName);
        buttonOK.setOnClickListener(listener);
        dialogTitle.setText(title);
        dialogMessage.setText(content);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }
    static public AlertDialog getCustomTwoButtonDialog(Context context,String title, String content, String buttonOkName, String buttonCancelName, Button.OnClickListener okListener, Button.OnClickListener cancelListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.DialogStyle);
        Activity activity = (Activity)context;
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alertdialog_custom_two_button,null);
        builder.setView(dialogView);
        Button buttonOK = (Button)dialogView.findViewById(R.id.button_ok);
        Button buttonCancel = (Button)dialogView.findViewById(R.id.button_cancel);
        TextView dialogTitle = (TextView)dialogView.findViewById(R.id.title);
        TextView dialogMessage = (TextView)dialogView.findViewById(R.id.message);
        buttonOK.setText(buttonOkName);
        buttonCancel.setText(buttonCancelName);
        buttonOK.setOnClickListener(okListener);
        buttonCancel.setOnClickListener(cancelListener);
        dialogTitle.setText(title);
        dialogMessage.setText(content);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }
}
