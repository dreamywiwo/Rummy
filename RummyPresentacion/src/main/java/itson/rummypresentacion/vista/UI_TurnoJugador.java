/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package itson.rummypresentacion.vista;

import itson.rummypresentacion.controlador.ControlTurno;
import itson.rummypresentacion.modelo.IModelo;
import itson.rummypresentacion.modelo.IObserver;
import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class UI_TurnoJugador extends javax.swing.JFrame implements IComponente, IObserver {

    private String id = "root";
    private List<IComponente> componentes;
    private UI_Grupo uiGrupoMano;
    private UI_Tablero uiTablero;
    private ControlTurno control;
    private String jugadorId;
    private javax.swing.JComponent glassBlocker;

    public UI_TurnoJugador(String jugadorId) {
        this.jugadorId = jugadorId;
        this.componentes = new ArrayList<>();
        initComponents();
        inicializarComponentesVisuales();
        bloquearJugador();
    }

    public void registrarEnModelo(Observable modelo) {
        modelo.addObserver(new java.util.Observer() {
            @Override
            public void update(Observable o, Object arg) {
                if (arg instanceof IModelo) {
                    UI_TurnoJugador.this.update((IModelo) arg);
                }
            }
        });
        System.out.println("UI_TurnoJugador registrado como observer del modelo");
    }

    @Override
    public void update(IModelo modelo) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("UI_TurnoJugador recibió actualización del modelo");
            actualizar(modelo);
            actualizarInformacionTurno(modelo);
            actualizarEstadoBotones(modelo);
        });
    }

    private void inicializarComponentesVisuales() {
        uiGrupoMano = new UI_Grupo("mano_jugador");
        uiTablero = new UI_Tablero("tablero_principal");

        // Agregar componentes al composite
        agregarComponente(uiGrupoMano);
        agregarComponente(uiTablero);

        jPanelContenedorMano.setLayout(new BorderLayout());
        jPanelContenedorTablero.setLayout(new BorderLayout());

        jPanelContenedorMano.add(uiGrupoMano, BorderLayout.CENTER);
        jPanelContenedorTablero.add(uiTablero, BorderLayout.CENTER);

        uiGrupoMano.setTablero(uiTablero);
        uiTablero.setGrupoMano(uiGrupoMano);

        configurarBotones();
    }

    //TODO: 
    private void configurarBotones() {
        btnTerminarTurno.addActionListener(e -> {
            try {
                control.terminarTurno(jugadorId);
                mostrarMensaje("El jugador: " + jugadorId + " finalizo su turno");
            } catch (Exception ex) {
                mostrarMensaje("Error al terminar turno: " + ex.getMessage());
            }
        });
        btnTomarFicha.addActionListener(e -> {
            try {
                control.tomarFicha(jugadorId);
                mostrarMensaje("Ficha agregada a la mano");
            } catch (Exception ex) {
                mostrarMensaje("Error al tomar ficha: " + ex.getMessage());
            }
        });
    }

    // Implementación del método actualizar del Composite
    @Override
    public void actualizar(IModelo modelo) {
        try {
            System.out.println("Propagando actualización a componentes hijos...");

            // 1. Primero actualizar el estado específico de UI_TurnoJugador
            actualizarEstadoBotones(modelo);
            actualizarInformacionTurno(modelo);

            // 2. Luego propagar la actualización a todos los componentes hijos
            // Esto automáticamente actualizará UI_Grupo y UI_Tablero
            for (IComponente componente : componentes) {
                componente.actualizar(modelo);
            }

            // actualizar este componente
            this.revalidate();
            this.repaint();

            System.out.println("Actualización completada para " + componentes.size() + " componentes");

        } catch (Exception ex) {
            System.err.println("Error en actualización composite: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    //TODO:
    private void actualizarEstadoBotones(IModelo modelo) {
        boolean esMiTurno = modelo.esTurnoDelJugador(jugadorId);
        btnTerminarTurno.setEnabled(esMiTurno);
        btnTomarFicha.setEnabled(esMiTurno);
        if (esMiTurno) {
            btnTerminarTurno.setText("TERMINAR TURNO");
            btnTomarFicha.setText("TOMAR FICHA");
        } else {
            btnTerminarTurno.setText("ESPERANDO...");
            btnTomarFicha.setText("ESPERANDO...");
        }
        if (uiTablero != null) {
            uiTablero.setEnabled(esMiTurno);
        }
        if (uiGrupoMano != null) {
            uiGrupoMano.setEnabled(esMiTurno);
        }
        habilitar(esMiTurno);
    }

    //TOOD:
    private void actualizarInformacionTurno(IModelo modelo) {
        try {
            String jugadorActual = modelo.getJugadorActualId();
            setTitle("Turno de: " + jugadorActual);
        } catch (Exception e) {
            System.err.println("Error obteniendo jugador actual: " + e.getMessage());
        }
    }

    public void mostrarMensaje(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }

    public void setControlador(ControlTurno control) {
        this.control = control;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnTomarFicha = new javax.swing.JButton();
        btnTerminarTurno = new javax.swing.JButton();
        jPanelContenedorMano = new javax.swing.JPanel();
        jPanelContenedorTablero = new javax.swing.JPanel();
        jLabelFondo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnTomarFicha.setText("TOMAR FICHA");
        btnTomarFicha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTomarFichaActionPerformed(evt);
            }
        });
        getContentPane().add(btnTomarFicha, new org.netbeans.lib.awtextra.AbsoluteConstraints(1610, 1010, 100, 80));

        btnTerminarTurno.setText("TERMINAR TURNO");
        getContentPane().add(btnTerminarTurno, new org.netbeans.lib.awtextra.AbsoluteConstraints(1610, 910, 100, 80));

        jPanelContenedorMano.setBackground(new java.awt.Color(39, 41, 72));
        jPanelContenedorMano.setPreferredSize(new java.awt.Dimension(1418, 235));
        jPanelContenedorMano.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        getContentPane().add(jPanelContenedorMano, new org.netbeans.lib.awtextra.AbsoluteConstraints(138, 905, 1450, 190));

        javax.swing.GroupLayout jPanelContenedorTableroLayout = new javax.swing.GroupLayout(jPanelContenedorTablero);
        jPanelContenedorTablero.setLayout(jPanelContenedorTableroLayout);
        jPanelContenedorTableroLayout.setHorizontalGroup(
            jPanelContenedorTableroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1230, Short.MAX_VALUE)
        );
        jPanelContenedorTableroLayout.setVerticalGroup(
            jPanelContenedorTableroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 660, Short.MAX_VALUE)
        );

        getContentPane().add(jPanelContenedorTablero, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 210, 1230, 660));

        jLabelFondo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI_TurnoJugador.png"))); // NOI18N
        getContentPane().add(jLabelFondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnTomarFichaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTomarFichaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnTomarFichaActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnTerminarTurno;
    private javax.swing.JButton btnTomarFicha;
    private javax.swing.JLabel jLabelFondo;
    private javax.swing.JPanel jPanelContenedorMano;
    private javax.swing.JPanel jPanelContenedorTablero;
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

    // PRUEBAS
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
}
