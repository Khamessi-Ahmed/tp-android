package com.example.tp1.modele;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.tp1.outils.MySQLiteOpenHelper;

import java.util.Date;

public class AccesLocal {
    // propriétés
    private String nomBase = "bdTP1.sqlite";
    private Integer versionBase = 1;
    private MySQLiteOpenHelper accesBD;
    private SQLiteDatabase bd;

    /**
     * Constructeur
     * @param contexte
     */
    public AccesLocal(Context contexte) {
        accesBD = new MySQLiteOpenHelper(contexte, nomBase, null, versionBase);
    }

    /**
     * Ajout d'un profil dans la BD
     * @param profil
     */
    public void ajout(Profil profil) {
        bd = accesBD.getWritableDatabase();
        String req = "INSERT INTO profil (datemesure, poids, taille, age, sexe) VALUES ";
        req += "(\"" + profil.getDateMesure() + "\"," + profil.getPoids() + "," + profil.getTaille() + "," + profil.getAge() + "," + profil.getSexe() + ")";
        bd.execSQL(req);
    }

    /**
     * Récupérationdu dernier profil de la BD
     * @return
     */
    public Profil recupDernier() {
        bd = accesBD.getReadableDatabase();
        Profil profil = null;
        String req = "SELECT * FROM profil";
        Cursor curseur = bd.rawQuery(req, null);
        curseur.moveToLast();
        if(!curseur.isAfterLast()) {
            Date date = new Date();
            Integer poids = curseur.getInt(1);
            Integer taille = curseur.getInt(2);
            Integer age = curseur.getInt(3);
            Integer sexe = curseur.getInt(4);
            profil = new Profil(date, poids, taille, age, sexe);
        }
        curseur.close();
        return profil;
    }
}
