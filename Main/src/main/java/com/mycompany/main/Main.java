/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.main;

import itson.producerjugador.emitters.JugarTurnoEmitter;
import itson.producerjugador.facade.IProducerJugador;
import itson.producerjugador.facade.ProducerJugador;
import itson.rummypresentacion.controlador.ControladorTurno;
import itson.rummypresentacion.modelo.Modelo;
import itson.rummypresentacion.vista.UI_TurnoJugador;
import itson.serializer.implementacion.JsonSerializer;

/**
 *
 * @author Dana Chavez
 */
public class Main {
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            JsonSerializer jsonSerializer = new JsonSerializer();
            JugarTurnoEmitter emitter = new JugarTurnoEmitter(jsonSerializer);
            IProducerJugador producer = new ProducerJugador(emitter);
            Modelo modelo = new Modelo(producer);
            ControladorTurno controlador = new ControladorTurno(modelo);
            UI_TurnoJugador ventana = new UI_TurnoJugador(controlador);
            ventana.setVisible(true);
        });
    }
}
