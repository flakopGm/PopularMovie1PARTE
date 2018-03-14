package com.example.android.movieapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.movieapp.Settings.SettingActivity;
import com.example.android.movieapp.Utilities.MovieAdapter;
import com.example.android.movieapp.Utilities.NetworkUtils;
import com.example.android.movieapp.data.MovieJson;
import com.example.android.movieapp.model.Movie;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    @BindView(R.id.gridview)
    GridView gridView;
    @BindView(R.id.noconecttion)
    ImageView imageViewNoConecttion;
    @BindView(R.id.text_noconecttion)
    TextView textViewNoConecttion;

    // Constantes auxiliares.
    private static final String ORDEN_POPULAR = "popular";
    private static final String ORDEN_DEFAULT = ORDEN_POPULAR;
    private static final String API_KEY = NetworkUtils.API_KEY_COMPROBACION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(MainActivity.this);
        imageViewNoConecttion.setImageResource(R.drawable.sinconexion);
        hayKeyApi();

        // Iniciamos el trabajo de fondo que nos presentará las portadas de las películas.
        cargarPortadas(definirPreferences());

        // Definimos la escucha al GridView y según su selección pasamos la información.
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetailsMovieActivity.class);
                Movie currentMovie = (Movie) parent.getItemAtPosition(position);
                intent.putExtra("movieSelect", currentMovie);

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
    }

    // Si hay conexión a internet (podemos realizar la solicitud de conexión) entonces procedemos
    // con el trabajo de fondo de lo contrario avisamos de la falla de conexión.
    private void cargarPortadas(String ordenMovies) {
        if (hayInternet()) {
            new MovieAsyncTask().execute(ordenMovies);
            gridView.setVisibility(View.VISIBLE);
            textViewNoConecttion.setVisibility(View.GONE);
            imageViewNoConecttion.setVisibility(View.GONE);
        } else {
            gridView.setVisibility(View.GONE);
            textViewNoConecttion.setVisibility(View.VISIBLE);
            imageViewNoConecttion.setVisibility(View.VISIBLE);
            Toast.makeText(this, R.string.toast, Toast.LENGTH_SHORT).show();
        }
    }

    // Escucha para cambios de preferencias.
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(R.string.key_orden_settings)) {
            cargarPortadas(sharedPreferences.getString(getString(R.string.key_orden_settings),
                    getString(R.string.popular)));
        }
    }

    public class MovieAsyncTask extends AsyncTask<String, Void, Movie[]> {

        @Override
        protected Movie[] doInBackground(String... strings) {

            URL urlDefault = NetworkUtils.buildUrl(strings[0]);
            try {
                String jsonMovie = NetworkUtils.getResponseFromHttpUrl(urlDefault);

                return MovieJson.recogerDatosMovieDeJson(jsonMovie);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Movie[] movies) {

            if (movies != null) {
                MovieAdapter adapter = new MovieAdapter(MainActivity.this, movies);
                gridView.setAdapter(adapter);
            } else {
                if (API_KEY.equals("") || API_KEY.isEmpty()) {
                } else {
                    Toast.makeText(MainActivity.this, R.string.ocurrioError, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // Comprobación de conexión.
    private boolean hayInternet() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // Definición de preferences.
    private String definirPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String orderUser = sharedPreferences.getString(getString(R.string.key_orden_settings), getString(R.string.order_popular));
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        if (orderUser.equals("")) {
            orderUser = ORDEN_DEFAULT;
        }
        return orderUser;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.orden_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.orden_preference:
                lanzarSettings();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Método auxiliar, sólo lanza la actividad de ajustes.
    private void lanzarSettings() {
        Intent intent = new Intent(MainActivity.this, SettingActivity.class);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    // Método auxiliar de aviso para la key.
    private void hayKeyApi() {
        if (API_KEY.isEmpty() || API_KEY.equals("")) {
            Toast.makeText(this, R.string.faltaKeyApi, Toast.LENGTH_LONG).show();
        }
    }
}
