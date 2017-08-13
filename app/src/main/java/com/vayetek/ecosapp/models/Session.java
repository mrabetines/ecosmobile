package com.vayetek.ecosapp.models;

/**
 * Created by S4M37 on 19/01/2016.
 */
public class Session {
    private int id_Session;
    private String nom;
    private int id_Niveau;
    private String niveau;

    public int getId_Session() {
        return id_Session;
    }

    public void setId_Session(int id_Session) {
        this.id_Session = id_Session;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getId_Niveau() {
        return id_Niveau;
    }

    public void setId_Niveau(int id_Niveau) {
        this.id_Niveau = id_Niveau;
    }

    public String getNiveau() {
        return niveau;
    }

    public void setNiveau(String niveau) {
        this.niveau = niveau;
    }
}
