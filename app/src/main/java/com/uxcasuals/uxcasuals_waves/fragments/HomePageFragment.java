package com.uxcasuals.uxcasuals_waves.fragments;


import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.otto.Subscribe;
import com.uxcasuals.uxcasuals_waves.R;
import com.uxcasuals.uxcasuals_waves.adapters.StationsAdapter;
import com.uxcasuals.uxcasuals_waves.events.PlayStationEvent;
import com.uxcasuals.uxcasuals_waves.events.ToggleControlIconEvent;
import com.uxcasuals.uxcasuals_waves.models.Station;
import com.uxcasuals.uxcasuals_waves.utils.AsyncHelper;
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
    }

    public void setStations(List<Station> stations) {
        this.stations = stations;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventHelper.getInstance().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        slidingUpPanelLayout = (SlidingUpPanelLayout) view.findViewById(R.id.sliding_layout);
        slidingUpPanelLayout.hidePanel();
        RecyclerView stationsView = (RecyclerView)view.findViewById(R.id.stations_view);
        RecyclerView.LayoutManager layout = new GridLayoutManager(getActivity(), 2);
        StationsAdapter stationsAdapter = new StationsAdapter(stations);
        stationsAdapter.setContext(getActivity().getApplicationContext());
        stationsView.setLayoutManager(layout);
        stationsView.setAdapter(stationsAdapter);

        return view;
    }

    @Override
    public void onDestroy() {
        EventHelper.getInstance().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void showPlayerControls(PlayStationEvent playStationEvent) {
        NetworkImageView playingStationImage = (NetworkImageView)
                slidingUpPanelLayout.findViewById(R.id.playing_station_image);
        playingStationImage.setImageUrl(playStationEvent.getStation().getLogo(),
                AsyncHelper.getInstance(getActivity().getApplicationContext()).getImageLoader());

        TextView playingStationMessage = (TextView)
                slidingUpPanelLayout.findViewById(R.id.playing_station_message);
        Resources resources = getResources();
        String message = String.format(resources.getString(R.string.favourite_station_playing),
                resources.getString(R.string.listening), playStationEvent.getStation().getName());
        playingStationMessage.setText(message);
        slidingUpPanelLayout.showPanel();
    }

    @Subscribe
    public void togglePlayerControls(ToggleControlIconEvent toggleControlIconEvent) {
        ImageButton playerControls = (ImageButton)
                slidingUpPanelLayout.findViewById(R.id.playing_station_state_icons);
        TextView playingStationMessage = (TextView)
                slidingUpPanelLayout.findViewById(R.id.playing_station_message);
        String message;
        Resources resources = getResources();

        if(toggleControlIconEvent.STATE == ToggleControlIconEvent.IS_PLAYING) {
            playerControls.setImageResource(R.drawable.ic_pause_black_48dp);
            message = String.format(resources.getString(R.string.favourite_station_playing),
                    resources.getString(R.string.listening), toggleControlIconEvent.getStation().getName());
        } else {
            playerControls.setImageResource(R.drawable.ic_play_arrow_black_48dp);
            message = String.format(resources.getString(R.string.favourite_station_playing),
                    resources.getString(R.string.listen), toggleControlIconEvent.getStation().getName());

        }
        playingStationMessage.setText(message);
    }
}
