/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.dominiorummy.entidades;

import itson.rummydtos.FichaDTO;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author victoria
 */

public class Mano {

    private final List<Ficha> fichas = new ArrayList<>();

    public Mano() {}

    public List<Ficha> getFichas() {
        return fichas;
    }

    public void agregarFicha(Ficha f) {
        if (f != null) fichas.add(f);
    }

    public boolean tieneFicha(String fichaId) {
        return fichas.stream().anyMatch(f -> f.getId().equals(fichaId));
    }

    /**
     * Quita la primera ocurrencia con id y la retorna. Retorna null si no existe.
     */
    public Ficha quitarFicha(String fichaId) {
        Iterator<Ficha> it = fichas.iterator();
        while (it.hasNext()) {
            Ficha f = it.next();
            if (f.getId().equals(fichaId)) {
                it.remove();
                return f;
            }
        }
        return null;
    }

    /**
     * Clona el contenido (deep copy de fichas para snapshot/rollback).
     */
    public List<Ficha> clonarContenido() {
        return fichas.stream()
                .map(Ficha::clonar)
                .collect(Collectors.toList());
    }

    /**
     * Restaurar la mano a un estado (lista de fichas copiada).
     */
    public void restaurar(List<Ficha> estadoOriginal) {
        fichas.clear();
        if (estadoOriginal != null) {
            estadoOriginal.forEach(f -> fichas.add(f.clonar()));
        }
    }

}