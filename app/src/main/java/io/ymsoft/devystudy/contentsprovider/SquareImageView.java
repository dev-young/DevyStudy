package io.ymsoft.devystudy.contentsprovider;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;

/** 넓이에 맞춰 정사각형를 유지하는 이미지뷰 */
public class SquareImageView extends ImageView {


    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        setMeasuredDimension(width, width);
    }
}
