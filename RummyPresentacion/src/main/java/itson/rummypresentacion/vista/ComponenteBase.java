/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.rummypresentacion.vista;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Dana Chavez
 */
public abstract class ComponenteBase implements UI_Componente {
    protected String id;
    protected List<UI_Componente> componentes;
    protected boolean visible;
    
    public ComponenteBase(String id) {
        this.id = id;
        this.componentes = new ArrayList<>();
        this.visible = true;
    }
    
    @Override
    public void mostrar() {
        this.visible = true;
        for (UI_Componente componente : componentes) {
            componente.mostrar();
        }
    }
    
    @Override
    public void ocultar() {
        this.visible = false;
        for (UI_Componente componente : componentes) {
            componente.ocultar();
        }
    }
    
    @Override
    public void actualizar() {
        for (UI_Componente componente : componentes) {
            componente.actualizar();
        }
    }
    
    @Override
    public void agregarComponente(UI_Componente componente) {
        componentes.add(componente);
    }
    
    @Override
    public void removerComponente(UI_Componente componente) {
        componentes.remove(componente);
    }
    
    @Override
    public UI_Componente getComponente(int index) {
        return componentes.get(index);
    }
    
    @Override
    public List<UI_Componente> getComponentes() {
        return new ArrayList<>(componentes);
    }
}
