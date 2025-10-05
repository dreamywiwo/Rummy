/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.rummypresentacion.vista;

import itson.rummypresentacion.modelo.IModelo;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JToggleButton;

public class UI_Ficha extends JToggleButton implements IComponente{
    private int numero;
    private Color color;
    private boolean puedeSerMovida = true;
    private String id;
    
    public UI_Ficha(int numero, Color color) {
        this.numero = numero;
        this.color = color;
        
        setPreferredSize(new Dimension(60, 90));
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
    }
    
    public void setPuedeSerMovida(boolean puedeSerMovida) {
        this.puedeSerMovida = puedeSerMovida;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (!puedeSerMovida) {
            g2.setColor(new Color(240, 240, 240));
        } else {
            g2.setColor(Color.WHITE);
        }
        g2.fillRoundRect(2, 2, getWidth()-4, getHeight()-4, 12, 12);

        if (isSelected()) {
            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke(3f));
        } else if (!puedeSerMovida) {
            g2.setColor(Color.DARK_GRAY);
            g2.setStroke(new BasicStroke(1f));
        } else {
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2f));
        }
        g2.drawRoundRect(2, 2, getWidth()-4, getHeight()-4, 12, 12);

        g2.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fm = g2.getFontMetrics();
        String text = String.valueOf(numero);
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();
        
        g2.setColor(puedeSerMovida ? color : new Color(
            color.getRed(), color.getGreen(), color.getBlue(), 150
        ));
        g2.drawString(text, (getWidth() - textWidth)/2, (getHeight() + textHeight)/2 - 8);
        
        g2.dispose();
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public int getNumero() {
        return numero;
    }
    
    public Color getFichaColor() {
        return color;
    }

    /* Se definen los metodos de IComponente */
    @Override
    public void mostrar() {
        setVisible(true);
    }

    @Override
    public void ocultar() {
        setVisible(false);
    }

    @Override
    public void actualizar(IModelo modelo) {
        // Las fichas individuales no necesitan logica compleja de actualización
        // Solo repintarse si es necesario
        this.repaint();
    }

    @Override
    public void agregarComponente(IComponente componente) {
        throw new UnsupportedOperationException("UI_Ficha no puede contener componentes hijos");
    }

    @Override
    public void removerComponente(IComponente componente) {
        throw new UnsupportedOperationException("UI_Ficha no puede contener componentes hijos");
    }

    @Override
    public IComponente getComponente(int index) {
        throw new UnsupportedOperationException("UI_Ficha no puede contener componentes hijos");
    }

    @Override
    public List<IComponente> getComponentes() {
        return new ArrayList<>(); 
    }

    @Override
    public String getId() {
        return "ficha_" + numero + "_" + color.toString();
    }
}
