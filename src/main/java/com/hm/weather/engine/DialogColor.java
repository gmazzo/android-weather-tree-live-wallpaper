package com.hm.weather.engine;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.SystemClock;
import android.util.StateSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.hm.weather.R;

public class DialogColor extends AlertDialog implements OnSeekBarChangeListener, android.content.DialogInterface.OnClickListener {
    static final int[] STATE_FOCUSED = new int[1];
    static final int[] STATE_PRESSED = new int[1];
    private SeekBar mAlpha;
    private int mColor;
    private SeekBar mHue;
    private IconPreviewDrawable mIcon;
    private OnClickListener mListener;
    private GradientDrawable mPreviewDrawable = new GradientDrawable();
    private SeekBar mSaturation;
    private Object mTag;
    private boolean mUseAlpha;
    private SeekBar mValue;

    public interface OnClickListener {
        void onClick(Object obj, int i);
    }

    static class IconPreviewDrawable extends Drawable {
        private Bitmap mBitmap;
        private int mTintColor;
        private Bitmap mTmpBitmap = Bitmap.createBitmap(this.mBitmap.getWidth(), this.mBitmap.getHeight(), Config.ARGB_8888);
        private Canvas mTmpCanvas = new Canvas(this.mTmpBitmap);

        public IconPreviewDrawable(Resources resources, int resId) {
            this.mBitmap = BitmapFactory.decodeResource(resources, resId);
        }

        public void draw(Canvas canvas) {
            Rect rect = getBounds();
            float f1 = ((float) (rect.width() - this.mBitmap.getWidth())) / 2.0f;
            float f5 = (0.75f * ((float) rect.height())) - (((float) this.mBitmap.getHeight()) / 2.0f);
            this.mTmpCanvas.drawColor(0, Mode.CLEAR);
            this.mTmpCanvas.drawBitmap(this.mBitmap, 0.0f, 0.0f, null);
            this.mTmpCanvas.drawColor(this.mTintColor, Mode.SRC_ATOP);
            canvas.drawBitmap(this.mTmpBitmap, f1, f5, null);
        }

        public int getOpacity() {
            return -1;
        }

        public void setAlpha(int alpha) {
        }

        public void setColorFilter(int color, Mode mode) {
            this.mTintColor = color;
        }

        public void setColorFilter(ColorFilter filter) {
        }
    }

    static class ScrollAnimation extends Animation {
        private static final long DURATION = 750;
        private float mCurrent;
        private float mFrom;
        private float mTo;

        public ScrollAnimation() {
            setDuration(DURATION);
            setInterpolator(new DecelerateInterpolator());
        }

        protected void applyTransformation(float interpolatedTime, Transformation t) {
            this.mCurrent = this.mFrom + ((this.mTo - this.mFrom) * interpolatedTime);
        }

        public float getCurrent() {
            return this.mCurrent;
        }

        public void startScrolling(float from, float to) {
            this.mFrom = from;
            this.mTo = to;
            startNow();
        }
    }

    static class TextSeekBarDrawable extends Drawable implements Runnable {
        private static final long DELAY = 50;
        private boolean mActive;
        private ScrollAnimation mAnimation;
        private int mDelta;
        private Paint mOutlinePaint;
        private Paint mPaint = new Paint();
        private Drawable mProgress;
        private String mText;
        private float mTextWidth;
        private float mTextXScale;

        public TextSeekBarDrawable(Resources res, int textId, boolean textScale, int drawableId) {
            this.mText = res.getString(textId);
            this.mProgress = res.getDrawable(drawableId);
            this.mPaint.setTypeface(Typeface.DEFAULT_BOLD);
            this.mPaint.setTextSize(16.0f);
            this.mPaint.setColor(-16777216);
            this.mOutlinePaint = new Paint(this.mPaint);
            this.mOutlinePaint.setStyle(Style.STROKE);
            this.mOutlinePaint.setStrokeWidth(3.0f);
            this.mOutlinePaint.setColor(-1140866304);
            this.mOutlinePaint.setMaskFilter(new BlurMaskFilter(1.0f, Blur.NORMAL));
            this.mTextWidth = this.mOutlinePaint.measureText(this.mText);
            if (textScale) {
                this.mTextXScale = 1.0f;
            }
            this.mAnimation = new ScrollAnimation();
        }

        public void draw(Canvas canvas) {
            this.mProgress.draw(canvas);
            if (this.mAnimation.hasStarted() && !this.mAnimation.hasEnded()) {
                this.mAnimation.getTransformation(AnimationUtils.currentAnimationTimeMillis(), null);
                this.mTextXScale = this.mAnimation.getCurrent();
            }
            Rect localRect = getBounds();
            float f7 = 6.0f + (this.mTextXScale * (((((float) localRect.width()) - this.mTextWidth) - 6.0f) - 6.0f));
            float f10 = (((float) localRect.height()) + this.mPaint.getTextSize()) / 2.0f;
            if (this.mActive) {
                this.mOutlinePaint.setAlpha(255);
                this.mPaint.setAlpha(255);
            } else {
                this.mOutlinePaint.setAlpha(127);
                this.mPaint.setAlpha(127);
            }
            canvas.drawText(this.mText, f7, f10, this.mOutlinePaint);
            canvas.drawText(this.mText, f7, f10, this.mPaint);
        }

        public int getOpacity() {
            return -1;
        }

        public boolean isStateful() {
            return true;
        }

        protected void onBoundsChange(Rect rect) {
            this.mProgress.setBounds(rect);
        }

