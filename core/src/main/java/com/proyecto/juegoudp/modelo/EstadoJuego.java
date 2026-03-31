package com.proyecto.juegoudp.modelo;

import java.util.ArrayList;
import java.util.List;
// esta es la que nos sirve para udp
public class EstadoJuego {
    private List<Ficha> fichas = new ArrayList<>();

    public EstadoJuego(){
        fichas = new ArrayList<>();
    }
    public void agregarFicha (Ficha ficha){
        fichas.add(ficha);
    }
    public List<Ficha> getFichas(){
        return fichas;
    }
}
