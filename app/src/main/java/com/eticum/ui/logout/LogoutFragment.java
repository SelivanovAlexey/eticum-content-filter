package com.eticum.ui.logout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.eticum.App;
import com.eticum.R;
import com.eticum.utils.ActivityControlsUtils;
import com.eticum.utils.SharedPreferencesUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogoutFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_logout, container, false);
        final Button button = root.findViewById(R.id.exit_from_app_button);
        EditText editText = root.findViewById(R.id.exit_password);
        TextView errorText = root.findViewById(R.id.text_input_error);

//        button.setOnClickListener(v -> {
//            if (SharedPreferencesUtils.checkAccountPasswordHash(editText.getText().toString())) {
//                SharedPreferencesUtils.removeLoggedIn();
//
//                new MaterialAlertDialogBuilder(getContext(), R.style.CustomDialogTheme)
//                        .setTitle(R.string.on_exit_caption)
//                        .setMessage(R.string.on_exit_description)
//                        .setPositiveButton(R.string.on_exit_button, (dialog, which) ->
//                                ActivityControlsUtils.startLoginActivity(getActivity()))
//                        .setCancelable(false)
//                        .show();
//            } else errorText.setVisibility(View.VISIBLE);
//        });
        return root;
    }
}
