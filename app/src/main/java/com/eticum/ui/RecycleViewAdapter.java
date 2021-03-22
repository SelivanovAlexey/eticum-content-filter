package com.eticum.ui;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.eticum.App;
import com.eticum.R;
import com.eticum.activities.EticumActivity;
import com.eticum.databinding.CardsLayoutBinding;
import com.eticum.databinding.CardsLayoutSwitchBinding;
import com.eticum.filter.FilterInfoHolder;
import com.eticum.services.EticumVpnService;
import com.eticum.utils.Optional;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {

    private static final Integer VIEW_TYPE_SWITCH = 0;
    private static final Integer VIEW_TYPE_CARD = 1;
    private final List<StatusPageDataModel.ItemModel> dataSet;

    private final static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);


    private final EticumActivity activity;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CardsLayoutSwitchBinding clsBinding;
        private CardsLayoutBinding clBinding;

        public ViewHolder(CardsLayoutSwitchBinding clsBinding) {
            super(clsBinding.getRoot());
            this.clsBinding = clsBinding;
        }

        public ViewHolder(CardsLayoutBinding clBinding) {
            super(clBinding.getRoot());
            this.clBinding = clBinding;
        }
    }

    public RecycleViewAdapter(EticumActivity activity) {
        this.activity = activity;
        this.dataSet = getMappedProfileData(FilterInfoHolder.get());
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent,
                                         int viewType) {
        return viewType == VIEW_TYPE_CARD ?
                new ViewHolder(CardsLayoutBinding.inflate(LayoutInflater.from(activity))) :
                new ViewHolder(CardsLayoutSwitchBinding.inflate(LayoutInflater.from(activity)));
    }

    @Override
    public void onBindViewHolder(@NotNull final ViewHolder holder, final int listPosition) {
        if (listPosition != 0) {
            ImageView image = holder.clBinding.listImage;
            TextView title = holder.clBinding.textViewTitle;
            LinearLayout linearLayout = holder.clBinding.linear;
            RelativeLayout relativeLayoutOption = holder.clBinding.relativeLayoutOption;

            linearLayout.removeView(relativeLayoutOption);

            for (StatusPageDataModel.OptionModel optionModel : dataSet.get(listPosition - 1).getOptionModels()) {
                createCardViewRecord(optionModel, linearLayout);
            }

            Glide.with(App.getContext()).load(StatusPageDataModel.getIconsArray()[listPosition - 1]).into(image);
            title.setText(dataSet.get(listPosition - 1).getTitle());
        } else {
            Button btn = holder.clsBinding.protectButton;
            if (EticumVpnService.isRunning) btn.setVisibility(View.GONE);
            btn.setOnClickListener(view -> {
                if (EticumVpnService.isRunning) {
                    log.debug("Vpn service already enabled");
                } else {
                    log.debug("Attempt to start filtering");
                    activity.startFiltering();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? VIEW_TYPE_SWITCH : VIEW_TYPE_CARD;
    }

    private void createCardViewRecord(StatusPageDataModel.OptionModel optionModel, LinearLayout linearLayout) {
        TextView newOption = new TextView(activity);
        TextView newDescription = new TextView(activity);
        newOption.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        newOption.setTextAppearance(R.style.MontserratTextAppearanceBlack);
        newDescription.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        newDescription.setTextAppearance(R.style.MontserratTextAppearanceBlack);
        newOption.setText(optionModel.getOption());
        newDescription.setText(optionModel.getDescription());
        newDescription.setGravity(Gravity.END);

        RelativeLayout newRelativeLayout = new RelativeLayout(activity);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        layoutParams.setMargins(0, pxToDp(8), 0, pxToDp(6));
        layoutParams.setMarginStart(pxToDp(18));
        newRelativeLayout.setLayoutParams(layoutParams);

        newRelativeLayout.setGravity(Gravity.FILL);
        newRelativeLayout.addView(newOption);
        newRelativeLayout.addView(newDescription);
        linearLayout.addView(newRelativeLayout);
    }

    private int pxToDp(int pixels) {
        final float scale = activity.getResources().getDisplayMetrics().density;
        return (int) (pixels * scale + 0.5f);
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
        return activity.getString(resId);
    }
}