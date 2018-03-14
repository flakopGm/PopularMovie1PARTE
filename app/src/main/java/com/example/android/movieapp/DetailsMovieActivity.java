package com.example.android.movieapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.android.movieapp.model.Movie;
import com.squareup.picasso.Picasso;
import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * Clase para mostrar los detalles de la película seleccionada.
 */

public class DetailsMovieActivity extends AppCompatActivity {

    @BindView(R.id.poster)
    ImageView poster;
    @BindView(R.id.bandera)
    ImageView bandera;
    @BindView(R.id.original_title)
    TextView originalTitle;
    @BindView(R.id.fecha_lanza)
    TextView fechaLanzamiento;
    @BindView(R.id.idioma_original)
    TextView idiomaOriginal;
    @BindView(R.id.sipnosis)
    TextView sipnosis;
    @BindView(R.id.vote_average)
    RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_movie);
        ButterKnife.bind(DetailsMovieActivity.this);

        Context context = getApplicationContext();

        // Recogemos los datos pasados para proceder a mostrarlos de forma adecuada.
        Intent intent = getIntent();
        Movie currentMovie = intent.getParcelableExtra("movieSelect");

        // TITULO
        this.setTitle(currentMovie.getmTitulo());

        // FECHA
        String movieDate = currentMovie.getmFechaLanzamiento();
        // Si la fecha es nula o está vacía definiremos que no hay info, de lo contrario la fecha.
        if (movieDate.equals("") || movieDate == null) {
            fechaLanzamiento.setText(R.string.no_data);
        } else {
            fechaLanzamiento.setText(movieDate);
        }

        // VALORACIÓN
        String movieVoteAverage = currentMovie.getmPromedioVoto();
        double voto = Double.parseDouble(movieVoteAverage);
        // Promedio aproximado para el RatingBar según la valoración de la película.
        if (voto <= 3.5) {
            ratingBar.setRating(1.5f);
        } else if (voto > 3.5 && voto <= 5.5) {
            ratingBar.setRating(2.5f);
        } else if (voto > 5.5 && voto <= 8) {
            ratingBar.setRating(3.5f);
        } else if (voto > 8 && voto <= 9) {
            ratingBar.setRating(4);
        } else if (voto > 8.5 && voto <= 10) {
            ratingBar.setRating(5);
        } else {
            ratingBar.setRating(0);
        }

        // TITULO ORIGINAL
        originalTitle.setText(currentMovie.getmTituloOriginal().toUpperCase());

        // IDIOMA ORIGINAL
        String idioma = currentMovie.getmIdiomaOriginal();
        // Según el idioma se establece una bandera del pais, por defecto si no se encuentra el pais
        // en el grupo añadido se oculta la bandera y se muestra las iniciales del pais.
        idiomaOriginal.setText(currentMovie.getmIdiomaOriginal());
        switch (idioma) {
            case "es":
                bandera.setBackgroundResource(R.drawable.spain);
                idiomaOriginal.setVisibility(View.GONE);
                break;
            case "en":
                bandera.setBackgroundResource(R.drawable.estadosunidos);
                idiomaOriginal.setVisibility(View.GONE);
                break;
            case "ja":
                bandera.setBackgroundResource(R.drawable.japon);
                idiomaOriginal.setVisibility(View.GONE);
                break;
            case "it":
                bandera.setBackgroundResource(R.drawable.italia);
                idiomaOriginal.setVisibility(View.GONE);
                break;
            case "pt":
                bandera.setBackgroundResource(R.drawable.portugal);
                idiomaOriginal.setVisibility(View.GONE);
                break;
            case "hi":
                bandera.setBackgroundResource(R.drawable.hindu);
                idiomaOriginal.setVisibility(View.GONE);
                break;
            case "de":
                bandera.setBackgroundResource(R.drawable.aleman);
                idiomaOriginal.setVisibility(View.GONE);
                break;
            default:
                bandera.setVisibility(View.GONE);
                idiomaOriginal.setVisibility(View.VISIBLE);
        }

        // SIPNOSIS
        sipnosis.setText(currentMovie.getmSipnosis());

        // PORTADA
        Picasso.with(context)
                .load(currentMovie.getmPortada())
                .resize(getResources().getInteger(R.integer.TargetWidth), getResources().getInteger(R.integer.TargetHeight))
                .into(poster);
    }
}
