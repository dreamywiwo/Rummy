/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.rummypresentacion.vista;

import itson.rummydtos.FichaDTO;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceAdapter;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class UI_Mano extends JPanel {
    private Mano mano;
    private UI_Tablero tableroPanel;
    
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
    
    private void configurarDropTarget() {
        setDropTarget(new DropTarget() {
            @Override
            public synchronized void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_MOVE);
                    
                    Transferable t = evt.getTransferable();
                    FichaTransferable fichaT = (FichaTransferable) 
                        t.getTransferData(FichaTransferable.FICHA_FLAVOR);
                    
                    FichaDTO ficha = fichaT.getFicha();
                    ContenedorFichas origen = fichaT.getOrigen();
                    
                    if (origen == mano) {
                        evt.dropComplete(false);
                        return;
                    }

                    origen.remover(ficha);
                    mano.agregar(ficha);
                    
                    actualizarFichas();
                    tableroPanel.actualizarGrupos();
                    
                    evt.dropComplete(true);
                    System.out.println("Ficha " + ficha.getId() + " devuelta a la mano");
                    
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
    
    public void actualizarFichas() {
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
   
    private void configurarDragSource(UI_Ficha fichaComp) {
        DragSource ds = new DragSource();
        ds.createDefaultDragGestureRecognizer(
            fichaComp,
            DnDConstants.ACTION_MOVE,
            new DragGestureListener() {
                @Override
                public void dragGestureRecognized(DragGestureEvent dge) {
                    FichaDTO ficha = fichaComp.getFicha();
                    FichaTransferable transferable = 
                        new FichaTransferable(ficha, mano);
                    
                    dge.startDrag(
                        Cursor.getPredefinedCursor(Cursor.HAND_CURSOR),
                        transferable,
                        new DragSourceAdapter() {}
                    );
                }
            }
        );
    }
    
    public Mano getMano() {
        return mano;
    }
}