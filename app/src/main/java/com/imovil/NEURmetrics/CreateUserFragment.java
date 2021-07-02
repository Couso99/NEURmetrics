package com.imovil.NEURmetrics;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;

public class CreateUserFragment extends Fragment {

    private NewUserViewModel model;

    private TextView birthdayView;

    private EditText nameEdit, surnameEdit, centreEdit;
    private Spinner sexSpinner;
    private FloatingActionButton fab;

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

        if (savedInstanceState==null)
            model.initNewUser();

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

        nameEdit = view.findViewById(R.id.nameFieldEdit);
        surnameEdit = view.findViewById(R.id.surnameFieldEdit);
        birthdayView = view.findViewById(R.id.birthdayField);
        sexSpinner = view.findViewById(R.id.sexSpinner);
        centreEdit = view.findViewById(R.id.centreFieldEdit);
        fab = view.findViewById(R.id.createUserFab);

        model.getBirthday().observe(requireActivity(),calendar -> {
            String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
            birthdayView.setText(currentDateString);
        });

        if (model.getUser()!=null) {
            User user = model.getUser();
            nameEdit.setText(user.getName());
            surnameEdit.setText(user.getSurname());
            centreEdit.setText(user.getCentre());
        }


        nameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                model.setName(s.toString());
            }
        });

        surnameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                model.setSurname(s.toString());
            }
        });

        centreEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                model.setCentre(s.toString());
            }
        });

        sexSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                model.setSex(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        fab.setOnClickListener(v -> {
            model.uploadUser();
        });

        return view;
    }

    public static class DatePickerFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new DatePickerDialog(getContext(), (DatePickerDialog.OnDateSetListener) requireActivity(),1980,1,1);
        }
    }
}