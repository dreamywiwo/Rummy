/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.rummyeventos.actualizaciones;

import itson.rummyeventos.base.EventBase;

/**
 *
 * @author Dana Chavez
 */
public class CantidadFichasPublicoEvent extends EventBase {
    private String jugadorDestino; 
    private int size;

    public static final String TOPIC = "actualizaciones.estado";
    public static final String EVENT_TYPE = "sopa.actualizada";

    public CantidadFichasPublicoEvent() {
        super();
    }

    public CantidadFichasPublicoEvent(String jugadorDestino, int size) {
        super(TOPIC, EVENT_TYPE);
        this.jugadorDestino = jugadorDestino;
        this.size = size;
    }   

    public String getJugadorDestino() {
        return jugadorDestino;
    }

    public void setJugadorDestino(String jugadorDestino) {
        this.jugadorDestino = jugadorDestino;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
    
}
