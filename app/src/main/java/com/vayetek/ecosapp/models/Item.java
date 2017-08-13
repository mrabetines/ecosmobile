package com.vayetek.ecosapp.models;

public class Item {
    private boolean response;
    private int id_Item;
    private int valeur;
    private String label;
    private int id_Competence;
    private int id_TitreGItem;
    private int id_Banque;
    private String nom;

    public boolean isResponse() {
        return response;
    }

    public void setResponse(boolean response) {
        this.response = response;
    }

    public int getId_Item() {
        return id_Item;
    }

    public void setId_Item(int id_Item) {
        this.id_Item = id_Item;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getValeur() {
        return valeur;
    }

    public void setValeur(int valeur) {
        this.valeur = valeur;
    }

    public int getId_Competence() {
        return id_Competence;
    }

    public void setId_Competence(int id_Competence) {
        this.id_Competence = id_Competence;
    }

    public int getId_TitreGItem() {
        return id_TitreGItem;
    }

    public void setId_TitreGItem(int id_TitreGItem) {
        this.id_TitreGItem = id_TitreGItem;
    }

    public int getId_Banque() {
        return id_Banque;
    }

    public void setId_Banque(int id_Banque) {
        this.id_Banque = id_Banque;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
}
