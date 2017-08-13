package com.vayetek.ecosapp.models;

public class Station {
    private int id_Station;
    private int ponderation;
    private int id_Banque;
    private String label;
    private String situation_Clinique;
    private String instruction_Etudiant;
    private int id_Domaine;
    private int id_Critere;
    private int id_Systeme;
    private int id_Contexte;

    public int getId_Station() {
        return id_Station;
    }

    public void setId_Station(int id_Station) {
        this.id_Station = id_Station;
    }

    public int getPonderation() {
        return ponderation;
    }

    public void setPonderation(int ponderation) {
        this.ponderation = ponderation;
    }

    public int getId_Banque() {
        return id_Banque;
    }

    public void setId_Banque(int id_Banque) {
        this.id_Banque = id_Banque;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getSituation_Clinique() {
        return situation_Clinique;
    }

    public void setSituation_Clinique(String situation_Clinique) {
        this.situation_Clinique = situation_Clinique;
    }

    public String getInstruction_Etudiant() {
        return instruction_Etudiant;
    }

    public void setInstruction_Etudiant(String instruction_Etudiant) {
        this.instruction_Etudiant = instruction_Etudiant;
    }

    public int getId_Domaine() {
        return id_Domaine;
    }

    public void setId_Domaine(int id_Domaine) {
        this.id_Domaine = id_Domaine;
    }

    public int getId_Critere() {
        return id_Critere;
    }

    public void setId_Critere(int id_Critere) {
        this.id_Critere = id_Critere;
    }

    public int getId_Systeme() {
        return id_Systeme;
    }

    public void setId_Systeme(int id_Systeme) {
        this.id_Systeme = id_Systeme;
    }

    public int getId_Contexte() {
        return id_Contexte;
    }

    public void setId_Contexte(int id_Contexte) {
        this.id_Contexte = id_Contexte;
    }
}
