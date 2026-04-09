package com.proyecto.juegoudp.modelo;

/**
 * Participante de la partida: posición en el campo, avatar, puntaje y si porta la pelota.
 */

public class Jugador {
    private int id, avatarId, puntaje;
    private String nombre;
    private float x, y;
    private boolean tienePelota;

    public Jugador(int id, String nombre, int avatarId) {
        this.id = id;
        this.nombre = nombre;
        this.avatarId = avatarId;
        this.puntaje = 0;
        this.tienePelota = false;
        this.x = 512;
        this.y = 384;
    }
    // Getters y setters (todos)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getAvatarId() { return avatarId; }
    public void setAvatarId(int avatarId) { this.avatarId = avatarId; }
    public int getPuntaje() { return puntaje; }
    public void setPuntaje(int puntaje) { this.puntaje = puntaje; }
    public void sumarPuntaje(int puntos) { this.puntaje += puntos; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public float getX() { return x; }
    public void setX(float x) { this.x = x; }
    public float getY() { return y; }
    public void setY(float y) { this.y = y; }
    public boolean isTienePelota() { return tienePelota; }
    public void setTienePelota(boolean tienePelota) { this.tienePelota = tienePelota; }
}
