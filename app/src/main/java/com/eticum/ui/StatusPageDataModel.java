package com.eticum.ui;

import com.eticum.R;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

public class StatusPageDataModel {

    @Getter
    private static final int[] iconsArray = {
            R.drawable.ic_user,
            R.drawable.ic_subscribe,
            R.drawable.ic_profile
    };

    @Builder
    @Getter
    public static class ItemModel {
        private final String title;
        private final List<OptionModel> optionModels;
    }

    @Builder
    @Getter
    public static class OptionModel {
        private final String option;
        private final String description;
    }
}
