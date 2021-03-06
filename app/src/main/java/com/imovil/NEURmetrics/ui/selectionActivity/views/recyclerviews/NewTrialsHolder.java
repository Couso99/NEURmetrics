package com.imovil.NEURmetrics.ui.selectionActivity.views.recyclerviews;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.imovil.NEURmetrics.databinding.NewTrialListItemBinding;
import com.imovil.NEURmetrics.models.Trial;
import com.imovil.NEURmetrics.models.TrialInfo;

public class NewTrialsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    NewTrialListItemBinding binding;

    public NewTrialsHolder(@NonNull NewTrialListItemBinding binding) {
        super(binding.getRoot());
        binding.getRoot().setOnClickListener(this);
        this.binding = binding;
    }

    @Override
    public void onClick(View v) {
        NewTrialsListAdapter.clickListener.onItemClick(getAdapterPosition(), v);
    }

    public void bind(Trial trial){
        if (trial!=null) {
            TrialInfo info = trial.getTrialInfo();
            binding.nameView.setText(info.getName());
            if (info.getDescription()!=null) {
                binding.descriptionView.setText(info.getDescription());
            }
            else binding.descriptionLayout.setVisibility(View.GONE);
        }
        else binding.nameView.setText("No hay datos disponibles");
    }
}
