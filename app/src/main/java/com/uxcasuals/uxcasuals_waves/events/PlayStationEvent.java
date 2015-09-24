package com.uxcasuals.uxcasuals_waves.events;

import com.uxcasuals.uxcasuals_waves.models.Station;

/**
 * Created by Dhakchianandan on 25/09/15.
 */
public class PlayStationEvent {
    private Station station;

    public PlayStationEvent() {
    }

    public PlayStationEvent(Station station) {
        this.station = station;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    @Override
    public String toString() {
        return "PlayStationEvent{" +
                "station=" + station +
                '}';
    }
}
