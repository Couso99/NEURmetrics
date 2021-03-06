package com.imovil.NEURmetrics.ui.trialActivity.views.recyclerviews;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.imovil.NEURmetrics.databinding.SimpleListItemBinding;
import com.imovil.NEURmetrics.models.Trial;
import com.imovil.NEURmetrics.ui.trialActivity.views.recyclerviews.TestsHolder;

public class TestsListAdapter extends RecyclerView.Adapter <TestsHolder> {

    private Trial mTrial;
    SimpleListItemBinding binding;

    @NonNull
    @Override
    public TestsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = SimpleListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new TestsHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TestsHolder holder, int position) {
        if (mTrial !=null)
            holder.bind(mTrial.getTests().get(position));
        else
            holder.bind(null);
    }

    @Override
    public int getItemCount() {
        if (mTrial !=null)
            return mTrial.getTests().size();
        else
            return 0;
    }

    public void setTests(Trial mTrial) {
        this.mTrial = mTrial;
        notifyDataSetChanged();
    }


}