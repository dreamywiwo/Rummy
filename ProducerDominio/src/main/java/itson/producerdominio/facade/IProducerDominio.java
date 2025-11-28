/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package itson.producerdominio.facade;

import itson.rummydtos.FichaDTO;
import itson.rummydtos.TableroDTO;
import java.util.List;

/**
 *
 * @author Dana Chavez
 */
public interface IProducerDominio {
    
    public abstract void actualizarTablero(TableroDTO snapshotTablero);
    
    public abstract void actualizarManoJugador(List<FichaDTO> snapshotMano);
    
    public abstract void actualizarSopa(int numFichasSopa);
    
    public abstract void actualizarTurno(String nuevoTurno);
    
    public abstract void mostrarError(String mensajeError);

    public void juegoTerminado(String jugadorId);

}
