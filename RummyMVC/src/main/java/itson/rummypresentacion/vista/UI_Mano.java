package itson.rummypresentacion.vista;

import itson.rummydtos.FichaDTO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JPanel;

public class UI_Mano extends JPanel {

    private final List<FichaDTO> fichas = new ArrayList<>();
    private final List<UI_Ficha> fichasSeleccionadasVisualmente = new ArrayList<>();
    private final UI_Tablero tableroPanel;
    private boolean habilitado = true;
    private String titulo = "Tu Mano tiene 0 fichas";

    public UI_Mano(UI_Tablero tableroPanel) {
        this(tableroPanel, null);
    }

    public UI_Mano(UI_Tablero tableroPanel, List<FichaDTO> fichasIniciales) {
        this.tableroPanel = tableroPanel;

        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.LEFT, 8, 25));
        setPreferredSize(new Dimension(780, 110));

        if (fichasIniciales != null) {
            fichas.addAll(fichasIniciales);
        }

        configurarDropTarget();
        refrescar();
    }

    public void setFichas(List<FichaDTO> nuevas) {
        fichas.clear();
        if (nuevas != null) {
            fichas.addAll(nuevas);
        }
        ordenar();
        refrescar();
    }

    public List<FichaDTO> getFichas() {
        return new ArrayList<>(fichas);
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

    public List<FichaDTO> obtenerFichasParaMover(UI_Ficha fichaIniciadora) {
        List<FichaDTO> dtosAMover = new ArrayList<>();

        if (fichaIniciadora.isSeleccionada()) {
            for (UI_Ficha ui : fichasSeleccionadasVisualmente) {
                dtosAMover.add(ui.getFicha());
            }
        } else {
            limpiarSeleccion();
            dtosAMover.add(fichaIniciadora.getFicha());
            fichaIniciadora.setSeleccionada(true);
            fichasSeleccionadasVisualmente.add(fichaIniciadora);
        }

        return dtosAMover;
    }

    public void notificarSeleccion(UI_Ficha ficha) {
        if (ficha.isSeleccionada()) {
            if (!fichasSeleccionadasVisualmente.contains(ficha)) {
                fichasSeleccionadasVisualmente.add(ficha);
            }
        } else {
            fichasSeleccionadasVisualmente.remove(ficha);
        }
    }

    public void limpiarSeleccion() {
        for (UI_Ficha ui : fichasSeleccionadasVisualmente) {
            ui.setSeleccionada(false);
        }
        fichasSeleccionadasVisualmente.clear();
    }

    private void ordenar() {
        fichas.sort((f1, f2) -> {
            if (f1.isEsComodin() && !f2.isEsComodin()) {
                return 1;
            }
            if (!f1.isEsComodin() && f2.isEsComodin()) {
                return -1;
            }
            if (f1.isEsComodin() && f2.isEsComodin()) {
                return 0;
            }

            if (f1.getNumero() != f2.getNumero()) {
                return Integer.compare(f1.getNumero(), f2.getNumero());
            }
            return f1.getColor().compareTo(f2.getColor());
        });
    }

    public void refrescar() {
        removeAll();
        fichasSeleccionadasVisualmente.clear();

        for (FichaDTO dto : fichas) {
            UI_Ficha uiFicha = new UI_Ficha(dto);
            add(uiFicha);
        }

        actualizarTitulo();
        revalidate();
        repaint();
    }

    private void actualizarTitulo() {
        titulo = "Tu Mano (" + fichas.size() + " fichas)";
    }

    private void configurarDropTarget() {
        setDropTarget(new DropTarget() {
            @Override
            public synchronized void dragOver(DropTargetDragEvent dtde) {
                repaint();
            }

            @Override
            public synchronized void dragExit(DropTargetEvent dte) {
                repaint();
            }

            @Override
            public synchronized void drop(DropTargetDropEvent evt) {
                if (!habilitado) {
                    evt.rejectDrop();
                    return;
                }
                try {
                    evt.acceptDrop(DnDConstants.ACTION_MOVE);

                    Transferable t = evt.getTransferable();
                    FichaTransferable fichaT = (FichaTransferable) t.getTransferData(FichaTransferable.FICHA_FLAVOR);

                    List<FichaDTO> recibidas = fichaT.getFichas();
                    if (recibidas == null || recibidas.isEmpty()) {
                        evt.dropComplete(false);
                        return;
                    }

                    if (tableroPanel != null) {
                        tableroPanel.removerFichasDeOtrosContenedores(recibidas, UI_Mano.this);
                    }

                    Set<String> ids = new HashSet<>();
                    for (FichaDTO f : recibidas) {
                        if (f.getId() != null) {
                            ids.add(f.getId());
                        }
                    }
                    if (!ids.isEmpty()) {
                        fichas.removeIf(f -> f.getId() != null && ids.contains(f.getId()));
                    }

                    fichas.addAll(recibidas);
                    ordenar();
                    refrescar();

                    evt.dropComplete(true);

                } catch (Exception ex) {
                    ex.printStackTrace();
                    evt.dropComplete(false);
                } finally {
                    repaint();
                }
            }
        });
    }

    public void actualizarEstado(boolean esMiTurno) {
        this.habilitado = esMiTurno;
        if (!habilitado) {
            setBackground(new Color(200, 200, 200, 50));
        } else {
            setOpaque(false);
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        int padding = 6;
        int arc = 20;

        int x = padding;
        int y = padding;
        int w = getWidth() - padding * 2;
        int h = getHeight() - padding * 2;

        g2.setColor(new Color(255, 250, 240, 230));
        g2.fillRoundRect(x, y, w, h, arc, arc);

        g2.setColor(new Color(160, 130, 90, 200));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(x, y, w, h, arc, arc);
        g2.setColor(new Color(90, 70, 50));
        Font base = getFont();
        if (base == null) {
            base = new Font("SansSerif", Font.BOLD, 12);
        }
        g2.setFont(base.deriveFont(Font.BOLD, 12f));
        FontMetrics fm = g2.getFontMetrics();
        int textX = x + 12;
        int textY = y + fm.getAscent() + 4;
        g2.drawString(titulo, textX, textY);
        g2.dispose();
    }
}
