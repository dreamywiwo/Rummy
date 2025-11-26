/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package main.server;

import ClaseDispatcher.ColaDispatcher;
import ClaseDispatcher.Dispatcher;
import ClaseDispatcher.SocketOut;
import claseReceptor.ColaReceptor;
import claseReceptor.Receptor;
import claseReceptor.SocketIN;
import com.mycompany.broker.Broker;
import com.mycompany.broker.SubscriptionRegistry;
import com.mycompany.conexioninterfaces.IDispatcher;
import itson.directorio.implementacion.Directorio;
import itson.dominiorummy.entidades.Ficha;
import itson.dominiorummy.entidades.Sopa;
import itson.dominiorummy.entidades.Turno;
import itson.dominiorummy.facade.Dominio;
import itson.dominiorummy.facade.IDominio;
import itson.producerdominio.emitters.EstadoJuegoEmitter;
import itson.producerdominio.facade.IProducerDominio;
import itson.producerdominio.facade.ProducerDominio;
import itson.dominiorummy.entidades.Tablero;
import itson.serializer.implementacion.JsonSerializer;
import itson.traducerdominio.facade.TraducerDominio;
import itson.traducerdominio.mappers.EventMapper;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author jrasc
 */
public class broker {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int puertoBroker = 9999;
        String brokerIp = "192.168.100.4";
        String ipJ1 = "192.168.100.95";
        String ipJ2 = "192.168.100.4";

        JsonSerializer serializer = new JsonSerializer();
        Directorio directorio = new Directorio();
        SubscriptionRegistry registry = new SubscriptionRegistry();
        directorio.registerClient("Jugador1", ipJ1, 9001);
        registry.addSuscriptor("actualizaciones.estado", "jugador1");
        directorio.registerClient("Jugador2", ipJ2, 9002);
        registry.addSuscriptor("actualizaciones.estado", "jugador2");

        SocketOut socketOut = new SocketOut();
        socketOut.start();
        ColaDispatcher colaDispatcher = new ColaDispatcher();
        colaDispatcher.attach(socketOut);
        IDispatcher dispatcher = new Dispatcher(colaDispatcher);

        List<Ficha> fichasJuego = generarFichasRummy();
        Sopa sopa = new Sopa(fichasJuego);
        Tablero tablero = new Tablero();
        Turno turno = new Turno(new ArrayList<>(), 0);

        EstadoJuegoEmitter estadoEmitter = new EstadoJuegoEmitter(serializer, dispatcher, brokerIp, puertoBroker);
        IProducerDominio producerDominio = new ProducerDominio(estadoEmitter);

        IDominio dominio = new Dominio(tablero, producerDominio, turno, sopa, fichasJuego);

        EventMapper mapperDominio = new EventMapper(serializer, dominio);
        TraducerDominio traducerDominio = new TraducerDominio(serializer, mapperDominio);

        Broker brokerLogic = new Broker(directorio, dispatcher, serializer, registry);

        ColaReceptor colaReceptor = new ColaReceptor();

        colaReceptor.attach(new Receptor(brokerLogic));

        colaReceptor.attach(new Receptor(traducerDominio));

        SocketIN socketIN = new SocketIN(puertoBroker, colaReceptor);
        socketIN.start();
    }

    private static List<Ficha> generarFichasRummy() {
        List<Ficha> fichas = new ArrayList<>();
        String[] colores = {"rojo", "azul", "verde", "amarillo"};

        for (int i = 0; i < 2; i++) {
            for (String color : colores) {
                for (int num = 1; num <= 13; num++) {
                    String id = "f_" + color.charAt(0) + num + "_" + i;
                    fichas.add(new Ficha(id, num, color, false));
                }
            }
        }
        fichas.add(new Ficha("joker_1", 0, "comodin", true));
        fichas.add(new Ficha("joker_2", 0, "comodin", true));
        return fichas;
    }

}
