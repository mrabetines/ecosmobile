package com.vayetek.ecosapp.models;

public class Examen {
    private int id_Examen;
    private String date;
    private int max_Places;
    private int nbre_Places;
    private int id_Session;
    private int id_Stage ;
    private int id_Enseignant;
    private String nom;
    private int id_Niveau;

    public int getId_Examen() {
        return id_Examen;
    }

    public void setId_Examen(int id_Examen) {
        this.id_Examen = id_Examen;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getMax_Places() {
        return max_Places;
    }

    public void setMax_Places(int max_Places) {
        this.max_Places = max_Places;
    }

    public int getNbre_Places() {
        return nbre_Places;
    }

    public void setNbre_Places(int nbre_Places) {
        this.nbre_Places = nbre_Places;
    }

    public int getId_Session() {
        return id_Session;
    }

    public void setId_Session(int id_Session) {
        this.id_Session = id_Session;
    }

    public int getId_Stage() {
        return id_Stage;
    }

    public void setId_Stage(int id_Stage) {
        this.id_Stage = id_Stage;
    }

    public int getId_Enseignant() {
        return id_Enseignant;
    }

    public void setId_Enseignant(int id_Enseignant) {
        this.id_Enseignant = id_Enseignant;
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
}
