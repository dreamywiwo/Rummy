/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.rummypresentacion.vista;

import entidades.Jugador;
import itson.rummypresentacion.DTOs.FichaDTO;
import itson.rummypresentacion.DTOs.JugadorDTO;
import itson.rummypresentacion.modelo.IModelo;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Adrián
 */
public class UI_Jugador extends ComponenteBase {

    JugadorDTO player;
    List<JugadorDTO> jugadores;
    boolean esJugador;
    private List<UI_Ficha> fichas;
    JPanel contenedorPrincipal = new JPanel();

    public UI_Jugador(String id) {
        super(id);
        this.player = player;
        this.jugadores = new ArrayList<>();
        this.esJugador = "imagen_jugador".equals(id);
        inicializarComponente();
    }

    private void inicializarComponente() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        setBackground(new Color(39, 41, 72));
        setOpaque(true);
    }
    
    private void addJugadores() {
        for (int i = 0; i < jugadores.size(); i++) {
            JPanel panelJugadores = new JPanel();
            panelJugadores.setPreferredSize(new Dimension(200, 50)); // Tamaño del panel
            panelJugadores.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Borde para visualización
            panelJugadores.add(new JLabel(player.getNombre())); // Añade una etiqueta con el nombre del ítem
            panelJugadores.add(new JLabel(player.getMano().toString()));
            panelJugadores.add(new JLabel(player.getAvatar()));
            contenedorPrincipal.add(panelJugadores); // Añade el panel del ítem al contenedor principal
        }
        setOpaque(true);
    }

    public void actualizarFichas(IModelo modelo) {
        try {
            List<FichaDTO> numeroActual = modelo.getManoJugadorActual();
            JLabel fichas = new JLabel("Numero de fichas: " + numeroActual.size());
        } catch (Exception ex) {
            System.err.println("Error actualizando UI_Grupo: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public List<UI_Ficha> getFichas() {
        return new ArrayList<>(fichas);
    }

}
