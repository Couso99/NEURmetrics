package com.imovil.NEURmetrics;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.imovil.NEURmetrics.databinding.UserTrialListItemBinding;

public class UserTrialsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    UserTrialListItemBinding binding;

    public UserTrialsHolder(@NonNull UserTrialListItemBinding binding) {
        super(binding.getRoot());
        binding.getRoot().setOnClickListener(this);
        this.binding = binding;
    }

    public void bind(Trial trial){
        if (trial!=null) {
            TrialInfo info = trial.getTrialInfo();
            binding.nameView.setText(info.getName());
            if (info.getStartTime()!=0) {
                binding.dateView.setText(TrialTimer.getDateFromTimestamp(info.getStartTime()));
            }
            else binding.dateLayout.setVisibility(View.GONE);
            binding.scoreView.setText(info.getTotalScore()+(info.getTotalMaxScore()!=0 ? ("/"+info.getTotalMaxScore()) : ""));
        }
        else binding.nameView.setText("No hay datos disponibles");
    }

    @Override
    public void onClick(View v) {
        UserTrialsListAdapter.clickListener.onItemClick(getAdapterPosition(), v);
    }
}