        protected boolean onLevelChange(int level) {
            if (level < 4000 && this.mDelta <= 0) {
                this.mDelta = 1;
                this.mAnimation.startScrolling(this.mTextXScale, 1.0f);
                scheduleSelf(this, SystemClock.uptimeMillis() + DELAY);
            } else if (level > 6000 && this.mDelta >= 0) {
                this.mDelta = -1;
                this.mAnimation.startScrolling(this.mTextXScale, 0.0f);
                scheduleSelf(this, SystemClock.uptimeMillis() + DELAY);
            }
            return this.mProgress.setLevel(level);
        }

        protected boolean onStateChange(int[] states) {
            this.mActive = StateSet.stateSetMatches(DialogColor.STATE_FOCUSED, states) | StateSet.stateSetMatches(DialogColor.STATE_PRESSED, states);
            invalidateSelf();
            return false;
        }

        public void run() {
            this.mAnimation.getTransformation(AnimationUtils.currentAnimationTimeMillis(), null);
            this.mTextXScale = this.mAnimation.getCurrent();
            if (!this.mAnimation.hasEnded()) {
                scheduleSelf(this, SystemClock.uptimeMillis() + DELAY);
            }
            invalidateSelf();
        }

        public void setAlpha(int alpha) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }
    }

    static {
        STATE_FOCUSED[0] = 16842908;
        STATE_PRESSED[0] = 16842919;
    }

    public DialogColor(Context ctx, boolean useAlpha, Object tag, int color, OnClickListener listener, int iconId) {
        super(ctx);
        Drawable[] drawables;
        this.mUseAlpha = useAlpha;
        this.mTag = tag;
        this.mListener = listener;
        Resources resources = ctx.getResources();
        setTitle(R.string.color_dialog_title);
        setButton(-1, resources.getText(android.R.string.ok), this);
        setButton(-2, resources.getText(android.R.string.cancel), this);
        View mainView = LayoutInflater.from(ctx).inflate(R.layout.color_picker, null);
        setView(mainView);
        View preview = mainView.findViewById(R.id.preview);
        this.mPreviewDrawable.setCornerRadius(12.0f);
        if (useAlpha) {
            this.mIcon = new IconPreviewDrawable(resources, iconId);
            ClipDrawable r0 = new ClipDrawable(this.mPreviewDrawable, 48, 2);
            r0.setLevel(5000);
            drawables = new Drawable[]{r0, this.mIcon, resources.getDrawable(R.drawable.color_picker_frame)};
        } else {
            drawables = new Drawable[]{this.mPreviewDrawable, resources.getDrawable(R.drawable.color_picker_frame)};
        }
        preview.setBackgroundDrawable(new LayerDrawable(drawables));
        this.mHue = (SeekBar) mainView.findViewById(R.id.hue);
        this.mSaturation = (SeekBar) mainView.findViewById(R.id.saturation);
        this.mValue = (SeekBar) mainView.findViewById(R.id.value);
        this.mAlpha = (SeekBar) mainView.findViewById(R.id.alpha);
        this.mColor = color;
        float[] hsvColor = new float[3];
        Color.colorToHSV(color, hsvColor);
        int i10 = (int) ((((float) this.mHue.getMax()) * hsvColor[0]) / 360.0f);
        int i12 = (int) (((float) this.mSaturation.getMax()) * hsvColor[1]);
        int i14 = (int) (((float) this.mValue.getMax()) * hsvColor[2]);
        setupSeekBar(this.mHue, R.string.color_h, i10, resources, R.drawable.color_picker_hues);
        setupSeekBar(this.mSaturation, R.string.color_s, i12, resources, 17301612);
        setupSeekBar(this.mValue, R.string.color_v, i14, resources, 17301612);
        if (useAlpha) {
            setupSeekBar(this.mAlpha, R.string.color_a, (Color.alpha(color) * this.mAlpha.getMax()) / 255, resources, 17301612);
        } else {
            this.mAlpha.setVisibility(8);
        }
        updatePreview(color);
    }

    private void setupSeekBar(SeekBar seekBar, int textId, int progress, Resources res, int drawableId) {
        boolean textScale = false;
        if (progress < seekBar.getMax() / 2) {
            textScale = true;
        }
        seekBar.setProgressDrawable(new TextSeekBarDrawable(res, textId, textScale, drawableId));
        seekBar.setProgress(progress);
        seekBar.setOnSeekBarChangeListener(this);
    }

    private void update() {
        float[] arrayOfFloat = new float[]{((float) (this.mHue.getProgress() * 360)) / ((float) this.mHue.getMax()), ((float) this.mSaturation.getProgress()) / ((float) this.mSaturation.getMax()), ((float) this.mValue.getProgress()) / ((float) this.mValue.getMax())};
        if (this.mUseAlpha) {
            this.mColor = Color.HSVToColor((this.mAlpha.getProgress() * 255) / this.mAlpha.getMax(), arrayOfFloat);
        } else {
            this.mColor = Color.HSVToColor(arrayOfFloat);
        }
        updatePreview(this.mColor);
    }

    private void updatePreview(int color) {
        if (this.mUseAlpha) {
            this.mIcon.setColorFilter(color, Mode.SRC_ATOP);
            color |= -16777216;
        }
        this.mPreviewDrawable.setColor(color);
        this.mPreviewDrawable.invalidateSelf();
    }

    public void onClick(DialogInterface dialog, int which) {
        if (which == -1) {
            this.mListener.onClick(this.mTag, this.mColor);
        }
        dismiss();
    }

    public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
        update();
    }

    public void onStartTrackingTouch(SeekBar bar) {
    }

    public void onStopTrackingTouch(SeekBar bar) {
    }
}
