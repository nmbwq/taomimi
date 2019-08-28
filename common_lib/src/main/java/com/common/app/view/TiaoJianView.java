package com.common.app.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: zhengjr
 * @since: 2018/7/28
 * @describe:
 */

public class TiaoJianView extends RelativeLayout implements View.OnClickListener {


    protected Context mContext;
    protected View mRootView;
    private TextView mTvZongHe, mTvXiaoLiang, mTvJiaGe, mTvShouYi, mTvShaiXuan;//标题
    private View mVZongHe, mVXiaoLiang, mVJiaGe, mVShouYi, mVShaiXuan;//标题指示器、
    private LinearLayout mLlStyle;//单行多行切换按钮
    protected ImageView mIvStyle;
    protected int mOldPosition;//记录当前选中的标题，默认为0(默认选中综合)

    protected List<TextView> mTvs = new ArrayList<>();//存放标题，用于方便更改标题样式
    protected List<View> mVs = new ArrayList<>();//存放标题指示器，用于方便更改标题指示器样式

    //记录价格或者收益等的排序是否开启，true是从大到小，false是从小到大
    protected boolean mUpOrDown = true;

    //这里记录的是综合、筛选跟其他的不同 ，mState = true时，记录的是综合跟筛选，所以mUpOrDown状态无效
    protected boolean mState = true;
    protected boolean mIsSingleLine = false;//记录是单行展示样式

    private OnSelecterLisenter mOnSelecterLisenter;//选中状态监听

    public TiaoJianView(Context context) {
        this(context, null);
    }

