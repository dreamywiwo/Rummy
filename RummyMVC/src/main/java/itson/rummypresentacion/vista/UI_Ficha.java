package itson.rummypresentacion.vista;

import itson.rummydtos.FichaDTO;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceAdapter;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

public class UI_Ficha extends JLabel {
    private FichaDTO ficha;
    private ContenedorFichas contenedor;
    private static final int RADIO_BORDE = 15;
    
    public UI_Ficha(FichaDTO ficha) {
        this.ficha = ficha;
        this.contenedor = null;
        configurarComponente();
    }
    
    public UI_Ficha(FichaDTO ficha, ContenedorFichas contenedor) {
        this.ficha = ficha;
        this.contenedor = contenedor;
        configurarComponente();
        habilitarDragAndDrop();
    }
    
    private void configurarComponente() {
        setOpaque(false); 
        setHorizontalAlignment(CENTER);
        setVerticalAlignment(CENTER);
        setPreferredSize(new Dimension(60, 80));
        setBorder(new EmptyBorder(5, 5, 5, 5));
        actualizarVisual();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        
        g2d.setColor(new Color(0, 0, 0, 30));
        g2d.fill(new RoundRectangle2D.Float(3, 3, width - 3, height - 3, RADIO_BORDE, RADIO_BORDE));
        
        g2d.setColor(obtenerColorFondo());
        g2d.fill(new RoundRectangle2D.Float(0, 0, width - 3, height - 3, RADIO_BORDE, RADIO_BORDE));
        
        g2d.setColor(obtenerColorBorde());
        g2d.setStroke(new java.awt.BasicStroke(2));
        g2d.draw(new RoundRectangle2D.Float(1, 1, width - 4, height - 4, RADIO_BORDE, RADIO_BORDE));
        
        g2d.dispose();
        
        super.paintComponent(g);
    }
    
    private void habilitarDragAndDrop() {
        DragSource ds = new DragSource();
        ds.createDefaultDragGestureRecognizer(
            this,
            DnDConstants.ACTION_MOVE,
            new DragGestureListener() {
                @Override
                public void dragGestureRecognized(DragGestureEvent dge) {
                    
                    FichaTransferable transferable = new FichaTransferable(ficha, contenedor);
                    dge.startDrag(
                        Cursor.getPredefinedCursor(Cursor.HAND_CURSOR),
                        transferable,
                        new DragSourceAdapter() {}
                    );
                }
            }
        );
    }
    
    public void setContenedor(ContenedorFichas contenedor) {
        this.contenedor = contenedor;
        if (contenedor != null) {
            habilitarDragAndDrop();
        }
    }
    
    private void actualizarVisual() {
        if (ficha.isEsComodin()) {
            setText("O");
            setFont(new Font("Arial", Font.BOLD, 36));
            setForeground(new Color(139, 69, 19));
        } else {
            setText(String.valueOf(ficha.getNumero()));
            setFont(new Font("Arial", Font.BOLD, 32));
            setForeground(obtenerColorTexto());
        }
        setToolTipText(ficha.toString());
    }
    
    private Color obtenerColorFondo() {
        if (ficha.isEsComodin()) {
            return new Color(255, 228, 181); 
        }
        
        switch (ficha.getColor().toUpperCase()) {
            case "ROJO":
                return new Color(255, 250, 240); 
            case "AZUL":
                return new Color(245, 248, 255); 
            case "AMARILLO":
                return new Color(255, 253, 240); 
            case "NEGRO":
                return new Color(250, 250, 245); 
            default:
                return new Color(255, 253, 240);
        }
    }
    
    private Color obtenerColorTexto() {
        switch (ficha.getColor().toUpperCase()) {
            case "ROJO":
                return new Color(220, 140, 90); 
            case "AZUL":
                return new Color(100, 149, 237);
            case "AMARILLO":
                return new Color(218, 165, 32); 
            case "NEGRO":
                return new Color(105, 105, 105);
            default:
                return new Color(139, 69, 19);
        }
    }
    
    private Color obtenerColorBorde() {
        switch (ficha.getColor().toUpperCase()) {
            case "ROJO":
                return new Color(220, 140, 90, 150);
            case "AZUL":
                return new Color(100, 149, 237, 150);
            case "AMARILLO":
                return new Color(218, 165, 32, 150);
            case "NEGRO":
                return new Color(105, 105, 105, 150);
            default:
                return new Color(200, 200, 200, 150);
        }
    }
    
    public FichaDTO getFicha() {
        return ficha;
    }
}