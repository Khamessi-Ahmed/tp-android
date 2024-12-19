package com.example.systemsolaire;

public class Planet {

    private String nom;
    private int photo;
    private float  distanceSoleil;
    private String masse;
    private float periodTerr;
    private int nbrSatelite;

    public Planet(String nom, int photo, float distanceSoleil, String masse, float periodTerr, int nbrSatelite) {
        this.nom = nom;
        this.photo = photo;
        this.distanceSoleil = distanceSoleil;
        this.masse = masse;
        this.periodTerr = periodTerr;
        this.nbrSatelite = nbrSatelite;
    }

    public String getNom() {
        return nom;
    }

    public int getPhoto() {
        return photo;
    }

    public float getDistanceSoleil() {
        return distanceSoleil;
    }

    public String getMasse() {
        return masse;
    }

    public float getPeriodTerr() {
        return periodTerr;
    }

    public int getNbrSatelite() {
        return nbrSatelite;
    }
}
