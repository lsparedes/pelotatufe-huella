package com.example.akshika.opencvtest;

public class ItemPlayers {

    private String campeonato;
    private String serie;
    private String fecha;
    private String id_campeonato;


    public ItemPlayers(){
        super();
    }

    /////////////////////////////////////////////////////////////////

    public String getIdCampeonato() {
        return id_campeonato;
    }

    public void setIdCampeonato(String id_campeonato) {
        this.id_campeonato = id_campeonato;
    }

    /////////////////////////////////////////////////////////////////

    public String getCampeonato() {
        return campeonato;
    }

    public void setCampeonato(String campeonato) {
        this.campeonato = campeonato;
    }

    ///////////////////////////////////////////////////////////////

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    //////////////////////////////////////////////////////////////

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    //////////////////////////////////////////////////////////////

}
