/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.producerjugador.facade;

import itson.producerjugador.emitters.JugarTurnoEmitter;
import itson.rummydtos.FichaDTO;
import java.util.List;

/**
 *
 * @author Dana Chavez
 */
public class ProducerJugador implements IProducerJugador {
  
    JugarTurnoEmitter jugarTurnoEmitter;

    public ProducerJugador(JugarTurnoEmitter jugarTurnoEmitter) {
        this.jugarTurnoEmitter = jugarTurnoEmitter;
    }

    @Override
    public void crearGrupo(List<FichaDTO> fichas) {
        jugarTurnoEmitter.emitirGrupoCreadoEvent(fichas);
    }

    @Override
    public void actualizarGrupo(String grupoId, List<FichaDTO> fichasNuevas) {
        jugarTurnoEmitter.emitirGrupoActualizadoEvent(grupoId, fichasNuevas);
    }

    @Override
    public void tomarFicha() {
        jugarTurnoEmitter.emitirFichaTomadaEvent();
    }

    @Override
    public void terminarTurno() {
        jugarTurnoEmitter.emitirTerminoTurnoEvent();
    }
    
}
