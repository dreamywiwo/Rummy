/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.dominiorummy.entidades;

import java.util.List;

/**
 *
 * @author Adrián
 */
public class Sopa {

    private final List<Ficha> fichas;

    public Sopa(List<Ficha> fichas) {
        this.fichas = fichas;
    }

    
    public Ficha tomarFicha() {
        int numRandom = (int) (Math.random() * 14);
        if (numRandom == 14) {
            Ficha ficha = new Ficha("ficha", numRandom, "Comodin",true);
            return ficha;
        } else {
            int r = (int) (Math.random() * 5);
            String color = new String[]{"Roho", "Azul", "Verde", "Russia"}[r];
            Ficha ficha = new Ficha("ficha", numRandom, color,false);
            fichas.remove(ficha);
            return ficha;

        }
    }

    public void agregarFicha(Ficha ficha) {
        fichas.add(ficha);
        System.out.println("La ficha ha sido añadida");
    }
    
    public void descartarFicha(Ficha ficha){
        
        fichas.remove(ficha);
    }

}
