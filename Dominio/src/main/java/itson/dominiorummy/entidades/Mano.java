/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.dominiorummy.entidades;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author victoria
 */

public class Mano {

    private final List<Ficha> fichas;

    public Mano() {
        this.fichas = new ArrayList<>();
    }

    public boolean contieneFichas(List<Ficha> fichasABajar) {
        return this.fichas.containsAll(fichasABajar);
    }

    public void removerFichas(List<Ficha> fichasABajar) {
        this.fichas.removeAll(fichasABajar);
    }

    public void agregarFicha(Ficha ficha) {
        this.fichas.add(ficha);
    }

    public List<Ficha> getFichas() {
        return fichas;
    }
}