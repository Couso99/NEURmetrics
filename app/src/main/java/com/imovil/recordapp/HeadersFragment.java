package com.imovil.recordapp;

import android.os.Bundle;

import android.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HeadersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HeadersFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_TITLE = "title";
    private static final String ARG_H1 = "h1";
    private static final String ARG_H2 = "h2";

    private String title;
    private String h1;
    private String h2;


    public HeadersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param title Title.
     * @param h1 Header 1.
     * @param h2 Header 2.
     * @return A new instance of fragment HeadersFragment.
     */
    public static HeadersFragment newInstance(String title, String h1, String h2) {
        HeadersFragment fragment = new HeadersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_H1, h1);
        args.putString(ARG_H2, h2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_TITLE);
            h1 = getArguments().getString(ARG_H1);
            h2 = getArguments().getString(ARG_H2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_headers, container, false);

        TextView titleView, h1View, h2View;
        titleView = view.findViewById(R.id.title);
        h1View = view.findViewById(R.id.h1);
        h2View = view.findViewById(R.id.h2);

        if (title!=null) titleView.setText(title);
        if (h1!=null) h1View.setText(h1);
        if (h2!=null) h2View.setText(h2);


        return view;
    }
}