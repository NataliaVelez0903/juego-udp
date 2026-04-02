package com.proyecto.juegoudp.logica;
//  detecta fin de partida ...
public class SistemaTiempo {

    private float tiempoRestante;
    private boolean tiempoTerminado;

    public SistemaTiempo (float tiempoInicial){
        this.tiempoRestante = tiempoInicial;
        this.tiempoTerminado = false;
    }

    public void actualizar (float delta){
        if (!tiempoTerminado){
            tiempoRestante -=delta;

            if(tiempoRestante <= 0 ){
                tiempoRestante = 0;
                tiempoTerminado = true;
            }
        }
    }

    public boolean tiempoHaTerminado (){
        return tiempoTerminado;
    }

    public String getTiempoFormateado(){
        int totalSegundos = (int) Math.ceil(tiempoRestante);
        int minutos = totalSegundos / 60;
        int segundos = totalSegundos % 60;
        return String.format("%02d:%02d",minutos,segundos);
    }

}
