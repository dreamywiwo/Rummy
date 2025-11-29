/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ensambladores;

import ClaseDispatcher.ColaDispatcher;
import ClaseDispatcher.Dispatcher;
import ClaseDispatcher.SocketOut;
import claseReceptor.ColaReceptor;
import claseReceptor.Receptor;
import claseReceptor.SocketIN;
import com.mycompany.conexioninterfaces.IDispatcher;
import itson.dominiorummy.entidades.Ficha;
import itson.dominiorummy.entidades.Jugador;
import itson.dominiorummy.entidades.Sopa;
import itson.dominiorummy.entidades.Tablero;
import itson.dominiorummy.entidades.Turno;
import itson.dominiorummy.facade.Dominio;
import itson.dominiorummy.facade.IDominio;
import itson.producerdominio.emitters.EstadoJuegoEmitter;
import itson.producerdominio.facade.IProducerDominio;
import itson.producerdominio.facade.ProducerDominio;
import itson.producerjugador.emitters.InicializarJuegoEmitter;
import itson.serializer.implementacion.JsonSerializer;
import itson.traducerdominio.facade.TraducerDominio;
import itson.traducerdominio.mappers.EventMapper;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jrasc
 */
public class EnsambladorDominio {

    public void iniciarJuego(String brokerIp, int brokerPort, int puertoEscuchaJuego, List<Jugador> jugadores) {

        JsonSerializer serializer = new JsonSerializer();
        SocketOut socketOut = new SocketOut();
        socketOut.start();
        ColaDispatcher colaDispatcher = new ColaDispatcher();
        colaDispatcher.attach(socketOut);
        IDispatcher dispatcher = new Dispatcher(colaDispatcher);

        InicializarJuegoEmitter registroEmitter = new InicializarJuegoEmitter(serializer, dispatcher, brokerIp, brokerPort);
        registroEmitter.emitirRegistroJugadorEvent("Dominio", brokerIp, puertoEscuchaJuego);

        List<Ficha> fichasJuego = generarFichasRummy();
        Sopa sopa = new Sopa(fichasJuego);
        Tablero tablero = new Tablero();
        Turno turno = new Turno(jugadores, 0);

        EstadoJuegoEmitter estadoEmitter = new EstadoJuegoEmitter(serializer, dispatcher, brokerIp, brokerPort);
        IProducerDominio producerDominio = new ProducerDominio(estadoEmitter);

        IDominio dominio = new Dominio(tablero, producerDominio, turno, sopa, fichasJuego);

        for (Jugador j : jugadores) {
            dominio.agregarJugador(j);
            System.out.println("Dominio: Jugador agregado - " + j.getNombre());
        }

        EventMapper mapperDominio = new EventMapper(serializer, dominio);
        TraducerDominio traducerDominio = new TraducerDominio(serializer, mapperDominio);

        ColaReceptor colaReceptor = new ColaReceptor();
        colaReceptor.attach(new Receptor(traducerDominio));

        SocketIN socketIN = new SocketIN(puertoEscuchaJuego, colaReceptor);
        socketIN.start();

        System.out.println("EnsambladorDominio: Lógica de juego lista en puerto " + puertoEscuchaJuego);
    }

    private List<Ficha> generarFichasRummy() {
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
