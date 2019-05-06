package com.bitlove.fetlife.nativeapp.view.screen.resource;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.bitlove.fetlife.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapLoadingFragment extends Fragment {


    public MapLoadingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map_loading, container, false);
    }

}
