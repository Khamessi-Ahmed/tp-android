package com.example.systemsolaire;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PlanetDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planet_details);

        // Retrieve planet data from intent
        int planetPhoto=getIntent().getIntExtra("planet_photo",0);
        String planetName = getIntent().getStringExtra("planet_name");
        String planetMass = getIntent().getStringExtra("planet_mass");
        float planetDistance = getIntent().getFloatExtra("planet_distance", 0);

        int planetSatellites = getIntent().getIntExtra("planet_satellites", 0);
        float planetPeriod = getIntent().getFloatExtra("planet_period", 0);
        // Populate UI
        TextView nameTV = findViewById(R.id.nom);
        TextView massTV = findViewById(R.id.masse);
        TextView distanceTV = findViewById(R.id.distance);
        TextView periodTV = findViewById(R.id.period);
        TextView satellitesTV = findViewById(R.id.nbrSatelite);
        ImageView photoIV = (ImageView) findViewById(R.id.photo);

photoIV.setImageResource(planetPhoto);
        nameTV.setText(planetName);
        massTV.setText("Masse: " + planetMass);
            distanceTV.setText("Distance du soleil : " + planetDistance + " million km");
        satellitesTV.setText("nombre de Satelite: " + planetSatellites);
        periodTV.setText("Period de revolution :" + planetPeriod + " jour terrestres");

    }
}
