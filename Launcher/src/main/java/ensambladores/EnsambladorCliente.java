package ensambladores;

import ClaseDispatcher.ColaDispatcher;
import ClaseDispatcher.Dispatcher;
import ClaseDispatcher.SocketOut;
import claseReceptor.ColaReceptor;
import claseReceptor.Receptor;
import claseReceptor.SocketIN;
import com.mycompany.conexioninterfaces.IDispatcher;
import itson.producerjugador.emitters.InicializarJuegoEmitter;
import itson.producerjugador.emitters.JugarTurnoEmitter;
import itson.producerjugador.facade.IProducerJugador;
import itson.producerjugador.facade.ProducerJugador;
import itson.rummydtos.JugadorDTO; // <--- IMPORTANTE
import itson.rummypresentacion.controlador.ControladorTurno;
import itson.rummypresentacion.modelo.Modelo;
import itson.rummypresentacion.vista.UI_TurnoJugador;
import itson.serializer.implementacion.JsonSerializer;
import itson.traducerjugador.facade.TraducerJugador;
import itson.traducerjugador.mappers.EventMapper;

public class EnsambladorCliente {
    
    public UI_TurnoJugador construirJugador(String ipBroker, int puertoBroker, String miIp, int miPuerto, String miId) {
        
        JsonSerializer jsonSerializer = new JsonSerializer();
        
        SocketOut socketOut = new SocketOut();
        socketOut.start();
        ColaDispatcher colaDispatcher = new ColaDispatcher();
        colaDispatcher.attach(socketOut);
        IDispatcher dispatcher = new Dispatcher(colaDispatcher);
        
        JugarTurnoEmitter emitterJugar = new JugarTurnoEmitter(jsonSerializer, dispatcher, ipBroker, puertoBroker);
        InicializarJuegoEmitter emitterInit = new InicializarJuegoEmitter(jsonSerializer, dispatcher, ipBroker, puertoBroker);
        
        IProducerJugador producer = new ProducerJugador(emitterJugar, emitterInit, miId);
        
        Modelo modelo = new Modelo(producer);
        modelo.setJugadorLocal(miId);
        ControladorTurno controlador = new ControladorTurno(modelo, producer, miId);
        

        String rutaAvatar = (miPuerto % 2 == 0) ? "/imageFish.png" : "/imageBun.png";
        String nombreJugador = "Jugador " + (miPuerto % 100); 

        JugadorDTO miPerfil = new JugadorDTO(miId, nombreJugador, rutaAvatar);

        UI_TurnoJugador ventana = new UI_TurnoJugador(controlador, miPerfil);
        
        modelo.suscribir(ventana);
        
        EventMapper eventMapper = new EventMapper(jsonSerializer);
        eventMapper.setListener(modelo);
        eventMapper.setJugadorId(miId);
        
        TraducerJugador traducer = new TraducerJugador(jsonSerializer, eventMapper);
        Receptor receptor = new Receptor(traducer);
        ColaReceptor colaReceptor = new ColaReceptor();
        colaReceptor.attach(receptor);
        
        SocketIN socketIn = new SocketIN(miPuerto, colaReceptor);
        socketIn.start();
        
        System.out.println("Ensamblador: Cliente " + nombreJugador + " (" + miId + ") iniciado en " + miIp + ":" + miPuerto);

        // 4. Registro Automático
        emitterInit.emitirRegistroJugadorEvent(miId, miIp, miPuerto); 
        System.out.println("Ensamblador: Solicitud de registro enviada automáticamente.");
        
        return ventana;
    }
}