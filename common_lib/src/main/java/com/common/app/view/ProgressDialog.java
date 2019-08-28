package com.common.app.view;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.DrawableRes;
import android.view.KeyEvent;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.R;
import com.common.app.http.cancle.ApiCancleManager;
import com.common.app.utils.CustomProgressDialog;

/**
 * @author: zhengjr
 * @since: 2018/11/29
 * @describe:
 */

public class ProgressDialog {

    private ProgressDialog() {
    }

    private static class ProgressDialogInstance {
        private static final ProgressDialog INSTANCE = new ProgressDialog();
    }

    public static ProgressDialog getInstance() {
        return ProgressDialogInstance.INSTANCE;
    }


    private Dialog mDialog;

    public ProgressDialog createDialog(Context context, final boolean canCancle) {
        dismiss();

        mDialog = new Dialog(context, R.style.CustomProgressDialog);
        mDialog.setContentView(R.layout.progress_dialog_layout);
        mDialog.setCanceledOnTouchOutside(false);
        ImageView iv_loading_logo = (ImageView) mDialog.findViewById(R.id.iv_loading_logo);
        ObjectAnimator animator = ObjectAnimator.ofFloat(iv_loading_logo, "rotation", 0F, 360F);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(1000);
        animator.setDuration(2000).start();

        mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (canCancle) {
                        ApiCancleManager.getInstance().cancelAll();
                        return false;
                    } else {
                        return true;
                    }
                }
                return false;
            }
        });
        return this;
    }

    public ProgressDialog createDialog(Context context, final boolean canCancle, @DrawableRes int loadImg, String txt) {
        dismiss();

        mDialog = new Dialog(context, R.style.CustomProgressDialog);
        mDialog.setContentView(R.layout.progress_dialog_layout);
        mDialog.setCanceledOnTouchOutside(false);
        ImageView iv_loading_logo = (ImageView) mDialog.findViewById(R.id.iv_loading_logo);
        TextView tv_loading_desc = (TextView) mDialog.findViewById(R.id.tv_loading_desc);
        tv_loading_desc.setText(txt);
        iv_loading_logo.setImageResource(loadImg);

        mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (canCancle) {
                        ApiCancleManager.getInstance().cancelAll();
                        return false;
                    } else {
                        return true;
                    }
                }
                return false;
            }
        });

        return this;
    }

    public void show() {
        try {
            if (mDialog != null) {
                mDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dismiss() {
        try {
            if (mDialog != null) {
                mDialog.dismiss();
                mDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
