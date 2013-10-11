package com.excilys.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;

import com.excilys.android.R;

public class PlayButton extends ImageButton {

    private boolean play;

    public PlayButton(Context context) {
        super(context);
    }

    public PlayButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PlayButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void toggle() {
        if (play){
            setImageResource(R.drawable.ic_action_9_av_play);
        } else {
            setImageResource(R.drawable.ic_action_9_av_pause);
        }
        play = !play;
    }

}
