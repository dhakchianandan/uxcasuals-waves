package com.uxcasuals.uxcasuals_waves.utils;

/**
 * Created by Dhakchianandan on 25/09/15.
 */
public class EventHelper {
    private static EventHelper instance = new EventHelper();

    public static EventHelper getInstance() {
        return instance;
    }

    private EventHelper() {
    }
}
