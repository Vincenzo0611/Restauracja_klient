package org.example;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Paragon implements Serializable {
    private int id;
    private String create_time;
    private String destroy_time;
    private int numer_stolika;
    private boolean zaplacony;
    private String kelner;

    List<Produkt_na_paragonie> products = new ArrayList<>();

    public Paragon(int id)
    {
        this.id = id;
    }

    public int getNumer_stolika() {
        return numer_stolika;
    }

    public int getId() {
        return id;
    }

    public String getKelner() {
        return kelner;
    }

    public void setKelner(String kelner) {
        this.kelner = kelner;
    }

    public List<Produkt_na_paragonie> getProducts() {
        return products;
    }

    public void setProducts(List<Produkt_na_paragonie> products) {
        this.products = products;
    }

    public String getCreate_time() {
        return create_time;
    }

    public String getDestroy_time() {
        return destroy_time;
    }
    public boolean isZaplacony() {
        return zaplacony;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNumer_stolika(int numer_stolika) {
        this.numer_stolika = numer_stolika;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public void setDestroy_time(String destroy_time) {
        this.destroy_time = destroy_time;
    }

    public void setZaplacony(boolean zaplacony) {
        this.zaplacony = zaplacony;
    }

    public void addProduct(Produkt_na_paragonie p)
    {
        products.add(p);
    }

    public void removeProduct_number(int number)
    {
        for(Produkt_na_paragonie p : products)
        {
            if(p.getNumer_na_paragonie() == number)
            {
                products.remove(p);
                return;
            }
        }
    }

    public void removeAllProducts()
    {
        products.clear();
    }


}
