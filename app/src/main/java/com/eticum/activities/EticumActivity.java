package com.eticum.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.eticum.R;
import com.eticum.databinding.ActivityMainBinding;
import com.eticum.databinding.CardsLayoutSwitchBinding;
import com.eticum.databinding.DrawerLayoutBinding;
import com.eticum.services.EticumVpnService;
import com.eticum.ui.RecycleViewAdapter;
import com.eticum.utils.CommonUtils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class EticumActivity extends AppCompatActivity {

    private DrawerLayoutBinding dBinding;
    private ActivityMainBinding amBinding;

    private CardsLayoutSwitchBinding clsBinding;

    private final static int REQUEST_VPN_ON_CREATE = 1;
    private final static int REQUEST_VPN_ON_SWITCH = 2;

    private AppBarConfiguration mAppBarConfiguration;
    @Getter
    private RecycleViewAdapter recycleViewAdapter;
    private EticumVpnService vpnService;

    @Override
    protected void onStart() {
        super.onStart();
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
        checkProtectionStatus();
        bindVpnService(serviceConnection);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(serviceConnection);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dBinding = DrawerLayoutBinding.inflate(getLayoutInflater());
        amBinding = dBinding.activityMain;
        clsBinding = CardsLayoutSwitchBinding.inflate(getLayoutInflater());

        recycleViewAdapter = new RecycleViewAdapter(this);

        setContentView(dBinding.getRoot());
        setSupportActionBar(amBinding.toolBar);
//        getSupportActionBar().setDisplayShowTitleEnabled(true);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_account, R.id.nav_logout)
                .setOpenableLayout(dBinding.drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(dBinding.navView, navController);

        initializeVpn(REQUEST_VPN_ON_CREATE);
    }

    private void checkProtectionStatus() {
        if (EticumVpnService.isRunning) {
            recycleViewAdapter.notifyItemChanged(0);
            amBinding.collapsingToolbar.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.eticum_green, getTheme()));
            amBinding.statusImageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_protected, getTheme()));
            amBinding.collapsingToolbar.setTitle(getString(R.string.device_protected));
        } else {
            amBinding.collapsingToolbar.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.greyish, getTheme()));
            amBinding.statusImageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_unprotected, getTheme()));
            amBinding.collapsingToolbar.setTitle(getString(R.string.device_unprotected));
        }
    }

    public void startFiltering() {
        initializeVpn(REQUEST_VPN_ON_SWITCH);
    }

    private void initializeVpn(int code) {
        onActivityResult(code, RESULT_OK, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
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

    private void enableVpn() {
        if (vpnService.isAlwaysOnEnabled() && vpnService.isBlockingEnabled()) {
            EticumVpnService.start(this);
            CommonUtils.showLoading(amBinding.indeterminateBar, amBinding.getRoot(), this);
        } else {
            // handle cancelation of enabling VPN
            runOnUiThread(() ->
                    Toast.makeText(this, getString(R.string.options_for_vpn_non_active), Toast.LENGTH_LONG)
                            .show()
            );
        }
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder binder) {
            EticumVpnService.ServiceBinder serviceBinder = (EticumVpnService.ServiceBinder) binder;
            vpnService = serviceBinder.getService();
            vpnService.registerCallback(() -> runOnUiThread(() -> {
                CommonUtils.hideLoading(amBinding.indeterminateBar, amBinding.getRoot(), EticumActivity.this);
                checkProtectionStatus();
            }));
        }

        public void onServiceDisconnected(ComponentName className) {
            vpnService = null;
            unbindService(serviceConnection);
        }
    };

    private void bindVpnService(ServiceConnection serviceConnection) {
        Intent intent = new Intent(this, EticumVpnService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
}
