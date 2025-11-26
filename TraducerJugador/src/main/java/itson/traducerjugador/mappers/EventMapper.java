/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.traducerjugador.mappers;

import itson.rummydtos.TableroDTO;
import itson.rummyeventos.actualizaciones.ErrorEvent;
import itson.rummyeventos.actualizaciones.ManoActualizadaEvent;
import itson.rummyeventos.actualizaciones.SopaActualizadaEvent;
import itson.rummyeventos.actualizaciones.TableroActualizadoEvent;
import itson.rummyeventos.actualizaciones.TurnoTerminadoEvent;
import itson.rummyeventos.base.EventBase;
import itson.rummypresentacion.modelo.IListener;
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
    private IListener listener;

    private final Map<String, BiConsumer<String, ISerializer>> handlers = new HashMap<>();

    public EventMapper(ISerializer serializer) {
        this.serializer = serializer;

        register("tablero.actualizado", this::handleTableroActualizado);
        register("mano.actualizada", this::handleManoActualizada);
        register("sopa.actualizada", this::handleSopaActualizada);
        register("termino.turno", this::handleTurnoTerminado);
        register("mensaje.error", this::handleError);
    }

    public void setListener(IListener listener) {
        this.listener = listener;
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

    private void handleTableroActualizado(String rawPayload, ISerializer serializer) {
        try {
            TableroActualizadoEvent event = serializer.deserialize(rawPayload, TableroActualizadoEvent.class);
            if (listener != null) {
                listener.recibirTablero(event.getTableroSnapshot());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleManoActualizada(String rawPayload, ISerializer serializer) {
        try {
            ManoActualizadaEvent event = serializer.deserialize(rawPayload, ManoActualizadaEvent.class);
            if (listener != null) {
                listener.recibirMano(event.getManoSnapshot());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleSopaActualizada(String rawPayload, ISerializer serializer) {
        try {
            SopaActualizadaEvent event = serializer.deserialize(rawPayload, SopaActualizadaEvent.class);
            if (listener != null) {
                listener.recibirSopa(event.getNumFichasRestantes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleTurnoTerminado(String rawPayload, ISerializer serializer) {
        try {
            TurnoTerminadoEvent event = serializer.deserialize(rawPayload, TurnoTerminadoEvent.class);
            if (listener != null) {
                listener.terminoTurno(event);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleError(String rawPayload, ISerializer serializer) {
        try {
            ErrorEvent event = serializer.deserialize(rawPayload, ErrorEvent.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
