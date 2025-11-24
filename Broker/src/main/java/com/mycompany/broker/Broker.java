/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.broker;

import interfaces.IDispatcher;
import interfaces.IReceptorComponente;
import itson.directorio.implementacion.ConnectionEndpoint;
import itson.directorio.implementacion.Directorio;
import itson.rummyeventos.base.EventBase;
import itson.serializer.interfaces.ISerializer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author jrasc
 */
public class Broker implements IReceptorComponente {

    private final Directorio directorio;
    private final IDispatcher dispatcher;
    private final ISerializer serializer;

    private final Map<String, List<String>> rutas = new HashMap<>();

    public Broker(Directorio directorio, IDispatcher dispatcher, ISerializer serializer, IReceptorComponente receptor) {
        this.directorio = directorio;
        this.dispatcher = dispatcher;
        this.serializer = serializer;
    }

    @Override
    public void recibirMensaje(String json) {
        procesarMensaje(json);
    }

    public void procesarMensaje(String json) {
        EventBase base = serializer.deserialize(json, EventBase.class);

        if (base == null || base.getEventType() == null) {
            return;
        }

        String eventType = base.getEventType();

        List<String> listaClientes = rutas.getOrDefault(eventType, new ArrayList<>());

        for (String subID : listaClientes) {
            Optional<ConnectionEndpoint> endpointOpt = directorio.getEndpoint(subID);
            if (endpointOpt.isPresent()) {
                ConnectionEndpoint endpoint = endpointOpt.get();
                String jsonEnviar = serializer.serialize(base);
                dispatcher.enviar(json, endpoint.getPort(), endpoint.getIp());
            }
        }

    }

    public void subscribe(String eventType, String clienteID, String ip, int port) {
        directorio.registerClient(clienteID, ip, port);
        rutas.computeIfAbsent(eventType, k -> new ArrayList<>()).add(clienteID);
        System.out.println("Cliente " + clienteID + " suscrito a " + eventType);
    }

    public void unsuscribe(String eventType, String clienteID) {
        if (rutas.containsKey(eventType)) {
            rutas.get(eventType).remove(clienteID);
            if (rutas.get(eventType).isEmpty()) {
                rutas.remove(eventType);
            }
        }
        boolean estaSuscrito = false;
        for (List<String> lista : rutas.values()) {
            if (lista.contains(clienteID)) {
                estaSuscrito = true;
                break;
            }
        }

        if (!estaSuscrito) {
            directorio.removeClient(clienteID);
        }
    }
}
