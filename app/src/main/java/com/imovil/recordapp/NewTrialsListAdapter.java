package com.imovil.recordapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.imovil.recordapp.databinding.NewTrialListItemBinding;
import com.imovil.recordapp.databinding.SimpleListItemBinding;

public class NewTrialsListAdapter extends RecyclerView.Adapter <NewTrialsHolder> {

    private Trials mTrials;
    NewTrialListItemBinding binding;

    public static NewTrialsListAdapter.ClickListener clickListener;

    @NonNull
    @Override
    public NewTrialsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = NewTrialListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new NewTrialsHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NewTrialsHolder holder, int position) {
        if (mTrials!=null)
            holder.bind(mTrials.getTrials().get(position));
        else
            holder.bind(null);
    }

    @Override
    public int getItemCount() {
        if (mTrials!=null)
            return mTrials.getTrials().size();
        else
            return 0;
    }

    public void setTrials(Trials mTrials) {
        this.mTrials = mTrials;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(NewTrialsListAdapter.ClickListener clickListener) {
        NewTrialsListAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }
}
