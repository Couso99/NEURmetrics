package com.imovil.recordapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.text.DateFormat;
import java.util.Calendar;

public class CreateUserFragment extends Fragment {

    TextView birthdayView;
    NewUserViewModel model;

    public CreateUserFragment() {
        // Required empty public constructor
    }

    public static CreateUserFragment newInstance() {
        CreateUserFragment fragment = new CreateUserFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        model = new ViewModelProvider(requireActivity()).get(NewUserViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_user, container, false);

        Button button = view.findViewById(R.id.setDateButton);
        button.setOnClickListener(v -> {
            DialogFragment datePicker = new DatePickerFragment();
            datePicker.show(getActivity().getSupportFragmentManager(), "date picker");
        });

        model.getBirthday().observe(requireActivity(),calendar -> {
            String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
            birthdayView.setText(currentDateString);
        });

        birthdayView = view.findViewById(R.id.birthdayField);

        return view;
    }


    public static class DatePickerFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new DatePickerDialog(getContext(), (DatePickerDialog.OnDateSetListener) requireActivity(),1980,1,1);
        }
    }
}