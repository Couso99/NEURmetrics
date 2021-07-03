package com.imovil.NEURmetrics;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.imovil.NEURmetrics.databinding.SimpleListItemBinding;

public class TestsHolder extends RecyclerView.ViewHolder {

    SimpleListItemBinding binding;

    public TestsHolder(@NonNull SimpleListItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(Test test){
        if (test!=null) {
            binding.nameView.setText(test.getName());
            binding.scoreView.setText(test.getScore()+ ((test.getMaxScore()>=0) ? " /"+test.getMaxScore(): ""));
        }
    }

}