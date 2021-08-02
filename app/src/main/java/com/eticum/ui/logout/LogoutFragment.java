package com.eticum.ui.logout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.eticum.activities.WelcomeActivity;
import com.eticum.databinding.FragmentLogoutBinding;
import com.eticum.services.EticumUserControlService;
import com.eticum.services.EticumVpnService;
import com.eticum.utils.CommonUtils;
import com.eticum.utils.SharedPreferencesUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogoutFragment extends Fragment {

    private FragmentLogoutBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLogoutBinding.inflate(inflater, container, false);

        binding.exitFromAppButton.setOnClickListener(v -> {
            if (SharedPreferencesUtils.checkAccountPasswordHash(binding.exitPassword.getText().toString())) {
                SharedPreferencesUtils.removeLoggedIn();
//                if(EticumVpnService.isRunning){
                    EticumVpnService.stop(getContext());
                    EticumUserControlService.stop();
//                }
                CommonUtils.startIntentActivity(getContext(), WelcomeActivity.class);
                getActivity().finish();
            } else binding.textInputError.setVisibility(View.VISIBLE);
        });

        return binding.getRoot();
    }
}
