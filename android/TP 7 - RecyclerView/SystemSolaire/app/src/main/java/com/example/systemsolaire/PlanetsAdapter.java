package com.example.systemsolaire;

import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PlanetsAdapter  extends RecyclerView.Adapter<PlanetsAdapter.MyViewHolder> {
    List<Planet> planets;


    PlanetsAdapter(List<Planet> planets){
        this.planets = planets;
    }


    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent , int viewType){
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view  = layoutInflater.inflate(R.layout.planet_item,parent,false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder , int position){
        holder.display(planets.get(position));
    }

    @Override
    public int getItemCount(){
        return planets.size();
    }




    class MyViewHolder extends  RecyclerView.ViewHolder{
        private TextView nomTV;
        private ImageView photoIV;
        private TextView distanceTV;
        private View planetContainer;


        public MyViewHolder( View itemView) {
            super(itemView);
            nomTV = (TextView) itemView.findViewById(R.id.nom);
            photoIV = (ImageView) itemView.findViewById(R.id.photo);
            distanceTV = (TextView) itemView.findViewById(R.id.distance);
            planetContainer=(View) itemView.findViewById(R.id.PlanetContainer);


        }

        void display(Planet planet){
            nomTV.setText(planet.getNom());
            photoIV.setImageResource(planet.getPhoto());
            distanceTV.setText(String.format("%.2f million km", planet.getDistanceSoleil()));
            planetContainer.setOnClickListener(view -> {
                Intent intent = new Intent(view.getContext(), PlanetDetailsActivity.class);
                intent.putExtra("planet_photo",planet.getPhoto());
                intent.putExtra("planet_name", planet.getNom());
                intent.putExtra("planet_mass", planet.getMasse());
                intent.putExtra("planet_distance", planet.getDistanceSoleil());
                intent.putExtra("planet_period",planet.getPeriodTerr());
                intent.putExtra("planet_satellites", planet.getNbrSatelite());
                view.getContext().startActivity(intent);
            });
        }

    }
}