package com.uxcasuals.uxcasuals_waves;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
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
import com.uxcasuals.uxcasuals_waves.utils.NetworkHelper;

import org.json.JSONArray;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

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

//    private BroadcastReceiver NetworkStateHandler = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(context, "Inside broadcast receiver", Toast.LENGTH_SHORT).show();
//            if(!(stations != null && stations.size() > 0)) loadStations();
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadStations();
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        PhoneStateListener phoneStateListener = new PhoneStateListener();
        if(telephonyManager != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
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

//    private void registerBroadcastReceivers() {
//        final IntentFilter filters = new IntentFilter();
//        filters.addAction("android.net.wifi.WIFI_STATE_CHANGED");
//        filters.addAction("android.net.conn.CONNECTIVITY_CHANGE");
//        filters.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
//        getApplicationContext().registerReceiver(NetworkStateHandler, filters);
//    }

    private void loadStations() {
        if(!NetworkHelper.isConnectedToInternet(getApplicationContext())) {
            Snackbar.make(findViewById(R.id.container_fluid),
                    "Network connectivity not available", Snackbar.LENGTH_SHORT).show();
//            registerBroadcastReceivers();
            return;
        }
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
                Toast.makeText(getApplicationContext(), "Error fetching stations", Toast.LENGTH_SHORT).show();
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
    protected void onDestroy() {
        EventHelper.getInstance().post(new ReleaseMediaPlayerEvent());
        if(mBound) {
            unbindService(connection);
            mBound = false;
        }
        super.onDestroy();
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
