package com.uxcasuals.uxcasuals_waves;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.uxcasuals.uxcasuals_waves.models.Station;
import com.uxcasuals.uxcasuals_waves.utils.AsyncHelper;

import org.json.JSONArray;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity {

    private final String TAG = MainActivity.class.getName();
    private final String SERVER_URL = "https://uxcasuals-waves.herokuapp.com/api/stations";
    private List<Station> stations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadStations();
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

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        AsyncHelper.getInstance(this).addToRequestQueue(request);
    }
}
