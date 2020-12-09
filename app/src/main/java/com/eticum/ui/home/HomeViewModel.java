package com.eticum.ui.home;

import android.app.Activity;
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
import com.eticum.filter.FilterInfoHolder;
import com.eticum.services.EticumVpnService;
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

    public HomeViewModel(Activity activity) {
        adapter = new MutableLiveData<>();
        adapter.setValue(new RecycleViewAdapter(
                activity,
                getMappedProfileData(FilterInfoHolder.get()),
                (RecycleViewAdapter.OnCheckedChangeListener) activity));
    }

    public LiveData<RecyclerView.Adapter> getAdapter() {
        return adapter;
    }

    private List<StatusPageDataModel.ItemModel> getMappedProfileData(FilterInfoHolder holder) {
        List<StatusPageDataModel.ItemModel> arrayList = new ArrayList<>();
        arrayList.add(StatusPageDataModel.ItemModel.builder()
                .title(getString(R.string.userinfo))
                .optionModels(Arrays.asList(
                        StatusPageDataModel.OptionModel.builder()
                                .option(getString(R.string.name_ru))
                                .description(holder.getUser().getName())
                                .build(),
                        StatusPageDataModel.OptionModel.builder()
                                .option(getString(R.string.email))
                                .description(holder.getUser().getEmail())
                                .build()))
                .build());

        arrayList.add(StatusPageDataModel.ItemModel.builder()
                .title(getString(R.string.subscription))
                .optionModels(Collections.singletonList(StatusPageDataModel.OptionModel.builder()
                        .option(getString(R.string.till))
                        .description(formatter.format(holder.getUser().getSubscriptionTill()))
                        .build()))
                .build());

        arrayList.add(StatusPageDataModel.ItemModel.builder()
                .title(getString(R.string.filtering))
                .optionModels(
                        Arrays.asList(
                                StatusPageDataModel.OptionModel.builder()
                                        .option(getString(R.string.profile_name))
                                        .description(Optional.ofNullable(holder.getProfile().getName()).filter(StringUtils::isNotEmpty).orElse(getString(R.string.profile_name_default)))
                                        .build(),
                                StatusPageDataModel.OptionModel.builder()
                                        .option(getString(R.string.mode))
                                        .description(getFiltrationMode(holder.getProfile().getMode().toString()))
                                        .build(),
                                StatusPageDataModel.OptionModel.builder()
                                        .option(getString(R.string.age))
                                        .description(holder.getProfile().getAge().toString())
                                        .build(),
                                StatusPageDataModel.OptionModel.builder()
                                        .option(getString(R.string.mode_words))
                                        .description(getBooleanResult(getString(R.string.mode_words)))
                                        .build(),
                                StatusPageDataModel.OptionModel.builder()
                                        .option(getString(R.string.interactive_mode))
                                        .description(getBooleanResult(getString(R.string.interactive_mode)))
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