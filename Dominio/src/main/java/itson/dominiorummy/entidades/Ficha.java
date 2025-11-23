/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.dominiorummy.entidades;

/**
 *
 * @author victoria
 */

public class Ficha {
    private final String id;
    private final int numero;
    private final String color;

    public Ficha(String id, int numero, String color) {
        this.id = id;
        this.numero = numero;
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public int getNumero() {
        return numero;
    }

    public String getColor() {
        return color;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Ficha ficha = (Ficha) obj;
        
        return id.equals(ficha.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}