package com.uxcasuals.uxcasuals_waves.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.otto.Subscribe;
import com.uxcasuals.uxcasuals_waves.R;
import com.uxcasuals.uxcasuals_waves.adapters.StationsAdapter;
import com.uxcasuals.uxcasuals_waves.events.PlayStationEvent;
import com.uxcasuals.uxcasuals_waves.models.Station;
import com.uxcasuals.uxcasuals_waves.utils.EventHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomePageFragment extends Fragment {
    private List<Station> stations = new ArrayList<>();
    private SlidingUpPanelLayout slidingUpPanelLayout;

    public HomePageFragment() {
        // Required empty public constructor
    }

    public void setStations(List<Station> stations) {
        this.stations = stations;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);
        slidingUpPanelLayout = (SlidingUpPanelLayout) view.findViewById(R.id.sliding_layout);
        slidingUpPanelLayout.hidePanel();
        RecyclerView stationsView = (RecyclerView)view.findViewById(R.id.stations_view);
        RecyclerView.LayoutManager layout = new GridLayoutManager(getActivity(), 2);
        StationsAdapter stationsAdapter = new StationsAdapter(stations);
        stationsView.setLayoutManager(layout);
        stationsView.setAdapter(stationsAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventHelper.getInstance().register(this);
    }

    @Override
    public void onStop() {
        EventHelper.getInstance().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void showPlayerControls(PlayStationEvent playStationEvent) {
        slidingUpPanelLayout.showPanel();
    }
}
