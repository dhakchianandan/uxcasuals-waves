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
import com.uxcasuals.uxcasuals_waves.events.CallStateEvent;
import com.uxcasuals.uxcasuals_waves.events.PlayStationEvent;
import com.uxcasuals.uxcasuals_waves.events.PrepareMediaPlayerEvent;
import com.uxcasuals.uxcasuals_waves.events.ReleaseMediaPlayerEvent;
import com.uxcasuals.uxcasuals_waves.events.ToggleControlIconEvent;
import com.uxcasuals.uxcasuals_waves.models.Station;
import com.uxcasuals.uxcasuals_waves.utils.AudioFocus;
import com.uxcasuals.uxcasuals_waves.utils.EventHelper;

import java.io.IOException;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, AudioManager.OnAudioFocusChangeListener {

    private static final String TAG = MusicService.class.getName();
    private static final String WIFI_TAG = "WIFI_LOCK";

    private MediaPlayer mMediaPlayer = null;
    private WifiManager.WifiLock wifiLock;

    private AudioManager audioManager;
    private AudioFocus audioFocus = AudioFocus.NO_STATE;

    private Station station;
    private boolean SYSTEM_GENERATED_AUDIO_FOCUS_CHANGE = false;

    class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                default:
                    super.handleMessage(msg);
            }
        }
    }

    public void startMediaPlayer() {
        tryToGetAudioFocus();
        if(audioFocus == AudioFocus.GAINED) {
            mMediaPlayer.start();
        } else {
            Toast
                .makeText(getApplicationContext(),
                        "Close other Media Applications and try", Toast.LENGTH_LONG).show();
        }
        EventHelper.getInstance().post(
                new ToggleControlIconEvent(ToggleControlIconEvent.IS_PLAYING, station));
    }

    public void pauseMediaPlayer() {
        releaseAudioFocus();
        mMediaPlayer.pause();
        EventHelper.getInstance().post(
                new ToggleControlIconEvent(ToggleControlIconEvent.IN_PAUSE, station));
    }

    public void releaseMediaPlayer() {
        if(mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            if(wifiLock.isHeld()) wifiLock.release();
        }
    }

    @Subscribe
    public void prepareMediaPlayer(PrepareMediaPlayerEvent prepareMediaPlayerEvent) {
        audioManager =
                (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
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
    public void toggleMediaPlayer(NotifyToggleEvent notifyToggleEvent) {
        if(mMediaPlayer != null) {
            if(mMediaPlayer.isPlaying()) {
                pauseMediaPlayer();
            } else {
                startMediaPlayer();
            }
        }
    }

    @Subscribe
    public void stopMediaPlayer(ReleaseMediaPlayerEvent releaseMediaPlayerEvent) {
        releaseMediaPlayer();
    }

    private void tryToGetAudioFocus() {
        int result = audioManager.requestAudioFocus(this,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if(result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            audioFocus = AudioFocus.GAINED;
        }
    }

    private void releaseAudioFocus() {
        audioManager.abandonAudioFocus(this);
        audioFocus = AudioFocus.LOST;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                pauseMediaPlayer();
                break;
        }
    }

    @Subscribe
    public void handlePhoneCallState(CallStateEvent callStateEvent) {
        if(mMediaPlayer != null) {
            if(mMediaPlayer.isPlaying()) {
                pauseMediaPlayer();
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        EventHelper.getInstance().register(this);
        return messenger.getBinder();
    }

    @Override
    public void onPrepared(MediaPlayer mMediaPlayer) {
        Toast.makeText(getApplicationContext(),
                "Playing " + station.getName(), Toast.LENGTH_SHORT).show();
        startMediaPlayer();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Toast.makeText(getApplicationContext(),
                "Problem with streaming. Try other stations!!", Toast.LENGTH_SHORT).show();
        return false;
    }

    final Messenger messenger = new Messenger(new MessageHandler());
}
