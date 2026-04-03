package com.proyecto.juegoudp.modelo;

public class ConfiguracionPartida {
    private int numeroJugadores = 2;
    private float tiempoLimite = 60;
    private boolean esHost = false;
    private String ipServidor = "";

    public int getNumeroJugadores() { return numeroJugadores; }
    public void setNumeroJugadores(int numeroJugadores) { this.numeroJugadores = numeroJugadores; }
    public float getTiempoLimite() { return tiempoLimite; }
    public void setTiempoLimite(float tiempoLimite) { this.tiempoLimite = tiempoLimite; }
    public boolean isEsHost() { return esHost; }
    public void setEsHost(boolean esHost) { this.esHost = esHost; }
    public String getIpServidor() { return ipServidor; }
    public void setIpServidor(String ipServidor) { this.ipServidor = ipServidor; }
}