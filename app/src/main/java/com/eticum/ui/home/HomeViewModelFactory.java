package com.eticum.ui.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.eticum.activities.EticumActivity;

public class HomeViewModelFactory implements ViewModelProvider.Factory {

    private final EticumActivity activity;

    public HomeViewModelFactory(EticumActivity activity) {
        this.activity = activity;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new HomeViewModel(activity);
    }
}
