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

        if (base == null || base.getEventType() == null) return;

        switch (base.getEventType()) {

            case RegistroJugadorEvent.EVENT_TYPE -> {
                RegistroJugadorEvent ev = serializer.deserialize(json, RegistroJugadorEvent.class);
                broker.suscribir(
                    RegistroJugadorEvent.TOPIC,
                    ev.getJugadorId(),
                    ev.getIp(),
                    ev.getPuerto()
                );
            }

            default -> {
                broker.publicar(json);
            }
        }
    }
}
