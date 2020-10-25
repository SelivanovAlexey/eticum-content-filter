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
import com.eticum.activities.EticumActivity;
import com.eticum.R;

import org.jetbrains.annotations.NotNull;
import java.util.List;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {

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
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cards_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int listPosition) {

        ImageView image = holder.listImage;
        TextView title = holder.textViewTitle;
        LinearLayout linearLayout = holder.linearLayout;
        RelativeLayout relativeLayoutOption = holder.relativeLayoutOption;

        linearLayout.removeView(relativeLayoutOption);

        for (StatusPageDataModel.OptionModel optionModel : dataSet.get(listPosition).getOptionModels()) {
            createCardViewRecord(optionModel, linearLayout);
        }

        Glide.with(App.getContext()).load(StatusPageDataModel.getIconsArray()[listPosition]).into(image);
        title.setText(dataSet.get(listPosition).getTitle());
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
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