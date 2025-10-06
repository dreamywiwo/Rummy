/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.rummypresentacion.vista;

/**
 *
 * @author Adrián/**
 */
import itson.rummypresentacion.DTOs.FichaDTO;
import itson.rummypresentacion.modelo.IModelo;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 *
 * @author Dana Chavez
 */
public class UI_Jugador extends ComponenteBase {
    private String jugadorId;
    private JLabel lblImagen;
    private JLabel lblInfo;
    private int cantidadFichas;
    private boolean esTurnoActual;
    private boolean esJugadorActual; // Si es el dueño de esta vista
    
    public UI_Jugador(String id, String jugadorId, boolean esJugadorActual) {
        super(id);
        this.jugadorId = jugadorId;
        this.esJugadorActual = esJugadorActual;
        this.cantidadFichas = 0;
        this.esTurnoActual = false;
        inicializarComponente();
    }
    
    private void inicializarComponente() {
        setLayout(new BorderLayout(10, 5));
        setPreferredSize(new Dimension(180, 120));
        setBackground(new Color(240, 240, 240));
        setOpaque(true);
        
        // Crear componentes
        crearImagenPerfil();
        crearPanelInfo();
        
        // Agregar al layout
        add(lblImagen, BorderLayout.WEST);
        add(lblInfo, BorderLayout.CENTER);
        
        // Borde para indicar turno
        setBorder(javax.swing.BorderFactory.createLineBorder(Color.GRAY, 1));
    }
    
    private void crearImagenPerfil() {
        try {
            // Determinar qué imagen usar basado en el jugadorId
            String nombreImagen = obtenerNombreImagen();
            String rutaImagen = "/" + nombreImagen;
            java.net.URL imagenURL = getClass().getResource(rutaImagen);
            
            if (imagenURL != null) {
                ImageIcon iconoOriginal = new ImageIcon(imagenURL);
                // Escalar imagen
                Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                lblImagen = new JLabel(new ImageIcon(imagenEscalada));
            } else {
                // Fallback a imagen mockeada
                System.err.println("No se encontró la imagen: " + rutaImagen);
                BufferedImage imagen = crearImagenMock();
                lblImagen = new JLabel(new ImageIcon(imagen));
            }
        } catch (Exception e) {
            System.err.println("Error cargando imagen: " + e.getMessage());
            // Fallback a imagen mockeada
            BufferedImage imagen = crearImagenMock();
            lblImagen = new JLabel(new ImageIcon(imagen));
        }
        lblImagen.setPreferredSize(new Dimension(80, 80));
    }
    
    private String obtenerNombreImagen() {
        // Asignar imágenes específicas a cada jugador
        if ("Jugador1".equals(jugadorId)) {
            return "imageBun.png"; // Jugador1 es el conejo
        } else if ("Jugador2".equals(jugadorId)) {
            return "imageFish.png"; // Jugador2 es el pez
        } else {
            return "imageBun.png"; // Por defecto
        }
    }
    
    private BufferedImage crearImagenMock() {
        BufferedImage imagen = new BufferedImage(80, 80, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = imagen.createGraphics();
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Fondo circular
        Color colorFondo = obtenerColorPorJugador();
        g2d.setColor(colorFondo);
        g2d.fillOval(5, 5, 70, 70);
        
        // Borde
        g2d.setColor(Color.DARK_GRAY);
        g2d.setStroke(new java.awt.BasicStroke(2));
        g2d.drawOval(5, 5, 70, 70);
        
        // Inicial del jugador
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        String inicial = jugadorId.substring(0, 1).toUpperCase();
        int x = 40 - g2d.getFontMetrics().stringWidth(inicial) / 2;
        int y = 45;
        g2d.drawString(inicial, x, y);
        
        g2d.dispose();
        return imagen;
    }
    
    private Color obtenerColorPorJugador() {
        if ("Jugador1".equals(jugadorId)) {
            return new Color(70, 130, 180); // Azul para Jugador1
        } else {
            return new Color(220, 20, 60);  // Rojo para Jugador2
        }
    }
    
    private void crearPanelInfo() {
        JPanel panelInfo = new JPanel(new BorderLayout(5, 5));
        panelInfo.setOpaque(false);
        
        // Nombre del jugador
        JLabel lblNombre = new JLabel(jugadorId + (esJugadorActual ? " (Tú)" : ""));
        lblNombre.setFont(new Font("Arial", Font.BOLD, 14));
        lblNombre.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Información de fichas
        lblInfo = new JLabel("");
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 12));
        lblInfo.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Estado del turno
        JLabel lblTurno = new JLabel("Esperando...");
        lblTurno.setFont(new Font("Arial", Font.ITALIC, 10));
        lblTurno.setHorizontalAlignment(SwingConstants.CENTER);
        lblTurno.setForeground(Color.GRAY);
        
        panelInfo.add(lblNombre, BorderLayout.NORTH);
        panelInfo.add(lblInfo, BorderLayout.CENTER);
        panelInfo.add(lblTurno, BorderLayout.SOUTH);
        
        lblInfo = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
            }
        };
        lblInfo.setLayout(new BorderLayout());
        lblInfo.add(panelInfo, BorderLayout.CENTER);
    }
    
    @Override
    public void actualizar(IModelo modelo) {
        try {
            // Actualizar cantidad de fichas
            if (esJugadorActual) {
                // Para el jugador actual, obtener su mano real
                List<FichaDTO> mano = modelo.getManoJugadorActual();
                this.cantidadFichas = mano != null ? mano.size() : 0;
            } else {
                // Para el oponente, mostrar cantidad mockeada o real si está disponible
                this.cantidadFichas = obtenerCantidadFichasOponente(modelo);
            }
            
            // Actualizar estado del turno
            this.esTurnoActual = modelo.esTurnoDe(jugadorId);
            
            // Actualizar UI
            actualizarVista();
            
        } catch (Exception e) {
            System.err.println("Error actualizando UI_Jugador: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private int obtenerCantidadFichasOponente(IModelo modelo) {
        // Mock: siempre mostrar 7 fichas para el oponente
        return 7;
    }
    
    private void actualizarVista() {
        // Actualizar texto de fichas
        lblInfo.setText("Fichas: " + cantidadFichas);
        
        // Actualizar apariencia basada en el turno
        if (esTurnoActual) {
            setBackground(new Color(220, 255, 220)); // Verde claro para turno activo
            setBorder(javax.swing.BorderFactory.createLineBorder(Color.GREEN, 3));
            lblInfo.setForeground(Color.GREEN);
        } else {
            setBackground(new Color(240, 240, 240)); // Gris para turno inactivo
            setBorder(javax.swing.BorderFactory.createLineBorder(Color.GRAY, 1));
            lblInfo.setForeground(Color.BLACK);
        }
        
        repaint();
    }

    public void setTamanoPreferido(int ancho, int alto) {
        setPreferredSize(new java.awt.Dimension(ancho, alto));
    }
}

