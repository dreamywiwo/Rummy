/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.dominiorummy.entidades;

import java.util.List;

/**
 *
 * @author victoria
 */

public class Grupo {
    
    private final List<Ficha> fichas;
    private final String jugadorCreadorId;

    public Grupo(List<Ficha> fichas, String jugadorCreadorId) {
        this.fichas = fichas;
        this.jugadorCreadorId = jugadorCreadorId;
    }
    
    public List<Ficha> getFichas() {
        return fichas;
    }

    public String getJugadorCreadorId() {
        return jugadorCreadorId;
    }
}
