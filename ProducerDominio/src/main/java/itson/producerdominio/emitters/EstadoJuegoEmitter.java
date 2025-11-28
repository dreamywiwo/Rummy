/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.producerdominio.emitters;

import com.mycompany.conexioninterfaces.IDispatcher;
import itson.rummydtos.FichaDTO;
import itson.rummydtos.TableroDTO;
import itson.rummyeventos.actualizaciones.ErrorEvent;
import itson.rummyeventos.actualizaciones.JuegoTerminadoEvent;
import itson.rummyeventos.actualizaciones.ManoActualizadaEvent;
import itson.rummyeventos.actualizaciones.SopaActualizadaEvent;
import itson.rummyeventos.actualizaciones.TableroActualizadoEvent;
import itson.rummyeventos.actualizaciones.TurnoTerminadoEvent;
import itson.serializer.implementacion.JsonSerializer;
import java.util.List;

/**
 *
 * @author Dana Chavez
 */
public class EstadoJuegoEmitter {

    JsonSerializer jsonSerializer;
    private final IDispatcher dispatcher;
    private final String brokerIp;
    private final int brokerPort;

    public EstadoJuegoEmitter(JsonSerializer jsonSerializer, IDispatcher dispatcher, String brokerIp, int brokerPort) {
        this.jsonSerializer = jsonSerializer;
        this.dispatcher = dispatcher;
        this.brokerIp = brokerIp;
        this.brokerPort = brokerPort;
    }

    public void emitirTableroActualizadoEvent(TableroDTO snapshotTablero) {
        TableroActualizadoEvent event = new TableroActualizadoEvent(snapshotTablero);
        String json = jsonSerializer.serialize(event);
        dispatcher.enviar(json, brokerPort, brokerIp);
    }

    public void emitirManoActualizadaEvent(List<FichaDTO> snapshotMano) {
        ManoActualizadaEvent event = new ManoActualizadaEvent(snapshotMano);
        String json = jsonSerializer.serialize(event);
        dispatcher.enviar(json, brokerPort, brokerIp);
    }

    public void emitirSopaActualizadaEvent(int numFichasSopa) {
        SopaActualizadaEvent event = new SopaActualizadaEvent(numFichasSopa);
        String json = jsonSerializer.serialize(event);
        dispatcher.enviar(json, brokerPort, brokerIp);
    }

    public void emitirTurnoTerminadoEvent(String nuevoTurno) {
        TurnoTerminadoEvent event = new TurnoTerminadoEvent(nuevoTurno);
        String json = jsonSerializer.serialize(event);
        dispatcher.enviar(json, brokerPort, brokerIp);
    }

    public void emitirErrorEvent(String mensajeError) {
        ErrorEvent event = new ErrorEvent(mensajeError);
        String json = jsonSerializer.serialize(event);
        dispatcher.enviar(json, brokerPort, brokerIp);
    }

    public void emitirJuegoTerminadoEvent(String jugadorId) {
        JuegoTerminadoEvent event = new JuegoTerminadoEvent(jugadorId);
        String json = jsonSerializer.serialize(event);
        dispatcher.enviar(json, brokerPort, brokerIp);
    }
}
