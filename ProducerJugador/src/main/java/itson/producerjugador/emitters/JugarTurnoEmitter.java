/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.producerjugador.emitters;

import itson.rummydtos.FichaDTO;
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
//    private final IDispatcher connectionOutbound;

    public JugarTurnoEmitter(JsonSerializer jsonSerializer) {
        this.jsonSerializer = jsonSerializer;
    }   
    
    public void emitirGrupoCreadoEvent(List<FichaDTO> fichas) {
        GrupoCreadoEvent event = new GrupoCreadoEvent(fichas);
        String json = jsonSerializer.serialize(event);
        System.out.println("hasta aqui llego el grupo creado" + fichas);
        System.out.println(json);
//        connectionOutbound.send(json);
    }

    public void emitirGrupoActualizadoEvent(String grupoId, List<FichaDTO> fichasNuevas) {
        GrupoActualizadoEvent event = new GrupoActualizadoEvent(grupoId, fichasNuevas);
        String json = jsonSerializer.serialize(event);
//      connectionOutbound.send(json);
    }

    public void emitirFichaTomadaEvent() {
        FichaTomadaEvent event = new FichaTomadaEvent();
        String json = jsonSerializer.serialize(event);
//      connectionOutbound.send(json);
    }

    public void emitirTerminoTurnoEvent() {
        TerminoTurnoEvent event = new TerminoTurnoEvent();
        String json = jsonSerializer.serialize(event);
//      connectionOutbound.send(json);
    }
    
}
