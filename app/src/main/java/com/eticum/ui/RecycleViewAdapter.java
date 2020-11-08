package com.eticum.ui;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.eticum.App;
import com.eticum.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {

    private static final Integer VIEW_TYPE_SWITCH = 0;
    private static final Integer VIEW_TYPE_CARD = 1;
    private List<StatusPageDataModel.ItemModel> dataSet;

    private Context context;

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView listImage;
        TextView textViewTitle;
        LinearLayout linearLayout;
        RelativeLayout relativeLayoutOption;
        TextView textViewOption;
        TextView textViewDescription;

        public ViewHolder(View itemView) {
            super(itemView);
            this.listImage = itemView.findViewById(R.id.listImage);
            this.textViewTitle = itemView.findViewById(R.id.textViewTitle);
            this.textViewOption = itemView.findViewById(R.id.textViewOption);
            this.textViewDescription = itemView.findViewById(R.id.textViewDescription);
            this.linearLayout = itemView.findViewById(R.id.linear);
            this.relativeLayoutOption = itemView.findViewById(R.id.relativeLayoutOption);
        }
    }

    public RecycleViewAdapter(Context context, List<StatusPageDataModel.ItemModel> data) {
        this.context = context;
        this.dataSet = data;
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
        return new ViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NotNull final ViewHolder holder, final int listPosition) {
        if (listPosition !=0) {
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
        TextView newOption = new TextView(context);
        TextView newDescription = new TextView(context);
        newOption.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        newOption.setTextAppearance(R.style.MontserratTextAppearanceBlack);
        newDescription.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        newDescription.setTextAppearance(R.style.MontserratTextAppearanceBlack);
        newOption.setText(optionModel.getOption());
        newDescription.setText(optionModel.getDescription());
        newDescription.setGravity(Gravity.END);

        RelativeLayout newRelativeLayout = new RelativeLayout(context);
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
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pixels * scale + 0.5f);
    }
}