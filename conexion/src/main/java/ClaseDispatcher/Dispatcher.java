/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ClaseDispatcher;

import interfaces.IDispatcher;

/**
 *
 * @author jrasc
 */
public class Dispatcher implements IDispatcher {
    
    private final ColaDispatcher cola;
    
    public Dispatcher(ColaDispatcher cola) {
        this.cola = cola;
    }
    
    @Override
    public void enviar(String json, int port, String ip) {
        cola.encolar(json, port, ip);
    }
    
}
