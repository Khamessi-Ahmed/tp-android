package com.example.tp1.controleur;

import android.content.Context;

import com.example.tp1.modele.AccesLocal;
import com.example.tp1.modele.Profil;
import com.example.tp1.outils.Serializer;

import java.util.Date;

public final class Controle {
    private static Controle instance = null;
    private static Profil profil;
    private static String nomFic = "saveprofil";
    private static AccesLocal accesLocal;

    /**
     * consructeur private
     */
    private Controle() {
        super();
    }

    /**
     * Création de l'instance
     * @return instance
     */
    public static final Controle getInstance(Context contexte) {
        if (Controle.instance == null) {
            Controle.instance = new Controle();
            accesLocal = new AccesLocal(contexte);
            profil = accesLocal.recupDernier();
            // recupSerialize(contexte);
        }
        return Controle.instance;
    }

    /**
     * Création du profil
     * @param poids
     * @param taille en cm
     * @param age
     * @param sexe 1 pour homme et 0 pour femme
     */
    public void creerProfil(Integer poids, Integer taille, Integer age, Integer sexe, Context contexte) {
        profil = new Profil(new Date(), poids, taille, age, sexe);
        accesLocal.ajout(profil);
        // Serializer.serialize(nomFic, profil, contexte);
    }

    /**
     * Récupération IMG du profil
     * @return IMG
     */
    public float getImg() {
        return profil.getImg();
    }

    /**
     * Récupération message du profil
     * @return message
     */
    public String getMessage() {
        return profil.getMessage();
    }

    /**
     * Récupération de l'objet sérialisé (le profil)
     * @param contexte
     */
    private static void recupSerialize(Context contexte) {
        profil = (Profil) Serializer.deSerialize(nomFic, contexte);
    }

    /**
     * Récupération poids du profil
     * @return poids
     */
    public Integer getPoids() {
        if (profil == null) {
            return null;
        }
        return profil.getPoids();
    }

    /**
     * Récupération taille du profil
     * @return taille
     */
    public Integer getTaille() {
        if (profil == null) {
            return null;
        }
        return profil.getTaille();
    }

    /**
     * Récupération age du profil
     * @return age
     */
    public Integer getAge() {
        if (profil == null) {
            return null;
        }
        return profil.getAge();
    }

    /**
     * Récupération sexe du profil
     * @return sexe
     */
    public Integer getSexe() {
        if (profil == null) {
            return null;
        }
        return profil.getSexe();
    }
}
