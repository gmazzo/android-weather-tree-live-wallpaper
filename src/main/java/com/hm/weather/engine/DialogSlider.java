package com.hm.weather.engine;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.hm.weather.R;

public class DialogSlider extends AlertDialog implements OnSeekBarChangeListener, android.content.DialogInterface.OnClickListener {
    private int mCurrentValue;
    private TextView mCurrentView;
    private OnClickListener mListener;
    private int mMinValue;
    private SeekBar mSeekBar;
    private Object mTag;

    public interface OnClickListener {
        void onClick(Object obj, int i);
    }

    public DialogSlider(Context context, Object tag, String title, String leftStr, String rightStr, int min, int max, int value, OnClickListener onclicklistener) {
        super(context);
        this.mTag = tag;
        this.mListener = onclicklistener;
        Resources resources = context.getResources();
        setTitle(title);
        setButton(-1, resources.getText(17039370), this);
        setButton(-2, resources.getText(17039360), this);
        View view = LayoutInflater.from(context).inflate(R.layout.value_slider, null);
        setView(view);
        this.mSeekBar = (SeekBar) view.findViewById(R.id.SliderBar);
        this.mSeekBar.setMax(max - min);
        this.mSeekBar.setProgress(value - min);
        this.mSeekBar.setOnSeekBarChangeListener(this);
        this.mMinValue = min;
        this.mCurrentValue = value;
        TextView textview1 = (TextView) view.findViewById(R.id.SliderLabelRight);
        ((TextView) view.findViewById(R.id.SliderLabelLeft)).setText(leftStr);
        textview1.setText(rightStr);
        this.mCurrentView = (TextView) view.findViewById(R.id.SliderLabelCenter);
        this.mCurrentView.setText(Integer.toString(value));
    }

    public void onClick(DialogInterface dialoginterface, int i) {
        if (i == -1) {
            this.mListener.onClick(this.mTag, this.mCurrentValue);
        }
        dismiss();
    }

    public void onProgressChanged(SeekBar seekbar, int i, boolean flag) {
        this.mCurrentValue = this.mMinValue + i;
        this.mCurrentView.setText(Integer.toString(this.mCurrentValue));
    }

    public void onStartTrackingTouch(SeekBar seekbar) {
    }

    public void onStopTrackingTouch(SeekBar seekbar) {
    }
}
