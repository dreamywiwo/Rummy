/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package itson.rummypresentacion.modelo;

import entidades.Ficha;
import java.util.List;

/**
 * IModelo es una segregacion del modelo, esta interfaz sirve 
 * para mandar a la vista lo que necesita para pintarse
 * 
 */
public interface IModelo {
    
    public String getJugadorActualId() throws Exception;

    public boolean esTurnoDelJugador(String idJugador);

    public int getTurnoNumero();

    public void registrarJugador(String idJugador);

    public List<String> getJugadores();
}
