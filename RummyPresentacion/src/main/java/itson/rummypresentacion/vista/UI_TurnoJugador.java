/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package itson.rummypresentacion.vista;

import itson.rummypresentacion.DTOs.FichaDTO;
import itson.rummypresentacion.controlador.ControlTurno;
import itson.rummypresentacion.modelo.IModelo;
import itson.rummypresentacion.modelo.IObserver;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class UI_TurnoJugador extends javax.swing.JFrame implements IComponente, IObserver {
    private String id = "root";
    private List<IComponente> componentes;
    private UI_Grupo uiGrupoMano;
    private UI_Tablero uiTablero;
//    private UI_Jugador uiJugadorActual;    // El jugador dueño de esta vista
//   private UI_Jugador uiJugadorOponente;  // El otro jugador
    private ControlTurno control;
    private String jugadorId;
    private String oponenteId;             // ID del oponente
    private javax.swing.JComponent glassBlocker;

    public UI_TurnoJugador(String jugadorId) {
        this.jugadorId = jugadorId;
        // Determinar quién es el oponente
        this.oponenteId = "Jugador1".equals(jugadorId) ? "Jugador2" : "Jugador1";
        this.componentes = new ArrayList<>();
        initComponents();
        inicializarComponentesVisuales();
        bloquearJugador();
        
    }
    
    @Override
    public void update(IModelo modelo) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("UI_TurnoJugador '" + jugadorId + "' recibió actualización del modelo");
            actualizar(modelo);
        });
    }
    
    private void inicializarComponentesVisuales() {
        // Crear componentes de jugador
//        uiJugadorActual = new UI_Jugador("jugador_actual", jugadorId, true);
//        uiJugadorOponente = new UI_Jugador("jugador_oponente", oponenteId, false);
        
        // Crear resto de componentes
        uiGrupoMano = new UI_Grupo("mano_jugador");
        uiTablero = new UI_Tablero("tablero_principal");
        
        uiTablero.setPadre(this);

        // Agregar componentes al composite
//        agregarComponente(uiJugadorOponente);  // Oponente primero (se dibuja arriba)
//        agregarComponente(uiJugadorActual);    // Jugador actual después (se dibuja abajo)
        agregarComponente(uiGrupoMano);
        agregarComponente(uiTablero);

        // Configurar contenedores
        jPanelJugadorOponente.setLayout(new BorderLayout());
        jPanelJugadorActual.setLayout(new BorderLayout());
        jPanelContenedorMano.setLayout(new BorderLayout());
        jPanelContenedorTablero.setLayout(new BorderLayout());

        // Agregar componentes a los paneles
//        jPanelJugadorOponente.add(uiJugadorOponente, BorderLayout.CENTER);
//        jPanelJugadorActual.add(uiJugadorActual, BorderLayout.CENTER);
        jPanelContenedorMano.add(uiGrupoMano, BorderLayout.CENTER);
        jPanelContenedorTablero.add(uiTablero, BorderLayout.CENTER);

        // Configurar relaciones
        uiGrupoMano.setTablero(uiTablero);
        uiTablero.setGrupoMano(uiGrupoMano);

        configurarBotones();
        
        System.out.println("Vista configurada para: " + jugadorId);
        System.out.println("Oponente: " + oponenteId);
        System.out.println("Total de componentes hijos: " + componentes.size());
    }
    
    private void configurarBotones() {
        btnTerminarTurno.addActionListener(e -> {
            if (control != null) {
                control.terminarTurno(jugadorId);
            } else {
                mostrarMensaje("Controlador no configurado");
            }
        });
        
        btnTomarFicha.addActionListener(e -> {
            if (control != null) {
                control.tomarFicha(jugadorId);
                control.terminarTurno(jugadorId);
            } else {
                mostrarMensaje("Controlador no configurado");
            }
        });
    }

    // Implementación del método actualizar del Composite
    @Override
    public void actualizar(IModelo modelo) {
        try {
            System.out.println("UI_TurnoJugador '" + jugadorId + "' propagando actualización a " + componentes.size() + " componentes hijos...");

            // 1. Actualizar estado de la UI principal
            actualizarEstadoBotones(modelo);
            actualizarInformacionTurno(modelo);
            
            // 2. Manejar eventos especiales (errores, mensajes)
            manejarEventosEspeciales(modelo);

            // 3. Propagar la actualización a todos los componentes hijos
            for (IComponente componente : componentes) {
                System.out.println("Propagando actualización a: " + componente.getId());
                componente.actualizar(modelo);
            }

            // 4. Actualizar este componente
            this.revalidate();
            this.repaint();

            System.out.println("Actualización completada para " + componentes.size() + " componentes");

        } catch (Exception ex) {
            System.err.println("Error en actualización composite: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void actualizarEstadoBotones(IModelo modelo) {
        boolean esMiTurno = modelo.esTurnoDe(jugadorId);
        
        // Actualizar botones
        btnTerminarTurno.setEnabled(esMiTurno);
        btnTomarFicha.setEnabled(esMiTurno);
        
        if (esMiTurno) {
            btnTerminarTurno.setText("TERMINAR TURNO");
            btnTomarFicha.setText("TOMAR FICHA");
        } else {
            btnTerminarTurno.setText("ESPERANDO...");
            btnTomarFicha.setText("ESPERANDO...");
        }
        
        // Actualizar componentes hijos
        if (uiTablero != null) {
            uiTablero.setEnabled(esMiTurno);
        }
        if (uiGrupoMano != null) {
            uiGrupoMano.setEnabled(esMiTurno);
        }
        
        // Actualizar bloqueo de interfaz
        habilitar(esMiTurno);
    }

    private void actualizarInformacionTurno(IModelo modelo) {
        try {
            String jugadorActual = modelo.getJugadorActivoId();
            int turnoActual = modelo.getTurnoActual();
            
            String titulo = String.format("Rummy - Turno %d - Jugador Actual: %s (Tú: %s)", 
                turnoActual, jugadorActual, jugadorId);
            setTitle(titulo);
            
        } catch (Exception e) {
            System.err.println("Error obteniendo información del turno: " + e.getMessage());
        }
    }
    
    private void manejarEventosEspeciales(IModelo modelo) {
        String ultimoEvento = modelo.getUltimoEvento();
        String ultimoError = modelo.getUltimoError();
        
        // Manejar errores
        if (ultimoError != null && !ultimoError.isEmpty()) {
            mostrarMensaje(ultimoError);
        }
        
        // Manejar eventos específicos
        if (ultimoEvento != null) {
            switch (ultimoEvento) {
                case "TURNO_TERMINADO":
                    if (modelo.esTurnoDe(jugadorId)) {
                        mostrarMensaje("¡Turno terminado exitosamente!");
                    }
                    break;
                case "FICHA_TOMADA":
                    mostrarMensaje("Ficha tomada del pozo");
                    break;
                case "FICHAS_COLOCADAS":
                    mostrarMensaje("Fichas colocadas en el tablero");
                    break;
                case "GRUPO_CREADO":
                    mostrarMensaje("Grupo creado exitosamente");
                    break;
            }
        }
    }
    
    /**
     * Método mejorado para manejar clics en el tablero
     */
    public void onCeldaTableroClickeada(Point posicion, List<String> fichasSeleccionadasIds) { 
        if (control != null) { 
            List<FichaDTO> fichasDTO = uiGrupoMano.getFichasSeleccionadasDTO(); 
            if (fichasDTO == null || fichasDTO.isEmpty()) { 
                mostrarMensaje("Selecciona fichas de tu mano primero"); 
                return; 
            } 
            control.colocarFichasEnTablero(jugadorId, fichasDTO, posicion); 
        } else { 
            mostrarMensaje("Controlador no configurado"); 
        } 
    } 
 
 
    /**
     * Método para recibir notificaciones de selección de fichas desde UI_Tablero
     */
    public void onFichasSeleccionadas(List<String> fichasIds) {
        System.out.println("UI_TurnoJugador: Procesando selección de " + fichasIds.size() + " fichas");
        
        if (control != null) {
            // Toggle cada ficha seleccionada en el controlador
            for (String fichaId : fichasIds) {
                control.toggleSeleccionFicha(fichaId);
            }
        } else {
            System.err.println("ERROR: Controlador no configurado en UI_TurnoJugador");
        }
    }

    /**
     * Convierte IDs de fichas a FichaDTO usando la mano actual
     */
    private List<FichaDTO> convertirIdsAFichaDTO(List<String> fichasSeleccionadasIds) {
        List<FichaDTO> fichasDTO = new ArrayList<>();
        if (uiGrupoMano != null) {
            for (UI_Ficha fichaUI : uiGrupoMano.getFichas()) {
                if (fichasSeleccionadasIds.contains(fichaUI.getId())) {
                    // Crear FichaDTO a partir de la UI_Ficha
                    FichaDTO fichaDTO = new FichaDTO(
                        fichaUI.getNumero(),
                        convertirColorAString(fichaUI.getFichaColor()),
                        fichaUI.getId()
                    );
                    fichasDTO.add(fichaDTO);
                }
            }
        }
        return fichasDTO;
    }
    
    /**
     * Convierte Color de Java a String para el DTO
     */
    private String convertirColorAString(Color color) {
        if (color.equals(Color.RED)) return "rojo";
        if (color.equals(Color.BLUE)) return "azul";
        if (color.equals(Color.GREEN)) return "verde";
        if (color.equals(Color.YELLOW)) return "amarillo";
        if (color.equals(Color.BLACK)) return "negro";
        return "gris";
    }
    
    public void mostrarMensaje(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }

    public void setControlador(ControlTurno control) {
        this.control = control;
        System.out.println("Controlador configurado para UI_TurnoJugador '" + jugadorId + "'");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelContenedorMano = new javax.swing.JPanel();
        jPanelJugadorActual = new javax.swing.JPanel();
        jPanelContenedorTablero = new javax.swing.JPanel();
        jPanelJugadorOponente = new javax.swing.JPanel();
        panelBotones = new javax.swing.JPanel();
        btnTerminarTurno = new javax.swing.JButton();
        btnTomarFicha = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanelContenedorMano.setBackground(new java.awt.Color(39, 41, 72));
        jPanelContenedorMano.setOpaque(false);
        jPanelContenedorMano.setPreferredSize(new java.awt.Dimension(1418, 235));
        jPanelContenedorMano.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        javax.swing.GroupLayout jPanelJugadorActualLayout = new javax.swing.GroupLayout(jPanelJugadorActual);
        jPanelJugadorActual.setLayout(jPanelJugadorActualLayout);
        jPanelJugadorActualLayout.setHorizontalGroup(
            jPanelJugadorActualLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 250, Short.MAX_VALUE)
        );
        jPanelJugadorActualLayout.setVerticalGroup(
            jPanelJugadorActualLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 190, Short.MAX_VALUE)
        );

        jPanelContenedorMano.add(jPanelJugadorActual, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 250, 190));

        getContentPane().add(jPanelContenedorMano, java.awt.BorderLayout.SOUTH);

        jPanelContenedorTablero.setOpaque(false);

        javax.swing.GroupLayout jPanelJugadorOponenteLayout = new javax.swing.GroupLayout(jPanelJugadorOponente);
        jPanelJugadorOponente.setLayout(jPanelJugadorOponenteLayout);
        jPanelJugadorOponenteLayout.setHorizontalGroup(
            jPanelJugadorOponenteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 254, Short.MAX_VALUE)
        );
        jPanelJugadorOponenteLayout.setVerticalGroup(
            jPanelJugadorOponenteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 188, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanelContenedorTableroLayout = new javax.swing.GroupLayout(jPanelContenedorTablero);
        jPanelContenedorTablero.setLayout(jPanelContenedorTableroLayout);
        jPanelContenedorTableroLayout.setHorizontalGroup(
            jPanelContenedorTableroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelContenedorTableroLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelJugadorOponente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(2128, Short.MAX_VALUE))
        );
        jPanelContenedorTableroLayout.setVerticalGroup(
            jPanelContenedorTableroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelContenedorTableroLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelJugadorOponente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(715, Short.MAX_VALUE))
        );

        getContentPane().add(jPanelContenedorTablero, java.awt.BorderLayout.CENTER);

        panelBotones.setOpaque(false);
        panelBotones.setLayout(new java.awt.GridLayout(2, 1));

        btnTerminarTurno.setText("TERMINAR TURNO");
        panelBotones.add(btnTerminarTurno);

        btnTomarFicha.setText("TOMAR FICHA");
        btnTomarFicha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTomarFichaActionPerformed(evt);
            }
        });
        panelBotones.add(btnTomarFicha);

        getContentPane().add(panelBotones, java.awt.BorderLayout.EAST);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnTomarFichaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTomarFichaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnTomarFichaActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnTerminarTurno;
    private javax.swing.JButton btnTomarFicha;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanelContenedorMano;
    private javax.swing.JPanel jPanelContenedorTablero;
    private javax.swing.JPanel jPanelJugadorActual;
    private javax.swing.JPanel jPanelJugadorOponente;
    private javax.swing.JPanel panelBotones;
    // End of variables declaration//GEN-END:variables

    @Override
    public void mostrar() {
        this.setVisible(true);
        for (IComponente componente : componentes) {
            componente.mostrar();
        }
    }

    @Override
    public void ocultar() {
        this.setVisible(false);
        for (IComponente componente : componentes) {
            componente.ocultar();
        }
    }

    @Override
    public void agregarComponente(IComponente componente) {
        componentes.add(componente);
    }

    @Override
    public void removerComponente(IComponente componente) {
        componentes.remove(componente);
    }

    @Override
    public IComponente getComponente(int index) {
        return componentes.get(index);
    }

    @Override
    public List<IComponente> getComponentes() {
        return new ArrayList<>(componentes);
    }

    @Override
    public String getId() {
        return id;
    }

    // Pruebas

    public void agregarFichaAMano(int numero, Color color) {
        if (uiGrupoMano != null) {
            uiGrupoMano.agregarFicha(new UI_Ficha(numero, color));
        } else {
            System.err.println("Error: uiGrupoMano no está inicializado");
        }
    }

    public void limpiarMano() {
        if (uiGrupoMano != null) {
            uiGrupoMano.limpiarGrupo();
        }
    }

    public UI_Tablero getUITablero() {
        return uiTablero;
    }

    public UI_Grupo getUIGrupoMano() {
        return uiGrupoMano;
    }

    // Bloqueo / Habilitacion

    private void bloquearJugador() {
        if (glassBlocker != null) {
            return;
        }

        javax.swing.JPanel blocker = new javax.swing.JPanel(null) {
            @Override
            public boolean isOpaque() {
                return false;
            }
        };
        java.awt.event.MouseAdapter mouseEater = new java.awt.event.MouseAdapter() {
        };
        blocker.addMouseListener(mouseEater);
        blocker.addMouseMotionListener(mouseEater);
        blocker.addMouseWheelListener(mouseEater);
        blocker.setFocusable(true);
        blocker.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                e.consume();
            }

            @Override
            public void keyTyped(java.awt.event.KeyEvent e) {
                e.consume();
            }

            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                e.consume();
            }
        });
        blocker.setCursor(java.awt.Cursor.getDefaultCursor());

        glassBlocker = blocker;
        setGlassPane(glassBlocker);
    }

    private void habilitar(boolean habilitada) {
        bloquearJugador();
        boolean bloquear = !habilitada;
        glassBlocker.setVisible(bloquear);
        if (bloquear) {
            glassBlocker.requestFocusInWindow();
        } else {
            if (btnTomarFicha.isEnabled()) {
                btnTomarFicha.requestFocusInWindow();
            } else {
                this.requestFocusInWindow();
            }
        }
    }
    //PRUEBAS
    public void cargarFichasDeEjemploTemporal() {
        if (uiGrupoMano != null) {
            // Fichas de ejemplo temporales
            uiGrupoMano.agregarFicha(new UI_Ficha(1, Color.RED));
            uiGrupoMano.agregarFicha(new UI_Ficha(2, Color.RED));
            uiGrupoMano.agregarFicha(new UI_Ficha(3, Color.RED));
            uiGrupoMano.agregarFicha(new UI_Ficha(7, Color.BLUE));
            uiGrupoMano.agregarFicha(new UI_Ficha(7, Color.GREEN));
            uiGrupoMano.agregarFicha(new UI_Ficha(7, Color.YELLOW));
            
            System.out.println("Fichas temporales cargadas en la mano");
        }
    }
}


