package com.eticum.activities;

import android.animation.LayoutTransition;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.eticum.R;
import com.eticum.api.EticumApiService;
import com.eticum.api.http.utils.AuthCallback;
import com.eticum.utils.ActivityControlsUtils;
import com.eticum.utils.SharedPreferencesUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WelcomeActivity extends AppCompatActivity {

    private RelativeLayout layoutMain, layoutLabel, parentLayout, helpActions, root;
    private Handler handler = new Handler();

    private EditText loginUsername;
    private EditText loginPassword;
    private Button loginButton;

    private final AuthCallback authCallback = new AuthCallback() {
        @Override
        public void onSuccess() {
            ActivityControlsUtils.startEticumActivity(WelcomeActivity.this);
            finish();
        }

        @Override
        public void onFailure(Integer errorCode) {
            handleAuthError(errorCode);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        layoutMain = findViewById(R.id.rellayMain);
        layoutLabel = findViewById(R.id.rellayLabel);
        parentLayout = findViewById(R.id.rellayParent);
        helpActions = findViewById(R.id.helpActions);
        root = findViewById(R.id.rellayRoot);

        // If user not logged in Eticum (first launch or else) go through login procedure
        if (!SharedPreferencesUtils.isLoggedIn()) {
            proceedAnimation();

            loginButton = findViewById(R.id.loginButton);
            loginUsername = findViewById(R.id.username);
            loginPassword = findViewById(R.id.password);
            TextView registerLink = findViewById(R.id.registerTextView);
            registerLink.setClickable(true);
            registerLink.setMovementMethod(LinkMovementMethod.getInstance());

            loginButton.setOnClickListener(v -> {
                String loginText = loginUsername.getText().toString();
                String passwordText = loginPassword.getText().toString();
                SharedPreferencesUtils.setAccountPasswordHash(passwordText);

                EticumApiService.doAuth(loginText, passwordText, authCallback);
            });
        }
        // If user already logged in, he should have an access token. In this case will proceed authentication by access token
        else EticumApiService.doAuth(authCallback);
    }

    private void handleAuthError(Integer errorCode) {
    }

    private void proceedAnimation() {
        LayoutTransition layoutTransition = new LayoutTransition();
        layoutTransition.setDuration(LayoutTransition.APPEARING, 800);
        parentLayout.setLayoutTransition(layoutTransition);
        root.setLayoutTransition(layoutTransition);
        handler.postDelayed(() -> layoutLabel.setVisibility(View.VISIBLE), 1000);
        handler.postDelayed(() -> {
            layoutMain.setVisibility(View.VISIBLE);
            helpActions.setVisibility(View.VISIBLE);
        }, 1800);
    }
}
