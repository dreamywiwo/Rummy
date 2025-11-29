/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.rummypresentacion.vista;

import itson.rummydtos.FichaDTO;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;

/**
 *
 * @author Dana Chavez
 */
public class UI_Grupo extends JPanel {

    private final String idGrupo;
    private final int numeroVisual;
    private final List<FichaDTO> fichas = new ArrayList<>();
    private UI_Tablero tablero;

    private Border bordeNormal;
    private Border bordeHover;

    // Tamaños y Configuración
    private static final int ANCHO_FICHA = 60;
    private static final int ALTO_FICHA = 90;
    private static final int GAP_H = 5;
    private static final int MARGEN_INTERNO = 10;

    // Colores de estado
    private static final Color COLOR_VALIDO = new Color(40, 180, 60);   // Verde
    private static final Color COLOR_INVALIDO = new Color(220, 60, 60); // Rojo
    private static final Color COLOR_NEUTRO = new Color(40, 110, 220);  // Azul

    public UI_Grupo(String idGrupo, List<FichaDTO> fichasIniciales, UI_Tablero tablero) {
        super();
        this.idGrupo = idGrupo;
        this.tablero = tablero;
        this.numeroVisual = extraerNumeroEstable(idGrupo);

        if (fichasIniciales != null) {
            fichas.addAll(fichasIniciales);
        }

        configurarPanel();
        crearBordes();
        actualizarBordes();
        ordenar();
        refrescar();
        configurarDropTarget();
    }

