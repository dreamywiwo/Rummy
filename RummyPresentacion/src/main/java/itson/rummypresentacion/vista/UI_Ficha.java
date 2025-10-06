/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.rummypresentacion.vista;

import itson.rummypresentacion.DTOs.FichaDTO;
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

public class UI_Ficha extends JToggleButton implements IComponente {
    private FichaDTO fichaDTO;
    private boolean puedeSerMovida = true;
    
    public UI_Ficha(FichaDTO fichaDTO) {
        this.fichaDTO = fichaDTO;
        setPreferredSize(new Dimension(60, 90));
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
    }

    public UI_Ficha(int numero, Color color) {
        String colorStr = convertirColorAString(color);
        this.fichaDTO = new FichaDTO(numero, colorStr, "temp_" + numero + "_" + colorStr);
        setPreferredSize(new Dimension(60, 90));
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
    }
    
    private String convertirColorAString(Color color) {
        if (color.equals(Color.RED)) return "rojo";
        if (color.equals(Color.BLUE)) return "azul";
        if (color.equals(Color.GREEN)) return "verde";
        if (color.equals(Color.YELLOW)) return "amarillo";
        if (color.equals(Color.BLACK)) return "negro";
        return "gris";
    }
    
    public FichaDTO getFichaDTO() {
        return fichaDTO;
    }
    
    public int getNumero() {
        return fichaDTO.getNumero();
    }
    
    public Color getFichaColor() {
        return convertirColorDTO(fichaDTO.getColor());
    }
    
    private Color convertirColorDTO(String colorDTO) {
        if (colorDTO == null) return Color.GRAY;
        switch (colorDTO.toLowerCase()) {
            case "rojo": return Color.RED;
            case "azul": return Color.BLUE;
            case "verde": return Color.GREEN;
            case "amarillo": return Color.YELLOW;
            case "negro": return Color.BLACK;
            default: return Color.GRAY;
        }
    }
    
    @Override
    public String getId() {
        return fichaDTO.getId();
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

        // Borde - MÁS VISIBLE cuando está seleccionado
        if (isSelected()) {
            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke(4f)); // Borde más grueso
            g2.drawRoundRect(2, 2, getWidth()-4, getHeight()-4, 12, 12);
        } else if (!puedeSerMovida) {
            g2.setColor(Color.DARK_GRAY);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(2, 2, getWidth()-4, getHeight()-4, 12, 12);
        } else {
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(2, 2, getWidth()-4, getHeight()-4, 12, 12);
        }

        // Número
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fm = g2.getFontMetrics();
        String text = String.valueOf(getNumero());
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();
        
        g2.setColor(puedeSerMovida ? getFichaColor() : new Color(
            getFichaColor().getRed(), getFichaColor().getGreen(), 
            getFichaColor().getBlue(), 150
        ));
        g2.drawString(text, (getWidth() - textWidth)/2, (getHeight() + textHeight)/2 - 8);
        
        g2.dispose();
    }
    
    // Se definen los metodos de IComponente 
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

}
