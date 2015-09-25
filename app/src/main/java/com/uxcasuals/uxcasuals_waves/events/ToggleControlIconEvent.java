package com.uxcasuals.uxcasuals_waves.events;

/**
 * Created by Dhakchianandan on 25/09/15.
 */
public class ToggleControlIconEvent {
    public static final int IS_PLAYING = 0;
    public static final int IN_PAUSE = 1;
    public int STATE;

    public ToggleControlIconEvent(int STATE) {
        this.STATE = STATE;
    }
}
