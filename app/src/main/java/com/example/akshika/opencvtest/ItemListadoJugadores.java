package com.example.akshika.opencvtest;

public class ItemListadoJugadores {

    private String nombre;
    private String id;
    private String fingerprint;


    public ItemListadoJugadores(){
        super();
    }

    /////////////////////////////////////////////////////////////////

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /////////////////////////////////////////////////////////////////

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /////////////////////////////////////////////////////////////////

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }
}
