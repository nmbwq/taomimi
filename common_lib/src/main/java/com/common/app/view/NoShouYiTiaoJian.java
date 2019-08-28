package com.common.app.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.common.R;

/**
 * @author: zhengjr
 * @since: 2018/8/22
 * @describe:
 */

public class NoShouYiTiaoJian extends TiaoJianView {


    public NoShouYiTiaoJian(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void initView() {
        super.initView();
//        mRootView.findViewById(R.id.ll_shouyi).setVisibility(GONE);
    }

    public void updataShowStyle(){
        this.mIsSingleLine = !mIsSingleLine;
        mIvStyle.setImageResource(this.mIsSingleLine ? R.drawable.ic_tiaojian_single : R.drawable.ic_tiaojian_more);
    }

    public void updataStyles(int currentPosition){
        if (currentPosition == 2 || currentPosition == 3){
            mState = false;

            if (mOldPosition != currentPosition) {
                mTvs.get(mOldPosition).setSelected(false);
                mTvs.get(currentPosition).setSelected(true);

                mVs.get(mOldPosition).setSelected(false);
                mVs.get(currentPosition).setSelected(true);
                this.mOldPosition = currentPosition;
            }
        }else if (currentPosition == 0) {
            mState = true;

            if (mOldPosition != currentPosition) {
                mTvs.get(mOldPosition).setSelected(false);
                mTvs.get(currentPosition).setSelected(true);

                mVs.get(mOldPosition).setSelected(false);
                mVs.get(currentPosition).setSelected(true);
                this.mOldPosition = currentPosition;
            }

        } else {
            mState = false;
            if (mOldPosition != currentPosition) {
                this.mUpOrDown = true;

                mTvs.get(mOldPosition).setSelected(false);
                mTvs.get(currentPosition).setSelected(true);

                mVs.get(mOldPosition).setSelected(false);
                mVs.get(currentPosition).setSelected(true);


                this.mOldPosition = currentPosition;
                //设置默认选中向上
                TextView currentTv = mTvs.get(currentPosition);
                Drawable rightDras = this.mUpOrDown
                        ? getResources().getDrawable(R.drawable.btn_tiaojian_img_up_selector)
                        : getResources().getDrawable(R.drawable.btn_tiaojian_img_down_selector);
                currentTv.setCompoundDrawablesWithIntrinsicBounds(null, null, rightDras, null);
                mTvs.get(currentPosition).setSelected(true);
            } else {
                TextView currentTv = mTvs.get(currentPosition);
                Drawable rightDras = this.mUpOrDown
                        ? getResources().getDrawable(R.drawable.btn_tiaojian_img_up_selector)
                        : getResources().getDrawable(R.drawable.btn_tiaojian_img_down_selector);
                currentTv.setCompoundDrawablesWithIntrinsicBounds(null, null, rightDras, null);
                mTvs.get(currentPosition).setSelected(true);
            }
            this.mUpOrDown = !this.mUpOrDown;
        }
    }
}
