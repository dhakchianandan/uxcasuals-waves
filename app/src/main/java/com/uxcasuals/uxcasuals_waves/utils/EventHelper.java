package com.uxcasuals.uxcasuals_waves.utils;

import com.squareup.otto.Bus;

/**
 * Created by Dhakchianandan on 25/09/15.
 */
public class EventHelper extends Bus {
    private static EventHelper instance = new EventHelper();

    public static EventHelper getInstance() {
        return instance;
    }

    private EventHelper() {
    }
}
