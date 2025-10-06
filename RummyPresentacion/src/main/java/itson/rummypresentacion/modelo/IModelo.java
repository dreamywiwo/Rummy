/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package itson.rummypresentacion.modelo;

import itson.rummypresentacion.DTOs.FichaDTO;
import itson.rummypresentacion.DTOs.GrupoDTO;
import java.util.List;

/**
 * IModelo es una segregacion del modelo, esta interfaz sirve 
 * para mandar a la vista lo que necesita para pintarse
 * 
 */

public interface IModelo extends ISubject{
    
    // Informacion
    String getJugadorActivoId();
    List<FichaDTO> getManoJugadorActual();
    List<GrupoDTO> getGruposTablero();
    int getTurnoActual();
    boolean esTurnoDe(String jugadorId);
    
    // Estado
    List<String> getFichasSeleccionadas();
    String getUltimoEvento();
    String getUltimoError();
    long getTimestamp();
    
    // TODO: A futuro (algo que se podria llevar)
    boolean isJuegoTerminado();
    String getGanador();
    
    void registrarJugador(String idJugador);
    List<String> getJugadores();
    
    List<FichaDTO> getManoDe(String jugadorId);
}