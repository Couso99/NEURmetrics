package com.imovil.NEURmetrics.ui.selectionActivity.views.recyclerviews;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.imovil.NEURmetrics.databinding.UserListItemBinding;
import com.imovil.NEURmetrics.models.User;
import com.imovil.NEURmetrics.models.Users;

import java.util.List;

public class UsersListAdapter extends RecyclerView.Adapter <UsersHolder> {

    private Users mUsers;
    private UserListItemBinding binding;

    public static ClickListener clickListener;

    @NonNull
    @Override
    public UsersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = UserListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new UsersHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersHolder holder, int position) {
        if (mUsers!=null)
            holder.bind(mUsers.getUsers().get(position));
        else
            holder.bind(null);
    }

    @Override
    public int getItemCount() {
        if (mUsers!=null && mUsers.getUsers() != null) {
            if (!mUsers.getUsers().isEmpty())
                return mUsers.getUsers().size();
            return 0;
        }
        else
            return 0;
    }

    public void setFilter(List<User> newList){
        mUsers=new Users();
        mUsers.getUsers().addAll(newList);
        notifyDataSetChanged();
    }

    public void setUsers(Users mUsers) {
        this.mUsers = mUsers;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        UsersListAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }
}
