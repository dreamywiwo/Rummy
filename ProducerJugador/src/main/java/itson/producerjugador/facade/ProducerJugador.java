/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.producerjugador.facade;

import itson.producerjugador.emitters.InicializarJuegoEmitter;
import itson.producerjugador.emitters.JugarTurnoEmitter;
import itson.rummydtos.FichaDTO;
import java.util.List;

/**
 *
 * @author Dana Chavez
 */
public class ProducerJugador implements IProducerJugador {

    private final JugarTurnoEmitter jugarTurnoEmitter;
    private final InicializarJuegoEmitter inicializarJuegoEmitter;

    public ProducerJugador(JugarTurnoEmitter jugarTurnoEmitter, InicializarJuegoEmitter inicializarJuegoEmitter, String jugadorId) {
        this.jugarTurnoEmitter = jugarTurnoEmitter;
        this.inicializarJuegoEmitter = inicializarJuegoEmitter;
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

    @Override
    public void registrarJugador(String miId, String ipCliente, int miPuertoDeEscucha) {
        inicializarJuegoEmitter.emitirRegistroJugadorEvent(miId, ipCliente, miPuertoDeEscucha);
    }

    @Override
    public void devolverFicha(String grupoId, String fichaId) {
        jugarTurnoEmitter.emitirFichaDevueltaEvent(grupoId, fichaId);
    }

}
