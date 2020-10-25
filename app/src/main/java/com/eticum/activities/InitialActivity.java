package com.eticum.activities;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.eticum.R;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InitialActivity extends AppCompatActivity {

    private enum State {needAccessibility, needDeviceAdmin, needReboot}

    private State currentSetupState;
    private Animation animation;

    private TextView caption;
    private TextView description;
    private Button buttonText;

    private DevicePolicyManager dpm;
    private ComponentName deviceAdminReceiver;

    private final View.OnClickListener initialButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            if (currentSetupState == null) {
//                if (!isAccessibilityServiceEnabled(InitialActivity.this, UserActionsControlService.class)) {
//                    drawAccessibilityRequestLayout();
//                    currentSetupState = State.needAccessibility;
//                } else if (!isAdminFeatureEnabled(deviceAdminReceiver)) {
//                    drawDeviceAdminReceiverRequestLayout();
//                    currentSetupState = State.needDeviceAdmin;
//                } else {
//                    drawRequestRebootLayout();
//                    currentSetupState = State.needReboot;
//                }
//            } else if (!isAccessibilityServiceEnabled(InitialActivity.this, UserActionsControlService.class))
//                ActivityControlsUtils.turnOnAccessibility(InitialActivity.this);
//            else if (!isAdminFeatureEnabled(deviceAdminReceiver)) {
//                ActivityControlsUtils.activateDeviceAdmin(InitialActivity.this, deviceAdminReceiver);
//            } else drawRequestRebootLayout();

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.initial_setup_layout);
//
//        animation = AnimationUtils.loadAnimation(this, R.anim.fadein);
//        caption = findViewById(R.id.captionTextView);
//        description = findViewById(R.id.textViewDescription);
//        buttonText = findViewById(R.id.go_to_code_button);
//
//        dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
//        deviceAdminReceiver = new ComponentName(App.getContext(), RemoveAppAdminReceiver.class);
//        findViewById(R.id.go_to_code_button).setOnClickListener(initialButtonListener);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (currentSetupState != null) {
//            if (!isAccessibilityServiceEnabled(this, UserActionsControlService.class)) {
//                    drawAccessibilityRequestLayout();
//            } else {
//                currentSetupState = State.needDeviceAdmin;
//                if (!isAdminFeatureEnabled(deviceAdminReceiver)) {
//                        drawDeviceAdminReceiverRequestLayout();
//                } else {
//                    currentSetupState = State.needReboot;
//                    drawRequestRebootLayout();
//                }
//            }
//        }
    }

    public static boolean isAccessibilityServiceEnabled(Context context, Class<? extends AccessibilityService> service) {
        AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK);

        for (AccessibilityServiceInfo enabledService : enabledServices) {
            ServiceInfo enabledServiceInfo = enabledService.getResolveInfo().serviceInfo;
            if (enabledServiceInfo.packageName.equals(context.getPackageName()) && enabledServiceInfo.name.equals(service.getName()))
                return true;
        }
        return false;
    }

    private boolean isAdminFeatureEnabled(ComponentName devAdminReceiver) {
        DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        return dpm.isAdminActive(devAdminReceiver);
    }

    private void drawAccessibilityRequestLayout() {
        drawRequestSettingLayout(R.string.request_description_accessibility);
    }

    private void drawDeviceAdminReceiverRequestLayout() {
        drawRequestSettingLayout(R.string.request_description_deviceadmin);
    }

    private void drawRequestSettingLayout(int descriptionResourceId) {
        caption.setText(R.string.request_caption_settings);
        caption.startAnimation(animation);
        description.setText(descriptionResourceId);
        description.startAnimation(animation);
        buttonText.setText(R.string.button_forward_to);
        buttonText.startAnimation(animation);
        buttonText.setVisibility(View.VISIBLE);
    }

    /**
     * At the end of this method a proxy changer task starts.
     * This task changes the global proxy settings.
     * Proxy will be available after device restart.
     */
    private void drawRequestRebootLayout() {
//        caption.setText(R.string.request_caption_reboot);
//        caption.startAnimation(animation);
//        description.setText(R.string.request_description_reboot);
//        description.startAnimation(animation);
//        buttonText.setVisibility(View.INVISIBLE);
//        SharedPreferencesUtils.setPassedInitialSetup();
//
//        new ProxyChangerTask(dpm, log).execute(ProxyChangerTask.Action.enable, null);

    }
}
