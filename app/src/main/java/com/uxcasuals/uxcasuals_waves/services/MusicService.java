package com.uxcasuals.uxcasuals_waves.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.uxcasuals.uxcasuals_waves.events.NotifyToggleEvent;
import com.uxcasuals.uxcasuals_waves.events.PlayStationEvent;
import com.uxcasuals.uxcasuals_waves.events.PrepareMediaPlayerEvent;
import com.uxcasuals.uxcasuals_waves.events.ReleaseMediaPlayerEvent;
import com.uxcasuals.uxcasuals_waves.events.ToggleControlIconEvent;
import com.uxcasuals.uxcasuals_waves.models.Station;
import com.uxcasuals.uxcasuals_waves.utils.EventHelper;

import java.io.IOException;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    private static final String TAG = MusicService.class.getName();
    private static final String WIFI_TAG = "WIFI_LOCK";

    private MediaPlayer mMediaPlayer = null;
    private WifiManager.WifiLock wifiLock;
    private Station station;

    class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Subscribe
    public void prepareMediaPlayer(PrepareMediaPlayerEvent prepareMediaPlayerEvent) {
        if(mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL, MusicService.WIFI_TAG);
            wifiLock.acquire();

            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnErrorListener(this);
        }
    }

    public void pauseMediaPlayer() {
        mMediaPlayer.pause();
        EventHelper.getInstance().post(new ToggleControlIconEvent(ToggleControlIconEvent.IN_PAUSE));
    }

    public void resumeMediaPlayer() {
        mMediaPlayer.start();
        EventHelper.getInstance().post(new ToggleControlIconEvent(ToggleControlIconEvent.IS_PLAYING));
    }

    public void releaseMediaPlayer() {
        if(mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

        if(wifiLock.isHeld()) {
            wifiLock.release();
        }
    }


    @Subscribe
    public void playStation(PlayStationEvent playStationEvent) {
        station = playStationEvent.getStation();
        if(mMediaPlayer != null) {
            mMediaPlayer.reset();
            try {
                mMediaPlayer.setDataSource(station.getUrl());
                mMediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Subscribe
    public void stopMediaPlayer(ReleaseMediaPlayerEvent releaseMediaPlayerEvent) {
        releaseMediaPlayer();
    }

    @Subscribe
    public void toggleMediaPlayer(NotifyToggleEvent notifyToggleEvent) {
        if(mMediaPlayer != null) {
            if(mMediaPlayer.isPlaying()) {
                pauseMediaPlayer();
            } else {
                resumeMediaPlayer();
            }
        }
    }

    final Messenger messenger = new Messenger(new MessageHandler());

    @Override
    public void onPrepared(MediaPlayer mMediaPlayer) {
        Toast.makeText(getApplicationContext(), "Playing " + station.getName(), Toast.LENGTH_SHORT).show();
        resumeMediaPlayer();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Toast.makeText(getApplicationContext(), "Problem with streaming. Try other stations!!", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        EventHelper.getInstance().register(this);
        return messenger.getBinder();
    }
}
