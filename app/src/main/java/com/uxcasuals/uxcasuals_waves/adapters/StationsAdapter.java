package com.uxcasuals.uxcasuals_waves.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.uxcasuals.uxcasuals_waves.R;
import com.uxcasuals.uxcasuals_waves.models.Station;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dhakchianandan on 25/09/15.
 */
public class StationsAdapter extends RecyclerView.Adapter<StationsAdapter.ViewHolder> {
    private List<Station> stations = new ArrayList<Station>();

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CardView staionView;
        public ViewHolder(CardView view) {
            super(view);
            staionView = view;
        }
    }

    public void setStations(List<Station> stations) {
        this.stations = stations;
    }

    public StationsAdapter(List<Station> stations) {
        this.stations = stations;
    }

    @Override
    public StationsAdapter.ViewHolder onCreateViewHolder(ViewGroup application, int i) {
        CardView stationView = (CardView) LayoutInflater.from(application.getContext())
                .inflate(R.layout.station_view, application, false);
        return new ViewHolder(stationView);
    }

    @Override
    public void onBindViewHolder(StationsAdapter.ViewHolder viewHolder, int position) {
        CardView stationView = viewHolder.staionView;
        TextView stationNameView = (TextView) stationView.findViewById(R.id.station_name);
        ImageView stationImageView = (ImageView) stationView.findViewById(R.id.station_logo);

        final Station station = stations.get(position);
        stationNameView.setText(station.getName());
        stationImageView.setImageResource(R.drawable.ic_music_circle);
//        new BitmapLoader(stationImageView, station.getLogo()).execute();
//        AlbumArtCache.getInstance().fetchBitmap(station.getLogo(), stationImageView);

        stationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return stations.size();
    }
}
