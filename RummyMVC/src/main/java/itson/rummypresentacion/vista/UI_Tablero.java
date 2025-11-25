/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package itson.rummypresentacion.vista;

import itson.rummydtos.FichaDTO;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class UI_Tablero extends javax.swing.JPanel {

    private final UI_TurnoJugador ventanaPrincipal;
    private Tablero tablero;
    private List<UI_Grupo> gruposPanels;
    private JPanel panelLibre;

    private double escalaActual = 1.0;
    private static final double ESCALA_MINIMA = 0.5;
    private static final double ESCALA_MAXIMA = 1.0;
    private static final int MARGEN_BORDE = 20;
    private static final double REDUCCION_ZOOM = 0.1;

    public UI_Tablero(Tablero tablero, UI_TurnoJugador ventanaPrincipal) {
        this.tablero = tablero;
        this.gruposPanels = new ArrayList<>();
        this.ventanaPrincipal = ventanaPrincipal;

        configurarPanel();
        inicializarComponentes();
        habilitarDragAndDrop();
    }

    private void configurarPanel() {
        setLayout(null);
        setOpaque(false);
        setPreferredSize(new Dimension(803, 448));
    }

    private void inicializarComponentes() {
        panelLibre = new JPanel(null);
        panelLibre.setOpaque(false);
        add(panelLibre);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        int centroX = getWidth() / 2;
        int centroY = getHeight() / 2;

        g2d.translate(centroX, centroY);
        g2d.scale(escalaActual, escalaActual);
        g2d.translate(-centroX, -centroY);
    }

    private void habilitarDragAndDrop() {
        new DropTarget(this, new DropTargetAdapter() {
            @Override
            public void drop(DropTargetDropEvent event) {
                try {
                    event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);

                    // Obtener datos
                    FichaTransferable ft = (FichaTransferable) event.getTransferable().getTransferData(FichaTransferable.FICHA_FLAVOR);
                    List<FichaDTO> fichas = ft.getFichas();
                    ContenedorFichas origen = ft.getOrigen();

                    if (fichas != null && !fichas.isEmpty()) {
                        // 1. Remover del origen (Mano o Grupo)
                        if (origen != null) {
                            for (FichaDTO f : fichas) {
                                origen.remover(f);
                            }
                            actualizarOrigenUI(origen);

                            // Si venía de la mano, limpiar la selección visual
                            if (origen instanceof Mano) {
                                // Necesitamos acceder a la UI_Mano para limpiar selección
                                // Puedes buscarla en los componentes o pasarla como referencia
                                buscarYLimpiarSeleccionMano();
                            }
                        }

                        // 2. Crear el grupo en el punto donde se soltó
                        Point puntoAjustado = convertirPuntoConEscala(event.getLocation());

                        // Modificamos crearGrupoEnPunto para aceptar lista
                        crearGrupoEnPunto(puntoAjustado, fichas);

                        event.dropComplete(true);
                    } else {
                        event.dropComplete(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    event.dropComplete(false);
                }
            }
        });
    }

    private void buscarYLimpiarSeleccionMano() {
        if (ventanaPrincipal != null && ventanaPrincipal.getUIMano() != null) {
            ventanaPrincipal.getUIMano().limpiarSeleccion();
        }
    }

    private Point convertirPuntoConEscala(Point puntoOriginal) {
        int centroX = getWidth() / 2;
        int centroY = getHeight() / 2;

        int x = (int) ((puntoOriginal.x - centroX) / escalaActual + centroX);
        int y = (int) ((puntoOriginal.y - centroY) / escalaActual + centroY);

        return new Point(x, y);
    }

    private void actualizarOrigenUI(ContenedorFichas origen) {
        Container parent = this.getParent();
        while (parent != null) {
            buscarYActualizarEnContainer(parent, origen);
            parent = parent.getParent();
        }

        buscarYActualizarEnContainer(this, origen);
    }

    private void buscarYActualizarEnContainer(Container container, ContenedorFichas origen) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof UI_Mano) {
                UI_Mano manoPanel = (UI_Mano) comp;
                if (manoPanel.getMano() == origen) {
                    manoPanel.actualizarFichas();
                    return;
                }
            } else if (comp instanceof UI_Grupo) {
                UI_Grupo grupoPanel = (UI_Grupo) comp;
                if (grupoPanel.getGrupo() == origen) {
                    grupoPanel.actualizarFichas();
                    return;
                }
            } else if (comp instanceof Container) {
                buscarYActualizarEnContainer((Container) comp, origen);
            }
        }
    }

    /**
     * Crea un grupo o agrega fichas a uno existente en el punto especificado.
     * Soporta recibir una lista de fichas para la multiselección.
     */
    private void crearGrupoEnPunto(Point punto, List<FichaDTO> fichas) {
        if (fichas == null || fichas.isEmpty()) {
            return;
        }

        // 1. Verificar si se soltó sobre o muy cerca de un grupo existente
        UI_Grupo grupoCercano = encontrarGrupoCercano(punto);

        if (grupoCercano != null) {
            // AGREGAR A GRUPO EXISTENTE
            for (FichaDTO f : fichas) {
                grupoCercano.getGrupo().agregar(f);
            }

            // Actualizar visualmente (esto redimensiona el panel automáticamente)
            grupoCercano.actualizarFichas();

            // Asegurar que el grupo no se salga del tablero al crecer
            Point posicionAjustada = ajustarPosicionDentroDelTablero(
                    grupoCercano.getLocation(),
                    grupoCercano.getWidth(),
                    grupoCercano.getHeight()
            );
            grupoCercano.setLocation(posicionAjustada);

            // Notificar al controlador que el grupo cambió
            if (ventanaPrincipal != null) {
                ventanaPrincipal.notificarGrupoActualizado(
                        grupoCercano.getGrupo().getId(),
                        grupoCercano.getGrupo().getFichas()
                );
            }

            ajustarZoomSiEsNecesario();
            repaint();
            return;
        }

        // 2. CREAR UN NUEVO GRUPO
        Grupo nuevoGrupo = new Grupo("grupo_" + tablero.getGrupos().size());
        tablero.agregarGrupo(nuevoGrupo);

        // Agregar todas las fichas al nuevo modelo
        for (FichaDTO f : fichas) {
            nuevoGrupo.agregar(f);
        }

        UI_Grupo grupoPanel = new UI_Grupo(nuevoGrupo, gruposPanels.size(), this);

        // Calcular dimensiones iniciales estimadas para calcular colisiones
        int anchoFicha = 60;
        int altoFicha = 80;
        int margen = 10;
        int espacio = 5;

        // El ancho depende de cuántas fichas estamos bajando
        int anchoGrupo = (fichas.size() * anchoFicha) + ((fichas.size() - 1) * espacio) + (margen * 2);
        int altoGrupo = altoFicha + (margen * 2) + 20; // +20 por el título del borde

        // Centrar el grupo en el mouse
        int x = punto.x - anchoGrupo / 2;
        int y = punto.y - altoGrupo / 2;
        Point posicionCentrada = new Point(x, y);

        // Ajustar para que no se salga de los bordes
        Point posicionFinal = ajustarPosicionDentroDelTablero(posicionCentrada, anchoGrupo, altoGrupo);

        // Lógica de anti-colisión (buscar espacio libre)
        int intentos = 0;
        int desplazamiento = 30;
        while (hayColision(posicionFinal, anchoGrupo, altoGrupo) && intentos < 8) {
            switch (intentos) {
                case 0:
                    posicionFinal.x += desplazamiento;
                    break;
                case 1:
                    posicionFinal.x -= desplazamiento * 2;
                    break;
                case 2:
                    posicionFinal.x += desplazamiento;
                    posicionFinal.y += desplazamiento;
                    break;
                case 3:
                    posicionFinal.y -= desplazamiento * 2;
                    break;
                case 4:
                    posicionFinal.x += desplazamiento;
                    break;
                case 5:
                    posicionFinal.x -= desplazamiento * 2;
                    break;
                case 6:
                    posicionFinal.x += desplazamiento;
                    posicionFinal.y += desplazamiento;
                    break;
                case 7:
                    posicionFinal.x -= desplazamiento;
                    break;
            }
            posicionFinal = ajustarPosicionDentroDelTablero(posicionFinal, anchoGrupo, altoGrupo);
            intentos++;
        }

        // Configurar y añadir el panel
        grupoPanel.setBounds(posicionFinal.x, posicionFinal.y, anchoGrupo, altoGrupo);
        grupoPanel.setBorder(new LineBorder(Color.WHITE, 2, true));
        grupoPanel.setOpaque(true);
        grupoPanel.setBackground(new Color(60, 160, 60));

        // UI_Grupo calcula su tamaño exacto al crearse, pero forzamos un update para asegurar
        grupoPanel.actualizarFichas();

        gruposPanels.add(grupoPanel);
        add(grupoPanel);

        // Notificar creación al controlador
        if (ventanaPrincipal != null) {
            ventanaPrincipal.notificarGrupoCreado(nuevoGrupo.getFichas());
        }

        ajustarZoomSiEsNecesario();
        repaint();
    }

    private Point ajustarPosicionDentroDelTablero(Point posicionOriginal, int anchoGrupo, int altoGrupo) {
        int x = posicionOriginal.x;
        int y = posicionOriginal.y;

        int anchoTablero = getWidth();
        int altoTablero = getHeight();

        if (x < MARGEN_BORDE) {
            x = MARGEN_BORDE;
        } else if (x + anchoGrupo > anchoTablero - MARGEN_BORDE) {
            x = anchoTablero - anchoGrupo - MARGEN_BORDE;
        }

        if (y < MARGEN_BORDE) {
            y = MARGEN_BORDE;
        } else if (y + altoGrupo > altoTablero - MARGEN_BORDE) {
            y = altoTablero - altoGrupo - MARGEN_BORDE;
        }

        return new Point(x, y);
    }

    private boolean hayColision(Point posicion, int ancho, int alto) {
        java.awt.Rectangle nuevaArea = new java.awt.Rectangle(posicion.x, posicion.y, ancho, alto);

        for (UI_Grupo grupo : gruposPanels) {
            java.awt.Rectangle areaGrupo = grupo.getBounds();
            areaGrupo.grow(10, 10);
            if (nuevaArea.intersects(areaGrupo)) {
                return true;
            }
        }

        return false;
    }

    private double calcularPorcentajeOcupacion() {
        if (gruposPanels.isEmpty()) {
            return 0.0;
        }

        double areaTablero = getWidth() * getHeight();

        double areaOcupada = 0;
        for (UI_Grupo grupo : gruposPanels) {
            areaOcupada += grupo.getWidth() * grupo.getHeight();
        }

        return (areaOcupada / areaTablero) * 100;
    }

    private void ajustarZoomSiEsNecesario() {
        double ocupacion = calcularPorcentajeOcupacion();

        if (ocupacion > 70 && escalaActual > ESCALA_MINIMA) {
            double nuevaEscala = escalaActual - REDUCCION_ZOOM;
            if (nuevaEscala < ESCALA_MINIMA) {
                nuevaEscala = ESCALA_MINIMA;
            }

            aplicarZoom(nuevaEscala);
        }
    }

    private void aplicarZoom(double nuevaEscala) {
        if (nuevaEscala < ESCALA_MINIMA || nuevaEscala > ESCALA_MAXIMA) {
            return;
        }

        escalaActual = nuevaEscala;

        revalidate();
        repaint();

    }

    public void hacerZoomOut() {
        double nuevaEscala = escalaActual - REDUCCION_ZOOM;
        if (nuevaEscala >= ESCALA_MINIMA) {
            aplicarZoom(nuevaEscala);
        } else {
        }
    }

    public void hacerZoomIn() {
        double nuevaEscala = escalaActual + REDUCCION_ZOOM;
        if (nuevaEscala <= ESCALA_MAXIMA) {
            aplicarZoom(nuevaEscala);
        } else {
        }
    }

    public void resetearZoom() {
        aplicarZoom(1.0);
    }

    public double getEscalaActual() {
        return escalaActual;
    }

    private UI_Grupo encontrarGrupoCercano(Point punto) {
        for (UI_Grupo grupoPanel : gruposPanels) {
            int gx = grupoPanel.getX();
            int gy = grupoPanel.getY();
            int gw = grupoPanel.getWidth();
            int gh = grupoPanel.getHeight();

            boolean cercaHorizontal = (punto.x >= gx - 40 && punto.x <= gx + gw + 40);
            boolean cercaVertical = (punto.y >= gy - 20 && punto.y <= gy + gh + 20);

            if (cercaHorizontal && cercaVertical) {
                return grupoPanel;
            }
        }
        return null;
    }

    private void ajustarTamanoGrupo(UI_Grupo grupoPanel) {
        int numFichas = grupoPanel.getGrupo().size();
        int anchoFicha = 60;
        int margen = 10;

        int nuevoAncho = (numFichas * anchoFicha) + margen * 2;
        int nuevoAlto = grupoPanel.getHeight();

        grupoPanel.setSize(new Dimension(nuevoAncho, nuevoAlto));
        grupoPanel.revalidate();
        grupoPanel.repaint();
    }

    public void actualizarGrupos() {
        removeAll();
        gruposPanels.clear();

        List<Grupo> grupos = tablero.getGrupos();
        for (int i = 0; i < grupos.size(); i++) {
            UI_Grupo grupoPanel = new UI_Grupo(grupos.get(i), i, this);
            grupoPanel.setBounds(50, 50 + (i * 100), 200, 80);
            gruposPanels.add(grupoPanel);
            add(grupoPanel);
        }

        revalidate();
        repaint();
    }

    public Tablero getTablero() {
        return tablero;
    }

    public UI_TurnoJugador getVentanaPrincipal() {
        return ventanaPrincipal;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setPreferredSize(new java.awt.Dimension(803, 448));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 803, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 448, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
