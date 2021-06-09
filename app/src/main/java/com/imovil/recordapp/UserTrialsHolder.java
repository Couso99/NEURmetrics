package com.imovil.recordapp;

import android.view.View;

import androidx.annotation.NonNull;

import com.imovil.recordapp.databinding.SimpleListItemBinding;

public class UserTrialsHolder extends NewTrialsHolder {

    public UserTrialsHolder(@NonNull SimpleListItemBinding binding) {
        super(binding);
    }

    @Override
    public void bind(Trial trial){
        if (trial!=null) {
            TrialInfo info = trial.getTrialInfo();
            binding.text1.setText("TrialID: " + info.getTrialID() + "\n - Start Time: "+TrialTimer.getDateFromTimestamp(info.getStartTime())+"\n - Score: "
            +info.getTotalScore()+"/"+info.getTotalMaxScore());
        }

        else
            binding.text1.setText("No hay datos disponibles");
    }

    @Override
    public void onClick(View v) {
        UserTrialsListAdapter.clickListener.onItemClick(getAdapterPosition(), v);
    }
}
