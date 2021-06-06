package com.imovil.recordapp;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.imovil.recordapp.databinding.SimpleListItemBinding;

public class UsersHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    SimpleListItemBinding binding;

    public UsersHolder(@NonNull SimpleListItemBinding binding) {
        super(binding.getRoot());
        binding.getRoot().setOnClickListener(this);
        this.binding = binding;
    }

    @Override
    public void onClick(View v) {
        UsersListAdapter.clickListener.onItemClick(getAdapterPosition(), v);
    }

    public void bind(User user){
        if (user!=null)
            binding.text1.setText("Name: " + user.getName() +" - Surname: "+user.getSurname()+
                    " - UserID: "+user.getUserID());
        else
            binding.text1.setText("No hay datos disponibles");
    }

}
