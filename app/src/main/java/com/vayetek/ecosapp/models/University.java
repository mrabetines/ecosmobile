package com.vayetek.ecosapp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class University implements Serializable {
    @SerializedName("id_University")
    private
    int idUniversity;
    private String label;
    private String region;
    private String login;


    public String getLabel() {
        return label;
    }

    public String getRegion() {
        return region;
    }

    public int getIdUniveristy() {
        return idUniversity;
    }

    public String getLogin() {
        return login;
    }
}
