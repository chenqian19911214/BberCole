package com.bber.company.android.tools;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.listener.IactionListener;
import com.bber.company.android.view.activity.LoginActivity;
import com.bber.company.android.view.customcontrolview.DialogView;

/**
 * Created by Administrator on 2015/10/15 0015.
 */
public class DialogTool {

    public IactionListener iactionListener;

    public static AlertDialog createDialog(final Context context, View layout, int textID, int cancleID, int yesID) {
        TextView dialog_text = layout.findViewById(R.id.dialog_text);
        TextView dialog_cancle = layout.findViewById(R.id.dialog_cancle);
        TextView dialog_sure = layout.findViewById(R.id.dialog_sure);
        dialog_text.setText(context.getResources().getText(textID));
        dialog_cancle.setText(context.getResources().getText(cancleID));
        dialog_cancle.setTag(0);
        dialog_sure.setTag(1);
        dialog_sure.setText(context.getResources().getText(yesID));
        final AlertDialog myDialog = new AlertDialog.Builder(context).create();
        myDialog.setCanceledOnTouchOutside(false);
        myDialog.setView(layout);
        myDialog.show();

        dialog_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        return myDialog;
    }

    public static AlertDialog createDialog(final Context context, View layout, String textID, int cancleID, int yesID) {
        TextView dialog_text = layout.findViewById(R.id.dialog_text);
        TextView dialog_cancle = layout.findViewById(R.id.dialog_cancle);
        TextView dialog_sure = layout.findViewById(R.id.dialog_sure);
        dialog_text.setText(textID);
        dialog_cancle.setText(context.getResources().getText(cancleID));
        dialog_cancle.setTag(0);
        dialog_sure.setTag(1);
        dialog_sure.setText(context.getResources().getText(yesID));
        final AlertDialog myDialog = new AlertDialog.Builder(context).create();
        myDialog.setCanceledOnTouchOutside(false);
        myDialog.setView(layout);
        myDialog.show();

        dialog_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        return myDialog;
    }

