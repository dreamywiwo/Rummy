/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package itson.rummypresentacion.vista;

import java.util.List;

/**
 *
 * @author Dana Chavez
 */
public interface UI_Componente {
    public void mostrar();
    public void ocultar();
    public void actualizar();
    public void agregarComponente(UI_Componente componente);
    public void removerComponente(UI_Componente componente);
    public UI_Componente getComponente(int index);
    public List<UI_Componente> getComponentes();
}
