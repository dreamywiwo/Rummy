/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.producerdominio.emitters;

import itson.rummydtos.FichaDTO;
import itson.rummydtos.TableroDTO;
import itson.rummyeventos.actualizaciones.ErrorEvent;
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
    //    private final IDispatcher connectionOutbound;


    public EstadoJuegoEmitter(JsonSerializer jsonSerializer) {
        this.jsonSerializer = jsonSerializer;
    }

    public void emitirTableroActualizadoEvent(TableroDTO snapshotTablero) {
        TableroActualizadoEvent event = new TableroActualizadoEvent(snapshotTablero);
        String json = jsonSerializer.serialize(event);  
//        connectionOutbound.send(json);
    }

    public void emitirManoActualizadaEvent(List<FichaDTO> snapshotMano) {
        ManoActualizadaEvent event = new ManoActualizadaEvent(snapshotMano);
        String json = jsonSerializer.serialize(event);
//        connectionOutbound.send(json);        
    }

    public void emitirSopaActualizadaEvent(int numFichasSopa) {
        SopaActualizadaEvent event = new SopaActualizadaEvent(numFichasSopa);
        String json = jsonSerializer.serialize(event);
//        connectionOutbound.send(json);
    }

    public void emitirTurnoTerminadoEvent(String nuevoTurno) {
        TurnoTerminadoEvent event = new TurnoTerminadoEvent(nuevoTurno);
        String json = jsonSerializer.serialize(event);
//        connectionOutbound.send(json);
    }

    public void emitirErrorEvent(String mensajeError) {
        ErrorEvent event = new ErrorEvent(mensajeError);
        String json = jsonSerializer.serialize(event);
//        connectionOutbound.send(json);
    }

}
