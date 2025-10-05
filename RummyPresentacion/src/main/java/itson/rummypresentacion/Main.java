/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package itson.rummypresentacion;

import itson.rummypresentacion.controlador.ControlTurno;
import itson.rummypresentacion.fachada.FachadaDominio;
import itson.rummypresentacion.fachada.IFachadaDominio;
import itson.rummypresentacion.modelo.IModelo;
import itson.rummypresentacion.modelo.Modelo;
import itson.rummypresentacion.vista.UI_TurnoJugador;
import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Main {

    public static void main(String[] args) {
        try {
            IFachadaDominio fachada = new FachadaDominio();
            IModelo modelo = new Modelo(fachada);

            UI_TurnoJugador vistaA = new UI_TurnoJugador("A");
            UI_TurnoJugador vistaB = new UI_TurnoJugador("B");

            ControlTurno controlA = new ControlTurno(vistaA, modelo);
            ControlTurno controlB = new ControlTurno(vistaB, modelo);

            controlA.registrarJugador("A");
            controlB.registrarJugador("B");

            vistaA.setControlador(controlA);
            vistaB.setControlador(controlB);

            modelo.suscribir(vistaA);
            modelo.suscribir(vistaB);
            try {
                cargarFichasDeEjemplo(vistaA);
                cargarFichasDeEjemplo(vistaB);
            } catch (Exception ignored) {
            }
            vistaA.setTitle("Rummy - Jugador A");
            vistaB.setTitle("Rummy - Jugador B");
            vistaA.setLocation(100, 100);
            vistaB.setLocation(1200, 100);

            vistaA.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
            vistaB.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

            vistaA.setVisible(true);
            vistaB.setVisible(true);
            vistaA.update(modelo);
            vistaB.update(modelo);

        } catch (Exception e) {
            System.err.println("Error al iniciar la aplicación:");
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void cargarFichasDeEjemplo(UI_TurnoJugador frame) {
        try {
            System.out.println("Cargando fichas de ejemplo...");

            frame.limpiarMano();

            // fichas de ejemplo
            frame.agregarFichaAMano(1, Color.RED);
            frame.agregarFichaAMano(2, Color.RED);
            frame.agregarFichaAMano(3, Color.RED);

            frame.agregarFichaAMano(7, Color.BLUE);
            frame.agregarFichaAMano(7, Color.GREEN);
            frame.agregarFichaAMano(7, Color.YELLOW);

            frame.agregarFichaAMano(4, Color.RED);
            frame.agregarFichaAMano(5, Color.BLUE);
            frame.agregarFichaAMano(6, Color.GREEN);

            System.out.println("9 fichas cargadas en la mano");

        } catch (Exception e) {
            System.err.println("Error cargando fichas de ejemplo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void configurarFrame(UI_TurnoJugador frame) {
        try {
            frame.setTitle("Rummy - Jugador 1 - [MODO PRUEBAS]");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            frame.pack();

            // Centrar en la pantalla
            frame.setLocationRelativeTo(null);

            // Mostrar el frame
            frame.setVisible(true);

            System.out.println("Frame configurado y visible");

        } catch (Exception e) {
            System.err.println("Error configurando frame: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
