/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package itson.rummypresentacion;

import itson.rummypresentacion.vista.UI_TurnoJugador;
import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Crear el frame con el ID del jugador 
                UI_TurnoJugador frame = new UI_TurnoJugador("jugador_1");

                // Configurar el frame primero
                configurarFrame(frame);
                
                // Luego cargar las fichas de ejemplo
                cargarFichasDeEjemplo(frame);
                
                System.out.println("Aplicación iniciada correctamente");
                
            } catch (Exception e) {
                System.err.println("Error al iniciar la aplicación:");
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
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
