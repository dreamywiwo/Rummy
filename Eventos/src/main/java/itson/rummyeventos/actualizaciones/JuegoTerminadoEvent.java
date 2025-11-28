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
public class JuegoTerminadoEvent extends EventBase {
    
    private String jugadorGanadorId;
    
    public static final String TOPIC = "actualizaciones.estado";
    public static final String EVENT_TYPE = "juego.terminado";

    public JuegoTerminadoEvent() {
        super();
    }

    public JuegoTerminadoEvent(String jugadorGanadorId) {
        super(TOPIC, EVENT_TYPE);
        this.jugadorGanadorId = jugadorGanadorId;
    }

    public String getJugadorGanadorId() {
        return jugadorGanadorId;
    }

}
