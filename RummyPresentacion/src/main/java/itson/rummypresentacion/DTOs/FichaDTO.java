/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.rummypresentacion.DTOs;

import java.util.Objects;

public class FichaDTO {
    private int numero;
    private String color;
    private String id;
    
    public FichaDTO(int numero, String color, String id) {
        this.numero = numero;
        this.color = color;
        this.id = id;
    }

    public int getNumero() {
        return numero;
    }

    public String getColor() {
        return color;
    }

    public String getId() {
        return id;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        FichaDTO ficha = (FichaDTO) obj;
        return numero == ficha.numero && 
               Objects.equals(color, ficha.color) && 
               Objects.equals(id, ficha.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(numero, color, id);
    }
    
    @Override
    public String toString() {
        return "FichaDTO{id='" + id + "', numero=" + numero + ", color='" + color + "'}";
    }
}
