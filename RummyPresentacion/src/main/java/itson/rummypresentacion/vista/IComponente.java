/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package itson.rummypresentacion.vista;

import itson.rummypresentacion.modelo.IModelo;
import java.util.List;

public interface IComponente {
    public void mostrar();
    public void ocultar();
    public void actualizar(IModelo modelo); 
    public void agregarComponente(IComponente componente);
    public void removerComponente(IComponente componente);
    public IComponente getComponente(int index);
    public List<IComponente> getComponentes();
    public String getId();
}
