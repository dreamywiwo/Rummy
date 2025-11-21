/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.rummypresentacion.vista;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Dana Chavez
 */
public class Tablero implements Serializable {
    private List<Grupo> grupos;
    private Mano manoJugador;

    public Tablero() {
        this.grupos = new ArrayList<>();
        this.manoJugador = new Mano("jugador1");
    }

    public void agregarGrupo(Grupo grupo) {
        grupos.add(grupo);
    }

    public void removerGrupo(Grupo grupo) {
        grupos.remove(grupo);
    }

    public List<Grupo> getGrupos() {
        return grupos;
    }

    public Mano getManoJugador() {
        return manoJugador;
    }
}