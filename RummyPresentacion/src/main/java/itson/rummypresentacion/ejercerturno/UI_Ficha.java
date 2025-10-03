/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.rummypresentacion.ejercerturno;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JToggleButton;

/**
 *
 * @author Dana Chavez
 */
public class UI_Ficha extends JToggleButton {

    private int numero;
    private Color color;

    public UI_Ficha(int numero, Color color) {
        this.numero = numero;
        this.color = color;

        setPreferredSize(new Dimension(60, 90));
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);

        if (isSelected()) {
            g2.setColor(Color.RED); 
        } else {
            g2.setColor(Color.GRAY);
        }
        g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);

        g2.setFont(new Font("Arial", Font.BOLD, 28));
        FontMetrics fm = g2.getFontMetrics();
        String text = String.valueOf(numero);
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();

        g2.setColor(color);
        g2.drawString(text, (getWidth()-textWidth)/2, (getHeight()+textHeight)/2 - 10);

        g2.dispose();
    }

    public int getNumero() {
        return numero;
    }

    public Color getFichaColor() {
        return color;
    }
}
