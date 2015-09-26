package com.uxcasuals.uxcasuals_waves.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.uxcasuals.uxcasuals_waves.R;
import com.uxcasuals.uxcasuals_waves.events.PlayStationEvent;
import com.uxcasuals.uxcasuals_waves.models.Station;
import com.uxcasuals.uxcasuals_waves.utils.AsyncHelper;
import com.uxcasuals.uxcasuals_waves.utils.EventHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dhakchianandan on 25/09/15.
 */
public class StationsAdapter extends RecyclerView.Adapter<StationsAdapter.ViewHolder> {
    private List<Station> stations = new ArrayList<Station>();
    private Context context;

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

    public void setContext(Context context) {
        this.context = context;
    }

    public StationsAdapter(List<Station> stations) {
        EventHelper.getInstance().register(this);
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
        NetworkImageView stationImageView = (NetworkImageView) stationView.findViewById(R.id.station_logo);

        final Station station = stations.get(position);
        stationNameView.setText(station.getName());

//        stationImageView.setDefaultImageResId(R.drawable.ic_icon);
        ImageLoader imageLoader = AsyncHelper.getInstance(context).getImageLoader();
        stationImageView.setImageUrl(station.getLogo(), imageLoader);

        stationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventHelper.getInstance().post(new PlayStationEvent(station));
            }
        });
    }

    @Override
    public int getItemCount() {
        return stations.size();
    }
}
