/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.mycompany.broker;

import ClaseDispatcher.ColaDispatcher;
import ClaseDispatcher.Dispatcher;
import ClaseDispatcher.SocketOut;
import claseReceptor.ColaReceptor;
import claseReceptor.Receptor;
import claseReceptor.SocketIN;
import interfaces.IDispatcher;
import itson.directorio.implementacion.Directorio;
import itson.serializer.implementacion.JsonSerializer;
import itson.serializer.interfaces.ISerializer;

/**
 *
 * @author jrasc
 */
public class NewMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int puertoBroker = 9999;

        System.out.println("iniciando broker");

        ISerializer serializer = new JsonSerializer();
        Directorio directorio = new Directorio();

        directorio.registerClient("estado", "192.168.100.95", 9001);

        SocketOut socketOut = new SocketOut();
        socketOut.start();
        ColaDispatcher cola = new ColaDispatcher();
        cola.attach(socketOut);
        IDispatcher dispatcher = new Dispatcher(cola);

        Broker brokerLogic = new Broker(directorio, dispatcher, serializer, null);

        Receptor receptor = new Receptor(brokerLogic);
        ColaReceptor colaReceptor = new ColaReceptor();
        colaReceptor.attach(receptor);

        SocketIN socketIN = new SocketIN(puertoBroker, colaReceptor);
        socketIN.start();
        System.out.println("broker escuchando por el puerto " + puertoBroker);
    }

}
