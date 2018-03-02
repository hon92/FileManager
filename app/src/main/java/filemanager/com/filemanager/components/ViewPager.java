package filemanager.com.filemanager.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Honza on 05.12.2017.
 */

public class ViewPager extends android.support.v4.view.ViewPager {
    private boolean scrollable = true;

    public ViewPager(Context context) {
        super(context);
    }

    public ViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!scrollable) return false;
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!scrollable) return false;
        return super.onTouchEvent(ev);
    }

    public void setScrollEnabled(boolean value) {
        scrollable = value;
    }
}
