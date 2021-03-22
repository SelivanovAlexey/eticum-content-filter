package com.eticum.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.eticum.activities.EticumActivity;
import com.eticum.ui.RecycleViewAdapter;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<RecyclerView.Adapter<RecycleViewAdapter.ViewHolder>> adapter;

    public HomeViewModel(EticumActivity activity) {
        adapter = new MutableLiveData<>();
        adapter.setValue(activity.getRecycleViewAdapter());
    }

    public LiveData<RecyclerView.Adapter<RecycleViewAdapter.ViewHolder>> getAdapter() {
        return adapter;
    }


}