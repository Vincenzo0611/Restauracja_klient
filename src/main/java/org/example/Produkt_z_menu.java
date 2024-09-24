package org.example;

import java.io.Serializable;

public class Produkt_z_menu implements Serializable {
    private String nazwa;
    private int id;
    private int kod_z_kasy;

    public Produkt_z_menu(String nazwa, int id, int kod_z_kasy) {
        this.nazwa = nazwa;
        this.id = id;
        this.kod_z_kasy = kod_z_kasy;
    }

    public String getNazwa() {
        return nazwa;
    }

    public int getId() {
        return id;
    }

    public int getKod_z_kasy()
    {
        return kod_z_kasy;
    }
}
