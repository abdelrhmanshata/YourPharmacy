package com.abdelrhmanshata.yourpharmacy.Model;

import java.io.Serializable;

public class ModelID implements Serializable {

    String ID;

    public ModelID() {
    }

    public ModelID(String ID) {
        this.ID = ID;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
