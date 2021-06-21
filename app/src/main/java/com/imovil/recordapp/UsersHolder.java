package com.imovil.recordapp;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.imovil.recordapp.databinding.UserListItemBinding;

public class UsersHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    UserListItemBinding binding;

    public UsersHolder(@NonNull UserListItemBinding binding) {
        super(binding.getRoot());
        binding.getRoot().setOnClickListener(this);
        this.binding = binding;
    }

    @Override
    public void onClick(View v) {
        UsersListAdapter.clickListener.onItemClick(getAdapterPosition(), v);
    }

    public void bind(User user){
        if (user!=null) {
            binding.nameView.setText(user.getName());
            binding.surnameView.setText(user.getSurname());

            if (user.getSex()!=null) {
                String sex = user.getSex();
                String sexString;

                /*switch (sex) {
                    case "M":
                        sexString = getString(R.string.sexMale);
                        break;
                    case "F":
                        sexString = (R.string.sexFemale);
                        break;
                    case "X":
                        sexString = R.string.sexOther;
                        break;
                    default:
                        sexString = R.string.sexUnknown;
                        break;
                }*/

                binding.genderView.setText(sex);
            }
            else binding.genderLayout.setVisibility(View.GONE);

            if (user.getBirthday() != 0) {
                long bday_timestamp = user.getBirthday();
                long difference_In_Time = System.currentTimeMillis()/1000-bday_timestamp;

                long difference_In_Years = (difference_In_Time / (60L * 60 * 24 * 365));

                binding.ageView.setText(String.valueOf(difference_In_Years));
            }
            else binding.ageLayout.setVisibility(View.GONE);

            if (user.getCentre()!=null) {
                binding.centreView.setText(user.getCentre());
            }
            else binding.centreLayout.setVisibility(View.GONE);
            //binding.ageView.setText();
        }
    }

}
