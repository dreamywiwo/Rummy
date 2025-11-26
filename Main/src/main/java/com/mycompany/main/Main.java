/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.main;

import ClaseDispatcher.ColaDispatcher;
import ClaseDispatcher.Dispatcher;
import ClaseDispatcher.SocketOut;
import claseReceptor.ColaReceptor;
import claseReceptor.Receptor;
import claseReceptor.SocketIN;
import com.mycompany.conexioninterfaces.IDispatcher;
import itson.producerjugador.emitters.JugarTurnoEmitter;
import itson.producerjugador.facade.IProducerJugador;
import itson.producerjugador.facade.ProducerJugador;
import itson.rummypresentacion.controlador.ControladorTurno;
import itson.rummypresentacion.modelo.Modelo;
import itson.rummypresentacion.vista.UI_TurnoJugador;
import itson.serializer.implementacion.JsonSerializer;
import itson.traducerjugador.facade.TraducerJugador;
import itson.traducerjugador.mappers.EventMapper;

/**
 * Raíz de Composición del Cliente (Jugador)
 *
 * @author Dana Chavez
 */
public class Main {

    public static void main(String[] args) {

        int miPuerto = 9002;
        String brokerIp = "192.168.100.4";
        int brokerPort = 9999;
        String miId = "Jugador2";

        JsonSerializer jsonSerializer = new JsonSerializer();

        SocketOut socketOut = new SocketOut();
        socketOut.start();
        ColaDispatcher colaDispatcher = new ColaDispatcher();
        colaDispatcher.attach(socketOut);
        IDispatcher dispatcher = new Dispatcher(colaDispatcher);
        JugarTurnoEmitter emitter = new JugarTurnoEmitter(jsonSerializer, dispatcher, brokerIp, brokerPort);
        IProducerJugador producer = new ProducerJugador(emitter, miId);
        Modelo modelo = new Modelo(producer);
        ControladorTurno controlador = new ControladorTurno(modelo, producer);

        EventMapper eventMapper = new EventMapper(jsonSerializer);
        eventMapper.setListener(modelo);

        TraducerJugador traducer = new TraducerJugador(jsonSerializer, eventMapper);

        Receptor receptor = new Receptor(traducer);
        ColaReceptor colaReceptor = new ColaReceptor();
        colaReceptor.attach(receptor);

        SocketIN socketIn = new SocketIN(miPuerto, colaReceptor);
        socketIn.start();

        System.out.println("Cliente iniciado en puerto: " + miPuerto);
        java.awt.EventQueue.invokeLater(() -> {
            UI_TurnoJugador ventana = new UI_TurnoJugador(controlador);
            modelo.suscribir(ventana);
            ventana.setVisible(true);
        });
    }
}
