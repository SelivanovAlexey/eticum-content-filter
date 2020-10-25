package com.eticum.ui.home;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.eticum.App;
import com.eticum.R;
import com.eticum.api.EticumApiService;
import com.eticum.api.http.model.Profile;
import com.eticum.api.http.model.User;
import com.eticum.ui.RecycleViewAdapter;
import com.eticum.ui.StatusPageDataModel;
import com.eticum.utils.Optional;

import org.apache.commons.lang3.StringUtils;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<RecyclerView.Adapter> adapter;
    private WeakReference<Context> ctx = new WeakReference<>(App.getContext());

    private final static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    public HomeViewModel() {
        adapter = new MutableLiveData<>();
        adapter.setValue(new RecycleViewAdapter(App.getContext(), getMappedProfileData(EticumApiService.getUser(), EticumApiService.getProfile())));
    }

    public LiveData<RecyclerView.Adapter> getAdapter() {
        return adapter;
    }

    private List<StatusPageDataModel.ItemModel> getMappedProfileData(User user, Profile profile) {
        List<StatusPageDataModel.ItemModel> arrayList = new ArrayList<>();
        arrayList.add(StatusPageDataModel.ItemModel.builder()
                .title(getString(R.string.userinfo))
                .optionModels(Arrays.asList(
                        StatusPageDataModel.OptionModel.builder()
                                .description(getString(R.string.name_ru))
                                .option(user.getName())
                                .build(),
                        StatusPageDataModel.OptionModel.builder()
                                .description(getString(R.string.email))
                                .option(user.getEmail())
                                .build()))
                .build());

        arrayList.add(StatusPageDataModel.ItemModel.builder()
                .title(getString(R.string.subscription))
                .optionModels(Collections.singletonList(StatusPageDataModel.OptionModel.builder()
                        .description(getString(R.string.till))
                        .option(formatter.format(user.getSubscriptionTill()))
                        .build()))
                .build());

        arrayList.add(StatusPageDataModel.ItemModel.builder()
                .title(getString(R.string.filtering))
                .optionModels(
                        Arrays.asList(
                                StatusPageDataModel.OptionModel.builder()
                                        .description(getString(R.string.profile_name))
                                        .option(Optional.ofNullable(profile.getName()).filter(StringUtils::isNotEmpty).orElse(getString(R.string.profile_name_default)))
                                        .build(),
                                StatusPageDataModel.OptionModel.builder()
                                        .description(getString(R.string.mode))
                                        .option(getFiltrationMode(profile.getMode().toString()))
                                        .build(),
                                StatusPageDataModel.OptionModel.builder()
                                        .description(getString(R.string.age))
                                        .option(profile.getAge().toString())
                                        .build(),
                                StatusPageDataModel.OptionModel.builder()
                                        .description(getString(R.string.mode_words))
                                        .option(getBooleanResult(getString(R.string.mode_words)))
                                        .build(),
                                StatusPageDataModel.OptionModel.builder()
                                        .description(getString(R.string.interactive_mode))
                                        .option(getBooleanResult(getString(R.string.interactive_mode)))
                                        .build()))
                .build());
        return arrayList;
    }

    private String getFiltrationMode(String mode) {
        switch (mode) {
            case "allow":
                return getString(R.string.profile_mode_allow);
            case "deny":
                return getString(R.string.profile_mode_deny);
            case "info":
            default:
                return getString(R.string.profile_mode_info);
        }
    }

    private String getBooleanResult(String mode) {
        switch (mode) {
            case "true":
                return getString(R.string.yes_ru);
            default:
            case "false":
                return getString(R.string.no_ru);
        }
    }

    private String getString(int resId) {
        return ctx.get().getString(resId);
    }
}