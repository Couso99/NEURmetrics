package com.imovil.recordapp;

import androidx.recyclerview.widget.RecyclerView;

abstract class BaseTrialsListAdapter extends RecyclerView.Adapter {
    abstract void setTrials(Trials mTrials);

    abstract void setOnItemClickListener(UsersListAdapter.ClickListener clickListener);

}
