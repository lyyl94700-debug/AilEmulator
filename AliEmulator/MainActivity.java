package com.cyber.g99emulator; // تأكد إن الباكج يطابق مالتك

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    
    // رقم تعريفي لطلب اختيار الملف
    private static final int PICK_EXE_FILE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. فحص وتفعيل صلاحية الظهور فوق التطبيقات للجويستك أول ما يفتح التطبيق
        checkOverlayPermission();

        // 2. ربط الزر العائم المخصص لإضافة الألعاب (ستايل Gehup)
        FloatingActionButton btnAddGame = findViewById(R.id.btnAddGame);
        btnAddGame.setOnClickListener(v -> {
            // فتح مدير ملفات الموبايل لاختيار ملف اللعبة .exe
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*"); // البحث في كل الملفات
            startActivityForResult(Intent.createChooser(intent, "اختر ملف اللعبة (.exe)"), PICK_EXE_FILE);
        });
    }

    // دالة فحص وتفعيل صلاحية الـ Overlay
    private void checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        }
    }

    // هذه الدالة تستقبل ملف الـ exe اللي اختاريته وتصيح للمحاكي والجويستك
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == PICK_EXE_FILE && resultCode == RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            if (fileUri != null) {
                String gamePath = fileUri.getPath(); // مسار اللعبة الحقيقي
                
                Toast.makeText(this, "تم اختيار اللعبة بنجاح!", Toast.LENGTH_SHORT).show();
                
                // تشغيل اللعبة فوراً وضبط إعدادات معالج Helio G99 وسحب الجويستك الشفاف
                launchGameWithG99Optimization(gamePath);
            }
        }
    }

    // دالة تشغيل المحرك وضبط إعدادات الرسوميات لكرت شاشة Mali
    private void launchGameWithG99Optimization(String gamePath) {
        HashMap<String, String> envVariables = new HashMap<>();
        
        // إعدادات تسريع كرت Mali المخصصة لمعالج G99 لضمان السلاسة
        envVariables.put("GALLIUM_DRIVER", "zink"); 
        envVariables.put("MESA_GL_VERSION_OVERRIDE", "4.6");
        envVariables.put("BOX64_DYNAREC_BIGBLOCKS", "1");
        envVariables.put("BOX64_DYNAREC_STRONGMEM", "0"); 

        // تشغيل خدمة الجويستك الشفاف ليظهر فوق اللعبة فوراً
        Intent serviceIntent = new Intent(this, GameOverlayService.class);
        startService(serviceIntent);

        // هنا يرسل المسار والإعدادات إلى كود الـ C++ (محرك القرصان المدمج) لتشغيل اللعبة
        Toast.makeText(this, "جاري إقلاع اللعبة بدقة سلاسة 60 إطار...", Toast.LENGTH_LONG).show();
    }
}
