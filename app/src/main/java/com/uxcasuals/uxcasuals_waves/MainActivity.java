package com.uxcasuals.uxcasuals_waves;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.uxcasuals.uxcasuals_waves.events.NotifyToggleEvent;
import com.uxcasuals.uxcasuals_waves.events.PrepareMediaPlayerEvent;
import com.uxcasuals.uxcasuals_waves.events.ReleaseMediaPlayerEvent;
import com.uxcasuals.uxcasuals_waves.fragments.HomePageFragment;
import com.uxcasuals.uxcasuals_waves.fragments.LandingPageFragment;
import com.uxcasuals.uxcasuals_waves.models.Station;
import com.uxcasuals.uxcasuals_waves.services.MusicService;
import com.uxcasuals.uxcasuals_waves.utils.AsyncHelper;
import com.uxcasuals.uxcasuals_waves.utils.EventHelper;

import org.json.JSONArray;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity {

    private final String TAG = MainActivity.class.getName();
    private final String SERVER_URL = "https://uxcasuals-waves.herokuapp.com/api/stations";
    private List<Station> stations;
    private boolean BACK_BUTTON_PRESSED = false;

    private Messenger mMessenger = null;
    private boolean mBound = false;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMessenger = new Messenger(service);
            mBound = true;
            Log.d(TAG, "Connection Successfull..");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mMessenger = null;
            mBound = false;
            Log.d(TAG, "Connection Closed..");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadStations();
        getFragmentManager().beginTransaction()
                .replace(R.id.container_fluid, new LandingPageFragment())
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadStations() {
        JsonArrayRequest request = new JsonArrayRequest(SERVER_URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                stations = Arrays.asList(new Gson().fromJson(String.valueOf(response), Station[].class));
                Log.d(TAG, "Stations:" + stations);
                loadHomePageFragment();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        AsyncHelper.getInstance(this).addToRequestQueue(request);
    }

    private void loadHomePageFragment() {
        EventHelper.getInstance().post(new PrepareMediaPlayerEvent());
        HomePageFragment homePageFragment = new HomePageFragment();
        homePageFragment.setStations(stations);
        getFragmentManager().beginTransaction()
                .replace(R.id.container_fluid, homePageFragment)
                .commit();
    }

    public void toggleMediaPlayer(View view) {
        EventHelper.getInstance().post(new NotifyToggleEvent());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        EventHelper.getInstance().post(new ReleaseMediaPlayerEvent());
        if(mBound) {
            unbindService(connection);
            mBound = false;
        }
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if(this.BACK_BUTTON_PRESSED) {
            super.onBackPressed();
        } else {
            this.BACK_BUTTON_PRESSED = true;
            Toast.makeText(getApplicationContext(), "Press again to Exit...",  Toast.LENGTH_SHORT).show();

            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    BACK_BUTTON_PRESSED = false;
                }
            }, 10000);
        }
    }
}
