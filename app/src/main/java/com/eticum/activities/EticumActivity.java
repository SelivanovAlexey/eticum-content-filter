package com.eticum.activities;

import android.content.ComponentName;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import com.eticum.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class EticumActivity extends AppCompatActivity {

    private ComponentName devAdminReceiver;
    public static boolean wasStarted = false;

    private AppBarConfiguration mAppBarConfiguration;

    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private AppBarLayout appBar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private void findViews() {
        appBar = findViewById(R.id.appBarLayout);
        toolbar = findViewById(R.id.toolBar);
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.nav_view);
        recyclerView = findViewById(R.id.eticum_recycler_view);
    }

    public EticumActivity() {
//        this.devAdminReceiver = new ComponentName(App.getContext(), RemoveAppAdminReceiver.class);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wasStarted = true;
        setContentView(R.layout.drawer_layout);
        findViews();

        toolbar.setTitle(R.string.device_protected);
        setSupportActionBar(toolbar);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_account, R.id.nav_logout)
                .setOpenableLayout(drawerLayout)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (!isAccessibilityServiceEnabled(this, UserActionsControlService.class)) {
//            new MaterialAlertDialogBuilder(this, R.style.CustomDialogTheme)
//                    .setTitle("Еще немного")
//                    .setMessage("Для завершения настройки безопасности необходимо предоставить приложению расшиернные права")
//                    .setPositiveButton("Перейти", (dialog, which) -> {
//                        ActivityControlsUtils.turnOnAccessibility(this);
//                    })
//                    .setCancelable(false)
//                    .show();
//        } else if (!isAdminFeatureEnabled(devAdminReceiver)) {
//            new MaterialAlertDialogBuilder(this, R.style.CustomDialogTheme)
//                    .setTitle("Последний раз")
//                    .setMessage("Для окончательной настройки необходимо разрешить приложению функцию администрирования")
//                    .setPositiveButton("Перейти", (dialog, which) -> ActivityControlsUtils.activateDeviceAdmin(this, devAdminReceiver))
//                    .setCancelable(false)
//                    .show();
//        }
    }
}
