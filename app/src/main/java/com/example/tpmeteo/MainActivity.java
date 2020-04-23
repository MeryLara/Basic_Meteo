package com.example.tpmeteo;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private TextView ville, Date, Meteo, tmin, tmax, Pression, Humidite;
    private ImageView atmosphere;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ville = (TextView) findViewById(R.id.ville);
        Date = (TextView) findViewById(R.id.date);
        Meteo = (TextView) findViewById(R.id.meteo);
        tmin = (TextView) findViewById(R.id.tmin1);
        tmax = (TextView) findViewById(R.id.tmax1);
        Pression = (TextView) findViewById(R.id.pression1);
        Humidite = (TextView) findViewById(R.id.humidite1);
        atmosphere = (ImageView) findViewById(R.id.atmosphere);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        final Context context = this;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                ville.setText(query.toUpperCase());
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                String url = "http://api.openweathermap.org/data/2.5/weather?q=" + query + "&appid=f60f403bc9acf147429d89f2e8a21b3b";
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            java.util.Date date = new Date(jsonObject.getLong("dt") * 1000);
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            String dateStr = simpleDateFormat.format(date);
                            JSONObject main = jsonObject.getJSONObject("main");
                            int tmp = (int) (main.getDouble("temp") - 273.15);
                            int tempMin = (int) (main.getDouble("temp_min") - 273.15);
                            int tempMax = (int) (main.getDouble("temp_max") - 273.15);
                            int pression = (int) (main.getDouble("pressure"));
                            int humidite = (int) (main.getDouble("humidity"));
                            JSONArray weather = jsonObject.getJSONArray("weather");
                            Date.setText(dateStr);
                            Meteo.setText(String.valueOf(tmp + " °C"));
                            tmin.setText(String.valueOf(tempMin + " °C"));
                            tmax.setText(String.valueOf(tempMax + " °C"));
                            Pression.setText(String.valueOf(pression + " hPa"));
                            Humidite.setText(String.valueOf(humidite + " %"));
                            String meteo = weather.getJSONObject(0).getString("main");
                            switch (meteo) {
                                case "Rain":
                                    atmosphere.setImageResource(R.drawable.rain);
                                    break;
                                case "Clear":
                                    atmosphere.setImageResource(R.drawable.clear);
                                    break;
                                case "Thunderstorm":
                                    atmosphere.setImageResource(R.drawable.storm);
                                    break;
                                case "Clouds":
                                    atmosphere.setImageResource(R.drawable.cloud);
                                    break;
                                case "Mist":
                                    atmosphere.setImageResource(R.drawable.fog);
                                case "Snow":
                                    atmosphere.setImageResource(R.drawable.snow);




                            }
                        } catch (Exception e) {

                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ville.setText("Météo");
                        Date.setText("");
                        Meteo.setText("");
                        tmin.setText("");
                        tmax.setText(String.valueOf(""));
                        Pression.setText("");
                        Humidite.setText("");
                        Toast.makeText(context, "problème de connexion ou la ville saisi n'existe pas", Toast.LENGTH_LONG).show();

                    }


                });
                queue.add(stringRequest);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;

    }
}