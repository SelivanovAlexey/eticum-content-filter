package com.eticum.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eticum.R;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this, new HomeViewModelFactory(getActivity()))
                .get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.recyclerview_layout, container, false);
        final RecyclerView recyclerView = root.findViewById(R.id.eticum_recycler_view);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        homeViewModel.getAdapter().observe(getViewLifecycleOwner(), recyclerView::setAdapter);
        return root;
    }
}
