package com.eticum.activities;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.eticum.BuildConfig;
import com.eticum.R;
import com.eticum.databinding.InitialSetupLayoutBinding;
import com.eticum.receivers.RemoveAppAdminReceiver;
import com.eticum.services.EticumUserControlService;
import com.eticum.services.EticumVpnService;
import com.eticum.utils.CommonUtils;
import com.eticum.utils.SecurityUtils;
import com.eticum.utils.SharedPreferencesUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import lombok.val;

import static android.app.admin.DevicePolicyManager.EXTRA_DEVICE_ADMIN;
import static com.eticum.utils.CommonUtils.prepareIntent;
import static com.eticum.utils.CommonUtils.startIntentActivity;

@Slf4j
public class InitialActivity extends AppCompatActivity {

    private InitialSetupLayoutBinding binding;
    private Animation animation;

    private ComponentName deviceAdminReceiver;

    boolean initFlag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = InitialSetupLayoutBinding.inflate(getLayoutInflater());
        animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        deviceAdminReceiver = new ComponentName(this, RemoveAppAdminReceiver.class);

        binding.goToCodeButton.setOnClickListener(v -> validateSettings());
        setContentView(binding.getRoot());
        initFlag = true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!initFlag) validateSettings();
        initFlag = false;
    }

    private void validateSettings() {
        if (!isAccessibilityServiceEnabled(this))
            drawAccessibilityRequestLayout();
        else if (!isAdminFeatureEnabled(deviceAdminReceiver))
            drawDeviceAdminReceiverRequestLayout();
        else if (!isVpnPrepared())
            drawVpnCreateAdpaterLayout();
        else if (!isVpnConfigured())
            drawVpnRequestLayout();
        else if (!SecurityUtils.checkInstalledEticumCert()) {
            drawInstallCertLayout();
        } else {
            SharedPreferencesUtils.setPassedInitialSetup();
            CommonUtils.startIntentActivity(this, Settings.ACTION_SETTINGS,
                    Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            CommonUtils.startIntentActivity(this, EticumActivity.class);
            finish();
        }
    }

    public boolean isAccessibilityServiceEnabled(Context context){
        String prefString = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        return prefString!= null && prefString.contains(context.getPackageName() + "/" + (EticumUserControlService.class.getName()));
    }

    private boolean isAdminFeatureEnabled(ComponentName devAdminReceiver) {
        DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        return dpm.isAdminActive(devAdminReceiver);
    }

    public boolean isVpnConfigured() {
        return StringUtils.equals(
                Settings.Secure.getString(getContentResolver(), "always_on_vpn_app"),
                BuildConfig.APPLICATION_ID)
                &&
                Settings.Secure.getInt(getContentResolver(), "always_on_vpn_lockdown", 0) != 0;
    }

    private boolean isVpnPrepared() {
        val i = EticumVpnService.prepare(this);
        return i == null;
    }

    private void drawAccessibilityRequestLayout() {
        drawRequestSettingLayout(R.string.request_description_accessibility,
                v -> startIntentActivity(this,
                        Settings.ACTION_ACCESSIBILITY_SETTINGS));
    }

    private void drawDeviceAdminReceiverRequestLayout() {
        drawRequestSettingLayout(R.string.request_description_deviceadmin,
                v -> {
                    val intent = prepareIntent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    intent.putExtra(EXTRA_DEVICE_ADMIN, deviceAdminReceiver);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivityForResult(intent, 0);
                });
    }

    private void drawVpnCreateAdpaterLayout() {
        drawRequestSettingLayout(R.string.request_description_create_adapter,
                v -> {
                    val intent = EticumVpnService.prepare(this);
                    startActivityForResult(intent, 0);
                });
    }

    private void drawVpnRequestLayout() {
        drawRequestSettingLayout(R.string.request_description_vpn,
                v -> startIntentActivity(this,
                        Settings.ACTION_VPN_SETTINGS,
                        Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    private void drawInstallCertLayout() {
        drawRequestSettingLayout(R.string.request_certificate,
                v -> {
                    SecurityUtils.loadEticumCA(this);
                    startIntentActivity(this,
                            Settings.ACTION_SECURITY_SETTINGS);
                });
    }

    private void drawRequestSettingLayout(int descriptionResourceId, View.OnClickListener listener) {
        ((TextView) findViewById(R.id.captionTextViewInit)).setText(R.string.request_caption_settings);
        findViewById(R.id.captionTextViewInit).startAnimation(animation);
        binding.textViewDescriptionInit.startAnimation(animation);
        binding.textViewDescriptionInit.setText(descriptionResourceId);
        binding.goToCodeButton.startAnimation(animation);
        binding.goToCodeButton.setVisibility(View.VISIBLE);
        binding.goToCodeButton.setText(R.string.button_forward_to);
        binding.goToCodeButton.setOnClickListener(listener);
    }
}
