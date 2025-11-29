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
        String miIP = "127.0.0.1";
        int puertoBroker = 9999;
        int puertoDominio = 9000;

        EnsambladorServidor brokerAssembler = new EnsambladorServidor();
        brokerAssembler.iniciarBroker(puertoBroker);

        List<Jugador> jugadores = new ArrayList<>();
        jugadores.add(new Jugador("Jugador1", "Juan"));
        jugadores.add(new Jugador("Jugador2", "Dana"));

        EnsambladorDominio dominioAssembler = new EnsambladorDominio();

        dominioAssembler.iniciarJuego(miIP, puertoBroker, puertoDominio, jugadores);

        EnsambladorCliente ensamblador = new EnsambladorCliente();

        UI_TurnoJugador ventana = ensamblador.construirJugador(
                miIP,
                puertoBroker,
                miIP,
                9002,
                "Jugador1"
        );

        java.awt.EventQueue.invokeLater(() -> {
            ventana.setVisible(true);
            ventana.setTitle("Jugador1");
        });
    }
}