    private void configurarPanel() {
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.LEFT, GAP_H, 0));
        setBackground(new Color(240, 240, 255, 50));
        setDoubleBuffered(true);
        putClientProperty("Panel.isOptimizedDrawingEnabled", Boolean.TRUE);
    }

    private int extraerNumeroEstable(String id) {
        try {
            if (id != null && id.contains("_")) {
                String n = id.substring(id.lastIndexOf("_") + 1);
                return Integer.parseInt(n) + 1;
            }
            return (Math.abs(id.hashCode()) % 100) + 1;
        } catch (Exception e) {
            return 1;
        }
    }

    private void crearBordes() {
        String titulo = "Grupo " + numeroVisual;
        Border padding = BorderFactory.createEmptyBorder(25, MARGEN_INTERNO, MARGEN_INTERNO, MARGEN_INTERNO);

        TitledBorder borderTituloHover = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 160, 255), 3),
                titulo
        );
        borderTituloHover.setTitleColor(new Color(20, 60, 130));
        borderTituloHover.setTitleFont(new Font("Arial", Font.BOLD, 12));
        borderTituloHover.setTitleJustification(TitledBorder.LEFT);
        bordeHover = new CompoundBorder(borderTituloHover, padding);
    }

    private void actualizarBordes() {
        boolean esValido = validarGrupo();

        Color colorBorde;
        if (fichas.isEmpty()) {
            colorBorde = COLOR_NEUTRO;
        } else if (esValido) {
            colorBorde = COLOR_VALIDO;
        } else {
            colorBorde = COLOR_INVALIDO;
        }

        String titulo = "Grupo " + numeroVisual + (esValido ? " (OK)" : "");

        TitledBorder borderTitulo = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(colorBorde, 2),
                titulo
        );
        borderTitulo.setTitleColor(colorBorde.darker());
        borderTitulo.setTitleFont(new Font("Arial", Font.BOLD, 12));
        borderTitulo.setTitleJustification(TitledBorder.LEFT);

        Border padding = BorderFactory.createEmptyBorder(25, MARGEN_INTERNO, MARGEN_INTERNO, MARGEN_INTERNO);
        bordeNormal = new CompoundBorder(borderTitulo, padding);

        setBorder(bordeNormal);
    }

    // ==========================================
    // VALIDACIÓN VISUAL
    // ==========================================
    public boolean validarGrupo() {
        if (fichas.size() < 3) {
            return false;
        }
        return esSetValido() || esSecuenciaValida();
    }

    private boolean esSetValido() {
        // Regla: Mismo número, colores distintos
        if (fichas.isEmpty()) {
            return true;
        }

        List<FichaDTO> normales = new ArrayList<>();
        for (FichaDTO f : fichas) {
            if (f != null && !f.isEsComodin()) {
                normales.add(f);
            }
        }

        if (normales.isEmpty()) {
            return true;
        }

        int numeroRef = normales.get(0).getNumero();
        Set<String> coloresVistos = new HashSet<>();

        for (FichaDTO f : normales) {
            if (f.getNumero() != numeroRef) {
                return false;
            }
            if (f.getColor() != null && !coloresVistos.add(f.getColor())) {
                return false;
            }
        }
        return true;
    }

    private boolean esSecuenciaValida() {
        if (fichas.isEmpty()) {
            return true;
        }

        List<FichaDTO> normales = new ArrayList<>();
        int comodines = 0;
        for (FichaDTO f : fichas) {
            if (f == null) {
                continue;
            }
            if (f.isEsComodin()) {
                comodines++;
            } else {
                normales.add(f);
            }
        }

        if (normales.isEmpty()) {
            return true;
        }

        String colorRef = normales.get(0).getColor();
        for (FichaDTO f : normales) {
            if (f.getColor() == null || !f.getColor().equals(colorRef)) {
                return false;
            }
        }

        List<FichaDTO> ordenadas = new ArrayList<>(normales);
        Collections.sort(ordenadas, (a, b) -> Integer.compare(a.getNumero(), b.getNumero()));

        for (int i = 0; i < ordenadas.size() - 1; i++) {
            int actual = ordenadas.get(i).getNumero();
            int siguiente = ordenadas.get(i + 1).getNumero();
            int hueco = siguiente - actual - 1;

            if (hueco < 0) {
                return false;
            }
            if (hueco > 0) {
                if (comodines >= hueco) {
                    comodines -= hueco;
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    // ==========================================
    // UI Y DROP TARGET (BLINDADO)
    // ==========================================
    private void configurarDropTarget() {
        setDropTarget(new DropTarget(this, new DropTargetAdapter() {

            @Override
            public void dragEnter(DropTargetDragEvent dtde) {
                dtde.acceptDrag(DnDConstants.ACTION_MOVE);
                setBorder(bordeHover);
                repaint();
            }

            @Override
            public void dragOver(DropTargetDragEvent dtde) {
                dtde.acceptDrag(DnDConstants.ACTION_MOVE);
                setBorder(bordeHover);
                repaint();
            }

            @Override
            public void dragExit(DropTargetEvent dte) {
                setBorder(bordeNormal);
                repaint();
            }

            @Override
            public void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_MOVE);
                    Transferable t = evt.getTransferable();

                    if (!t.isDataFlavorSupported(FichaTransferable.FICHA_FLAVOR)) {
                        evt.dropComplete(false);
                        return;
                    }

                    FichaTransferable fichaT = (FichaTransferable) t.getTransferData(FichaTransferable.FICHA_FLAVOR);
                    List<FichaDTO> recibidas = fichaT.getFichas();

                    if (recibidas == null || recibidas.isEmpty()) {
                        evt.dropComplete(false);
                        return;
                    }

                    // 1. Limpiar del origen visualmente (Mano o Tablero)
                    if (tablero != null) {
                        tablero.removerFichasDeOtrosContenedores(recibidas, UI_Grupo.this);
                    }

                    // 2. Agregar al grupo local (BLINDADO contra nulos)
                    Set<String> misIds = new HashSet<>();
                    for (FichaDTO f : fichas) {
                        if (f.getId() != null) {
                            misIds.add(f.getId());
                        }
                    }

                    boolean huboCambios = false;
                    for (FichaDTO f : recibidas) {
                        // Si la ficha es válida y no está ya en el grupo por ID
                        if (f != null && f.getId() != null && !misIds.contains(f.getId())) {
                            fichas.add(f);
                            huboCambios = true;
                        }
                    }

                    if (huboCambios) {
                        try {
                            ordenar(); // Intentar ordenar
                        } catch (Exception e) {
                            System.err.println("Error ordenando fichas en drop: " + e.getMessage());
                        }

                        refrescar(); // Actualizar visualmente SIEMPRE

                        // Notificar al negocio
                        if (tablero != null && tablero.getVentanaPrincipal() != null) {
                            try {
                                tablero.getVentanaPrincipal()
                                        .notificarGrupoActualizado(idGrupo, getFichas());
                            } catch (Exception ex) {
                                System.err.println("Error notificando actualización: " + ex.getMessage());
                            }
                        }
                    }

                    evt.dropComplete(true);

                } catch (Exception ex) {
                    System.err.println("Error crítico en Drop: " + ex.getMessage());
                    ex.printStackTrace();
                    evt.dropComplete(false);
                } finally {
                    actualizarBordes();
                    repaint();
                }
            }
        }));
    }

    @Override
    public Dimension getPreferredSize() {
        int n = fichas.size();
        if (n == 0) {
            n = 1;
        }
        int anchoContenido = (n * ANCHO_FICHA) + (Math.max(0, n - 1) * GAP_H);
        int anchoTotal = anchoContenido + (MARGEN_INTERNO * 2) + 20;
        int altoTotal = ALTO_FICHA + 40;
        return new Dimension(Math.max(160, anchoTotal), altoTotal);
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    public void refrescar() {
        removeAll();
        for (FichaDTO ficha : fichas) {
            if (ficha != null) {
                UI_Ficha uiFicha = new UI_Ficha(ficha);
                add(uiFicha);
            }
        }
        actualizarBordes();
        revalidate();
        repaint();
    }

    private void ordenar() {
        // Ordenamiento seguro contra nulos para evitar crashes en el drop
        fichas.sort((f1, f2) -> {
            if (f1 == null) {
                return 1;
            }
            if (f2 == null) {
                return -1;
            }

            if (f1.isEsComodin()) {
                return 1;
            }
            if (f2.isEsComodin()) {
                return -1;
            }

            int numCmp = Integer.compare(f1.getNumero(), f2.getNumero());
            if (numCmp != 0) {
                return numCmp;
            }

            String c1 = f1.getColor() != null ? f1.getColor() : "";
            String c2 = f2.getColor() != null ? f2.getColor() : "";
            return c1.compareTo(c2);
        });
    }

    // Getters y métodos públicos
    public String getIdGrupo() {
        return idGrupo;
    }

    public List<FichaDTO> getFichas() {
        return new ArrayList<>(fichas);
    }

    public void setFichas(List<FichaDTO> nuevas) {
        fichas.clear();
        if (nuevas != null) {
            fichas.addAll(nuevas);
        }
        ordenar();
        refrescar();
    }

    public void removerFichasPorId(Set<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        boolean cambio = fichas.removeIf(f -> f.getId() != null && ids.contains(f.getId()));
        if (cambio) {
            ordenar();
            refrescar();
        }
    }
}
