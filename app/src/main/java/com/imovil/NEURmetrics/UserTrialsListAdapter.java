package com.imovil.NEURmetrics;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.imovil.NEURmetrics.databinding.UserTrialListItemBinding;

public class UserTrialsListAdapter extends RecyclerView.Adapter <UserTrialsHolder> {

    private Trials mTrials;
    UserTrialListItemBinding binding;

    public static UserTrialsListAdapter.ClickListener clickListener;

    @NonNull
    @Override
    public UserTrialsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = UserTrialListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new UserTrialsHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserTrialsHolder holder, int position) {
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

    public void setOnItemClickListener(UserTrialsListAdapter.ClickListener clickListener) {
        UserTrialsListAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }
}