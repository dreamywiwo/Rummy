/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.producerjugador.emitters;

import com.mycompany.conexioninterfaces.IDispatcher;
import itson.rummydtos.FichaDTO;
import itson.rummyeventos.acciones.FichaDevueltaEvent;
import itson.rummyeventos.acciones.FichaTomadaEvent;
import itson.rummyeventos.acciones.GrupoActualizadoEvent;
import itson.rummyeventos.acciones.GrupoCreadoEvent;
import itson.rummyeventos.acciones.TerminoTurnoEvent;
import itson.serializer.implementacion.JsonSerializer;
import java.util.List;

/**
 *
 * @author Dana Chavez
 */
public class JugarTurnoEmitter {

    private final JsonSerializer jsonSerializer;
    private final IDispatcher dispatcher;
    private final String brokerIp;
    private final int brokerPort;

    public JugarTurnoEmitter(JsonSerializer jsonSerializer, IDispatcher dispatcher, String brokerIp, int brokerPort) {
        this.jsonSerializer = jsonSerializer;
        this.dispatcher = dispatcher;
        this.brokerIp = brokerIp;
        this.brokerPort = brokerPort;
    }

    public void emitirGrupoCreadoEvent(List<FichaDTO> fichas) {
        GrupoCreadoEvent event = new GrupoCreadoEvent(fichas);
        String json = jsonSerializer.serialize(event);
        System.out.println("hasta aqui llego el grupo creado" + fichas);
        dispatcher.enviar(json, brokerPort, brokerIp);
    }

    public void emitirGrupoActualizadoEvent(String grupoId, List<FichaDTO> fichasNuevas) {
        GrupoActualizadoEvent event = new GrupoActualizadoEvent(grupoId, fichasNuevas);
        String json = jsonSerializer.serialize(event);
        dispatcher.enviar(json, brokerPort, brokerIp);
    }

    public void emitirFichaTomadaEvent() {
        FichaTomadaEvent event = new FichaTomadaEvent();
        String json = jsonSerializer.serialize(event);
        dispatcher.enviar(json, brokerPort, brokerIp);
    }

    public void emitirTerminoTurnoEvent() {
        TerminoTurnoEvent event = new TerminoTurnoEvent();
        String json = jsonSerializer.serialize(event);
        dispatcher.enviar(json, brokerPort, brokerIp);
    }

    public void emitirFichaDevueltaEvent(String grupoId, String fichaId) {
        FichaDevueltaEvent event = new FichaDevueltaEvent(grupoId, fichaId);
        String json = jsonSerializer.serialize(event);
        dispatcher.enviar(json, brokerPort, brokerIp);
    }

}
