/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package atm;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Alberto
 */
public class Devolucion extends JFrame {

    public Devolucion(int cantidad) {
        setTitle("Minima Cantidad de Billetes");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(4, 5, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.setPreferredSize(new Dimension(1500, 1500));
        List<JLabel> billeteLabels = new ArrayList<>();
        int billete50 = 0;
        int billete20 = 0;
        int billete10 = 0;
        int billete5 = 0;

        if (cantidad >= 50) {
            billete50 = (int) (cantidad / 50);
            cantidad %= 50;
        }

        if (cantidad >= 20) {
            billete20 = (int) (cantidad / 20);
            cantidad %= 20;
        }

        if (cantidad >= 10) {
            billete10 = (int) (cantidad / 10);
            cantidad %= 10;
        }

        if (cantidad >= 5) {
            billete5 = (int) (cantidad / 5);
            cantidad %= 5;
        }

        for (int i = 0; i < billete50; i++) {
            ImageIcon billete50Icon = new ImageIcon("./src/ATM_Images/50Euros.png"); // Ruta de la imagen del billete de 50
            JLabel billete50Label = new JLabel(new ImageIcon(billete50Icon.getImage().getScaledInstance(200, -1, Image.SCALE_SMOOTH)));
            billeteLabels.add(billete50Label);
        }

        for (int i = 0; i < billete20; i++) {
            ImageIcon billete20Icon = new ImageIcon("./src/ATM_Images/20Euros.png"); // Ruta de la imagen del billete de 20
            JLabel billete20Label = new JLabel(new ImageIcon(billete20Icon.getImage().getScaledInstance(200, -1, Image.SCALE_SMOOTH)));
            billeteLabels.add(billete20Label);
        }

        for (int i = 0; i < billete10; i++) {
            ImageIcon billete10Icon = new ImageIcon("./src/ATM_Images/10Euros.png"); // Ruta de la imagen del billete de 10
            JLabel billete10Label = new JLabel(new ImageIcon(billete10Icon.getImage().getScaledInstance(200, -1, Image.SCALE_SMOOTH)));
            billeteLabels.add(billete10Label);
        }

        for (int i = 0; i < billete5; i++) {
            ImageIcon billete5Icon = new ImageIcon("./src/ATM_Images/5Euros.png"); // Ruta de la imagen del billete de 5
            JLabel billete5Label = new JLabel(new ImageIcon(billete5Icon.getImage().getScaledInstance(200, -1, Image.SCALE_SMOOTH)));
            billeteLabels.add(billete5Label);
        }

        for (JLabel billeteLabel : billeteLabels) {
            panel.add(billeteLabel);
     
        }

        add(panel);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        int cantidad = 1475;
        Devolucion frame = new Devolucion(cantidad);
    }

}
