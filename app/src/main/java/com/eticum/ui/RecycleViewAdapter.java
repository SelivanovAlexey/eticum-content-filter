package com.eticum.ui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.eticum.App;
import com.eticum.R;
import com.eticum.utils.ActivityControlsUtils;
import com.eticum.utils.SharedPreferencesUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {

    private static final Integer VIEW_TYPE_SWITCH = 0;
    private static final Integer VIEW_TYPE_CARD = 1;
    private List<StatusPageDataModel.ItemModel> dataSet;
    private OnCheckedChangeListener mOnCheckedChangeListener;

    private Activity activity;

    static class ViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {

        ImageView listImage;
        TextView textViewTitle;
        LinearLayout linearLayout;
        RelativeLayout relativeLayoutOption;
        TextView textViewOption;
        TextView textViewDescription;
        SwitchCompat switchCompat;
        OnCheckedChangeListener onCheckedChangeListener;

        public ViewHolder(View itemView, OnCheckedChangeListener onCheckedChangeListener) {
            super(itemView);
            this.listImage = itemView.findViewById(R.id.listImage);
            this.textViewTitle = itemView.findViewById(R.id.textViewTitle);
            this.textViewOption = itemView.findViewById(R.id.textViewOption);
            this.textViewDescription = itemView.findViewById(R.id.textViewDescription);
            this.linearLayout = itemView.findViewById(R.id.linear);
            this.relativeLayoutOption = itemView.findViewById(R.id.relativeLayoutOption);
            this.switchCompat = itemView.findViewById(R.id.switch_main);
            this.onCheckedChangeListener = onCheckedChangeListener;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            onCheckedChangeListener.onCheckedChange(isChecked);
        }
    }

    public RecycleViewAdapter(Activity activity, List<StatusPageDataModel.ItemModel> data, OnCheckedChangeListener listener) {
        this.activity = activity;
        this.dataSet = data;
        this.mOnCheckedChangeListener = listener;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent,
                                         int viewType) {
        int layout;
        if (viewType == VIEW_TYPE_CARD) {
            layout = R.layout.cards_layout;
        } else {
            layout = R.layout.cards_layout_switch;
        }

        View view = LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);
        return new ViewHolder(view, mOnCheckedChangeListener);


    }

    @Override
    public void onBindViewHolder(@NotNull final ViewHolder holder, final int listPosition) {
        if (listPosition != 0) {
            ImageView image = holder.listImage;
            TextView title = holder.textViewTitle;
            LinearLayout linearLayout = holder.linearLayout;
            RelativeLayout relativeLayoutOption = holder.relativeLayoutOption;

            linearLayout.removeView(relativeLayoutOption);

            for (StatusPageDataModel.OptionModel optionModel : dataSet.get(listPosition - 1).getOptionModels()) {
                createCardViewRecord(optionModel, linearLayout);
            }

            Glide.with(App.getContext()).load(StatusPageDataModel.getIconsArray()[listPosition - 1]).into(image);
            title.setText(dataSet.get(listPosition - 1).getTitle());
        } else {
            holder.switchCompat.setOnCheckedChangeListener(holder);
            if (SharedPreferencesUtils.isVpnEnabled() != holder.switchCompat.isChecked())
                holder.switchCompat.setChecked(SharedPreferencesUtils.isVpnEnabled());
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

    public interface OnCheckedChangeListener {
        void onCheckedChange(boolean checked);
    }
}