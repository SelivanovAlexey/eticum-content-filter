package com.eticum.ui.home;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.eticum.App;
import com.eticum.activities.EticumActivity;


import java.lang.ref.WeakReference;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<RecyclerView.Adapter> adapter;
    private WeakReference<Context> ctx = new WeakReference<>(App.getContext());

    public HomeViewModel(EticumActivity activity) {
        adapter = new MutableLiveData<>();
        adapter.setValue(activity.getRecycleViewAdapter());
    }

    public LiveData<RecyclerView.Adapter> getAdapter() {
        return adapter;
    }


}