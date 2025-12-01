package com.mycompany.broker;

import com.mycompany.conexioninterfaces.IReceptorComponente;
import interfaces.IBroker;
import itson.rummyeventos.base.EventBase;
import itson.rummyeventos.sistema.RegistroJugadorEvent;
import itson.serializer.interfaces.ISerializer;

public class ProcesadorEventosBroker implements IReceptorComponente {

    private final IBroker broker;
    private final ISerializer serializer;

    public ProcesadorEventosBroker(IBroker broker, ISerializer serializer) {
        this.broker = broker;
        this.serializer = serializer;
    }

    @Override
    public void recibirMensaje(String json) {

        EventBase base = serializer.deserialize(json, EventBase.class);

        if (base == null || base.getEventType() == null) {
            return;
        }

        switch (base.getEventType()) {

            case RegistroJugadorEvent.EVENT_TYPE -> {
                RegistroJugadorEvent ev = serializer.deserialize(json, RegistroJugadorEvent.class);

                String jugadorId = ev.getJugadorId();
                String ip = ev.getIp();
                int puerto = ev.getPuerto();
                broker.suscribir(
                        RegistroJugadorEvent.TOPIC, // "sistema.registro"
                        jugadorId,
                        ip,
                        puerto
                );
                if ("Dominio".equals(jugadorId)) {
                    broker.suscribir(
                            "acciones.jugador",
                            jugadorId,
                            ip,
                            puerto
                    );
                } else {
                    broker.suscribir(
                            "actualizaciones.estado",
                            jugadorId,
                            ip,
                            puerto
                    );
                }
            }

            default -> {
                broker.publicar(json);
            }
        }
    }
}
