/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.rummypresentacion.DTOs;

/**
 *
 * @author jrasc
 */
public class FichaDTO {

    private final int numero;
    private final java.awt.Color color;

    public FichaDTO(int numero, java.awt.Color color) {
        this.numero = numero;
        this.color = color;
    }

    public int getNumero() {
        return numero;
    }

    public java.awt.Color getColor() {
        return color;
    }
}
