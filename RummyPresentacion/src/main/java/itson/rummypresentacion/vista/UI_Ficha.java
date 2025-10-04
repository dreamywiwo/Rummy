/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.rummypresentacion.vista;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JToggleButton;

public class UI_Ficha extends JToggleButton {
    private int numero;
    private Color color;
    private boolean puedeSerMovida = true;
    
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
        
        // Fondo
        if (!puedeSerMovida) {
            g2.setColor(new Color(240, 240, 240));
        } else {
            g2.setColor(Color.WHITE);
        }
        g2.fillRoundRect(2, 2, getWidth()-4, getHeight()-4, 12, 12);
        
        // Borde
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
        
        // Número
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fm = g2.getFontMetrics();
        String text = String.valueOf(numero);
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();
        
        g2.setColor(puedeSerMovida ? color : new Color(
            color.getRed(), color.getGreen(), color.getBlue(), 150
        ));
        g2.drawString(text, (getWidth()-textWidth)/2, (getHeight()+textHeight)/2 - 8);
        
        g2.dispose();
    }
    
    public int getNumero() {
        return numero;
    }
    
    public Color getFichaColor() {
        return color;
    }
}
