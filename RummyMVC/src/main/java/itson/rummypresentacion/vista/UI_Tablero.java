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
    private List<UI_Grupo> gruposVisuales;
    private JPanel panelLibre;

    private double escalaActual = 1.0;
    private static final double ESCALA_MINIMA = 0.5;
    private static final double ESCALA_MAXIMA = 1.0;
    private static final int MARGEN_BORDE = 20;
    private static final double REDUCCION_ZOOM = 0.1;

    public UI_Tablero(Tablero tablero, UI_TurnoJugador ventanaPrincipal) {
        this.tablero = tablero;
        this.gruposVisuales = new ArrayList<>();
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
        Graphics2D g2d = (Graphics2D) g.create();

        int centroX = getWidth() / 2;
        int centroY = getHeight() / 2;

        g2d.translate(centroX, centroY);
        g2d.scale(escalaActual, escalaActual);
        g2d.translate(-centroX, -centroY);

        super.paintComponent(g);

        g2d.dispose();
    }

    private void habilitarDragAndDrop() {
        new DropTarget(this, new DropTargetAdapter() {
            @Override
            public void drop(DropTargetDropEvent event) {
                try {
                    event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);

                    FichaTransferable ft = (FichaTransferable) event
                            .getTransferable()
                            .getTransferData(FichaTransferable.FICHA_FLAVOR);
                    List<FichaDTO> fichas = ft.getFichas();
                    ContenedorFichas origen = ft.getOrigen();

                    if (fichas == null || fichas.isEmpty()) {
                        event.dropComplete(false);
                        return;
                    }

                    if (origen instanceof Mano) {
                        buscarYLimpiarSeleccionMano();
                    }

                    Point puntoAjustado = convertirPuntoConEscala(event.getLocation());
                    crearGrupoEnPunto(puntoAjustado, fichas);

                    if (ventanaPrincipal != null) {
                        ventanaPrincipal.notificarGrupoCreado(fichas);
                    }

                    event.dropComplete(true);

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

        Grupo nuevoGrupo = new Grupo("temp_" + System.currentTimeMillis());
        for (FichaDTO f : fichas) {
            nuevoGrupo.agregar(f);
        }

        UI_Grupo grupoPanel = new UI_Grupo(nuevoGrupo, gruposVisuales.size(), this);

        int anchoFicha = (int) (60 * escalaActual);
        int espacio = (int) (5 * escalaActual);
        int margen = (int) (10 * escalaActual);
        int anchoGrupo = (fichas.size() * anchoFicha) + ((fichas.size() - 1) * espacio) + (margen * 2);
        int altoGrupo = (int) (80 * escalaActual) + (margen * 2) + 20;

        grupoPanel.setBounds(punto.x, punto.y, anchoGrupo, altoGrupo);
        grupoPanel.setBorder(new LineBorder(Color.WHITE, 2, true));
        grupoPanel.setOpaque(true);
        grupoPanel.setBackground(new Color(60, 160, 60));
        grupoPanel.actualizarFichas();

        gruposVisuales.add(grupoPanel);
        add(grupoPanel);

        if (ventanaPrincipal != null) {
            ventanaPrincipal.notificarGrupoCreado(fichas);
        }

        repaint();
    }

    private Point ajustarPosicionDentroDelTablero(Point posicionOriginal, int anchoGrupo, int altoGrupo) {
        int x = posicionOriginal.x;
        int y = posicionOriginal.y;

        int anchoLogico = (int) (getWidth() / escalaActual);
        int altoLogico = (int) (getHeight() / escalaActual);

        if (x < MARGEN_BORDE) {
            x = MARGEN_BORDE;
        } else if (x + anchoGrupo > anchoLogico - MARGEN_BORDE) {
            x = anchoLogico - anchoGrupo - MARGEN_BORDE;
        }

        if (y < MARGEN_BORDE) {
            y = MARGEN_BORDE;
        } else if (y + altoGrupo > altoLogico - MARGEN_BORDE) {
            y = altoLogico - altoGrupo - MARGEN_BORDE;
        }

        return new Point(x, y);
    }

    // Método base (usado para grupos nuevos que aún no están en la lista)
    private boolean hayColision(Point posicion, int ancho, int alto) {
        return hayColision(posicion, ancho, alto, null);
    }

    // Sobrecarga: permite ignorar un grupo específico 
    private boolean hayColision(Point posicion, int ancho, int alto, UI_Grupo grupoAIgnorar) {
        java.awt.Rectangle nuevaArea = new java.awt.Rectangle(posicion.x, posicion.y, ancho, alto);
        // Padding para que no queden pegados
        nuevaArea.grow(10, 10);

        for (UI_Grupo otroGrupo : gruposVisuales) {
            if (otroGrupo == grupoAIgnorar) {
                continue;
            }

            if (nuevaArea.intersects(otroGrupo.getBounds())) {
                return true;
            }
        }
        return false;
    }

    private double calcularPorcentajeOcupacion() {
        if (gruposVisuales.isEmpty()) {
            return 0.0;
        }

        double areaTablero = getWidth() * getHeight();

        double areaOcupada = 0;
        for (UI_Grupo grupo : gruposVisuales) {
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
        for (UI_Grupo grupoPanel : gruposVisuales) {
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

    /**
     * Busca una posición libre usando un algoritmo de espiral. Empieza en el
     * punto deseado y se aleja circularmente hasta encontrar hueco.
     */
    private Point buscarPosicionLibre(Point puntoDeseado, int ancho, int alto, UI_Grupo grupoAIgnorar) {
        Point pCandidato = ajustarPosicionDentroDelTablero(puntoDeseado, ancho, alto);

        if (!hayColision(pCandidato, ancho, alto, grupoAIgnorar)) {
            return pCandidato;
        }

        double angulo = 0;
        int radio = 10;
        int incrementoRadio = 5;
        int maxIteraciones = 800;

        for (int i = 0; i < maxIteraciones; i++) {
            int x = puntoDeseado.x + (int) (radio * Math.cos(angulo));
            int y = puntoDeseado.y + (int) (radio * Math.sin(angulo));

            pCandidato = new Point(x, y);
            pCandidato = ajustarPosicionDentroDelTablero(pCandidato, ancho, alto);

            if (!hayColision(pCandidato, ancho, alto, grupoAIgnorar)) {
                return pCandidato;
            }

            angulo += 0.5;
            if (angulo >= Math.PI * 2) {
                angulo = 0;
                radio += incrementoRadio;
            }
        }
        return null;
    }

    public void actualizarGrupos() {
        removeAll();
        gruposVisuales.clear();

        int anchoFichaBase = (int) (60 * escalaActual);
        int altoFichaBase = (int) (80 * escalaActual);
        int margenBase = (int) (10 * escalaActual);
        int espacioBase = (int) (5 * escalaActual);

        int xPos = 20;
        int yPos = 20;
        int alturaFila = 0;
        int anchoPanel = getWidth() > 0 ? getWidth() : 800;
        int espacioEntreGrupos = 20;

        List<Grupo> grupos = tablero.getGrupos();

        for (int i = 0; i < grupos.size(); i++) {
            Grupo grupoModelo = grupos.get(i);

            int numFichas = grupoModelo.getFichas().size();
            numFichas = Math.max(numFichas, 1);

            int anchoGrupo = (numFichas * anchoFichaBase) + ((numFichas - 1) * espacioBase) + (margenBase * 2);
            int altoGrupo = altoFichaBase + (margenBase * 2) + 20;

            UI_Grupo grupoPanel = new UI_Grupo(grupoModelo, i, this);

            if (xPos + anchoGrupo > anchoPanel) {
                xPos = 20;
                yPos += alturaFila + espacioEntreGrupos;
                alturaFila = 0;
            }

            grupoPanel.setBounds(xPos, yPos, anchoGrupo, altoGrupo);

            xPos += anchoGrupo + espacioEntreGrupos;
            alturaFila = Math.max(alturaFila, altoGrupo);

            gruposVisuales.add(grupoPanel);
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
