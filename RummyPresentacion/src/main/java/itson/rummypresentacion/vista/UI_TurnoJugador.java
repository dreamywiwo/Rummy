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
    private UI_Jugador uiJugadorActual;    // El jugador dueño de esta vista
   private UI_Jugador uiJugadorOponente;  // El otro jugador
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
        uiJugadorActual = new UI_Jugador("jugador_actual", jugadorId, true);
        uiJugadorOponente = new UI_Jugador("jugador_oponente", oponenteId, false);

        // Crear resto de componentes
        uiGrupoMano = new UI_Grupo("mano_jugador");
        uiTablero = new UI_Tablero("tablero_principal");

        uiGrupoMano.manoIdJugador(jugadorId);

        
        uiTablero.setPadre(this);

        // Agregar componentes al composite
        agregarComponente(uiJugadorOponente);
        agregarComponente(uiJugadorActual);
        agregarComponente(uiGrupoMano);
        agregarComponente(uiTablero);

        jPanelContenedorMano.removeAll();
        jPanelContenedorTablero.removeAll();
        jPanelContenedorPerfiles.removeAll();
        jPanelJugadorOponente.removeAll();
        jPanelJugadorActual.removeAll();
        panelBotones.removeAll();

        jPanelContenedorTablero.setLayout(new java.awt.BorderLayout());
        jPanelContenedorPerfiles.setLayout(new java.awt.BorderLayout());
        jPanelContenedorMano.setLayout(new java.awt.BorderLayout());

        jPanelContenedorPerfiles.setLayout(new java.awt.BorderLayout());
        jPanelContenedorPerfiles.add(uiJugadorOponente, java.awt.BorderLayout.NORTH);
        jPanelContenedorPerfiles.add(uiJugadorActual, java.awt.BorderLayout.SOUTH);

        jPanelContenedorTablero.add(uiTablero, java.awt.BorderLayout.CENTER);
        jPanelContenedorTablero.add(jPanelContenedorPerfiles, java.awt.BorderLayout.WEST);
        jPanelContenedorMano.add(uiGrupoMano, java.awt.BorderLayout.CENTER);

        panelBotones.setLayout(new java.awt.GridLayout(2, 1));
        panelBotones.add(btnTomarFicha);
        panelBotones.add(btnTerminarTurno);

        uiGrupoMano.setTablero(uiTablero);
        uiTablero.setGrupoMano(uiGrupoMano);

        configurarBotones();

        jPanelContenedorPerfiles.setPreferredSize(new java.awt.Dimension(300, 800));
        uiJugadorOponente.setPreferredSize(new java.awt.Dimension(280, 150));
        uiJugadorActual.setPreferredSize(new java.awt.Dimension(280, 150));
        jPanelContenedorMano.setPreferredSize(new java.awt.Dimension(1400, 200));

        jPanelContenedorTablero.revalidate();
        jPanelContenedorTablero.repaint();
        jPanelContenedorMano.revalidate();
        jPanelContenedorMano.repaint();
        jPanelContenedorPerfiles.revalidate();
        jPanelContenedorPerfiles.repaint();
        
        uiJugadorOponente.setTamanoPreferido(280, 120);
        uiJugadorActual.setTamanoPreferido(280, 120);
        uiGrupoMano.setTamanoPreferido(1200, 150);
        uiTablero.setTamanoPreferido(1000, 600);

        // Ajustar el panel de perfiles para que ocupe menos espacio
        jPanelContenedorPerfiles.setPreferredSize(new java.awt.Dimension(200, 800));

        System.out.println("Vista reorganizada para: " + jugadorId);
        System.out.println("Oponente: " + oponenteId);
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
                case "PRIMER_BAJADA_30":
                    mostrarMensaje("Primer movimiento exitoso: has cumplido con los 30 puntos");
                    break;
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
        jPanelContenedorTablero = new javax.swing.JPanel();
        jPanelContenedorPerfiles = new javax.swing.JPanel();
        jPanelJugadorOponente = new javax.swing.JPanel();
        jPanelJugadorActual = new javax.swing.JPanel();
        panelBotones = new javax.swing.JPanel();
        btnTomarFicha = new javax.swing.JButton();
        btnTerminarTurno = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanelContenedorMano.setBackground(new java.awt.Color(39, 41, 72));
        jPanelContenedorMano.setOpaque(false);
        jPanelContenedorMano.setPreferredSize(new java.awt.Dimension(1418, 235));
        jPanelContenedorMano.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        getContentPane().add(jPanelContenedorMano, java.awt.BorderLayout.SOUTH);

        jPanelContenedorTablero.setOpaque(false);

        javax.swing.GroupLayout jPanelJugadorOponenteLayout = new javax.swing.GroupLayout(jPanelJugadorOponente);
        jPanelJugadorOponente.setLayout(jPanelJugadorOponenteLayout);
        jPanelJugadorOponenteLayout.setHorizontalGroup(
            jPanelJugadorOponenteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 264, Short.MAX_VALUE)
        );
        jPanelJugadorOponenteLayout.setVerticalGroup(
            jPanelJugadorOponenteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 201, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanelJugadorActualLayout = new javax.swing.GroupLayout(jPanelJugadorActual);
        jPanelJugadorActual.setLayout(jPanelJugadorActualLayout);
        jPanelJugadorActualLayout.setHorizontalGroup(
            jPanelJugadorActualLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanelJugadorActualLayout.setVerticalGroup(
            jPanelJugadorActualLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 201, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanelContenedorPerfilesLayout = new javax.swing.GroupLayout(jPanelContenedorPerfiles);
        jPanelContenedorPerfiles.setLayout(jPanelContenedorPerfilesLayout);
        jPanelContenedorPerfilesLayout.setHorizontalGroup(
            jPanelContenedorPerfilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelJugadorActual, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanelContenedorPerfilesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelJugadorOponente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelContenedorPerfilesLayout.setVerticalGroup(
            jPanelContenedorPerfilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelContenedorPerfilesLayout.createSequentialGroup()
                .addComponent(jPanelJugadorActual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 964, Short.MAX_VALUE)
                .addComponent(jPanelJugadorOponente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanelContenedorTableroLayout = new javax.swing.GroupLayout(jPanelContenedorTablero);
        jPanelContenedorTablero.setLayout(jPanelContenedorTableroLayout);
        jPanelContenedorTableroLayout.setHorizontalGroup(
            jPanelContenedorTableroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelContenedorTableroLayout.createSequentialGroup()
                .addComponent(jPanelContenedorPerfiles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(3011, Short.MAX_VALUE))
        );
        jPanelContenedorTableroLayout.setVerticalGroup(
            jPanelContenedorTableroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelContenedorTableroLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelContenedorPerfiles, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        getContentPane().add(jPanelContenedorTablero, java.awt.BorderLayout.CENTER);

        panelBotones.setOpaque(false);
        panelBotones.setLayout(new java.awt.GridLayout(2, 1));

        btnTomarFicha.setText("TOMAR FICHA");
        btnTomarFicha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTomarFichaActionPerformed(evt);
            }
        });
        panelBotones.add(btnTomarFicha);

        btnTerminarTurno.setText("TERMINAR TURNO");
        panelBotones.add(btnTerminarTurno);

        getContentPane().add(panelBotones, java.awt.BorderLayout.EAST);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnTomarFichaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTomarFichaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnTomarFichaActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnTerminarTurno;
    private javax.swing.JButton btnTomarFicha;
    private javax.swing.JPanel jPanelContenedorMano;
    private javax.swing.JPanel jPanelContenedorPerfiles;
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


