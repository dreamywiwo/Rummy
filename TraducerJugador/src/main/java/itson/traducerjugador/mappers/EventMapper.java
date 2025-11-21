/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.traducerjugador.mappers;

import itson.rummyeventos.actualizaciones.ErrorEvent;
import itson.rummyeventos.actualizaciones.ManoActualizadaEvent;
import itson.rummyeventos.actualizaciones.SopaActualizadaEvent;
import itson.rummyeventos.actualizaciones.TableroActualizadoEvent;
import itson.rummyeventos.actualizaciones.TurnoTerminadoEvent;
import itson.rummyeventos.base.EventBase;
import itson.serializer.interfaces.ISerializer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 *
 * @author Dana Chavez
 */
public class EventMapper {

    private final ISerializer serializer;
//    private final IListener listener;

    private final Map<String, BiConsumer<String, ISerializer>> handlers = new HashMap<>();

    //agregar IListener al constructor
    public EventMapper(ISerializer serializer) {
        this.serializer = serializer;
//        this.listener = listener;

        // Registrar handlers
        register("tablero.actualizado", this::handleTableroActualizado);
        register("mano.actualizada", this::handleManoActualizada);
        register("sopa.actualizada", this::handleSopaActualizada);
        register("turno.terminado", this::handleTurnoTerminado);
        register("mensaje.error", this::handleError);
    }

    public void register(String eventType, BiConsumer<String, ISerializer> handler) {
        handlers.put(eventType, handler);
    }

    public void route(EventBase base, String rawPayload) {
        String et = base.getEventType();

        BiConsumer<String, ISerializer> handler = handlers.get(et);

        if (handler == null) {
            System.out.println("No existe handler para " + et);
            return;
        }

        handler.accept(rawPayload, serializer);
    }

    // Handlers 
    private void handleTableroActualizado(String rawPayload, ISerializer serializer) {
        try {
            TableroActualizadoEvent event = serializer.deserialize(rawPayload, TableroActualizadoEvent.class);
//            llamaremos algo de IListener
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleManoActualizada(String rawPayload, ISerializer serializer) {
        try {
            ManoActualizadaEvent event = serializer.deserialize(rawPayload, ManoActualizadaEvent.class);
//            llamaremos algo de IListener
        } catch (Exception e) {
            e.printStackTrace();
        }
    }    
    
    private void handleSopaActualizada(String rawPayload, ISerializer serializer) {
        try {
            SopaActualizadaEvent event = serializer.deserialize(rawPayload, SopaActualizadaEvent.class);
//            llamaremos algo de IListener
        } catch (Exception e) {
            e.printStackTrace();
        }
    } 

    private void handleTurnoTerminado(String rawPayload, ISerializer serializer) {
        try {
            TurnoTerminadoEvent event = serializer.deserialize(rawPayload, TurnoTerminadoEvent.class);
//            llamaremos algo de IListener
        } catch (Exception e) {
            e.printStackTrace();
        }
    }  
    
    private void handleError(String rawPayload, ISerializer serializer) {
        try {
            ErrorEvent event = serializer.deserialize(rawPayload, ErrorEvent.class);
//            llamaremos algo de IListener
        } catch (Exception e) {
            e.printStackTrace();
        }
    }  
}
