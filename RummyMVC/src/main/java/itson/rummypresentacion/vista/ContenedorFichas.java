/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.rummypresentacion.vista;

import itson.rummydtos.FichaDTO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ContenedorFichas implements Serializable {
    protected List<FichaDTO> fichas;
    protected String id;
    
    public ContenedorFichas(String id) {
        this.id = id;
        this.fichas = new ArrayList<>();
    }
    
    public void agregar(FichaDTO ficha) {
        fichas.add(ficha);
    }
    
    public void remover(FichaDTO ficha) {
        fichas.remove(ficha);
    }
    
    public boolean contiene(FichaDTO ficha) {
        return fichas.contains(ficha);
    }
    
    public List<FichaDTO> getFichas() {
        return new ArrayList<>(fichas);
    }
    
    public String getId() {
        return id;
    }
    
    public int size() {
        return fichas.size();
    }
}
