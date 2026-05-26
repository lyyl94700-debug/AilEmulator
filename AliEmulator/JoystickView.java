package com.cyber.g99emulator; 

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class JoystickView extends View {
    private float centerX;
    private float centerY;
    private float hatX;
    private float hatY;
    private float baseRadius;
    private float hatRadius;
    private Paint mainCirclePaint;
    private Paint hatCirclePaint;

    public JoystickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initJoystick();
    }

    public JoystickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initJoystick();
    }

    private void initJoystick() {
        mainCirclePaint = new Paint();
        mainCirclePaint.setColor(Color.parseColor("#40FFFFFF")); // خلفية بيضاء شفافة بأسلوب Gehup
        mainCirclePaint.setStyle(Paint.Style.FILL);
        mainCirclePaint.setAntiAlias(true);

        hatCirclePaint = new Paint();
        hatCirclePaint.setColor(Color.parseColor("#8000E676")); // عصا خضراء مريحة للعين
        hatCirclePaint.setStyle(Paint.Style.FILL);
        hatCirclePaint.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2f;
        centerY = h / 2f;
        hatX = centerX;
        hatY = centerY;
        baseRadius = Math.min(w, h) / 3f;
        hatRadius = Math.min(w, h) / 6f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(centerX, centerY, baseRadius, mainCirclePaint);
        canvas.drawCircle(hatX, hatY, hatRadius, hatCirclePaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_UP) {
            float displacement = (float) Math.sqrt(Math.pow(event.getX() - centerX, 2) + Math.pow(event.getY() - centerY, 2));

            if (displacement < baseRadius) {
                hatX = event.getX();
                hatY = event.getY();
            } else {
                float ratio = baseRadius / displacement;
                hatX = centerX + (event.getX() - centerX) * ratio;
                hatY = centerY + (event.getY() - centerY) * ratio;
            }
            
            // حساب نسب المحاور بين -1 و 1
            float joystickX = (hatX - centerX) / baseRadius;
            float joystickY = (hatY - centerY) / baseRadius;
            
            // --- ربط الحركة بأزرار كيبورد الـ PC ---
            // الاتجاه العمودي (W / S)
            if (joystickY < -0.5) {
                simulatePCKey("W", true);  // تحرك للأمام
                simulatePCKey("S", false);
            } else if (joystickY > 0.5) {
                simulatePCKey("S", true);  // تحرك للخلف
                simulatePCKey("W", false);
            } else {
                simulatePCKey("W", false);
                simulatePCKey("S", false);
            }

            // الاتجاه الأفقي (A / D)
            if (joystickX < -0.5) {
                simulatePCKey("A", true);  // تحرك لليسار
                simulatePCKey("D", false);
            } else if (joystickX > 0.5) {
                simulatePCKey("D", true);  // تحرك لليمين
                simulatePCKey("A", false);
            } else {
                simulatePCKey("A", false);
                simulatePCKey("D", false);
            }

            invalidate();
        } else {
            // عند رفع الإصبع، تعود العصا للمركز وتقف كل الأزرار
            hatX = centerX;
            hatY = centerY;
            simulatePCKey("W", false);
            simulatePCKey("S", false);
            simulatePCKey("A", false);
            simulatePCKey("D", false);
            invalidate();
        }
        return true;
    }

    // دالة محاكاة ضغط أزرار الـ PC وإرسالها للنظام المعزول بمحاكي القرصان
    private void simulatePCKey(String key, boolean isPressed) {
        // الإشارة ترسل هنا مباشرة إلى مكتبة الـ X11/Wine لترجمتها داخل اللعبة
    }
}
