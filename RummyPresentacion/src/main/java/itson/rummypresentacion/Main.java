/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package itson.rummypresentacion;

import itson.rummypresentacion.DTOs.FichaDTO;
import itson.rummypresentacion.controlador.ControlTurno;
import itson.rummypresentacion.fachada.FachadaDominio;
import itson.rummypresentacion.fachada.IFachadaDominio;
import itson.rummypresentacion.modelo.Modelo;
import itson.rummypresentacion.vista.UI_TurnoJugador;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class Main {
    public static void main(String[] args) {
        try {
            IFachadaDominio fachada = new FachadaDominio();
            Modelo modelo = new Modelo(fachada);

            UI_TurnoJugador vistaJugador1 = new UI_TurnoJugador("Jugador1");
            UI_TurnoJugador vistaJugador2 = new UI_TurnoJugador("Jugador2");

            ControlTurno controlJugador1 = new ControlTurno(vistaJugador1, modelo);
            ControlTurno controlJugador2 = new ControlTurno(vistaJugador2, modelo);

            // Configurar controladores y vistas
            vistaJugador1.setControlador(controlJugador1);
            vistaJugador2.setControlador(controlJugador2);

            // Suscribir vistas al modelo
            modelo.suscribir(vistaJugador1);
            modelo.suscribir(vistaJugador2);

            // Registrar jugadores
            controlJugador1.registrarJugador("Jugador1");
            controlJugador2.registrarJugador("Jugador2");

            // INICIALIZAR CON DATOS MOCK QUE SÍ ESTÉN EN EL MODELO
            List<FichaDTO> manoJugador1 = crearManoEjemploJugador1();
            List<FichaDTO> manoJugador2 = crearManoEjemploJugador2();
            List<FichaDTO> pozoFichas = crearPozoEjemplo();

            System.out.println("DEBUG - Inicializando juego con:");
            System.out.println(" - Mano Jugador1: " + manoJugador1.size() + " fichas");
            System.out.println(" - Mano Jugador2: " + manoJugador2.size() + " fichas");
            System.out.println(" - Pozo: " + pozoFichas.size() + " fichas");

            // Inicializar el juego a través del controlador
            controlJugador1.inicializarJuego(manoJugador1, manoJugador2, pozoFichas);

            // Configurar ventanas
            vistaJugador1.setTitle("Rummy - Jugador 1");
            vistaJugador2.setTitle("Rummy - Jugador 2");
            vistaJugador1.setLocation(100, 100);
            vistaJugador2.setLocation(1200, 100);

            vistaJugador1.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
            vistaJugador2.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

            vistaJugador1.setVisible(true);
            vistaJugador2.setVisible(true);

            System.out.println("Juego inicializado correctamente");

        } catch (Exception e) {
            System.err.println("Error al iniciar la aplicación:");
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private static List<FichaDTO> crearManoEjemploJugador1() {
        List<FichaDTO> mano = new ArrayList<>();
        // Grupo de secuencia: 1,2,3 rojos
        mano.add(new FichaDTO(1, "rojo", "J1_1"));
        mano.add(new FichaDTO(2, "rojo", "J1_2"));
        mano.add(new FichaDTO(3, "rojo", "J1_3"));
        
        // Grupo del mismo número: 7s de diferentes colores
        mano.add(new FichaDTO(7, "azul", "J1_4"));
        mano.add(new FichaDTO(7, "verde", "J1_5"));
        mano.add(new FichaDTO(7, "amarillo", "J1_6"));
        
        // Fichas sueltas
        mano.add(new FichaDTO(4, "rojo", "J1_7"));
        mano.add(new FichaDTO(5, "azul", "J1_8"));
        mano.add(new FichaDTO(6, "verde", "J1_9"));
        
        return mano;
    }

    private static List<FichaDTO> crearManoEjemploJugador2() {
        List<FichaDTO> mano = new ArrayList<>();
        // Grupo de secuencia: 1,2,3 azules
        mano.add(new FichaDTO(1, "azul", "J2_1"));
        mano.add(new FichaDTO(2, "azul", "J2_2"));
        mano.add(new FichaDTO(3, "azul", "J2_3"));
        
        // Grupo de secuencia: 7,8,9 rojos
        mano.add(new FichaDTO(7, "rojo", "J2_4"));
        mano.add(new FichaDTO(8, "rojo", "J2_5"));
        mano.add(new FichaDTO(9, "rojo", "J2_6"));
        
        // Fichas sueltas
        mano.add(new FichaDTO(4, "azul", "J2_7"));
        mano.add(new FichaDTO(5, "verde", "J2_8"));
        mano.add(new FichaDTO(6, "amarillo", "J2_9"));
        
        return mano;
    }

    private static List<FichaDTO> crearPozoEjemplo() {
        List<FichaDTO> pozo = new ArrayList<>();
        pozo.add(new FichaDTO(10, "rojo", "P1"));
        pozo.add(new FichaDTO(11, "azul", "P2"));
        pozo.add(new FichaDTO(12, "verde", "P3"));
        pozo.add(new FichaDTO(13, "amarillo", "P4"));
        pozo.add(new FichaDTO(1, "negro", "P5")); // Comodín
        return pozo;
    }
    
    
}