/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.dominiorummy.entidades;

/**
 *
 * @author victoria
 */

public class Jugador {
    
    private final String id;
    private final String nombre;
    private final Mano mano;
    private boolean primerGrupoBajado;

    public Jugador(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.mano = new Mano();
        this.primerGrupoBajado = false;
    }

    public String getId() {
        return id;
    }

    public Mano getMano() {
        return mano;
    }

    public boolean isPrimerGrupoBajado() {
        return primerGrupoBajado;
    }

    public void setPrimerGrupoBajado(boolean primerGrupoBajado) {
        this.primerGrupoBajado = primerGrupoBajado;
    }
}
