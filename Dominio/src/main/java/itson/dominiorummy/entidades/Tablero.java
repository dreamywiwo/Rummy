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

public class Tablero {
    
    private final List<Ficha> sopa;
    private final List<Grupo> gruposEnMesa;

    public Tablero() {
        this.sopa = new ArrayList<>();
        this.gruposEnMesa = new ArrayList<>();
    }

    public void agregarGrupo(Grupo grupo) {
        this.gruposEnMesa.add(grupo);
    }
}