package com.example.mnemosyne.chat;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by mnemosyne on 2016/3/11.
 */
public class DetectSizeChangeRelativeLayout extends RelativeLayout {
    public DetectSizeChangeRelativeLayout(Context context) {
        super(context);
    }
    public DetectSizeChangeRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public DetectSizeChangeRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        ChatRoom.msgHandler.post(ChatRoom.fullScroll);
    }
}
