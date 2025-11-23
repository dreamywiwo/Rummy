/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.dominiorummy.entidades;

/**
 *
 * @author victoria
 */

public class Turno {
    
    private String jugadorTurnoId;
    
    public Turno() {
        this.jugadorTurnoId = null;
    }
    
    public boolean esTurnoDelJugador(String jugadorId) {
        return this.jugadorTurnoId != null && this.jugadorTurnoId.equals(jugadorId); 
    }

    public void setJugadorTurnoId(String jugadorTurnoId) {
        this.jugadorTurnoId = jugadorTurnoId;
    }
}
