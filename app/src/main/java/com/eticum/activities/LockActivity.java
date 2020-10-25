package com.eticum.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class LockActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.lock_layout);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
//                WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
//        findViewById(R.id.unlock_button).setOnClickListener(v -> {
//            Intent startMain = new Intent(Intent.ACTION_MAIN);
//            startMain.addCategory(Intent.CATEGORY_HOME);
//            startMain.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//            startMain.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//            startActivity(startMain);
//            finishAffinity();
//        });
    }
}
