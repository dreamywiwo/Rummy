/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package itson.producerdominio.facade;

import itson.producerdominio.emitters.EstadoJuegoEmitter;
import itson.rummydtos.FichaDTO;
import itson.rummydtos.TableroDTO;
import java.util.List;

/**
 *
 * @author Dana Chavez
 */
public class ProducerDominio implements IProducerDominio {
    
    EstadoJuegoEmitter estadoJuegoEmitter;

    public ProducerDominio(EstadoJuegoEmitter estadoJuegoEmitter) {
        this.estadoJuegoEmitter = estadoJuegoEmitter;
    }

    @Override
    public void actualizarTablero(TableroDTO snapshotTablero) {
        estadoJuegoEmitter.emitirTableroActualizadoEvent(snapshotTablero);
    }

    @Override
    public void actualizarManoJugador(List<FichaDTO> snapshotMano) {
        estadoJuegoEmitter.emitirManoActualizadaEvent(snapshotMano);
    }

    @Override
    public void actualizarSopa(int numFichasSopa) {
        estadoJuegoEmitter.emitirSopaActualizadaEvent(numFichasSopa);
    }

    @Override
    public void actualizarTurno(String nuevoTurno) {
        estadoJuegoEmitter.emitirTurnoTerminadoEvent(nuevoTurno);
    }

    @Override
    public void mostrarError(String mensajeError) {
        estadoJuegoEmitter.emitirErrorEvent(mensajeError);
    }

    @Override
    public void juegoTerminado(String jugadorId) {
        estadoJuegoEmitter.emitirJuegoTerminadoEvent(jugadorId);
    }

}
