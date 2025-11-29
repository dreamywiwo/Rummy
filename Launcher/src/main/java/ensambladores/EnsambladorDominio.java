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
import java.util.Iterator;
import java.util.List;

public class EnsambladorDominio {

    // --- CLASE MOCK INTERNA PARA LA SOPA ---
    private class SopaDeterminista extends Sopa {

        private final List<Ficha> misFichas;

        public SopaDeterminista(List<Ficha> fichas) {
            super(fichas);
            this.misFichas = fichas;
        }

        @Override
        public Ficha tomarFicha() {
            if (misFichas.isEmpty()) {
                return null;
            }

            return misFichas.remove(0);
        }
    }

    public void iniciarJuego(String brokerIp, int brokerPort, int puertoEscuchaJuego, List<Jugador> jugadores) {

        JsonSerializer serializer = new JsonSerializer();
        SocketOut socketOut = new SocketOut();
        socketOut.start();
        ColaDispatcher colaDispatcher = new ColaDispatcher();
        colaDispatcher.attach(socketOut);
        IDispatcher dispatcher = new Dispatcher(colaDispatcher);

        InicializarJuegoEmitter registroEmitter = new InicializarJuegoEmitter(serializer, dispatcher, brokerIp, brokerPort);
        registroEmitter.emitirRegistroJugadorEvent("Dominio", brokerIp, puertoEscuchaJuego);

        // 1. GENERAR TODAS LAS FICHAS
        List<Ficha> todasLasFichas = generarFichasRummy();

        // 2. MOCK: PREPARAR MANO GANADORA PARA JUGADOR 1 (INDICE 0)
        List<Ficha> manoTrucada = new ArrayList<>();
        manoTrucada.add(buscarYQuitar(todasLasFichas, "rojo", 1));
        manoTrucada.add(buscarYQuitar(todasLasFichas, "rojo", 2));
        manoTrucada.add(buscarYQuitar(todasLasFichas, "rojo", 3)); 
        manoTrucada.add(buscarYQuitar(todasLasFichas, "azul", 10));
        manoTrucada.add(buscarYQuitar(todasLasFichas, "verde", 10)); 

        // 3. MOCK: PREPARAR LA SIGUIENTE FICHA EN LA SOPA
        Ficha fichaParaComer = buscarYQuitar(todasLasFichas, "amarillo", 10);
        todasLasFichas.add(0, fichaParaComer);

        // 4. USAR LA SOPA DETERMINISTA
        Sopa sopa = new SopaDeterminista(todasLasFichas);

        Tablero tablero = new Tablero();

        // 5. TURNOS
        Turno turno = new Turno(jugadores, 0);

        EstadoJuegoEmitter estadoEmitter = new EstadoJuegoEmitter(serializer, dispatcher, brokerIp, brokerPort);
        IProducerDominio producerDominio = new ProducerDominio(estadoEmitter);

        IDominio dominio = new Dominio(tablero, producerDominio, turno, sopa, todasLasFichas);

        // 6. ASIGNAR JUGADORES E INYECTAR MANO
        for (int i = 0; i < jugadores.size(); i++) {
            Jugador j = jugadores.get(i);

            if (i == 0) { 
                for (Ficha f : manoTrucada) {
                    j.getMano().agregarFicha(f);
                }
            } else {
                for (int k = 0; k < 7; k++) {
                    j.getMano().agregarFicha(sopa.tomarFicha());
                }
            }

            dominio.agregarJugador(j);
            System.out.println("Dominio: Jugador agregado - " + j.getNombre());
        }

        EventMapper mapperDominio = new EventMapper(serializer, dominio);
        TraducerDominio traducerDominio = new TraducerDominio(serializer, mapperDominio);
        ColaReceptor colaReceptor = new ColaReceptor();
        colaReceptor.attach(new Receptor(traducerDominio));
        SocketIN socketIN = new SocketIN(puertoEscuchaJuego, colaReceptor);
        socketIN.start();

        System.out.println("EnsambladorDominio: MODO PRUEBAS ACTIVADO en puerto " + puertoEscuchaJuego);
    }

    // UTILIDAD PARA BUSCAR Y EXTRAER FICHAS DEL MAZO GLOBAL
    private Ficha buscarYQuitar(List<Ficha> lista, String color, int numero) {
        Iterator<Ficha> it = lista.iterator();
        while (it.hasNext()) {
            Ficha f = it.next();
            if (f.getColor().equalsIgnoreCase(color) && f.getNumero() == numero) {
                it.remove(); 
                return f;   
            }
        }
        return null; 
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
