/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.rummypresentacion.vista;

import itson.rummydtos.FichaDTO;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 *
 * @author Dana Chavez
 */
public class UI_Grupo extends JPanel {

    private Grupo grupo;
    private int indiceGrupo;
    private UI_Tablero tableroPanel;

    public UI_Grupo(Grupo grupo, int indice, UI_Tablero tableroPanel) {
        this.grupo = grupo;
        this.indiceGrupo = indice;
        this.tableroPanel = tableroPanel;
        configurarPanel();
        configurarDropTarget();
        actualizarFichas();
    }

    private void configurarPanel() {
        setLayout(null);
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLUE, 2),
                "Grupo " + (indiceGrupo + 1)
        ));
        setBackground(new Color(240, 240, 255));
        setPreferredSize(new Dimension(700, 100));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
    }

    private void configurarDropTarget() {
        setDropTarget(new DropTarget() {
            @Override
            public synchronized void drop(DropTargetDropEvent evt) {
                if (!isEnabled()) {
                    evt.rejectDrop();
                    return;
                }
                try {
                    evt.acceptDrop(DnDConstants.ACTION_MOVE);

                    Transferable t = evt.getTransferable();

                    FichaTransferable fichaT = (FichaTransferable) t.getTransferData(FichaTransferable.FICHA_FLAVOR);

                    // 1. Obtenemos la LISTA de fichas, no solo una
                    List<FichaDTO> fichasRecibidas = fichaT.getFichas();
                    ContenedorFichas origen = fichaT.getOrigen();

                    if (origen == grupo) {
                        evt.dropComplete(false);
                        return;
                    }

                    String origenId = null;

                    // 2. Procesar remoción del origen para TODAS las fichas
                    if (origen != null) {
                        for (FichaDTO ficha : fichasRecibidas) {
                            origen.remover(ficha);
                        }

                        if (origen instanceof Grupo) {
                            origenId = ((Grupo) origen).getId();
                        } else if (origen instanceof Mano) {
                            origenId = "Mano";
                            // Si viene de la mano, limpiamos la selección visual
                            limpiarSeleccionMano();
                        }
                    }

                    // 3. Agregar todas las fichas a este grupo
                    for (FichaDTO ficha : fichasRecibidas) {
                        grupo.agregar(ficha);
                    }

                    // 4. Actualizar visualmente este grupo
                    actualizarFichas();

                    // 5. Actualizar visualmente el origen
                    if (origen != null) {
                        actualizarOrigenUI(origen);
                    }

                    // 6. Notificaciones al controlador 
                    if (tableroPanel != null) {
                        UI_TurnoJugador ventana = tableroPanel.getVentanaPrincipal();
                        if (ventana != null) {
                            // Notificar actualización del origen (Mano u otro Grupo)
                            if (origen != null && origenId != null) {
                                ventana.notificarGrupoActualizado(origenId, origen.getFichas());
                            }
                            ventana.notificarGrupoActualizado(grupo.getId(), grupo.getFichas());
                        }
                        tableroPanel.repaint();
                    }

                    evt.dropComplete(true);

                } catch (Exception ex) {
                    System.out.println("ERROR en drop: " + ex.getMessage());
                    evt.dropComplete(false);
                    ex.printStackTrace();
                }
            }

            @Override
            public synchronized void dragOver(DropTargetDragEvent dtde) {
                setBorder(BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Color.GREEN, 3),
                        "Grupo " + (indiceGrupo + 1) + " - Soltar aquí"
                ));
            }

            @Override
            public synchronized void dragExit(DropTargetEvent dte) {
                setBorder(BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Color.BLUE, 2),
                        "Grupo " + (indiceGrupo + 1) + " (" + grupo.size() + " fichas)"
                ));
            }
        });
    }

    private void limpiarSeleccionMano() {
        if (tableroPanel != null && tableroPanel.getVentanaPrincipal() != null) {
            UI_Mano uiMano = tableroPanel.getVentanaPrincipal().getUIMano();
            if (uiMano != null) {
                uiMano.limpiarSeleccion();
            }
        }
    }

    public void actualizarFichas() {
        removeAll();

        double escala = (tableroPanel != null) ? tableroPanel.getEscalaActual() : 1.0;

        int x = (int) (10 * escala);
        int y = (int) (20 * escala);
        int anchoFicha = (int) (60 * escala);
        int altoFicha = (int) (80 * escala);
        int espacio = (int) (5 * escala);

        grupo.ordenar();

        for (FichaDTO ficha : grupo.getFichas()) {
            UI_Ficha fichaComp = new UI_Ficha(ficha, grupo, escala);
            fichaComp.setBounds(x, y, anchoFicha, altoFicha);
            add(fichaComp);
            x += anchoFicha + espacio;
        }

        int nuevoAncho = x + (int) (10 * escala);
        int nuevoAlto = altoFicha + (int) (40 * escala);
        setPreferredSize(new Dimension(nuevoAncho, nuevoAlto));
        setSize(nuevoAncho, nuevoAlto);

        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLUE, 2),
                "Grupo " + (indiceGrupo + 1) + " (" + grupo.size() + " fichas)"
        ));

        revalidate();
        repaint();
    }

    private void actualizarOrigenUI(ContenedorFichas origen) {
        Container parent = this;
        while (parent != null) {
            buscarYActualizarEnContainer(parent, origen);
            parent = parent.getParent();
        }
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

    public Grupo getGrupo() {
        return grupo;
    }
}
