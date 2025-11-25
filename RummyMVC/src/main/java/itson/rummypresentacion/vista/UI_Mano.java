package itson.rummypresentacion.vista;

import itson.rummydtos.FichaDTO;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class UI_Mano extends JPanel {

    private Mano mano;
    private UI_Tablero tableroPanel;

    // Lista visual de lo que el usuario ha seleccionado con clic
    private List<UI_Ficha> fichasSeleccionadasVisualmente = new ArrayList<>();

    public UI_Mano(Mano mano, UI_Tablero tableroPanel) {
        this.mano = mano;
        this.tableroPanel = tableroPanel;

        configurarPanel();
        configurarDropTarget();
        actualizarFichas();
    }

    private void configurarPanel() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 5, 10));
        setOpaque(false);
        setPreferredSize(new Dimension(0, 130));
    }

    /**
     * Llamado por UI_Ficha cuando recibe un clic
     */
    public void notificarSeleccion(UI_Ficha fichaUI) {
        if (fichaUI.isSeleccionada()) {
            if (!fichasSeleccionadasVisualmente.contains(fichaUI)) {
                fichasSeleccionadasVisualmente.add(fichaUI);
            }
        } else {
            fichasSeleccionadasVisualmente.remove(fichaUI);
        }
        System.out.println("Seleccionadas: " + fichasSeleccionadasVisualmente.size());
    }

    /**
     * Llamado cuando empieza el arrastre. Decide si mover una o todas.
     */
    public List<FichaDTO> obtenerFichasParaMover(UI_Ficha fichaIniciadora) {
        List<FichaDTO> dtosAMover = new ArrayList<>();

        if (fichaIniciadora.isSeleccionada()) {
            for (UI_Ficha ui : fichasSeleccionadasVisualmente) {
                dtosAMover.add(ui.getFicha());
            }
        } 
        else {
            limpiarSeleccion();
            dtosAMover.add(fichaIniciadora.getFicha());
            // Opcional: Si quieres que se vea seleccionada mientras arrastras:
            fichaIniciadora.setSeleccionada(true);
            fichasSeleccionadasVisualmente.add(fichaIniciadora);
        }

        return dtosAMover;
    }

    public void limpiarSeleccion() {
        for (UI_Ficha ui : fichasSeleccionadasVisualmente) {
            ui.setSeleccionada(false);
        }
        fichasSeleccionadasVisualmente.clear();
    }

    private void configurarDropTarget() {
        setDropTarget(new DropTarget() {
            @Override
            public synchronized void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_MOVE);

                    Transferable t = evt.getTransferable();
                    FichaTransferable fichaT = (FichaTransferable) t.getTransferData(FichaTransferable.FICHA_FLAVOR);

                    List<FichaDTO> fichas = fichaT.getFichas();
                    ContenedorFichas origen = fichaT.getOrigen();

                    if (origen == mano) {
                        evt.dropComplete(false);
                        return;
                    }

                    for (FichaDTO f : fichas) {
                        if (origen != null) {
                            origen.remover(f);
                        }
                        mano.agregar(f);
                    }

                    actualizarFichas();

                    if (origen != null) {
                        actualizarOrigenEnTablero(origen);
                    }

                    // Notificaciones al socket
                    if (tableroPanel != null && tableroPanel.getVentanaPrincipal() != null) {
                        // TODO código de notificación
                    }

                    evt.dropComplete(true);

                } catch (Exception ex) {
                    evt.dropComplete(false);
                    ex.printStackTrace();
                }
            }

            @Override
            public synchronized void dragOver(DropTargetDragEvent dtde) {
                setBorder(BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(200, 150, 50), 3),
                        "Tu Mano - Soltar aquí"
                ));
            }

            @Override
            public synchronized void dragExit(DropTargetEvent dte) {
                setBorder(BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(101, 67, 33), 3),
                        "Tu Mano (" + mano.size() + " fichas)"
                ));
            }
        });
    }

    private void actualizarOrigenEnTablero(ContenedorFichas origen) {
        if (origen instanceof Grupo) {
            for (Component comp : tableroPanel.getComponents()) {
                if (comp instanceof UI_Grupo) {
                    UI_Grupo uiGrupo = (UI_Grupo) comp;
                    if (uiGrupo.getGrupo() == origen) {
                        if (origen.size() == 0) {
                            tableroPanel.remove(uiGrupo);
                            tableroPanel.getTablero().getGrupos().remove(origen);
                        } else {
                            uiGrupo.actualizarFichas();
                        }
                        tableroPanel.repaint();
                        return;
                    }
                }
            }
        }
    }

    public void actualizarFichas() {
        fichasSeleccionadasVisualmente.clear();
        removeAll();
        mano.ordenar();

        for (FichaDTO ficha : mano.getFichas()) {
            UI_Ficha fichaComp = new UI_Ficha(ficha, mano);
            add(fichaComp);
        }

        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(101, 67, 33), 3),
                "Tu Mano (" + mano.size() + " fichas)"
        ));

        revalidate();
        repaint();
    }

    public Mano getMano() {
        return mano;
    }
}