    public TiaoJianView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public TiaoJianView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        //初始化view布局
        initView();
    }

    public void initView() {
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.tiaojian_layout, this);
        //标题
        mTvZongHe = mRootView.findViewById(R.id.tv_zonghe);
        mTvXiaoLiang = mRootView.findViewById(R.id.tv_xiaoliang);
        mTvJiaGe = mRootView.findViewById(R.id.tv_jiage);
        mTvShouYi = mRootView.findViewById(R.id.tv_shouyi);
        mTvShaiXuan = mRootView.findViewById(R.id.tv_shaixuan);
        mTvShaiXuan.setSelected(true);


        //标题指示器
        mVZongHe = mRootView.findViewById(R.id.v_zonghe);
        mVXiaoLiang = mRootView.findViewById(R.id.v_xiaoliang);
        mVJiaGe = mRootView.findViewById(R.id.v_jiage);
        mVShouYi = mRootView.findViewById(R.id.v_shouyi);
        mVShaiXuan = mRootView.findViewById(R.id.v_shaixuan);
        mLlStyle = mRootView.findViewById(R.id.ll_style);
        mIvStyle = mRootView.findViewById(R.id.iv_style);

        mTvs.add(mTvZongHe);
        mTvs.add(mTvJiaGe);
        mTvs.add(mTvXiaoLiang);
        mTvs.add(mTvShouYi);
        mTvs.add(mTvShaiXuan);

        mVs.add(mVZongHe);
        mVs.add(mVJiaGe);
        mVs.add(mVXiaoLiang);
        mVs.add(mVShouYi);
        mVs.add(mVShaiXuan);

        mTvs.get(mOldPosition).setSelected(true);
        mVs.get(mOldPosition).setSelected(true);

        mTvZongHe.setOnClickListener(this);
        mTvXiaoLiang.setOnClickListener(this);
        mTvJiaGe.setOnClickListener(this);
        mTvShouYi.setOnClickListener(this);
        mTvShaiXuan.setOnClickListener(this);
        mLlStyle.setOnClickListener(this);

        setPopupWindowLayout(R.layout.tiaojian_popupwind_layout);
        changeShowStyle();//默认为单行

    }

    /**
     * 更改单行多行展示样式
     */
    protected void changeShowStyle(){
        this.mIsSingleLine = !mIsSingleLine;
        mIvStyle.setImageResource(this.mIsSingleLine ? R.drawable.ic_tiaojian_single : R.drawable.ic_tiaojian_more);
        //回调通知
        if (mOnSelecterLisenter != null) {
            mOnSelecterLisenter.onChangeStyle(this.mIsSingleLine);
        }
    }

    protected void changeStyles(int currentPosition) {

        if (currentPosition == 2 || currentPosition == 3){

            if (currentPosition == mOldPosition){
                return;
            }

            mState = false;

            if (mOldPosition != currentPosition) {
                mTvs.get(mOldPosition).setSelected(false);
                mTvs.get(currentPosition).setSelected(true);

                mVs.get(mOldPosition).setSelected(false);
                mVs.get(currentPosition).setSelected(true);
                this.mOldPosition = currentPosition;
            }
            if (mOnSelecterLisenter != null) {
                mOnSelecterLisenter.onSelecter(currentPosition, mState);
            }
        }else if (currentPosition == 0) {
            if (currentPosition == mOldPosition){
                return;
            }

            mState = true;

            if (mOldPosition != currentPosition) {
                mTvs.get(mOldPosition).setSelected(false);
                mTvs.get(currentPosition).setSelected(true);

                mVs.get(mOldPosition).setSelected(false);
                mVs.get(currentPosition).setSelected(true);
                this.mOldPosition = currentPosition;
            }
            if (mOnSelecterLisenter != null) {
                mOnSelecterLisenter.onSelecter(currentPosition, mState);
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
                        ? getResources().getDrawable(R.drawable.btn_tiaojian_img_down_selector)
                        : getResources().getDrawable(R.drawable.btn_tiaojian_img_up_selector);
                currentTv.setCompoundDrawablesWithIntrinsicBounds(null, null, rightDras, null);
                mTvs.get(currentPosition).setSelected(true);
            } else {
                TextView currentTv = mTvs.get(currentPosition);
                Drawable rightDras = this.mUpOrDown
                        ? getResources().getDrawable(R.drawable.btn_tiaojian_img_down_selector)
                        : getResources().getDrawable(R.drawable.btn_tiaojian_img_up_selector);
                currentTv.setCompoundDrawablesWithIntrinsicBounds(null, null, rightDras, null);
                mTvs.get(currentPosition).setSelected(true);
            }
            if (mOnSelecterLisenter != null) {
                mOnSelecterLisenter.onSelecter(currentPosition, mUpOrDown);
            }
            this.mUpOrDown = !this.mUpOrDown;
        }


    }

    public void setSelectLisenter(OnSelecterLisenter onSelecterLisenter) {
        this.mOnSelecterLisenter = onSelecterLisenter;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_zonghe) {
            changeStyles(0);
            //取消弹出框
//                showPopupwind(mTvZongHe);
        } else if (id == R.id.tv_xiaoliang) {
            changeStyles(2);
            dismissPopupwind();
        } else if (id == R.id.tv_jiage) {
            changeStyles(1);
            dismissPopupwind();
        } else if (id == R.id.tv_shouyi) {
            changeStyles(3);
            dismissPopupwind();
        } else if (id == R.id.tv_shaixuan) {
//                changeStyles(4);
            if (mOnSelecterLisenter != null) {
                mOnSelecterLisenter.onSelecter(4, mState);
            }
        }else if(id == R.id.ll_style){
            changeShowStyle();
        }
    }

    public interface OnSelecterLisenter {

        //选择更改回调
        void onSelecter(int position, boolean sort);

        //样式更改回调
        void onChangeStyle(boolean isSingleLine);
    }

    private void showPopupwinds() {

        View contentView = LayoutInflater.from(mContext).inflate(R.layout.tiaojian_popupwind_layout, null);
        final PopupWindow popupWindow = new PopupWindow(mContext);
        popupWindow.setContentView(contentView);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(100);

//        popupWindow.setAnimationStyle(R.style.contextMenuAnim);

        popupWindow.showAsDropDown(this);
        contentView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }

            }
        });
    }

    private PopupWindow mPopupWindow;
    private int popuwindLayout;

    public void setPopupWindowLayout(@LayoutRes int layout) {
        this.popuwindLayout = layout;
    }

    private PopupWindow createPopupwind() {
        View contentView = LayoutInflater.from(mContext).inflate(popuwindLayout, null);
        mPopupWindow = new PopupWindow(mContext);
        mPopupWindow.setContentView(contentView);
        mPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mPopupWindow.setHeight(300);
        contentView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });
        return mPopupWindow;
    }

    private void showPopupwind(View view) {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
        if (mPopupWindow != null) {
            mPopupWindow = null;
        }
        mPopupWindow = createPopupwind();
        mPopupWindow.showAsDropDown(view);
    }

    private void dismissPopupwind() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }
}
