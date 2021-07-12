package com.imovil.NEURmetrics;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HeadersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HeadersFragment extends Fragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_H1 = "h1";
    private static final String ARG_H2 = "h2";

    /*private String title;
    private String h1;
    private String h2;

    private Activity activity;*/
    private TrialViewModel model;

    private TextView titleView, h1View, h2View;
    private LinearLayout linearLayout;

    public HeadersFragment() {
        // Required empty public constructor
    }

    public static HeadersFragment newInstance(String title, String h1, String h2) {
        HeadersFragment fragment = new HeadersFragment();
        Bundle args = new Bundle();
        /*args.putString(ARG_TITLE, title);
        args.putString(ARG_H1, h1);
        args.putString(ARG_H2, h2);*/
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            /*title = getArguments().getString(ARG_TITLE);
            h1 = getArguments().getString(ARG_H1);
            h2 = getArguments().getString(ARG_H2);*/
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_headers, container, false);

        model = new ViewModelProvider(requireActivity()).get(TrialViewModel.class);

        titleView = view.findViewById(R.id.title);
        h1View = view.findViewById(R.id.h1);
        h2View = view.findViewById(R.id.h2);
        linearLayout = view.findViewById(R.id.headersLayout);

        if(!model.isShowingResults()) {
            Test test = model.getTest();
            titleView.setText(test.getTitle() != null ? test.getTitle() : "");
            h1View.setText(test.getH1() != null ? test.getH1() : "");
            h2View.setText(test.getH2() != null ? test.getH2() : "");
            if (h1View.getVisibility()==View.GONE) {
                h1View.setVisibility(View.VISIBLE);
                h2View.setVisibility(View.VISIBLE);
            }
        }
        else {
            titleView.setText("Resultados finales");
            h1View.setVisibility(View.GONE);
            h2View.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        model.getIsUpdateHeaders().observe(requireActivity(), isUpdateHeaders -> {
            if(isUpdateHeaders) {
                if(model.isShowingResults()) {
                    titleView.setText(R.string.finalScoreHeader);
                    h1View.setVisibility(View.GONE);
                    h2View.setVisibility(View.GONE);
                }
                else {
                    Test test = model.getTest();
                    titleView.setText(test.getTitle() != null ? test.getTitle() : "");
                    h1View.setText(test.getH1() != null ? test.getH1() : "");
                    h2View.setText(test.getH2() != null ? test.getH2() : "");
                    if (h1View.getVisibility()==View.GONE) {
                        h1View.setVisibility(View.VISIBLE);
                        h2View.setVisibility(View.VISIBLE);
                    }
                }
                model.setIsUpdateHeaders(false);
            }
        });
    }
}