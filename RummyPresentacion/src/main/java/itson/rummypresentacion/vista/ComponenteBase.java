/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.rummypresentacion.vista;

import itson.rummypresentacion.modelo.IModelo;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

/**
 *
 * @author Dana Chavez
 */
public abstract class ComponenteBase extends JPanel implements IComponente {
     protected String id;
    protected List<IComponente> componentes;
    protected boolean visible;

    public ComponenteBase(String id) {
        this.id = id;
        this.componentes = new ArrayList<>();
        this.visible = true;
    }

    @Override
    public void mostrar() {
        this.visible = true;
        this.setVisible(true);
        for (IComponente componente : componentes) {
            componente.mostrar();
        }
    }

    @Override
    public void ocultar() {
        this.visible = false;
        this.setVisible(false);
        for (IComponente componente : componentes) {
            componente.ocultar();
        }
    }

    @Override
    public void actualizar(IModelo modelo) {
        System.out.println("DEBUG - ComponenteBase.actualizar: " + getId() + " con " + componentes.size() + " hijos");
        
        // Propagar la actualización a todos los componentes hijos
        for (IComponente componente : componentes) {
            System.out.println("DEBUG - Propagando a hijo: " + componente.getId());
            componente.actualizar(modelo);
        }
    }
    
    @Override
    public void agregarComponente(IComponente componente) {
        componentes.add(componente);
        System.out.println("DEBUG - Componente agregado: " + componente.getId() + " a " + getId());
        System.out.println("DEBUG - Total componentes en " + getId() + ": " + componentes.size());
    }

    @Override
    public void removerComponente(IComponente componente) {
        componentes.remove(componente);
    }

    @Override
    public IComponente getComponente(int index) {
        return componentes.get(index);
    }

    @Override
    public List<IComponente> getComponentes() {
        return new ArrayList<>(componentes);
    }

    @Override
    public String getId() {
        return id;
    }
}
