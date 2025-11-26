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

    private final JugarTurnoEmitter jugarTurnoEmitter;
    private final String jugadorId;

    public ProducerJugador(JugarTurnoEmitter jugarTurnoEmitter, String jugadorId) {
        this.jugarTurnoEmitter = jugarTurnoEmitter;
        this.jugadorId = jugadorId;
    }

    @Override
    public void crearGrupo(List<FichaDTO> fichas) {
        jugarTurnoEmitter.emitirGrupoCreadoEvent(jugadorId, fichas);
    }

    @Override
    public void actualizarGrupo(String grupoId, List<FichaDTO> fichasNuevas) {
        jugarTurnoEmitter.emitirGrupoActualizadoEvent(grupoId, fichasNuevas);
    }

    @Override
    public void tomarFicha() {
        jugarTurnoEmitter.emitirFichaTomadaEvent(jugadorId);
    }

    @Override
    public void terminarTurno() {
        jugarTurnoEmitter.emitirTerminoTurnoEvent(jugadorId);
    }

}
