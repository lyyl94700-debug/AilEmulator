package com.cyber.g99emulator; // تأكد أن الباكج مالتك مكتوب صح هنا

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

public class GameOverlayService extends Service {
    private WindowManager windowManager;
    private View overlayView;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        
        // ربط ملف التصميم الشفاف بالخدمة
        overlayView = LayoutInflater.from(this).inflate(R.layout.overlay_controls, null);

        // إعدادات إظهار الأزرار فوق الألعاب والتطبيقات (Overlay Window)
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        // إضافة واجهة الجويستك للشاشة فوراً
        windowManager.addView(overlayView, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // إخفاء الجويستك عند إغلاق اللعبة أو المحاكي
        if (overlayView != null) {
            windowManager.removeView(overlayView);
        }
    }
}
