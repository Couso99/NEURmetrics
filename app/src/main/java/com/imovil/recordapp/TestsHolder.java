package com.imovil.recordapp;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.imovil.recordapp.databinding.SimpleListItemBinding;

public class TestsHolder extends RecyclerView.ViewHolder {

    SimpleListItemBinding binding;

    public TestsHolder(@NonNull SimpleListItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(Test test){
        if (test!=null)
           binding.text1.setText("ID: " + test.getTestID() +" -  Name: " + test.getName() +" - Score: "+test.getScore()+" /"+test.getMaxScore());
        else
            binding.text1.setText("No hay datos disponibles");
    }

}