/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package claseReceptor;

import interfaces.IReceptorComponente;
import interfaces.IReceptorObserver;

/**
 *
 * @author jrasc
 */
public class Receptor<T> implements IReceptorObserver {
    
    private final IReceptorComponente componente;
    
    public Receptor(IReceptorComponente componente) {
        this.componente = componente;
    }
    
    @Override
    public void update(String json, int port, String ip) {
        componente.recibirMensaje(json);
    }
    
}
