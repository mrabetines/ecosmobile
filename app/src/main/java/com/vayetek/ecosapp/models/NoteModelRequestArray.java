package com.vayetek.ecosapp.models;


import java.util.ArrayList;
import java.util.List;

public class NoteModelRequestArray {
    private List<NoteModelRequest> noteModelRequests;

    public NoteModelRequestArray(List<NoteModelRequest> noteModelRequests) {
        this.noteModelRequests = noteModelRequests;
    }

    public NoteModelRequestArray() {
        noteModelRequests = new ArrayList<>();
    }

    public List<NoteModelRequest> getNoteModelRequests() {
        if (noteModelRequests == null) {
            noteModelRequests = new ArrayList<>();
        }
        return noteModelRequests;
    }

    public void setNoteModelRequests(List<NoteModelRequest> noteModelRequests) {
        this.noteModelRequests = noteModelRequests;
    }
}
