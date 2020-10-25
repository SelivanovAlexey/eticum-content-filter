package com.eticum.ui;

import com.eticum.R;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

public class StatusPageDataModel {

    @Getter
    private static int[] iconsArray = {
            R.drawable.ic_user,
            R.drawable.ic_subscribe,
            R.drawable.ic_profile
    };

    @Builder
    @Getter
    public static class ItemModel {
        private String title;
        private List<OptionModel> optionModels;
    }

    @Builder
    @Getter
    public static class OptionModel {
        private String option;
        private String description;
    }
}
