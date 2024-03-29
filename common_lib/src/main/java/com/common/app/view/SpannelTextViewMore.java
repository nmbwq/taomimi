package com.common.app.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.common.R;


/**
 * Author: guoning
 * Date: 2017/10/3
 * Description:
 */

public class SpannelTextViewMore extends View {

    private int shopType;//店铺类型
    private Bitmap bitmap;
    private Paint paint;
    private String drawText;
    private String first = "";
    float mTextWidth;
    private Rect rect = new Rect();
    private float scaleDenisty;

    public SpannelTextViewMore(Context context) {
        this(context, null);
    }

    public SpannelTextViewMore(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpannelTextViewMore(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SpannelTextViewSinge);
        shopType = typedArray.getInt(R.styleable.SpannelTextViewSinge_shopType, 1);
        drawText = typedArray.getString(R.styleable.SpannelTextViewSinge_drawText);
        typedArray.recycle();

        scaleDenisty = getResources().getDisplayMetrics().scaledDensity;

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(12 * scaleDenisty);
        paint.setStyle(Paint.Style.FILL);
        //设置字体的颜色
        paint.setColor(getResources().getColor(R.color.c_5D5D5D));

        mTextWidth = paint.measureText(drawText) * getResources().getDisplayMetrics().scaledDensity;

        //不要再onDraw中处理这一步,应该避免在onDraw中分配对象内存
//        BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(shopType == 1 ? R.drawable.ic_tb_more : R.drawable.ic_tm_more);
        BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(shopType == 1 ? R.drawable.ic_tb_singe : R.drawable.ic_tm_singe);
        bitmap = drawable.getBitmap();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) getLayoutParams();
        canvas.drawBitmap(bitmap, lp.leftMargin, lp.topMargin, paint);
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();

        if (bitmap.getWidth() + mTextWidth + lp.leftMargin + lp.rightMargin > getMeasuredWidth()) {
            //换行
            for (int x = 0, length = drawText.length(); x < length; x++) {
                float v = paint.measureText(drawText.substring(0, x + 1));
                if (bitmap.getWidth() + v + lp.leftMargin + lp.rightMargin >= getMeasuredWidth()) {
                    first = drawText.substring(0, x - 1) + "";
                    break;
                }else {
                    first = drawText;
                }
            }
        }else {
            first = drawText;
        }

        canvas.drawText(first, 3 * scaleDenisty + lp.leftMargin + bitmap.getWidth(), (3.5f * scaleDenisty + 0.5f) + lp.topMargin + (fontMetrics.descent - fontMetrics.ascent) / 2, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);

        setMeasuredDimension(width, (int) (20 * scaleDenisty));
    }

    @SuppressWarnings("必须在布局中定义默认的drawText")
    public void setDrawText(String drawText) {

        if (null == drawText && drawText.length() <= 0) {
            this.drawText = "         ";
        } else {
            this.drawText = drawText;
        }

        mTextWidth = paint.measureText(drawText) * getResources().getDisplayMetrics().scaledDensity;
//        measure(0,0);
//        invalidate();
        //三步流程都走
        requestLayout();
    }

    public void setShopType(int shopType) {
        this.shopType = shopType;
//        BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(shopType == 1 ? R.drawable.ic_tb_more : R.drawable.ic_tm_more);
        BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(shopType == 1 ? R.drawable.ic_tb_singe : R.drawable.ic_tm_singe);
        bitmap = drawable.getBitmap();
        //只走onDraw
        invalidate();
        //postInvalidate();
    }

}
