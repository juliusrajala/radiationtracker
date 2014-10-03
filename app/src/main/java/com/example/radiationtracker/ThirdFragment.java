package com.example.radiationtracker;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Julius on 23.9.2014.
 * Kolmas näkymä joka tulee piirtämään GL:n avulla texturesurfacen kamera-feedistä.
 * TODO: Katso mallia secondFragmentistä ja netistä
 */
public class ThirdFragment extends Fragment {
    public static ThirdFragment newInstance(){
        ThirdFragment fragmentThird = new ThirdFragment();
        Bundle args = new Bundle();
        fragmentThird.setArguments(args);
        return fragmentThird;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_third, container, false);
        return view;
    }


}
