/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.rummypresentacion.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Dana Chavez
 */
public class Modelo implements IModelo, ISubject{
    
    private List<IObserver> observers = new ArrayList<>();

    @Override
    public void suscribir(IObserver observer) {
        observers.add(observer);
    }

    @Override
    public void notificar(IObserver observer) {
        observer.update(this);
    }
    
    public void notificarObservers() {
        for (IObserver observer : observers) {
            observer.update(this);
        }
    }
    
}
