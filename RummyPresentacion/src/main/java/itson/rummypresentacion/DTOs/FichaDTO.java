package itson.rummypresentacion.DTOs;

import java.io.Serializable;
import java.util.Objects;

public class FichaDTO implements Serializable {
    private int numero;
    private String color;
    private String id;
    private boolean esComodin;

    // Constructor original
    public FichaDTO(int numero, String color, String id) {
        this.numero = numero;
        this.color = color;
        this.id = id;
        this.esComodin = false;
    }

    // Constructor nuevo que incluye el comodín
    public FichaDTO(int numero, String color, String id, boolean esComodin) {
        this.numero = numero;
        this.color = color;
        this.id = id;
        this.esComodin = esComodin;
    }

    // Getters y Setters
    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    // Getter nuevo para el comodín
    public boolean isEsComodin() { return esComodin; }
    public void setEsComodin(boolean esComodin) { this.esComodin = esComodin; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        FichaDTO fichaDTO = (FichaDTO) obj;
        return Objects.equals(id, fichaDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "FichaDTO{" + "id='" + id + '\'' + ", numero=" + numero + ", color='" + color + '\'' + ", esComodin=" + esComodin + '}';
    }
}