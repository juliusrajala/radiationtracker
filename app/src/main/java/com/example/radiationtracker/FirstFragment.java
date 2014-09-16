package com.example.radiationtracker;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Julius on 16.9.2014.
 */
public class FirstFragment extends Fragment {
    private String title;
    private int page;

    public static FirstFragment newInstance(int page, String title){
        FirstFragment fragmentFirst = new FirstFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle", 0);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        TextView tvLabel = (TextView)view.findViewById(R.id.tvLabel);
        tvLabel.setText((page + "-- " + title));
        return view;
    }
}
