package com.eticum.activities;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.eticum.Constants;
import com.eticum.R;
import com.eticum.api.EticumApiService;
import com.eticum.api.http.utils.AuthCallback;
import com.eticum.databinding.ActivityLoginBinding;
import com.eticum.utils.CommonUtils;
import com.eticum.utils.SharedPreferencesUtils;


import lombok.extern.slf4j.Slf4j;

import static com.eticum.utils.CommonUtils.runAsync;

@Slf4j
public class WelcomeActivity extends AppCompatActivity {

    private ActivityLoginBinding alBinding;

    private final Handler handler = new Handler();

    private final AuthCallback authCallback = new AuthCallback() {
        @Override
        public void onSuccess() {
            SharedPreferencesUtils.setLoggedIn();
            SharedPreferencesUtils.setAccountPasswordHash(alBinding.password.getText().toString());
            CommonUtils.startIntentActivity(WelcomeActivity.this, EticumActivity.class);
            finish();
        }

        @Override
        public void onFailure(Integer errorCode) {
            runOnUiThread(() ->
                    CommonUtils.hideLoading(alBinding.indeterminateBar, alBinding.getRoot(), WelcomeActivity.this));
            handleAuthError(errorCode);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(alBinding.getRoot());

        // If user not logged in Eticum (first launch or else) go through login procedure
        if (!SharedPreferencesUtils.isLoggedIn()) {
            proceedAnimation();

            alBinding.registerTextView.setClickable(true);
            alBinding.registerTextView.setMovementMethod(LinkMovementMethod.getInstance());

            alBinding.loginButton.setOnClickListener(v -> {
                String loginText = alBinding.username.getText().toString();
                String passwordText = alBinding.password.getText().toString();

                CommonUtils.showLoading(alBinding.indeterminateBar, alBinding.getRoot(), this);
                EticumApiService.doAuth(loginText, passwordText, authCallback);
            });
        }
        // If user already logged in, he should have an access token. In this case will proceed authentication by access token
        else EticumApiService.doAuth(authCallback);
    }

    //TODO
    private void handleAuthError(Integer errorCode) {
        if (errorCode == 99) alBinding.textinputError.setText(Constants.DEFAULT_HTTP_ERROR);
        else alBinding.textinputError.setText(Constants.errorList.get(--errorCode));
        handler.postDelayed(() -> alBinding.textinputError.setVisibility(View.VISIBLE), 0);
        handler.postDelayed(() -> alBinding.textinputError.setVisibility(View.GONE), 7000);
    }

    private void proceedAnimation() {
        LayoutTransition layoutTransition = new LayoutTransition();
        layoutTransition.setDuration(LayoutTransition.APPEARING, 800);
        alBinding.rellayParent.setLayoutTransition(layoutTransition);
        alBinding.rellayRoot.setLayoutTransition(layoutTransition);
        alBinding.aboveRellayParent.setLayoutTransition(layoutTransition);
        alBinding.getRoot().setLayoutTransition(layoutTransition);

        handler.postDelayed(() -> alBinding.rellayLabel.setVisibility(View.VISIBLE), 1000);
        handler.postDelayed(() -> {
            alBinding.rellayMain.setVisibility(View.VISIBLE);
            alBinding.helpActions.setVisibility(View.VISIBLE);
        }, 1800);
    }
}
