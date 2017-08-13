package com.vayetek.ecosapp.models;


import java.io.Serializable;
import java.util.Arrays;

public class NoteModelRequest implements Serializable {
    private String idStudent;
    private String idPatient;
    private String idEnseignant;
    private int idStation;
    private int[] notes;
    private int resultCode;

    public NoteModelRequest(String idStudent, String idPatient, String idEnseignant, int idStation, int[] notes, int resultCode) {
        this.idStudent = idStudent;
        this.idPatient = idPatient;
        this.idEnseignant = idEnseignant;
        this.idStation = idStation;
        this.notes = notes;
        this.resultCode = resultCode;
    }

    public String getIdStudent() {
        return idStudent;
    }

    public String getIdPatient() {
        return idPatient;
    }

    public String getIdEnseignant() {
        return idEnseignant;
    }

    public int getIdStation() {
        return idStation;
    }

    public int[] getNotes() {
        return notes;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setIdStudent(String idStudent) {
        this.idStudent = idStudent;
    }

    public void setIdPatient(String idPatient) {
        this.idPatient = idPatient;
    }

    public void setIdEnseignant(String idEnseignant) {
        this.idEnseignant = idEnseignant;
    }

    public void setIdStation(int idStation) {
        this.idStation = idStation;
    }

    public void setNotes(int[] notes) {
        this.notes = notes;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    @Override
    public String toString() {
        return "NoteModelRequest{" +
                "idStudent='" + idStudent + '\'' +
                ", idPatient='" + idPatient + '\'' +
                ", idEnseignant='" + idEnseignant + '\'' +
                ", idStation=" + idStation +
                ", notes=" + Arrays.toString(notes) +
                ", resultCode=" + resultCode +
                '}';
    }
}
