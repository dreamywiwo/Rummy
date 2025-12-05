/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.producerdominio.emitters;

import com.mycompany.conexioninterfaces.IDispatcher;
import itson.rummyeventos.sistema.RegistroDominioEvent;
import itson.serializer.implementacion.JsonSerializer;

/**
 *
 * @author Dana Chavez
 */
public class InicializarJuegoEmitter {
    private final JsonSerializer jsonSerializer;
    private final IDispatcher dispatcher;
    private final String brokerIp;
    private final int brokerPort;

    public InicializarJuegoEmitter(JsonSerializer jsonSerializer, IDispatcher dispatcher, String brokerIp, int brokerPort) {
        this.jsonSerializer = jsonSerializer;
        this.dispatcher = dispatcher;
        this.brokerIp = brokerIp;
        this.brokerPort = brokerPort;
    }
    
    public void emitirRegistroDominioEvent(String miId, String ipCliente, int miPuertoDeEscucha) {
        RegistroDominioEvent event = new RegistroDominioEvent(miId, ipCliente, miPuertoDeEscucha);
        String json = jsonSerializer.serialize(event);
        dispatcher.enviar(json, brokerPort, brokerIp);
    }    
}