    public static AlertDialog createDialogTip(final Context context, View layout, String content, int cancleID, int yesID) {
        TextView dialog_text = layout.findViewById(R.id.dialog_text);
        TextView dialog_cancle = layout.findViewById(R.id.dialog_cancle);
        TextView dialog_sure = layout.findViewById(R.id.dialog_sure);
        dialog_text.setText(content);
        dialog_cancle.setText(context.getResources().getText(cancleID));
        dialog_cancle.setTag(0);
        dialog_sure.setTag(1);
        dialog_sure.setText(context.getResources().getText(yesID));
        final AlertDialog myDialog = new AlertDialog.Builder(context).create();
        myDialog.setCanceledOnTouchOutside(false);
        myDialog.setView(layout);
        myDialog.show();
        dialog_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
            }
        });

        return myDialog;
    }

    public static AlertDialog createDialogWarn(final Context context, View layout, int textID, int yesID) {
        TextView dialog_text = layout.findViewById(R.id.dialog_text);
        TextView dialog_sure = layout.findViewById(R.id.dialog_sure);
        dialog_text.setText(context.getResources().getText(textID));
        dialog_sure.setText(context.getResources().getText(yesID));
        final AlertDialog myDialog = new AlertDialog.Builder(context).create();
        myDialog.setCanceledOnTouchOutside(false);
        myDialog.setCancelable(false);
        myDialog.setView(layout);
        myDialog.show();
        dialog_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
                Intent intent = new Intent(context, LoginActivity.class);
                intent.putExtra("relogin", true);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        return myDialog;
    }

    public static AlertDialog createDel(final Context context, View layout) {
        TextView dialog_del = layout.findViewById(R.id.dialog_del);
        dialog_del.setTag(0);
        TextView dialog_transmit = layout.findViewById(R.id.dialog_transmit);
        dialog_transmit.setTag(1);
        TextView dialog_copy = layout.findViewById(R.id.dialog_copy);
        dialog_copy.setTag(2);
        final AlertDialog myDialog = new AlertDialog.Builder(context).create();
        myDialog.setCancelable(true);
        myDialog.setCanceledOnTouchOutside(true);
        myDialog.setView(layout);
        myDialog.show();
        return myDialog;
    }

    /**
     * edit dialog
     * *
     */
    public static AlertDialog createEditDialog(final Context context, View layout) {
        EditText dialog_edit = layout.findViewById(R.id.dialog_edit);
        TextView dialog_cancle = layout.findViewById(R.id.dialog_cancle);
        TextView dialog_sure = layout.findViewById(R.id.dialog_sure);
        dialog_edit.setTag(0);
        dialog_sure.setTag(1);
        final AlertDialog myDialog = new AlertDialog.Builder(context).create();
        myDialog.setCanceledOnTouchOutside(false);
        myDialog.setView(layout);
        myDialog.show();
        dialog_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        return myDialog;
    }

    /**
     * 上传图片dialog
     * *
     */
    public static AlertDialog createUploadDialog(final Context context, View layout) {
        TextView photo_album = layout.findViewById(R.id.dialog_photo_album);
        TextView photo_graph = layout.findViewById(R.id.dialog_photo_graph);
        TextView cancle = layout.findViewById(R.id.dialog_cancle);
        photo_album.setTag(0);
        photo_graph.setTag(1);
        final AlertDialog myDialog = new AlertDialog.Builder(context).create();
        myDialog.setCanceledOnTouchOutside(true);
        Window window = myDialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.BOTTOM;
        myDialog.show();
        window.setWindowAnimations(R.style.dialog_anim);  //添加动画
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
        window.setContentView(layout);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        return myDialog;
    }

    /**
     * 上传图片dialog
     * *
     */
    public static AlertDialog createReplyDialog(final Context context, View layout) {
        final AlertDialog myDialog = new AlertDialog.Builder(context).create();
        myDialog.setCanceledOnTouchOutside(true);
        Window window = myDialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.BOTTOM;
        myDialog.show();
        window.setWindowAnimations(R.style.dialog_anim);  //添加动画
        Display d = window.getWindowManager().getDefaultDisplay();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = (int) (d.getHeight() * 0.5);
        window.setAttributes(params);
        window.setContentView(layout);
        return myDialog;
    }

    /**
     * ProgressDialog
     * *
     */
    public static void createProgressDialog(Context context, boolean is) {
     /*   ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(text);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();*/

        DialogView.show(context, is);
        //  return progressDialog;
    }

    public static void dismiss(Context context) {
     /*   ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(text);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();*/

        DialogView.dismiss(context);
        //  return progressDialog;
    }

    /**
     * select dialog
     * *
     */
    public static AlertDialog createSelectDialog(final Context context, View layout, int textID, int cancleID, int yesID) {
        TextView dialog_text = layout.findViewById(R.id.dialog_text);
        TextView dialog_cancle = layout.findViewById(R.id.dialog_cancle);
        TextView dialog_sure = layout.findViewById(R.id.dialog_sure);
        dialog_text.setText(context.getResources().getText(textID));
        dialog_cancle.setText(context.getResources().getText(cancleID));
        dialog_cancle.setTag(0);
        dialog_sure.setTag(1);
        dialog_sure.setText(context.getResources().getText(yesID));
        final AlertDialog myDialog = new AlertDialog.Builder(context).create();
        myDialog.setCanceledOnTouchOutside(false);
        myDialog.setView(layout);
        myDialog.show();
        return myDialog;
    }

    /**
     * select dialog
     * *
     */
    public static AlertDialog createSureDialog(final Context context, View layout, String notice, String sure) {
        TextView dialog_text = layout.findViewById(R.id.dialog_text);
        TextView dialog_cancle = layout.findViewById(R.id.dialog_cancle);
        TextView dialog_sure = layout.findViewById(R.id.dialog_sure);
        dialog_text.setText(notice);
        dialog_cancle.setTag(0);
        dialog_sure.setTag(1);
        dialog_sure.setText(sure);
        final AlertDialog myDialog = new AlertDialog.Builder(context).create();
        myDialog.setCanceledOnTouchOutside(false);
        myDialog.setView(layout);
        myDialog.show();
        return myDialog;
    }

    /**
     * Checkbox dialog
     * *
     */
    public static AlertDialog createCheckboxDialog(final Context context, View layout) {
        CheckBox dialog_checkBox = layout.findViewById(R.id.dialog_checkBox_cancle);
        TextView dialog_cancle = layout.findViewById(R.id.dialog_reminding_sure);
        TextView dialog_sure = layout.findViewById(R.id.dialog_reminding_text);
        dialog_checkBox.setTag(0);
        dialog_cancle.setTag(1);
        final AlertDialog myDialog = new AlertDialog.Builder(context).create();
        myDialog.setCanceledOnTouchOutside(false);
        myDialog.setView(layout);
        myDialog.show();
        return myDialog;
    }

    public void setActionListener(IactionListener _listener) {
        iactionListener = _listener;
    }

    /***
     * 保存二维码并且跳转支付宝和微信
     * @param context
     * @param layout
     * @param textID
     * @param yesID
     * @return
     */
    public AlertDialog createDialogStart(final Context context, View layout, int textID, int yesID) {
//        TextView dialog_text = (TextView) layout.findViewById(R.id.dialog_text);
        TextView dialog_cancle = layout.findViewById(R.id.button_cancal);
        TextView dialog_sure = layout.findViewById(R.id.button_sure);
        dialog_cancle.setText(context.getResources().getText(textID));
        dialog_sure.setText(context.getResources().getText(yesID));
        final AlertDialog myDialog = new AlertDialog.Builder(context).create();
        myDialog.setCanceledOnTouchOutside(false);
        myDialog.setCancelable(false);
        myDialog.setView(layout);
        myDialog.show();
        dialog_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
                iactionListener.SuccessCallback("");
            }
        });
        dialog_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        return myDialog;
    }

    /***
     * 保存二维码并且跳转支付宝和微信
     * @param context
     * @param
     * @param
     * @param
     * @return
     */
    public AlertDialog createDialogSaveImage(final Context context) {
//        TextView dialog_text = (TextView) layout.findViewById(R.id.dialog_text);
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.alert_save_image, null);
        TextView dialog_cancle = layout.findViewById(R.id.button_cancal);
        TextView saveImage_one = layout.findViewById(R.id.saveImage_one);
        TextView saveImage_all = layout.findViewById(R.id.saveImage_all);
        final AlertDialog myDialog = new AlertDialog.Builder(context).create();
//        myDialog.setCanceledOnTouchOutside(false);
//        myDialog.setCancelable(false);
        myDialog.setView(layout);
        myDialog.show();
        saveImage_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
                iactionListener.SuccessCallback("1");
            }
        });
        saveImage_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
                iactionListener.SuccessCallback("2");
            }
        });
        dialog_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        return myDialog;
    }

}
