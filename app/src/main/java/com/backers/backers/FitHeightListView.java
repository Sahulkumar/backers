package com.backers.backers;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Created by so on 2017-08-09.
 */

public class FitHeightListView extends ListView {

    private android.view.ViewGroup.LayoutParams params;
    private int oldCount = 0;

    public FitHeightListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    //old method, have some problem
//    @Override
//    protected void onDraw(Canvas canvas)
//    {
//        if (getCount() != oldCount)
//        {
//            int height = getChildAt(0).getHeight() + 1 ;
//            oldCount = getCount();
//            params = getLayoutParams();
//            params.height = getCount() * height;
////            params.height+=getCount();
//            setLayoutParams(params);
//        }
//
//        super.onDraw(canvas);
//    }

    //new method, perfect
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMeasureSpec_custom = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec_custom);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getMeasuredHeight();
    }
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev){
//        if(ev.getAction()==MotionEvent.ACTION_MOVE)
//            return true;
//        return super.dispatchTouchEvent(ev);
//    }
}