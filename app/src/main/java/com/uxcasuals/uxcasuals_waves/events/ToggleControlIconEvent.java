package com.uxcasuals.uxcasuals_waves.events;

import com.uxcasuals.uxcasuals_waves.models.Station;

/**
 * Created by Dhakchianandan on 25/09/15.
 */
public class ToggleControlIconEvent {
    public static final int IS_PLAYING = 0;
    public static final int IN_PAUSE = 1;
    public int STATE;
    private Station station;

    public ToggleControlIconEvent(int STATE, Station station) {
        this.STATE = STATE;
        this.station = station;
    }

    public Station getStation() {
        return station;
    }
}
