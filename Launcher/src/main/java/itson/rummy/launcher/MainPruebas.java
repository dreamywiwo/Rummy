/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package itson.rummy.launcher;

import ensambladores.EnsambladorCliente;
import ensambladores.EnsambladorDominio;
import ensambladores.EnsambladorServidor;
import itson.dominiorummy.entidades.Jugador;
import itson.rummypresentacion.vista.UI_TurnoJugador;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 *
 * @author Dana Chavez
 */
public class MainPruebas {

    private static final String IP_LOCALHOST = "127.0.0.1";

    private static final int PUERTO_BROKER = 9999;
    private static final int PUERTO_DOMINIO = 9998;

    private static final int PUERTO_JUGADOR_1 = 9001;
    private static final int PUERTO_JUGADOR_2 = 9002;

    public static void main(String[] args) {

        new Thread(() -> {
            try {
                System.out.println("[BROKER] Iniciando...");
                EnsambladorServidor servidor = new EnsambladorServidor();
                servidor.iniciarBroker(PUERTO_BROKER);
            } catch (Exception e) {
                System.err.println("Error en Broker: " + e.getMessage());
            }
        }).start();

        esperar(1000);

        List<Jugador> listaJugadores = new ArrayList<>();
        listaJugadores.add(new Jugador("Jugador1"));
        listaJugadores.add(new Jugador("Jugador2"));

        System.out.println("[DOMINIO] Conectando al Broker y configurando juego (sin iniciar partida todavía)...");
        EnsambladorDominio ensambladorDominio = new EnsambladorDominio();
        try {
            ensambladorDominio.iniciarJuego(IP_LOCALHOST, PUERTO_BROKER, PUERTO_DOMINIO, listaJugadores);
        } catch (Exception e) {
            System.err.println("Error al configurar el Dominio: " + e.getMessage());
        }

        lanzarCliente("Jugador1", PUERTO_JUGADOR_1, 50, 50);
        lanzarCliente("Jugador2", PUERTO_JUGADOR_2, 600, 50);
        esperar(3000);

        System.out.println("[MAIN] Clientes listos. Indicando al Dominio que comience la partida...");
        ensambladorDominio.comenzarPartida();
    }

    /**
     * Método auxiliar para lanzar una ventana de cliente.
     *
     * @param id Nombre del jugador
     * @param puertoEscucha Puerto único para este cliente
     * @param x Posición X en pantalla
     * @param y Posición Y en pantalla
     */
    private static void lanzarCliente(String id, int puertoEscucha, int x, int y) {
        SwingUtilities.invokeLater(() -> {
            EnsambladorCliente ensamblador = new EnsambladorCliente();

            UI_TurnoJugador ventana = ensamblador.construirJugador(
                    IP_LOCALHOST, // IP Broker
                    PUERTO_BROKER, // Puerto Broker
                    IP_LOCALHOST, // Mi IP
                    puertoEscucha, // Mi Puerto Único
                    id // Mi ID
            );

            ventana.setTitle("Rummy - " + id + " (Puerto " + puertoEscucha + ")");
            ventana.setLocation(x, y);
            ventana.setVisible(true);
        });
    }

    /**
     * Método simple para pausar la ejecución
     */
    private static void esperar(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
