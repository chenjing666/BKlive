package com.biaoke.bklive.rongcloud.widge;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.biaoke.bklive.R;

/**
 * Created by hasee on 2017/4/12.
 */

public class InputPanel extends LinearLayout {

    public InputPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.widget_input_panel, this);
    }
}
