package com.example.systemsolaire;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private List<Planet> Planets;
    private PlanetsAdapter monAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView)findViewById(R.id.myRecyclerView);
        Planets = new ArrayList<>();
        Planets.add(new Planet("Mercure", R.drawable.mercure,52.662F,"3.285 × 10^23 kg",88F,0));
        Planets.add(new Planet("Vénus",R.drawable.venus,108.77F,"4.867 × 10^24 kg",117,0));
        Planets.add(new Planet("Terre",R.drawable.terre,147.68F,"5.97219 × 10^24 kilograms",365.25F,1));
        Planets.add(new Planet("Mars",R.drawable.mars,236.62F,"6.39 × 10^23 kg",687F,2));
        Planets.add(new Planet("Jupiter",R.drawable.jupiter,758.15F,"1.898 × 10^27 kg",4380F,95));
        Planets.add(new Planet("Saturne",R.drawable.saturne,14415.00F,"5.683 × 10^26 kg",10731F,146));
        Planets.add(new Planet("Uranus",R.drawable.uranus,29249.00F,"8.681 × 10^25 kg",30660F,27));
        Planets.add(new Planet("Neptune",R.drawable.neptune,44715.00F,"1.024 × 10^26 kg",601520,14));
        monAdapter=new PlanetsAdapter(Planets);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRecyclerView.setAdapter(monAdapter);

    }
}