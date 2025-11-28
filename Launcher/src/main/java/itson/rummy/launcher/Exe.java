/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package itson.rummy.launcher;

import ensambladores.EnsambladorCliente;
import ensambladores.EnsambladorDominio;
import ensambladores.EnsambladorServidor;
import itson.dominiorummy.entidades.Jugador;
import itson.rummypresentacion.vista.UI_TurnoJugador;
import java.util.ArrayList;
import java.util.List;

/**
 * Raíz de Composición del Cliente (Jugador)
 *
 * @author Dana Chavez
 */
public class Exe {

    public static void main(String[] args) {
        String brokerIp = "127.0.0.1";
        int puertoBroker = 9999;
        int puertoDominio = 9000;

        EnsambladorServidor brokerAssembler = new EnsambladorServidor();
        brokerAssembler.iniciarBroker(puertoBroker);

        List<Jugador> jugadores = new ArrayList<>();
        jugadores.add(new Jugador("Jugador1", "Juan"));
        jugadores.add(new Jugador("Jugador2", "Dana"));

        EnsambladorDominio dominioAssembler = new EnsambladorDominio();

        dominioAssembler.iniciarJuego(brokerIp, puertoBroker, puertoDominio, jugadores);

        int miPuerto = 9002;
        String miIp = "127.0.0.1";
        String miId = "Jugador1";

        EnsambladorCliente ensamblador = new EnsambladorCliente();

        UI_TurnoJugador ventana = ensamblador.construirJugador(
                brokerIp,
                puertoBroker,
                miIp,
                miPuerto,
                miId
        );

        java.awt.EventQueue.invokeLater(() -> {
            System.out.println("Cliente: " + miId);
            ventana.setVisible(true);
        });
    }
}
