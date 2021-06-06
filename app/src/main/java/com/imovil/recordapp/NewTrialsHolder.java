package com.imovil.recordapp;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.imovil.recordapp.databinding.SimpleListItemBinding;

public class NewTrialsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    SimpleListItemBinding binding;

    public NewTrialsHolder(@NonNull SimpleListItemBinding binding) {
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
            binding.text1.setText("TrialID: " + info.getTrialID());
        }

        else
            binding.text1.setText("No hay datos disponibles");
    }

}
