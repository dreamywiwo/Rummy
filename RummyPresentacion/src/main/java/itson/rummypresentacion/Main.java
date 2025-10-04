/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package itson.rummypresentacion;

import itson.rummypresentacion.vista.UI_TurnoJugador;
import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author Dana Chavez
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {

                UI_TurnoJugador frame = new UI_TurnoJugador();

                cargarFichasDeEjemplo(frame);
                configurarFrame(frame);
                
            } catch (Exception e) {
                System.err.println("Error al iniciar la aplicación:");
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    private static void cargarFichasDeEjemplo(UI_TurnoJugador frame) {

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
    }
    
    private static void configurarFrame(UI_TurnoJugador frame) {
        frame.setTitle("Rummy - Modo Selección (Corregido)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
