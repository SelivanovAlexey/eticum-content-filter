package com.eticum.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.VpnService;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import com.eticum.R;
import com.eticum.services.EticumVpnService;
import com.eticum.ui.RecycleViewAdapter;
import com.eticum.utils.SharedPreferencesUtils;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class EticumActivity extends AppCompatActivity implements RecycleViewAdapter.OnCheckedChangeListener {

    private final static int REQUEST_VPN_ON_CREATE = 1;
    private final static int REQUEST_VPN_ON_SWITCH = 2;

    private ComponentName devAdminReceiver;
    public static boolean wasStarted = false;

    private AppBarConfiguration mAppBarConfiguration;

    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private AppBarLayout appBar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private EticumVpnService vpnService;
    private Intent initIntent;

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

        initializeVpn(REQUEST_VPN_ON_CREATE);
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
        bindVpnService(serviceConnection);
    }


    @Override
    protected void onPause() {
        super.onPause();
        unbindService(serviceConnection);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            ((SwitchCompat) findViewById(R.id.switch_main)).setChecked(false);
            return;
        }
        if (requestCode == REQUEST_VPN_ON_CREATE) {
            log.debug("VPN ready to enable");
        }
        if (requestCode == REQUEST_VPN_ON_SWITCH) {
            log.debug("Enabling VPN");
            enableVpn();
        }
    }

    @Override
    public void onCheckedChange(boolean checked) {
        if (checked && EticumVpnService.isRunning) {
            log.debug("Skipping filtering action");
        } else if (checked && !EticumVpnService.isRunning) {
            log.debug("Try to start filtering");
            startFiltering();
        } else {
            log.debug("Try to stop filtering");
            stopFiltering();
        }
    }

    private void initializeVpn(int code) {
        Intent i = VpnService.prepare(this);
        if (i != null) {
//            ((SwitchCompat) findViewById(R.id.switch_main)).setChecked(false);
            startActivityForResult(i, code);
        } else {
            onActivityResult(code, RESULT_OK, null);
        }
    }

    public void startFiltering() {
        initializeVpn(REQUEST_VPN_ON_SWITCH);
    }

    private void enableVpn(){
        if (vpnService.isAlwaysOnEnabled() && vpnService.isBlockingEnabled()) {
            EticumVpnService.start(this);
        } else {
            // handle cancelation of enabling VPN
            ((SwitchCompat) findViewById(R.id.switch_main)).setChecked(false);
            Toast.makeText(this, "Need to enable Always-on VPN and Block connections " +
                    "without VPN in Eticum VPN settings", Toast.LENGTH_LONG)
                    .show();
        }
    }

    public void stopFiltering() {
        EticumVpnService.stop(this);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder binder) {
            EticumVpnService.ServiceBinder serviceBinder = (EticumVpnService.ServiceBinder) binder;
            vpnService = serviceBinder.getService();
        }

        public void onServiceDisconnected(ComponentName className) {
            vpnService = null;
        }
    };

    private void bindVpnService(ServiceConnection serviceConnection) {
        Intent intent = new Intent(this, EticumVpnService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
}
