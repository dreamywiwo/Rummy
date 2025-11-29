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
import com.mycompany.broker.Broker;
import com.mycompany.broker.SubscriptionRegistry;
import com.mycompany.conexioninterfaces.IDispatcher;
import itson.directorio.implementacion.Directorio;
import itson.directorio.interfaces.IDirectorio;
import itson.serializer.implementacion.JsonSerializer;
import itson.serializer.interfaces.ISerializer;

/**
 *
 * @author jrasc
 */
public class EnsambladorServidor {

    public void iniciarBroker(int puertoEscucha) {

        ISerializer serializer = new JsonSerializer();
        IDirectorio directorio = new Directorio();
        SubscriptionRegistry registry = new SubscriptionRegistry();

        SocketOut socketOut = new SocketOut();
        socketOut.start();

        ColaDispatcher colaDispatcher = new ColaDispatcher();
        colaDispatcher.attach(socketOut);

        IDispatcher dispatcher = new Dispatcher(colaDispatcher);

        Broker brokerLogic = new Broker(directorio, dispatcher, serializer, registry);

        ColaReceptor colaReceptor = new ColaReceptor();
        colaReceptor.attach(new Receptor(brokerLogic));
        
        String ip1 = "127.0.0.1";

        directorio.registerClient("Jugador1", ip1, 9002);
        registry.addSuscriptor("actualizaciones.estado", "Jugador1");
        
        directorio.registerClient("Dominio", ip1, 9000);
        registry.addSuscriptor("acciones.jugador", "Dominio");
        SocketIN socketIN = new SocketIN(puertoEscucha, colaReceptor);
        socketIN.start();

        System.out.println("EnsambladorBroker: Servidor Broker escuchando en el puerto " + puertoEscucha);
    }
}
